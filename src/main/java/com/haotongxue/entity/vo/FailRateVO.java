package com.haotongxue.entity.vo;

import com.haotongxue.entity.FailRate;
import lombok.Data;
import java.util.LinkedList;
import java.util.List;

@Data
public class FailRateVO {
    String collegeId;
    String majorId;
    String subjectId;
    String subjectName;
    String property;
    List<FailRate> list = new LinkedList<>();
}
