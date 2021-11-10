package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.controller.UserController;
import com.haotongxue.entity.User;
import com.haotongxue.service.IInfoService;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.UserContext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    @Autowired
    IUserService userService;

    @Autowired
    IInfoService iInfoService;

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

    //今日的PushCourseVo
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
}
