package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.cacheUtil.RedisLoader;
import com.haotongxue.entity.*;
import com.haotongxue.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Configuration
public class CourseInfoConfig {

    @Resource
    ITeacherService iTeacherService;

    @Resource
    ICourseService iCourseService;

    @Resource
    ISectionService iSectionService;

    @Resource
    IWeekService iWeekService;

    @Resource
    IClassroomService iClassroomService;

    @Autowired
    MyRedis myRedis;

    @Bean("courseInfo")
    public LoadingRedisCache getCourseInfo(){
        return myRedis.newBuilder()
                .expireAfterWrite(7,TimeUnit.DAYS)
                .build(new RedisLoader() {
                    @Override
                    public Object load(String key) {
                        int idx = key.indexOf("-");
                        String cacheType = key.substring(0,idx);
                        String realKey = key.substring(idx+1);

                        switch (cacheType){
                            case "teacher":
                                int realKeyInt1 = Integer.parseInt(realKey);
                                Teacher teacher = iTeacherService.getById(realKeyInt1);
                                return teacher.getName();
                            case "course":
                                Course course = iCourseService.getById(realKey);
                                return course.getName();
                            case "classroom":
                                int realKeyInt2 = Integer.parseInt(realKey);
                                Classroom classroom = iClassroomService.getById(realKeyInt2);
                                return classroom.getLocation();
                            case "week":
                                int realKeyInt3 = Integer.parseInt(realKey);
                                Week week = iWeekService.getById(realKeyInt3);
                                return week.getWeekId();
                            case "section":
                                int realKeyInt4 = Integer.parseInt(realKey);
                                Section section = iSectionService.getById(realKeyInt4);
                                return section.getSectionId();
                            default:return null;
                        }
                    }
                });
    }
}
