package com.haotongxue.service;

import com.haotongxue.entity.InfoTeacher;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface IInfoTeacherService extends IService<InfoTeacher> {
    boolean insertInfoTeacher(String infoId,Integer teacherId);
}
