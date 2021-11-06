package com.haotongxue.course_service.entity.dto;

import lombok.Data;

@Data
public class WeChatLoginDTO {
    private String code;

    private String nickName;

    private String avatarUrl;

    private String  gender;

    private String no;

    private String passward;
}
