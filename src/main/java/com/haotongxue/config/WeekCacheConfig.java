package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.cacheUtil.RedisLoader;
import com.haotongxue.mapper.InfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class WeekCacheConfig {

    @Autowired
    InfoMapper infoMapper;

    @Autowired
    MyRedis myRedis;

    @Bean("weekCache")
    public LoadingRedisCache getWhichWeek(){
        return myRedis.newBuilder()
                .expireAfterWrite(1000,TimeUnit.DAYS)
                .build(new RedisLoader() {
                    @Override
                    public Object load(String key) {
                        String cacheType = key.substring(0, 4);
                        if (cacheType.equals("week")){
                            Integer week = infoMapper.getWeekByToday();
                            return week;
                        }
                        return null;
                    }
                });
    }

}
