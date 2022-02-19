package com.haotongxue.config;

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
