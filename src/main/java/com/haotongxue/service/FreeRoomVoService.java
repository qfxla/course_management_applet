package com.haotongxue.service;

import com.haotongxue.entity.vo.FreeRoomVo;

import java.util.List;
import java.util.Map;

/**
 * @Description TODO
 * @date 2021/11/27 16:04
 */

public interface FreeRoomVoService {
    public List<FreeRoomVo>  queryFreeRoom(String campus, String building, int week, int xingqi);
}
