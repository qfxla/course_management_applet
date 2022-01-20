package com.haotongxue.controller;


import com.haotongxue.service.IStudentStatusService;
import com.haotongxue.utils.R;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2022-01-19
 */
@RestController
@RequestMapping("/zkCourse/studentStatus")
public class StudentStatusController {

    @Autowired
    IStudentStatusService studentStatusService;



    @GetMapping("/student")
    public R getStudent(@RequestParam String grade,
                        @RequestParam String collegeId,
                        @RequestParam String majorId,
                        @RequestParam String classId){
        SearchHit[] hits;
        try {
            hits = studentStatusService.getStudent(grade, collegeId, majorId, classId);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok().data("list",hits);
    }

    @GetMapping("/studentByName")
    public R getStudentByFuzzySearch(@RequestParam String content){
        SearchHit[] hits;
        try {
            hits = studentStatusService.getStudentByFuzzySearch(content);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok().data("list",hits);
    }
}

