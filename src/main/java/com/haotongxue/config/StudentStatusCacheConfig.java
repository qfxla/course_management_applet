package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.User;
import com.haotongxue.service.IStudentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class StudentStatusCacheConfig {

    @Autowired
    MyRedis<StudentStatus> myRedis;

    @Autowired
    IStudentStatusService studentStatusService;

    /**
     * 用于缓存学生的学籍表
     * @return
     */
    @Bean("studentStatusCache")
    public LoadingRedisCache<StudentStatus> getCache(){
        return myRedis.newBuilder()
                .expireAfterWrite(2, TimeUnit.DAYS)
                .setPrefix("stuStatus")
                .build(key -> studentStatusService.getById(key));
    }
}
