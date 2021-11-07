package com.haotongxue.utils;

/**
 * 通用返回类的返回码
 */
public interface ResultCode {

    Integer SUCCESS = 20000; //成功

    Integer ERROR = 20001; //失败

    Integer QUICK_LOGIN_ERROR = 20002; //快捷登录失败

    Integer NO_OR_PASSWORD_ERROR = 20003; //校园网登录不上，账号或密码错误
}
