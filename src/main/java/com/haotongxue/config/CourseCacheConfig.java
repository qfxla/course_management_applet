package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.service.IInfoService;
import com.haotongxue.utils.DateConvert;
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
