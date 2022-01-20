package com.haotongxue.cacheUtil;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoadingRedisCache<T> {

    private RedisLoader redisLoader;

    private RedisTemplate<String,Object> redisTemplate;

    private long duration;

    private TimeUnit timeUnit;

    private int expireFlag;

    private String prefix = "";

    /**
     * 返回key对应的value
     * @param key
     * @return
     */
    public T get(String key){
        T o = (T) redisTemplate.opsForValue().get(prefix+key);
        if (o == null){
            o = (T) redisLoader.load(key);
            put(key,o);
        }
        return o;
    }

    /**
     * 返回key对应的List
     * @param key
     * @return
     */
    public List<T> getForList(String key){
        List<Object> list = (List<Object>) redisTemplate.opsForValue().get(prefix+key);
        List<T> returnList = null;
        if (list == null){
            list = (List<Object>) redisLoader.load(key);
            put(key,list);
        }
        if(list != null){
            returnList = new ArrayList<>();
            for (Object o : list){
                returnList.add((T) o);
            }
        }
        return returnList;
    }

//    /**
//     * 通过多个条件查询
//     * @param keys
//     * @return
//     */
//    public List<T> getForCondition(String ...keys){
//        StringBuilder stringBuilder = new StringBuilder();
//        for (String key : keys) {
//            stringBuilder.append(key);
//        }
//        Set<String> set = redisTemplate.keys(stringBuilder.toString());
//        List<T> returnList = null;
//        List<Object> list = null;
//        if (set == null){
//            list = (List<Object>) redisLoader.load(keys);
//            put(stringBuilder.toString(),list);
//        }
//        if (list != null){
//            returnList = new ArrayList<>();
//            for (Object o : list){
//                returnList.add((T) o);
//            }
//        }
//        return returnList;
//    }

    public void put(String key,Object object){
        if (duration != 0){
            redisTemplate.opsForValue().set(prefix+key,object,duration,timeUnit);
        }else {
            redisTemplate.opsForValue().set(prefix+key,object);
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }


    public LoadingRedisCache(RedisLoader redisLoader, RedisTemplate<String, Object> redisTemplate, long duration, TimeUnit timeUnit, int expireFlag,String prefix) {
        this.redisLoader = redisLoader;
        this.redisTemplate = redisTemplate;
        this.duration = duration;
        this.timeUnit = timeUnit;
        this.expireFlag = expireFlag;
        this.prefix = prefix;
    }
}
