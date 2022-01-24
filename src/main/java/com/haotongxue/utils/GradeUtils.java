package com.haotongxue.utils;

import org.springframework.util.StringUtils;

public class GradeUtils {
    public static String getGrade(String no){
        if (StringUtils.isEmpty(no)){
            return "";
        }
        return no.substring(2, 4);
    }
}
