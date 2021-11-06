package com.haotongxue.course_service.utils;

public class UserContext {
    private static final ThreadLocal<String> user = new ThreadLocal<>();

    public static void add(String openId) {
        user.set(openId);
    }

    public static void remove() {
        user.remove();
    }

    /**
     * @return 当前登录用户的用户名
     */
    public static String getCurrentOpenid() {
        return user.get();
    }

}
