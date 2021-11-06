package com.haotongxue.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVO {

    private String openid;

    private String nickName;

    private String avatarUrl;

    private String  gender;
}
