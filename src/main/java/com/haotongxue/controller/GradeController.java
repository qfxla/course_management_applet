package com.haotongxue.controller;


import com.haotongxue.entity.Grade;
import com.haotongxue.service.GradeService;
import com.haotongxue.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    GradeService gradeService;

//    @GetMapping("/getGrade")
    @GetMapping("/authority/getGrade")
    public List<Grade> getGrade(@RequestParam("term") int term) {
        String openId = UserContext.getCurrentOpenid();
//        openId = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
        return gradeService.getGrade(openId, term);
    }
}