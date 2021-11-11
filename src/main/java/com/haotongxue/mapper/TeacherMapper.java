package com.haotongxue.mapper;

import com.haotongxue.entity.Teacher;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface TeacherMapper extends BaseMapper<Teacher> {
    //根据教师名查询是否已有该教师存在
    int isExistTeacher(String teacherName);
}
