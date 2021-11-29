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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
public class WeekCacheConfig {

    @Autowired
    InfoMapper infoMapper;

    //今天是哪周
    @Bean("weekCache")
    public LoadingCache<String,Object> whichWeek1(){
//        int min = DateConvert.cacheMin();  //这里是求当前时间到周一0时的分钟数
        return Caffeine.newBuilder()
//                .expireAfterWrite((min + 1), TimeUnit.MINUTES) //缓存时间为当前时间到下周一的0点
                .expireAfterWrite(10000,TimeUnit.DAYS)
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
