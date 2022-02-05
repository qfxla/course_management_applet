package com.haotongxue.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConcernDTO {
    @ApiModelProperty("被关注人的学号")
    private String concernedNo;
}
