package com.haotongxue.service.impl;

import com.haotongxue.entity.vo.FreeRoomVo;
import com.haotongxue.mapper.FreeRoomVoMapper;
import com.haotongxue.service.FreeRoomVoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        System.out.println(campus);
        System.out.println(building);
        System.out.println(week);
        System.out.println(xingqi);
        List<FreeRoomVo> freeRoomVoList = new ArrayList<>();
        List<String> roomNameList = freeRoomVoMapper.queryFreeRooms(campus, building, week, xingqi);
        for (String roomName : roomNameList) {
            FreeRoomVo freeRoomVo = new FreeRoomVo();
            List<Integer> sectionList = freeRoomVoMapper.queryFreeSections(roomName, campus, building, week, xingqi);
            freeRoomVo.setName(roomName);
            freeRoomVo.setSection(sectionList);
            freeRoomVoList.add(freeRoomVo);
        }
        return freeRoomVoList;
    }
}
