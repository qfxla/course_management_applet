package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.service.IInfoService;
import com.haotongxue.service.IUserService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    @Autowired
    IUserService userService;

    @Autowired
    IInfoService iInfoService;

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
                            User byId = userService.getById(realKey);
                            return byId;
                        }else if (cacheType.equals("cour")){
                            List<List> timeTables = iInfoService.getInfo(Integer.valueOf(realKey));
                            return timeTables;
                        }
                        return null;
                    }
                });
    }
}
