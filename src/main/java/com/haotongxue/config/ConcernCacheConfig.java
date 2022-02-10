package com.haotongxue.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.entity.Concern;
import com.haotongxue.entity.User;
import com.haotongxue.service.IConcernService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class ConcernCacheConfig {
    @Autowired
    MyRedis<Concern> myRedis;

    @Autowired
    IConcernService concernService;

    /**
     * 用于关注列表的缓存
     * @return
     */
    @Bean("concernCache")
    public LoadingRedisCache<Concern> getCache(){
        return myRedis.newBuilder()
                .expireAfterWrite(3, TimeUnit.DAYS)
                .build(no -> {
                    QueryWrapper<Concern> concernQueryWrapper = new QueryWrapper<>();
                    concernQueryWrapper.eq("no",no);
                    return concernService.list(concernQueryWrapper);
                });
    }

    @Bean("isConcernCache")
    public LoadingRedisCache<Concern> getIsConcernCache(){
        return myRedis.newBuilder()
                .expireAfterWrite(3,TimeUnit.DAYS)
                .setPrefix("isConcern")
                .build(key -> {
                    String no = key.substring(0,12);
                    String concernedNo = key.substring(12);
                    QueryWrapper<Concern> wrapper = new QueryWrapper<>();
                    wrapper.eq("no",no).eq("concerned_no",concernedNo);
                    return concernService.getOne(wrapper);
                });
    }
}
