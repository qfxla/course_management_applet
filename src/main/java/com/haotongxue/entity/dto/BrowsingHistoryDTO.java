package com.haotongxue.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BrowsingHistoryDTO {
    @ApiModelProperty("对方的学号")
    private String no;
}
