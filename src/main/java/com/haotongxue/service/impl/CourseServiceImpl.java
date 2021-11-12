package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.Course;
import com.haotongxue.entity.Info;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.CourseMapper;
import com.haotongxue.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    @Resource
    CourseMapper courseMapper;

    @Override
    public String addCourse(String courseName) {
        int existCourseCount = courseMapper.isExistCourse(courseName);
        if(existCourseCount == 1){
            QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name",courseName);
            Course existedCourse = getOne(queryWrapper);
            return existedCourse.getCourseId();
        }else{
            if(courseName == null){
                CourseException courseException = new CourseException();
                courseException.setCode(555);
                courseException.setMsg("课程名为空，插入t_course失败。");
                throw courseException;
            }
            UUID courseUUID = UUID.randomUUID();
            String courseId = courseUUID.toString();
            Course course = new Course();
            course.setCourseId(courseId);
            course.setName(courseName);
            boolean flag = save(course);
            if(flag){
                return courseId;
            }else {
                CourseException courseException = new CourseException();
                courseException.setCode(505);
                courseException.setMsg("插入对象到t_teacher失败。");
                throw courseException;
            }
        }
    }
}
