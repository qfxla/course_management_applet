package com.haotongxue.controller;


import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.vo.FreeRoomVo;
import com.haotongxue.service.FreeRoomVoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 空闲教室表 前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-11-26
 */
@RestController
@RequestMapping("/freeRoom")
public class FreeRoomController {

    @Resource
    FreeRoomVoService freeRoomVoService;


    @Resource(name = "freeRoomCache")
    LoadingCache<String,Object> freeRoomCache;

    @GetMapping("/getFreeByFour")
    public List<FreeRoomVo> getFreeByFour(@RequestParam("campus") String campus,
                                                   @RequestParam("building") String building,
                                                   @RequestParam("week") int week,
                                                   @RequestParam("xingqi") int xingqi){
        String cacheType = "freeRoom";
        //freeRoom-海珠校区-教学楼-13-5
        //freeRoom-白云校区-（白）曾宪梓楼-1-4
        String key = cacheType + "-" + campus + "-" + building + "-" + week + "-" + xingqi;
        return (List<FreeRoomVo>)freeRoomCache.get(key);
    }
}

