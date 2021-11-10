package com.haotongxue.mapper;

import com.haotongxue.entity.Course;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface CourseMapper extends BaseMapper<Course> {
    //根据课程名查询是否已有该课程存在
    int isExistCourse(String courseName);
}
