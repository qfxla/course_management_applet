package com.haotongxue.service;

import com.haotongxue.entity.InfoClassroom;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface IInfoClassroomService extends IService<InfoClassroom> {
    boolean insertInfoClassroom(String infoId,Integer classroomId);
}
