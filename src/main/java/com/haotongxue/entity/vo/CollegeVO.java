package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class CollegeVO {
    private int collegeId;
    private String collegeName;
    private List<MajorVO> list;
}
