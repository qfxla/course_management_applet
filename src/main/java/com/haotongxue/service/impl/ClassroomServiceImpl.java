package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.Classroom;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.ClassroomMapper;
import com.haotongxue.service.IClassroomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
public class ClassroomServiceImpl extends ServiceImpl<ClassroomMapper, Classroom> implements IClassroomService {
    @Resource
    ClassroomMapper classroomMapper;
    @Override

    public Integer addClassroom(String classroomName) {
        if(classroomName == null){
            classroomName = "无";
        }
        int existClassroomCount = classroomMapper.isExistClassroom(classroomName);
        if(existClassroomCount == 1){
            QueryWrapper<Classroom> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("location",classroomName);
            Classroom existedClassroom = getOne(queryWrapper);
            return existedClassroom.getClassroomId();
        }else{
            Classroom classroom = new Classroom();
            classroom.setLocation(classroomName);
            boolean flag = save(classroom);
            int classroomId = classroom.getClassroomId();     //最新ID
            if(flag){
                return classroomId;
            }else {
                CourseException courseException = new CourseException();
                courseException.setCode(505);
                courseException.setMsg("插入对象到t_classroom失败。");
                throw courseException;
            }
        }
    }
}
