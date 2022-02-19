package com.haotongxue.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.cacheUtil.MyRedis;
import com.haotongxue.entity.CoursePlus;
import com.haotongxue.service.ICoursePlusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CoursePlusCacheConfig {

    @Autowired
    MyRedis<List<CoursePlus[]>> myRedis;

    @Autowired
    ICoursePlusService coursePlusService;

    /**
     * 用于缓存学生一周课表
     * @return
     */
    @Bean("coursePlusCache")
    public LoadingRedisCache<List<CoursePlus[]>> getCache(){
        return myRedis.newBuilder()
                .expireAfterWrite(2, TimeUnit.DAYS)
                .setPrefix("coursePlus")
                .build(noAndWeek -> {
                    String[] split = noAndWeek.split(":");
                    String no = split[0];
                    String week = split[1];
                    QueryWrapper<CoursePlus> coursePlusQueryWrapper = new QueryWrapper<>();
                    coursePlusQueryWrapper.eq("no",no).eq("week",week);
                    List<CoursePlus> list = coursePlusService.list(coursePlusQueryWrapper);
                    List<CoursePlus[]> result = new ArrayList<>();
                    for (int i=0;i<7;i++){
                        result.add(new CoursePlus[12]);
                    }
                    for (CoursePlus coursePlus : list){
                        String dayOfWeek = coursePlus.getDayOfWeek();
                        CoursePlus[] coursePluses = result.get(Integer.parseInt(dayOfWeek));
                        String section = coursePlus.getSection();
                        for (int j=0;j<section.length();j++){
                            char c = section.charAt(j);
                            if (c == '0'){
                                coursePluses[0] = coursePlus;
                                coursePluses[1] = coursePlus;
                            }else if (c == '1'){
                                coursePluses[2] = coursePlus;
                                coursePluses[3] = coursePlus;
                            }else if (c == '2'){
                                coursePluses[4] = coursePlus;
                            }else if (c == '3'){
                                coursePluses[5] = coursePlus;
                                coursePluses[6] = coursePlus;
                            }else if (c == '4'){
                                coursePluses[7] = coursePlus;
                                coursePluses[8] = coursePlus;
                            }else if (c == '5'){
                                coursePluses[9] = coursePlus;
                                coursePluses[10] = coursePlus;
                                coursePluses[11] = coursePlus;
                            }
                        }
                    }
                    return result;
                });
    }
}
