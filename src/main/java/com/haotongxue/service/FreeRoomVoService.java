package com.haotongxue.service;

import com.haotongxue.entity.vo.FreeRoomVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description TODO
 * @date 2021/11/27 16:04
 */

public interface FreeRoomVoService {
    public List<FreeRoomVo> queryFreeRoom(String campus,String building,int week,int xingqi);
}
