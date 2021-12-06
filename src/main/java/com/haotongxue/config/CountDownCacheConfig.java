package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CountDownCacheConfig {

//    @Bean("countDownCache")
//    public Cache<String,String> getCache(){
//        return Caffeine.newBuilder()
//                .expireAfterWrite(3, TimeUnit.HOURS)
//                .build();
//    }
}
