package com.haotongxue.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.cacheUtil.RedisLoader;
import com.haotongxue.entity.Grade;
import com.haotongxue.mapper.GradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class GradeCacheConfig {

    @Autowired
    MyRedis myRedis;

    @Resource
    GradeMapper gradeMapper;

    @Bean("GradeCache")
    public LoadingRedisCache getUnThisGradeCache(){
        return myRedis.newBuilder()
            .expireAfterWrite(1,TimeUnit.DAYS)
            .build(new RedisLoader() {
               @Override
               public Object load(String key) {
                   String cacheType = key.substring(0, 5);
                   String realKey = key.substring(5);
                   String[] split = realKey.split(":");
                   String openId = split[0];
                   String term = split[1];
                   if (cacheType.equals("grade")){
                       List<Grade> gradeList = gradeMapper.selectList(new QueryWrapper<Grade>()
                               .eq("openid",openId).eq("term",term).
                               orderByDesc("term").orderByDesc("create_time"));
                       return gradeList;
                   }
                   return null;
               }
           });
    }
}
