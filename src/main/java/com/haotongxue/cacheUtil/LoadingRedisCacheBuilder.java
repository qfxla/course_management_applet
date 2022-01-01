package com.haotongxue.cacheUtil;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class LoadingRedisCacheBuilder {

    public static final int NO_EXPIRE = 0;

    public static final int EXPIRE_AFTER_WRITE = 1;

    public static final int EXPIRE_AFTER_ACCESS = 2;

    private int expireFlag;

    private long duration;

    private TimeUnit timeUnit;

    private RedisTemplate<String,Object> redisTemplate;

    public LoadingRedisCacheBuilder expireAfterWrite(long duration, TimeUnit timeUnit){
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.expireFlag = EXPIRE_AFTER_WRITE;
        return this;
    }

//    public LoadingRedisCacheBuilder expireAfterAccess(long duration, TimeUnit timeUnit){
//        this.duration = duration;
//        this.timeUnit = timeUnit;
//        this.expireFlag = EXPIRE_AFTER_ACCESS;
//        return this;
//    }

    public LoadingRedisCache build(RedisLoader loader){
        LoadingRedisCache loadingRedisCache = new LoadingRedisCache(loader,redisTemplate,duration,timeUnit,expireFlag);
        return loadingRedisCache;
    }

    public LoadingRedisCacheBuilder(RedisTemplate<String, Object> redisTemplate) {
        this.expireFlag = NO_EXPIRE;
        this.redisTemplate = redisTemplate;
    }
}
