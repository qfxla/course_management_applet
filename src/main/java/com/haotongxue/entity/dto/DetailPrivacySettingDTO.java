package com.haotongxue.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DetailPrivacySettingDTO {
    //学号数组
    @ApiModelProperty("学号数组")
    private List<String> list;
    //隐私设置 3 或 4
    @ApiModelProperty("隐私设置：3(只给谁看)或4(不给谁看)")
    private int setting;
}
