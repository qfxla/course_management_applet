package com.haotongxue.controller;

import com.haotongxue.entity.vo.AddCourseVo;
import com.haotongxue.service.AddCourseService;
import com.haotongxue.service.impl.AddCourseServiceImpl;
import com.haotongxue.utils.R;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @date 2021/11/21 12:47
 */
@RestController
@CrossOrigin
@Slf4j
@Api(tags = "添加课接口")
@RequestMapping("/addCourse")
public class AddCourseController {
    @Resource
    AddCourseService addCourseService;


    @PostMapping("/addCourses")
    public R addCourse(AddCourseVo addCourseVo){
        String openId = UserContext.getCurrentOpenid();
        boolean flag = addCourseService.addCourse(openId, addCourseVo);
        return flag? R.ok() : R.error();
    }
}
