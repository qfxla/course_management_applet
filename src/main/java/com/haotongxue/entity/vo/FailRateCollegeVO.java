package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class FailRateCollegeVO {
    private int collegeId;
    private String collegeName;
    private List<FailRateMajorVO> list;
}
