package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.cacheUtil.RedisLoader;
import com.haotongxue.service.IInfoService;
import com.haotongxue.utils.DateConvert;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CourseCacheConfig {

    @Autowired
    IInfoService iInfoService;

    @Autowired
    MyRedis myRedis;

    //每周的课表缓存
//    @Bean("courseCache")
//    public LoadingCache<String,Object> getCourseCache(){
//        int min = DateConvert.cacheMin();
//        return Caffeine.newBuilder()
//                .expireAfterWrite(min, TimeUnit.MINUTES)
//                .build(new CacheLoader<String, Object>() { //到下周一0点的分钟数
//                    @Override
//                    public @Nullable Object load(String key) throws Exception {
//                        String cacheType = key.substring(0, 4);
//                        String realKey = key.substring(4);
//                        String[] split = realKey.split(":");
//                        String openId = split[0];
//                        String week = split[1];
//                        if (cacheType.equals("cour")){
//                            List<List> timeTables = iInfoService.getInfo(openId,Integer.valueOf(week));
//                            return timeTables;
//                        }
//                        return null;
//                    }
//                });
//    }

    @Bean("courseCache")
    public LoadingRedisCache getCourseCache(){
        int min = DateConvert.cacheMin();
        return myRedis.newBuilder()
                .expireAfterWrite(min,TimeUnit.MINUTES)
                .build(key -> {
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
                });
    }
}
