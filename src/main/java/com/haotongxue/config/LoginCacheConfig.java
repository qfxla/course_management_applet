package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.entity.User;
import com.haotongxue.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class LoginCacheConfig {

    @Autowired
    IUserService userService;

    @Autowired
    MyRedis<User> myRedis;

    /**
     * 用于做商品的本地缓存处理
     * @return
     */
    @Bean("loginCache")
    public LoadingRedisCache<User> getCache(){
        return myRedis.newBuilder()
                .expireAfterWrite(3,TimeUnit.DAYS)
                .build(key -> userService.getById(key));
    }
}
