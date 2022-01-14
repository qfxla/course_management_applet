package com.haotongxue.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.College;
import com.haotongxue.service.ICollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zcj
 * @creat 2022-01-01-15:45
 */
@Component
public class WhichUtils {
    static ICollegeService iCollegeService;

    @Autowired
    private ICollegeService iCollegeService2;

    @PostConstruct
    public void init(){
        iCollegeService = iCollegeService2;
    }

    public static int getCollegeId(String no){
        String collegeStr = no.substring(5, 7);
        String collegeName = "";
        switch (collegeStr){
            case "01" :
                collegeName = "农业与生物学院";
                break;
            case "02" :
                collegeName = "信息科学与技术学院";
                break;
            case "05" :
                collegeName = "城乡建设学院";
                break;
            case "06" :
                collegeName = "环境科学与工程学院";
                break;
            case "07" :
                collegeName = "轻工食品学院";
                break;
            case "08":
                collegeName = "机电工程学院";
                break;
            case "09" :
                collegeName = "管理学院";
                break;
            case "10" :
                collegeName = "化学化工学院";
                break;
            case "11" :
                collegeName = "外国语学院";
                break;
            case "12" :
                collegeName = "何香凝艺术设计学院";
                break;
            case "13" :
                collegeName = "计算科学学院";
                break;
            case "14" :
                collegeName = "人文与社会科学学院";
                break;
            case "15" :
                collegeName = "园艺园林学院";
                break;
            case "16" :
                collegeName = "经贸学院";
                break;
            case "17" :
                collegeName = "自动化学院";
                break;
            case "18" :
                collegeName = "动物科技学院";
                break;
            default :
                break;
        }
        QueryWrapper<College> wrapper = new QueryWrapper<>();
        wrapper.eq("name",collegeName);
        College college = iCollegeService.getOne(wrapper);
        return college.getId();
    }


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

    //查是哪个专业的
}
