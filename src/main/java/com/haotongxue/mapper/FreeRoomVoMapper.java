package com.haotongxue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haotongxue.entity.vo.FreeRoomVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 查询空闲教室表 Mapper 接口
 * </p>
 *
 * @author CTC
 * @since 2021-11-27
 */

@Mapper
public interface FreeRoomVoMapper extends BaseMapper<FreeRoomVo> {
    public List<String> queryFreeRooms(String campus, String building, int week, int xingqi);
    public List<Integer> queryFreeSections(String roomName,String campus, String building, int week, int xingqi);
}
