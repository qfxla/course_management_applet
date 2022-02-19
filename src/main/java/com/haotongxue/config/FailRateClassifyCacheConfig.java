package com.haotongxue.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.entity.*;
import com.haotongxue.entity.Class;
import com.haotongxue.entity.vo.*;
import com.haotongxue.service.ICollegeService;
import com.haotongxue.service.IMajorService;
import com.haotongxue.service.ISubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class FailRateClassifyCacheConfig {
    @Autowired
    MyRedis<FailRateClassifyVO> myRedis;

    @Autowired
    ICollegeService collegeService;

    @Autowired
    IMajorService majorService;

    @Autowired
    ISubjectService subjectService;

    @Bean("failRateClassifyCache")
    public LoadingRedisCache<FailRateClassifyVO> getCache(){
        return myRedis.newBuilder()
                //.expireAfterWrite(3, TimeUnit.DAYS)
                .setPrefix("failRateClassify")
                .build(key -> {
                    List<College> collegeList = collegeService.list();
                    ArrayList<FailRateCollegeVO> failRateCollegeVOS = new ArrayList<>();
                    for (College college : collegeList){
                        FailRateCollegeVO failRateCollegeVO = new FailRateCollegeVO();
                        failRateCollegeVO.setCollegeId(college.getId());
                        failRateCollegeVO.setCollegeName(college.getName());

                        QueryWrapper<Major> majorQueryWrapper = new QueryWrapper<>();
                        majorQueryWrapper.eq("college_id",college.getId());
                        List<Major> majorList = majorService.list(majorQueryWrapper);
                        ArrayList<FailRateMajorVO> majorVOS = new ArrayList<>();
                        for (Major major : majorList){
                            FailRateMajorVO majorVO = new FailRateMajorVO();
                            majorVO.setMajorId(major.getMajorId());
                            majorVO.setName(major.getName());

                            QueryWrapper<Subject> subjectQueryWrapper = new QueryWrapper<>();
                            subjectQueryWrapper.eq("major_id",major.getMajorId());
                            List<Subject> subjectList = subjectService.list(subjectQueryWrapper);
                            ArrayList<FailRateSubjectVO> subjectVOS = new ArrayList<>();
                            for (Subject subject : subjectList){
                                FailRateSubjectVO subjectVO = new FailRateSubjectVO();
                                subjectVO.setSubjectId(subject.getSubjectId());
                                subjectVO.setSubjectName(subject.getSubjectName());
                                subject.setProperty(subject.getProperty());
                                subjectVOS.add(subjectVO);
                            }
                            majorVO.setList(subjectVOS);
                            majorVOS.add(majorVO);
                        }
                        failRateCollegeVO.setList(majorVOS);
                        failRateCollegeVOS.add(failRateCollegeVO);
                    }
                    FailRateClassifyVO failRateClassifyVO = new FailRateClassifyVO();
                    failRateClassifyVO.setList(failRateCollegeVOS);
                    return failRateClassifyVO;
                });
    }
}
