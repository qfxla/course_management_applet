package com.haotongxue.course_service.service.impl;

import com.haotongxue.course_service.entity.Course;
import com.haotongxue.course_service.mapper.CourseMapper;
import com.haotongxue.course_service.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

}
