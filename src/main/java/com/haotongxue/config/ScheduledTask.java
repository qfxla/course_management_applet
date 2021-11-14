package com.haotongxue.config;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.mapper.InfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.jsf.DecoratingNavigationHandler;

import javax.annotation.Resource;

/**
 * @author zcj
 * @creat 2021-11-13-22:40
 */
@Component
@EnableScheduling
public class ScheduledTask {

    @Autowired
    InfoMapper infoMapper;

//    @Resource(name = "weekCache")
//    LoadingCache<String,Object> weekCache;
//
//    @Scheduled(cron = "0 0 0 ? * MON")
//    public void updateCurrentWeek(){
//        Integer week = infoMapper.getWeekByToday();
//        weekCache.put("week",week);
//    }
}
