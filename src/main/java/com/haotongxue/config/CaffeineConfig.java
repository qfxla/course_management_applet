package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.*;
import com.haotongxue.service.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    @Autowired
    IUserService userService;

    @Autowired
    IInfoService iInfoService;

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

    /**
     * 用于做商品的本地缓存处理
     * @return
     */
    @Bean("loginCache")
    public LoadingCache<String,Object> getCache(){
        return Caffeine.newBuilder()
                .expireAfterAccess(3, TimeUnit.DAYS)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public @Nullable Object load(String key) throws Exception {
                        return userService.getById(key);
                    }
                });
    }

    //每周的课表缓存
    @Bean("courseCache")
    public LoadingCache<String,Object> getCourseCache(){
        return Caffeine.newBuilder()
                .expireAfterWrite(166, TimeUnit.HOURS)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public @Nullable Object load(String key) throws Exception {
                        String cacheType = key.substring(0, 4);
                        String realKey = key.substring(4);
                        String[] split = realKey.split(":");
                        String openId = split[0];
                        String week = split[1];
                        if (cacheType.equals("cour")){
                            List<List> timeTables = iInfoService.getInfo(openId,Integer.valueOf(week));
                            return timeTables;
                        }
                        return null;
                    }
                });
    }

    //今日的课表
    @Bean("todayCourseCache")
    public LoadingCache<String,Object> getTodayCourseCache(){
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)    //7 * 24 - 2 （-2是为了避免00点到2点访问的是昨天的课表）
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public @Nullable Object load(String key) throws Exception {
                        String cacheType = key.substring(0, 4);
                        String realKey = key.substring(4);
                        if (cacheType.equals("tody")){
                            List todayCourse = iInfoService.getTodayCourse(realKey);
                            return todayCourse;
                        }
                        return null;
                    }
                });
    }

    //所有教师、教室、周次、节次
    @Bean("courseInfo")
    public LoadingCache<String,Object> getCourseInfo(){
        return Caffeine.newBuilder()
                .expireAfterWrite(1,TimeUnit.DAYS)
                .build(new CacheLoader<String,Object>() {
                    @Override
                    public @Nullable Object load(@NonNull String Key) throws Exception {
                        int idx = Key.indexOf("-");
                        String cacheType = Key.substring(0,idx);
                        String realKey = Key.substring(idx+1);
                        int realKeyInt = Integer.parseInt(realKey);
                        switch (cacheType){
                            case "teacher":
                                Teacher teacher = iTeacherService.getById(realKeyInt);
                                return teacher.getName();
                            case "course":
                                Course course = iCourseService.getById(realKey);
                                return course.getName();
                            case "classroom":
                                Classroom classroom = iClassroomService.getById(realKey);
                                return classroom.getLocation();
                            case "week":
                                Week week = iWeekService.getById(realKey);
                                return week.getWeekId();
                            case "section":
                                Section section = iSectionService.getById(realKey);
                                return section.getSectionId();
                            default:return null;
                        }
                    }
                });
    }

}
