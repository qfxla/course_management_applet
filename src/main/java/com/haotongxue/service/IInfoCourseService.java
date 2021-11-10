package com.haotongxue.service;

import com.haotongxue.entity.InfoCourse;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface IInfoCourseService extends IService<InfoCourse> {
    boolean insertInfoCourse(String infoId,String courseId);
}
