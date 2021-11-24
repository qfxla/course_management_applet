package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.*;
import com.haotongxue.mapper.InfoMapper;
import com.haotongxue.service.*;
import com.haotongxue.utils.DateConvert;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    @Autowired
    IUserService userService;

    /**
     * 用于做商品的本地缓存处理
     * @return
     */
    @Bean("loginCache")
    public LoadingCache<String,Object> getCache(){
        return Caffeine.newBuilder()
                .expireAfterWrite(3,TimeUnit.DAYS)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public @Nullable Object load(String key) throws Exception {
                        return userService.getById(key);
                    }
                });
    }
}
