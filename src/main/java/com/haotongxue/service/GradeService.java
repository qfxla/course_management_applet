package com.haotongxue.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gargoylesoftware.htmlunit.WebClient;
import com.haotongxue.entity.CountDown;
import com.haotongxue.entity.Grade;
import com.haotongxue.entity.vo.AddCourseVo;

import java.util.List;

public interface GradeService extends IService<Grade> {
//    public int paGrade();
    public int paGrade(String openid, WebClient webClient);
}
