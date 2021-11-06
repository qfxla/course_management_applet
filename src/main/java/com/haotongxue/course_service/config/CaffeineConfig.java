package com.haotongxue.course_service.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.course_service.service.IUserService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    @Autowired
    IUserService userService;

    /**
     * 用于做商品的本地缓存处理
     * @return
     */
    @Bean
    public LoadingCache<String,Object> getCache(){
        return Caffeine.newBuilder()
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public @Nullable Object load(String key) throws Exception {
                        String cacheType = key.substring(0, 4);
                        String realKey = key.substring(4);
                        if (cacheType.equals("logi")){
                            return userService.getById(realKey);
                        }
                        return null;
                    }
                });
    }
}
