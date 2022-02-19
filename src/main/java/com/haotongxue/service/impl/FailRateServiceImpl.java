package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.*;
import com.haotongxue.entity.vo.ESVO;
import com.haotongxue.entity.vo.FailRateVO;
import com.haotongxue.mapper.FailRateMapper;
import com.haotongxue.service.GradeService;
import com.haotongxue.service.IFailRateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.service.IMajorService;
import com.haotongxue.service.ISubjectService;
import com.haotongxue.utils.ESUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
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

    @Autowired
    IMajorService majorService;

    @Autowired
    private ESUtils esUtils;

    @Autowired
    private RestHighLevelClient client;

    @Resource(name = "studentStatusCache")
    LoadingRedisCache<StudentStatus> studentStatusCache;


    AtomicInteger count = new AtomicInteger(0);

    @Override
    //@Transactional(rollbackFor = Exception.class)
    public void refreshRate() {
        log.info("--->开始更新挂科率<-----");
        ExecutorService executorService = Executors.newFixedThreadPool(16);

        ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();
        QueryWrapper<Grade> gradeQueryWrapper = new QueryWrapper<>();
        gradeQueryWrapper.eq("property","必修").or().eq("property","限选");
        List<Grade> list = gradeService.list(gradeQueryWrapper);
        for (Grade grade : list){
            executorService.execute(() -> {
                String subject = grade.getSubject();
                String property = grade.getProperty();
                //String term = grade.getTerm();
                StudentStatus studentStatus = studentStatusCache.get(grade.getOpenid());
                if (studentStatus == null){
                    return;
                }
                String majorId = studentStatus.getMajorId();
                QueryWrapper<Subject> subjectQueryWrapper = new QueryWrapper<>();
                subjectQueryWrapper
                        .eq("subject_name",subject)
                        .eq("property",property)
                        .eq("marjor_id",majorId);
                String key = subject + property + majorId;
                Subject subjectEntity = subjectService.getOne(subjectQueryWrapper);
                if (subjectEntity == null){
                    lockMap.computeIfAbsent(key, s -> new Object());
                    synchronized (lockMap.get(key)){
                        subjectEntity = subjectService.getOne(subjectQueryWrapper);
                        if (subjectEntity == null){
                            subjectEntity = new Subject();
                            subjectEntity.setSubjectName(subject);
                            subjectEntity.setMajorId(majorId);
                            subjectEntity.setProperty(property);
                            subjectService.save(subjectEntity);

                            //保存科目后，统计挂科率
                            String subjectId = subjectEntity.getSubjectId();
                            QueryWrapper<FailRate> failRateQueryWrapper = new QueryWrapper<>();

                            QueryWrapper<Grade> gradeWrapper = new QueryWrapper<>();
                            gradeWrapper
                                    .select("term")
                                    .eq("subject",subject)
                                    .eq("property",property)
                                    .groupBy("term");
                            List<Grade> termList = gradeService.list(gradeWrapper);
                            for (Grade item : termList){
                                String term = item.getTerm();
                                failRateQueryWrapper.eq("term",term).eq("subject_id",subjectId);
                                FailRate failRate = failRateService.getOne(failRateQueryWrapper);
                                if (failRate == null){
                                    failRate = failRateService.getOne(failRateQueryWrapper);
                                    if (failRate == null){
                                        failRate = new FailRate();
                                        failRate.setSubjectId(subjectId);
                                        failRate.setTerm(term);
                                        Integer totalCount = failRateMapper.countTotal(subject,property,majorId,term);
                                        //log.info("totalCount="+totalCount);
                                        Integer failCount = failRateMapper.countFail(subject,property,majorId,term);
                                        //log.info("failCount="+failCount);
//                                        try{
//
//                                        }catch (Exception e){
//                                            e.printStackTrace();
//                                            log.info(subject+" "+property+" "+majorId+" "+term);
//                                        }
                                        failRate.setTotalCount(totalCount);
                                        failRate.setFailCount(failCount);
                                        double rate = 0;
                                        if (totalCount != null && failCount != null && totalCount != 0){
                                            rate = (double) failCount/totalCount * 100;
                                        }
                                        //String failRateStr = String.valueOf(rate).substring(0, 3);
                                        failRate.setFailRate(rate);
                                        try{
                                            failRateService.save(failRate);
                                        }catch (Exception e){
                                            log.info("rate="+rate);
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }
                        }
                        lockMap.remove(key);
                    }
                }

                log.info("处理了条"+ count.incrementAndGet() +"成绩");
            });
        }
        executorService.shutdown();
    }

    @Override
    public void prepareES() {
        List<Subject> list = subjectService.list();
        int count = 1;
        for (Subject subject : list){
            String subjectId = subject.getSubjectId();
            String majorId = subject.getMajorId();
            String subjectName = subject.getSubjectName();
            String property = subject.getProperty();
            Major major = majorService.getById(majorId);
            Integer collegeId = major.getCollegeId();
            FailRateVO failRateVO = new FailRateVO();
            failRateVO.setCollegeId(String.valueOf(collegeId));
            failRateVO.setMajorId(majorId);
            failRateVO.setSubjectId(subjectId);
            failRateVO.setSubjectName(subjectName);
            failRateVO.setProperty(property);

            //查询这个专业的这个科目的挂科率
            QueryWrapper<FailRate> failRateQueryWrapper = new QueryWrapper<>();
            failRateQueryWrapper
                    .eq("subject_id",subjectId)
                    .orderByAsc("term")
                    .last("limit 3");
            failRateVO.setList(failRateService.list(failRateQueryWrapper));
            esUtils.esAddAsync("failrate", failRateVO, new ActionListener<IndexResponse>() {
                @Override
                public void onResponse(IndexResponse indexResponse) {

                }

                @Override
                public void onFailure(Exception e) {
                    log.info("ES插入失败--->subjectId="+failRateVO.getSubjectId());
                    e.printStackTrace();
                }
            });
            log.info("-->准备了"+ count++ +"个挂科率");
        }
    }

    @Override
    public List<ESVO> getSubjectFail(String collegeId, String majorId, String subjectId, Integer currentPage) throws IOException {
        SearchRequest request = new SearchRequest("failrate");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页
        searchSourceBuilder.from((currentPage-1)*25);
        searchSourceBuilder.size(25);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(collegeId)){
            boolQueryBuilder.must(QueryBuilders.termQuery("collegeId",collegeId));
        }
        if (!StringUtils.isEmpty(majorId)){
            boolQueryBuilder.must(QueryBuilders.termQuery("majorId",majorId));
        }
        if (!StringUtils.isEmpty(subjectId)){
            boolQueryBuilder.must(QueryBuilders.termQuery("subjectId",subjectId));
        }
        searchSourceBuilder.query(boolQueryBuilder);
        request.source(searchSourceBuilder);

        //发请求
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);

        return ESUtils.transformNormal(search.getHits().getHits());
    }
}
