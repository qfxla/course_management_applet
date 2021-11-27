package com.haotongxue.controller;


import com.haotongxue.entity.vo.FreeRoomVo;
import com.haotongxue.service.FreeRoomVoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/getFreeByFour")
    public List<FreeRoomVo> getFreeByFour(@RequestParam("campus") String campus,
                                          @RequestParam("building") String building,
                                          @RequestParam("week") int week,
                                          @RequestParam("xingqi") int xingqi){
        return freeRoomVoService.queryFreeRoom(campus,building,week,xingqi);
    }

}

