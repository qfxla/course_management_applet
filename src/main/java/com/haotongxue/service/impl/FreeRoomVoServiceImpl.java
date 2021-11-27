package com.haotongxue.service.impl;

import com.haotongxue.entity.vo.FreeRoomVo;
import com.haotongxue.mapper.FreeRoomVoMapper;
import com.haotongxue.service.FreeRoomVoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description TODO
 * @date 2021/11/27 16:07
 */
@Service
public class FreeRoomVoServiceImpl implements FreeRoomVoService {


    @Resource
    FreeRoomVoMapper freeRoomVoMapper;


    @Override
    public List<FreeRoomVo> queryFreeRoom(String campus, String building, int week, int xingqi) {
        return freeRoomVoMapper.queryFreeRooms(campus,building,week,xingqi);
    }
}
