package com.haotongxue.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddCourseDTO {
    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("上课地点")
    private String local;

    @ApiModelProperty("节次（0~5），0代表一到二节课，2仅代表第五节")
    private String section;

    @ApiModelProperty("星期几（0~6）")
    private String dayOfWeek;

    @ApiModelProperty("周次数组")
    private List<String> weeks;
}
