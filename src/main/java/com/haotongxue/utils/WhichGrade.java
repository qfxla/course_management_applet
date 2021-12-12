package com.haotongxue.utils;

import org.springframework.stereotype.Component;

/**
 * @author zcj
 * @creat 2021-12-12-20:13
 */
@Component
public class WhichGrade {
    //在选课时，看他人才培养方案是属于哪个年代的，如16,20
    public static Integer whichGrade(String no){
        int i = Integer.valueOf(no.substring(2,4));  //年级
        int grade = 0;  //所使用的培养方案年代
        if (i < 20){
            grade = 16;
        }else {
            grade = 20;
        }
        return grade;
    }
}
