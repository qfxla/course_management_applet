package com.haotongxue.cacheUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class MyRedis {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    public LoadingRedisCacheBuilder newBuilder(){
        return new LoadingRedisCacheBuilder(redisTemplate);
    }
}
