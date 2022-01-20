package com.haotongxue.config;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.cacheUtil.RedisLoader;
import com.haotongxue.service.FreeRoomVoService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    MyRedis myRedis;


    @Bean("freeRoomCache")
    public LoadingRedisCache getFreeCache(){
        return myRedis.newBuilder()
                .expireAfterWrite(7,TimeUnit.DAYS)
                .build(new RedisLoader() {
                    @Override
                    public Object load(String key) {
                        //freeRoom-海珠校区-教学楼-13-5
                        //freeRoom-白云校区-（白）曾宪梓楼-1-4
                        String cacheType = key.substring(0,key.indexOf("-"));
                        //System.out.println(cacheType);
                        String campus = key.substring(cacheType.length() + 1, key.indexOf("校区")) + "校区";
                        //System.out.println(campus);
                        String building = key.substring(cacheType.length() + campus.length() + 2,key.indexOf("楼")) + "楼";
                        //System.out.println(building);
                        String weekStr = key.substring(key.indexOf("楼") + 2,key.lastIndexOf("-"));
                        //System.out.println(weekStr);
                        int week = Integer.parseInt(weekStr);
                        //System.out.println(week);
                        String xingqiStr = key.substring(key.lastIndexOf("-") + 1);
                        //System.out.println(xingqiStr);
                        int xingqi = Integer.parseInt(xingqiStr);
                        return freeRoomVoService.queryFreeRoom(campus,building,week,xingqi);
                    }
                });
    }
}