package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.mapper.InfoMapper;
import com.haotongxue.utils.DateConvert;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class WeekCacheConfig {

    @Autowired
    InfoMapper infoMapper;

    //今天是哪周
    @Bean("weekCache")
    public LoadingCache<String,Object> whichWeek(){
        int min = DateConvert.cacheMin();
        return Caffeine.newBuilder()
                .expireAfterWrite(min, TimeUnit.MINUTES) //缓存时间为当前时间到下周一的0点
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public @Nullable Object load(String key) throws Exception {
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
