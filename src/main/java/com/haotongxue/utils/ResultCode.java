package com.haotongxue.utils;

/**
 * 通用返回类的返回码
 */
public interface ResultCode {

    Integer SUCCESS = 20000; //成功

    Integer ERROR = 20001; //失败

    Integer QUICK_LOGIN_ERROR = 20002; //快捷登录失败

    Integer NO_OR_PASSWORD_ERROR = 20003; //校园网登录不上，账号或密码错误

    Integer COURSE_IMPORT_UNFINISHED = 20004; //课程导入未成功

    Integer NEED_REFRESH_INFO = 20005; //需要重新获取用户信息，以达到最新

    Integer NO_TARGET = 20006; //不是我们的目标

    Integer EDU_PROBLEM = 20007; //教务网的问题
}
