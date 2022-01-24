package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.entity.Class;
import com.haotongxue.service.IClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ClassCacheConfig {

    @Autowired
    MyRedis<Class> myRedis;

    @Autowired
    IClassService classService;

    @Bean("classCache")
    public LoadingRedisCache<Class> getCache(){
        return myRedis.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .setPrefix("class")
                .build(key -> classService.getById(key));
    }
}
