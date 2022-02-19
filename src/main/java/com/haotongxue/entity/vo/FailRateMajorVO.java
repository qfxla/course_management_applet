package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class FailRateMajorVO {
    private String majorId;
    private String name;
    private List<FailRateSubjectVO> list;
}
