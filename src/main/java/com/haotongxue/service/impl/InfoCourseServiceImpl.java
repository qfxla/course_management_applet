package com.haotongxue.service.impl;

import com.haotongxue.entity.InfoCourse;
import com.haotongxue.entity.InfoWeek;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.InfoCourseMapper;
import com.haotongxue.service.IInfoCourseService;
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
public class InfoCourseServiceImpl extends ServiceImpl<InfoCourseMapper, InfoCourse> implements IInfoCourseService {

    @Override
    public boolean insertInfoCourse(String infoId, String courseId) {
        InfoCourse infoCourse = new InfoCourse();
        infoCourse.setInfoId(infoId);
        infoCourse.setCourseId(courseId);
        boolean flag = save(infoCourse);
        if(flag){
            return true;
        }else{
            CourseException courseException = new CourseException();
            courseException.setCode(505);
            courseException.setMsg("插入对象到t_info_course失败。");
            return false;
        }
    }
}
