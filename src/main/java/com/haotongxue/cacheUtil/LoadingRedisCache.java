package com.haotongxue.cacheUtil;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class LoadingRedisCache {

    private RedisLoader redisLoader;

    private RedisTemplate<String,Object> redisTemplate;

    private long duration;

    private TimeUnit timeUnit;

    private int expireFlag;

    public Object get(String key){
        Object o = redisTemplate.opsForValue().get(key);
        if (o == null){
            o = redisLoader.load(key);
            put(key,o);
        }
        return o;
    }

    public void put(String key,Object object){
        if (duration != 0){
            redisTemplate.opsForValue().set(key,object,duration,timeUnit);
        }else {
            redisTemplate.opsForValue().set(key,object);
        }
    }

    public void invalidate(String key){
        redisTemplate.delete(key);
    }



    public RedisLoader getRedisLoader() {
        return redisLoader;
    }

    public void setRedisLoader(RedisLoader redisLoader) {
        this.redisLoader = redisLoader;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public int getExpireFlag() {
        return expireFlag;
    }

    public void setExpireFlag(int expireFlag) {
        this.expireFlag = expireFlag;
    }

    public LoadingRedisCache(RedisLoader redisLoader, RedisTemplate<String, Object> redisTemplate, long duration, TimeUnit timeUnit, int expireFlag) {
        this.redisLoader = redisLoader;
        this.redisTemplate = redisTemplate;
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.expireFlag = expireFlag;
    }
}
