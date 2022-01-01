package com.haotongxue.controller;


import com.haotongxue.entity.Grade;
import com.haotongxue.service.GradeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-11-10
 */
@RestController
@RequestMapping("/zkCourse/grade")
public class GradeController {
    @Resource
    GradeService gradeService;

    @GetMapping("/getGrade")
    public List<Grade> getGrade(@RequestParam("openid") String openid){
        return gradeService.getGrade(openid);
    }
}