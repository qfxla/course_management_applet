package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.mapper.InfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

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

    @Resource(name = "weekCache")
    LoadingRedisCache weekCache;

//    @Scheduled(cron = "1 0 0 ? * MON")  //星期一0点1秒
    public void updateCurrentWeek(){
        Integer week = infoMapper.getWeekByToday();
        weekCache.put("week",week);
    }
}
