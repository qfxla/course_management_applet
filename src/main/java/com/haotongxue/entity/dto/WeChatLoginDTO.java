package com.haotongxue.entity.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户登录所需信息")
public class WeChatLoginDTO {
    private String code;

    @ApiModelProperty("昵称（如果是快捷登录则不传）")
    private String nickName;

    @ApiModelProperty("头像url（如果是快捷登录则不传）")
    private String avatarUrl;

    @ApiModelProperty("性别（如果是快捷登录则不传")
    private String  gender;

    private String no;

    private String passward;
}
