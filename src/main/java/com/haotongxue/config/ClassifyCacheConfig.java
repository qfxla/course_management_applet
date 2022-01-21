package com.haotongxue.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.entity.Class;
import com.haotongxue.entity.College;
import com.haotongxue.entity.Major;
import com.haotongxue.entity.vo.ClassVO;
import com.haotongxue.entity.vo.ClassifyVO;
import com.haotongxue.entity.vo.CollegeVO;
import com.haotongxue.entity.vo.MajorVO;
import com.haotongxue.service.IClassService;
import com.haotongxue.service.ICollegeService;
import com.haotongxue.service.IMajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ClassifyCacheConfig {

    @Autowired
    MyRedis<ClassifyVO> myRedis;

    @Autowired
    ICollegeService collegeService;

    @Autowired
    IMajorService majorService;

    @Autowired
    IClassService classService;

    @Bean("classifyCache")
    public LoadingRedisCache<ClassifyVO> getCache(){
        return myRedis.newBuilder()
                //.expireAfterWrite(1, TimeUnit.DAYS)
                .setPrefix("classify")
                .build(key -> {
                    List<College> collegeList = collegeService.list();
                    ArrayList<CollegeVO> collegeVOS = new ArrayList<>();
                    for (College college : collegeList){
                        CollegeVO collegeVO = new CollegeVO();
                        collegeVO.setCollegeId(college.getId());
                        collegeVO.setCollegeName(college.getName());

                        QueryWrapper<Major> majorQueryWrapper = new QueryWrapper<>();
                        majorQueryWrapper.eq("college_id",college.getId());
                        List<Major> majorList = majorService.list(majorQueryWrapper);
                        ArrayList<MajorVO> majorVOS = new ArrayList<>();
                        for (Major major : majorList){
                            MajorVO majorVO = new MajorVO();
                            majorVO.setMajorId(major.getMajorId());
                            majorVO.setName(major.getName());

                            QueryWrapper<Class> classQueryWrapper = new QueryWrapper<>();
                            classQueryWrapper.eq("major_id",major.getMajorId());
                            List<Class> classList = classService.list(classQueryWrapper);
                            ArrayList<ClassVO> classVOS = new ArrayList<>();
                            for (Class classEntity : classList){
                                ClassVO classVO = new ClassVO();
                                classVO.setClassId(classEntity.getClassId());
                                classVO.setClassName(classEntity.getName());
                                classVOS.add(classVO);
                            }
                            majorVO.setList(classVOS);
                            majorVOS.add(majorVO);
                        }
                        collegeVO.setList(majorVOS);
                        collegeVOS.add(collegeVO);
                    }
                    ClassifyVO classifyVO = new ClassifyVO();
                    classifyVO.setList(collegeVOS);
                    return classifyVO;
                });
    }
}
