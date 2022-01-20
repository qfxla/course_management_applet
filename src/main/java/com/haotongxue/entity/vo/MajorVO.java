package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class MajorVO {
    private String majorId;
    private String name;
    private List<ClassVO> list;
}
