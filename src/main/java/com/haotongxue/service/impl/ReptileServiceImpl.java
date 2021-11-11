package com.haotongxue.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.entity.User;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.service.*;
import com.haotongxue.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Service
@Slf4j
public class ReptileServiceImpl implements ReptileService {

    @Resource
    IInfoService infoService;

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

    @Resource
    IUserService iUserService;


    @Override
    public void pa(WebClient webClient,String currentOpenid) {
        HtmlPage page = null;
        try {
            page = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/xskb/xskb_list.do");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] sectionIds = new String[6];
        sectionIds[0] = "A0510B4234BA451499C8DDE3AD796254";  //1-2节
        sectionIds[1] = "CEEE5CA18F9546968B2478B34BECAF59";  //3-4节
        sectionIds[2] = "13CC5FA094F34E519AFA1A151EC9676E";  //5节
        sectionIds[3] = "FE4BF6D361F648CF902A89C879DF0A81";  //6-7节
        sectionIds[4] = "DAB50D67E3684EE7AE7781D2DCA83158";  //8-9节
        sectionIds[5] = "273908B0CB2248D2BF96D0CF529EB31F";  //10-12节
        DomElement[][] domElements = new DomElement[7][6];

        String key = "";
        int courseTotal = 0;
        //星期一~星期日：1-2~7-2
        if(page == null){
            CourseException courseException = new CourseException();
            courseException.setMsg("page为空。");
            courseException.setCode(555);
            throw courseException;
        }

        String infoId;
        //String currentOpenid = "o2LPU5iId1G-iwcxH46GwuQzcuNw";
        for (int i = 0;i < 7;i++){     //星期一到星期日
            for (int j = 0;j <= 5;j++){     //sectionIds[0]到sectionIds[5]
                if(j == 2){     //由于第5节为空，略过
                    continue;
                }
                key = sectionIds[j] + "-" + (i+1) + "-2";
                if(page.getElementById(key) == null){
                    throw new NullPointerException("Key过期了！");
                }else{
                    domElements[i][j] = page.getElementById(key);
                }
                String course = domElements[i][j].asText();
                String temp[] = new String[10];
                int num = 0;
                int index;
                for (int g = 0; course.contains("---------------------"); g = g + index) {
                    index = course.indexOf("---------------------");
                    temp[num] = course.substring(0,index);
                    course = course.substring(index+21);
                    num++;
                }
                temp[num] = course;
                String[] courseInfo = new String[4];
                for (int k = 0;k < temp.length;k++) {
                    if(temp[k] == null || temp[k].equals("") || temp[k].equals(" ")){
                        continue;
                    }
                    if(temp[k].indexOf("\n") == 1){
                        temp[k] = temp[k].substring(2);
                    }
                    ArrayList<Integer> weekList;
                    ArrayList<Integer> sectionList;
                    if(temp[k].contains("网络课")){
                        temp[k] = temp[k].substring(0,temp[k].indexOf("\n"));
                        courseInfo[0] = temp[k];
                        weekList = null;
                        sectionList = null;
                    }else{
                        int idx,cnum = 0;
                        for(int h = 0; temp[k].contains("\n") && cnum <= 3;h = h+idx){
                            idx = temp[k].indexOf("\n");
                            courseInfo[cnum] = temp[k].substring(0,idx);
                            temp[k] = temp[k].substring(idx+1);
                            cnum++;
                        }
                        log.info("courseInfo...");
                        log.info(Arrays.toString(courseInfo));
                        weekList = getWeekCount(courseInfo[2]);
                        sectionList = getSectionCount(courseInfo[2]);
                    }
                    int xingqiId = i+1;     //星期几

                    if(weekList == null || weekList.size() == 0){
                        infoId = infoService.addCourseInfo(xingqiId);
                        iUserInfoService.insertUserInfo(currentOpenid,infoId);
                        continue;
                    }

                    for (Integer weekId : weekList) {
                        for (Integer sectionId : sectionList) {
                            courseTotal++;
                            infoId = infoService.addCourseInfo(xingqiId);

                            iUserInfoService.insertUserInfo(currentOpenid, infoId);

                            String courseName = courseInfo[0];      //课程名
                            String courseId = iCourseService.addCourse(courseName); //添加课程t_course
                            String teacherName = courseInfo[1];     //教师名
                            Integer teacherId = iTeacherService.addTeacher(teacherName);  //添加教师t_teacher
                            String classroomName = courseInfo[3];   //教室名
                            Integer classroomId = iClassroomService.addClassroom(classroomName);    //添加教室t_classroom

                            //插入课程表与t_info的关联表
                            iInfoCourseService.insertInfoCourse(infoId, courseId);

                            //插入教师表与t_info的关联表
                            iInfoTeacherService.insertInfoTeacher(infoId, teacherId);

                            //插入教室表与t_info的关联表
                            iInfoClassroomService.insertInfoClassroom(infoId, classroomId);

                            //插入周次表与t_info的关联表
                            iInfoWeekService.insertInfoWeek(infoId, weekId);

                            //插入节次表与t_info的关联表
                            infoSectionService.insertInfoSection(infoId,sectionId);
                        }
                    }
//                    System.out.println("课程名===" + courseInfo[0]);
//                    System.out.println("教师名===" + courseInfo[1]);
//                    System.out.println("周次===" + weekList);
//                    System.out.println("节次===" + sectionList);
//                    System.out.println("地点===" + courseInfo[3]);
//                    System.out.println("星期" + (i+1));
                    log.info(String.valueOf(courseTotal));
                }
            }
        }
        System.out.println("总课程数===" + courseTotal);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",currentOpenid);
        User user = iUserService.getById(currentOpenid);
        user.setIsPa(1);
        iUserService.updateById(user);
    }
    public static ArrayList<Integer> getWeekCount(String weekAndSection){
        ArrayList<Integer> weekList = new ArrayList<>();
        int index = weekAndSection.indexOf("(周)");
        if(index == -1){
            return new ArrayList<>();
        }
        String subWeek  = weekAndSection.substring(0, index);     //1-3,5,15,18
        String[] weekArr = new String[10];
        int idx = subWeek.indexOf(",");     //1或3
        int num = 0,n = 0;
        while (subWeek.contains(",")){
            weekArr[num] = subWeek.substring(0,idx);    //第一个逗号前面的内容，给数组
            subWeek = subWeek.substring(idx+1);   //剩余内容
            n = subWeek.indexOf(",");
            idx = n;
            num++;
        }
        weekArr[num] = subWeek;
        for (String s : weekArr) {
            if(s!=null && !s.equals("")){
                if(s.contains("-")){
                    int ix = s.indexOf("-");
                    int begin = Integer.parseInt(s.substring(0,ix));
                    int end = Integer.parseInt(s.substring(ix+1));
                    for (int i = begin; i <= end; i++) {
                        weekList.add(i);
                    }
                }else{
                    weekList.add(Integer.parseInt(s));
                }
            }
        }
        return weekList;
    }


    public static ArrayList<Integer> getSectionCount(String weekAndSection){
        int begin = weekAndSection.indexOf("[") + 1;
        int end = weekAndSection.indexOf("节");
        if(end == -1){
            log.info("要报错了。。。。");
            log.info(weekAndSection);
        }
        String section = weekAndSection.substring(begin, end);
        int len = section.length();
        String first = section.substring(0,2);
        String last = section.substring(len-2,len);
        ArrayList<Integer> sectionList = new ArrayList<>();
        int firstInt = Integer.parseInt(first);
        int lastInt = Integer.parseInt(last);
        for (int i = firstInt; i <= lastInt; i++) {
            sectionList.add(i);
        }
        return sectionList;
    }


}
