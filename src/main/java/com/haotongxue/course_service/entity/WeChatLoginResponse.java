package com.haotongxue.course_service.entity;

import lombok.Data;

@Data
public class WeChatLoginResponse {
    private String openid;
    private String session_key;
    private String unionid;
    private Integer errcode;
    private String errmsg;
}
