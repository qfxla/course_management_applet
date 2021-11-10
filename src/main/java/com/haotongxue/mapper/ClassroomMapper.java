package com.haotongxue.mapper;

import com.haotongxue.entity.Classroom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface ClassroomMapper extends BaseMapper<Classroom> {
    //根据教室名查询是否已有该教室存在
    int isExistClassroom(String classroomName);
}
