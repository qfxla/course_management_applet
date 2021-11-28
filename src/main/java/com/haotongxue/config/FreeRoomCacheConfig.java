package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.service.FreeRoomVoService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description TODO
 * @date 2021/11/28 13:37
 */
@Configuration
public class FreeRoomCacheConfig {


    @Resource
    FreeRoomVoService freeRoomVoService;


    @Bean("freeRoomCache")
    public LoadingCache<String,Object> getFreeCache(){
        return Caffeine.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS)
                .build(new CacheLoader<String, Object>() {
                    @Override
                    public @Nullable Object load(@NonNull String key) throws Exception {
                        //freeRoom-海珠校区-教学楼-13-5
                        //freeRoom-白云校区-（白）曾宪梓楼-1-4
                        String cacheType = key.substring(0,key.indexOf("-"));
                        String campus = key.substring(cacheType.length() + 1, key.indexOf("校区")) + "校区";
                        String building = key.substring(cacheType.length() + campus.length() + 2,key.indexOf("楼")) + "楼";
                        String weekStr = key.substring(key.indexOf("楼") + 2,key.lastIndexOf("-"));
                        int week = Integer.parseInt(weekStr);
                        String xingqiStr = key.substring(key.lastIndexOf("-") + 1);
                        int xingqi = Integer.parseInt(xingqiStr);
                        return freeRoomVoService.queryFreeRoom(campus,building,week,xingqi);
                    }
                });
    }
}
