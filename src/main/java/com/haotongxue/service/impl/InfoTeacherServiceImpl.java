package com.haotongxue.service.impl;

import com.haotongxue.entity.InfoClassroom;
import com.haotongxue.entity.InfoTeacher;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.InfoTeacherMapper;
import com.haotongxue.service.IInfoTeacherService;
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
public class InfoTeacherServiceImpl extends ServiceImpl<InfoTeacherMapper, InfoTeacher> implements IInfoTeacherService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertInfoTeacher(String infoId, Integer teacherId) {
        InfoTeacher infoTeacher = new InfoTeacher();
        infoTeacher.setInfoId(infoId);
        infoTeacher.setTeacherId(teacherId);
        boolean flag = save(infoTeacher);
        if(flag){
            return true;
        }else{
            CourseException courseException = new CourseException();
            courseException.setCode(505);
            courseException.setMsg("插入对象到t_info_teacher失败。");
            return false;
        }
    }
}
