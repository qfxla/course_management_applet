package com.haotongxue.service;

import com.haotongxue.entity.Teacher;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface ITeacherService extends IService<Teacher> {
    Integer addTeacher(String teacherName);
    List<Teacher> getTeachers();
}
