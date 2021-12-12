package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.vo.SelectedRuleVo;
import com.haotongxue.service.ISelectedService;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author zcj
 * @creat 2021-12-09-9:55
 */
@Configuration
public class SelectedCacheConfig {
    @Autowired
    ISelectedService iSelectedService;

//    @Bean("selectedCache")
//    public LoadingCache<String,Object> getCache(){
//        return Caffeine.newBuilder()
//                .expireAfterWrite(9999, TimeUnit.DAYS)
//                .build(new CacheLoader<String, Object>() {
//                    @Override
//                    public @Nullable Object load(String key) throws Exception {
//                        String[] split = key.split(":");
//                        if (split[0].equals("selected")){
//                            return iSelectedService.getSelected(Integer.valueOf(split[1]),split[2]);
//                        }
//                        return null;
//                    }
//                });
//    }
}
