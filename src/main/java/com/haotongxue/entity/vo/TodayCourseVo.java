package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zcj
 * @creat 2021-11-09-15:47
 */
@Data
public class TodayCourseVo {
    private static final long serialVersionUID = 11L;
    String openId;
    String courseName;
    String classroom;
    String teacher;
    List<Integer> sections;
}
