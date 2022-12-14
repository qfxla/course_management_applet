package com.haotongxue.service;

import com.haotongxue.entity.Course;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface ICourseService extends IService<Course> {
    String addCourse(String courseName);
}
