package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.FailRate;
import com.haotongxue.entity.Grade;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.Subject;
import com.haotongxue.mapper.FailRateMapper;
import com.haotongxue.service.GradeService;
import com.haotongxue.service.IFailRateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.service.ISubjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2022-02-07
 */
@Slf4j
@Service
public class FailRateServiceImpl extends ServiceImpl<FailRateMapper, FailRate> implements IFailRateService {

    @Autowired
    GradeService gradeService;

    @Autowired
    ISubjectService subjectService;

    @Autowired
    IFailRateService failRateService;

    @Autowired
    FailRateMapper failRateMapper;

    @Resource(name = "studentStatusCache")
    LoadingRedisCache<StudentStatus> studentStatusCache;

    final Object subjectLock = new Object();

    final Object failRateLock = new Object();

    AtomicInteger count = new AtomicInteger(0);

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public void refreshRate() {
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        QueryWrapper<Grade> gradeQueryWrapper = new QueryWrapper<>();
        gradeQueryWrapper.eq("property","必修").or().eq("property","限选");
        List<Grade> list = gradeService.list(gradeQueryWrapper);
        for (Grade grade : list){
            executorService.execute(() -> {
                String subject = grade.getSubject();
                String property = grade.getProperty();
                String term = grade.getTerm();
                StudentStatus studentStatus = studentStatusCache.get(grade.getOpenid());
                if (studentStatus == null){
                    return;
                }
                String majorId = studentStatus.getMajorId();
                QueryWrapper<Subject> subjectQueryWrapper = new QueryWrapper<>();
                subjectQueryWrapper
                        .eq("subject_name",subject)
                        .eq("property",property);
                Subject subjectEntity = subjectService.getOne(subjectQueryWrapper);
                if (subjectEntity == null){
                    synchronized (subjectLock){
                        subjectEntity = subjectService.getOne(subjectQueryWrapper);
                        if (subjectEntity == null){
                            subjectEntity = new Subject();
                            subjectEntity.setSubjectName(subject);
                            subjectEntity.setMarjorId(majorId);
                            subjectEntity.setProperty(property);
                            subjectService.save(subjectEntity);

                            //保存科目后，统计挂科率
                            String subjectId = subjectEntity.getSubjectId();
                            QueryWrapper<FailRate> failRateQueryWrapper = new QueryWrapper<>();
                            failRateQueryWrapper.eq("term",term).eq("subject_id",subjectId);
                            FailRate failRate = failRateService.getOne(failRateQueryWrapper);
                            if (failRate == null){
                                failRate = failRateService.getOne(failRateQueryWrapper);
                                if (failRate == null){
                                    failRate = new FailRate();
                                    failRate.setSubjectId(subjectId);
                                    failRate.setTerm(term);
                                    int totalCount = failRateMapper.countTotal(subject,property,majorId,term);
                                    int failCount = 0;
                                    try{
                                        failCount = failRateMapper.countFail(subject,property,majorId,term);
                                        log.info(failCount+"");
                                    }catch (Exception e){
                                        log.info(subject+" "+property+" "+majorId+" "+term);
                                    }
                                    failRate.setTotalCount(totalCount);
                                    failRate.setFailCount(failCount);
                                    double rate = (double) failCount/totalCount * 100;
                                    String failRateStr = String.valueOf(rate).substring(0, 3);
                                    failRate.setFailRate(failRateStr);
                                    failRateService.save(failRate);
                                }
                            }
                        }
                    }
                }

                log.info("处理了条"+ count.incrementAndGet() +"成绩");
            });
        }
//        List<FailRate> failRates = failRateService.list();
//        count.set(0);
//        for (FailRate failRate : failRates){
//            executorService.execute(() -> {
//                int totalCount = failRate.getTotalCount();
//                int failCount = failRate.getFailCount();
//                double rate = (double) failCount/totalCount * 100;
//                String failRateStr = String.valueOf(rate).substring(0, 3);
//                failRate.setFailRate(failRateStr);
//                failRateService.updateById(failRate);
//                log.info("算完"+ count.incrementAndGet() +"条比率");
//            });
//        }
        executorService.shutdown();
    }
}
