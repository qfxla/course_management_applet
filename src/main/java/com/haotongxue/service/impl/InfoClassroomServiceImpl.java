package com.haotongxue.service.impl;

import com.haotongxue.entity.InfoClassroom;
import com.haotongxue.entity.InfoWeek;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.InfoClassroomMapper;
import com.haotongxue.service.IInfoClassroomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
public class InfoClassroomServiceImpl extends ServiceImpl<InfoClassroomMapper, InfoClassroom> implements IInfoClassroomService {

    @Override
    public boolean insertInfoClassroom(String infoId, Integer classroomId) {
        InfoClassroom infoClassroom = new InfoClassroom();
        infoClassroom.setInfoId(infoId);
        infoClassroom.setClassroomId(classroomId);
        boolean flag = save(infoClassroom);
        if(flag){
            return true;
        }else{
            CourseException courseException = new CourseException();
            courseException.setCode(505);
            courseException.setMsg("插入对象到t_info_classroom失败。");
            return false;
        }
    }
}
