package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.Course;
import com.haotongxue.entity.Teacher;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.CourseMapper;
import com.haotongxue.mapper.TeacherMapper;
import com.haotongxue.service.ITeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {

    @Resource
    TeacherMapper teacherMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addTeacher(String teacherName) {
        if(teacherName == null){
            teacherName = "无";
        }
        int existTeacherCount = teacherMapper.isExistTeacher(teacherName);
        if(existTeacherCount == 1){
            QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",teacherName);
            Teacher existedTeacher = getOne(queryWrapper);
            return existedTeacher.getTeacherId();
        }else{
            Teacher teacher = new Teacher();
            teacher.setName(teacherName);
            boolean flag = save(teacher);
            int teacherId = teacher.getTeacherId();     //最新ID
            if(flag){
                return teacherId;
            }else {
                CourseException courseException = new CourseException();
                courseException.setCode(505);
                courseException.setMsg("插入对象到t_teacher失败。");
                throw courseException;
            }
        }
    }
}
