package com.haotongxue.controller;


import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@RestController
@RequestMapping("/zkCourse/week")
public class WeekController {

//    @Resource(name = "weekCache")
//    LoadingCache<String,Object> weekCache;

    @Resource(name = "weekCache")
    LoadingRedisCache weekCache;

    @ApiOperation(value = "获得当前是哪周")
    @GetMapping()
    public R getWhichWeek(){
        //查今天是第几周
        int week = (int)weekCache.get("week");
        return R.ok().data("week",week);
    }

    @ApiOperation(value = "删除当前周的缓存")
    @GetMapping("/delWeekCache")
    public R delWeekCache(){
        weekCache.invalidate("week");
        return R.ok().data("mes","清除成功");
    }
}

