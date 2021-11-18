package com.haotongxue.service;

import com.haotongxue.entity.vo.AddCourseVo;

/**
 * @Description TODO
 * @date 2021/11/18 10:38
 */
public interface AddCourseService {

    public boolean addCourse(String openId,AddCourseVo addCourseVo);
}
