package com.haotongxue.service.impl;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.vo.AddCourseVo;
import com.haotongxue.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description TODO
 * @date 2021/11/18 10:38
 */
@Service
public class AddCourseServiceImpl implements AddCourseService {
    @Resource
    IInfoService iinfoService;

    @Resource
    ICourseService iCourseService;

    @Resource
    ITeacherService iTeacherService;

    @Resource
    IClassroomService iClassroomService;

    @Resource
    IInfoWeekService iInfoWeekService;

    @Resource
    IInfoSectionService infoSectionService;

    @Resource
    IInfoCourseService iInfoCourseService;

    @Resource
    IInfoClassroomService iInfoClassroomService;

    @Resource
    IInfoTeacherService iInfoTeacherService;

    @Resource
    IUserInfoService iUserInfoService;

    @Resource(name = "courseCache")
    LoadingCache<String,Object> cache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCourse(String openId,AddCourseVo addCourseVo) {
        boolean flag = false;
        String courseName = addCourseVo.getCourseName();      //课程名
        String teacherName = addCourseVo.getTeacherName();     //教师名
        String classroomName = addCourseVo.getClassRoom();   //教室名
        int xingqiId = addCourseVo.getXingqi();
        String weekStr = getWeekStr(addCourseVo.getWeekList());
        String sectionStr = getSectionStr(addCourseVo.getSection());

        String courseId = iCourseService.addCourse(courseName); //添加课程t_course
        Integer teacherId = iTeacherService.addTeacher(teacherName);  //添加教师t_teacher
        Integer classroomId = iClassroomService.addClassroom(classroomName);    //添加教室t_classroom


        //插入周次表与t_info的关联表
        for (Integer week : addCourseVo.getWeekList()) {
            String infoId;
            infoId = iinfoService.addCourseInfo(xingqiId,weekStr,sectionStr);
            //插入课程表与t_info的关联表
            iInfoCourseService.insertInfoCourse(infoId, courseId);

            //插入教师表与t_info的关联表
            iInfoTeacherService.insertInfoTeacher(infoId, teacherId);

            //插入教室表与t_info的关联表
            iInfoClassroomService.insertInfoClassroom(infoId, classroomId);

            //插入周次表与t_info的关联表
            iInfoWeekService.insertInfoWeek(infoId, week);

            //插入节次表与t_info的关联表
            infoSectionService.insertInfoSection(infoId,addCourseVo.getSection());

            //出入用户表与t_info的关联表
            iUserInfoService.insertUserInfo(openId,infoId);
        }

        //使缓存失效
        for (int i = 1; i <= 20; i++) {
            cache.invalidate("week" + openId + ":" + i);
        }
        flag = true;
        return flag;
    }

    public static String getWeekStr(List<Integer> weekList){
        List<String> weekStrList = new ArrayList<>();
        int[] weekArr = new int[21];
        for (Integer week : weekList) {
            weekArr[week] = week;
        }
        System.out.println(Arrays.toString(weekArr));
        int begin = 0,end = 0;
        for (int i = 1; i < weekArr.length; i++) {
            if(weekArr[i] == 0){
                continue;
            }
            if(weekArr[i-1] == 0){
                if(weekArr[i+1] == 0){
                    weekStrList.add(i + "");
                    weekArr[i] = 0;
                }else{
                    begin = i;
                }
            }else{
                if(weekArr[i+1]==0){
                    end = i;
                }
            }
            if(end > begin){
                weekStrList.add(begin + "-" + end);
                for (int j = begin; j <= end; j++) {
                    weekArr[j] = 0;
                }
                end = 0;
                begin = 0;
            }
        }
        String sub1 = weekStrList.toString().substring(1);
        String sub2 = sub1.substring(0,sub1.length()-1);
        String sub3 = sub2.replace(" ","") + "(周)";
        System.out.println(sub3);
        return sub3;
    }

    public static String getSectionStr(int section){
        switch (section){
            case 1:return "[01-02节]";
            case 3:return "[03-04节]";
            case 14:return "[01-02-03-04节]";
            case 5:return "[05节]";
            case 6:return "[06-07节]";
            case 8:return "[08-09节]";
            case 68:return "[06-07-08-09节]";
            case 10:return "[10-11-12节]";
            default:return "未知节次";
        }
    }
}
