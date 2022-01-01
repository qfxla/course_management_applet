package com.haotongxue.service.impl;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.vo.AddCourseVo;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.AddCourseMapper;
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

//    @Resource(name = "courseCache")
//    LoadingCache<String,Object> cache;

    @Resource(name = "courseCache")
    LoadingRedisCache cache;

    @Resource
    AddCourseMapper addCourseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addCourse(String openId,AddCourseVo addCourseVo) {
        boolean flag = false;
        String courseName = addCourseVo.getCourseName();      //课程名
        String teacherName = addCourseVo.getTeacherName();     //教师名
        String classroomName = addCourseVo.getClassRoom();   //教室名
        int xingqiId = addCourseVo.getXingqi();
        List<Integer> weekList = addCourseVo.getWeekList();
        System.out.println("qqqq");
        System.out.println(courseName);
        System.out.println(weekList);
        System.out.println(xingqiId);
        String weekStr = getWeekStr(weekList);
        int section = addCourseVo.getSection();
        String sectionStr = getSectionStr(section);

        //判断与原有课程是否有冲突
        boolean insertFlag = isConflict(openId, weekList, xingqiId, section);
        if(!insertFlag){
            return false;
        }


        String courseId = iCourseService.addCourse(courseName); //添加课程t_course
        Integer teacherId = iTeacherService.addTeacher(teacherName);  //添加教师t_teacher
        Integer classroomId = iClassroomService.addClassroom(classroomName);    //添加教室t_classroom

        int loopBegin,loopEnd;
        if(section == 1 || section == 3 || section == 6 || section == 8){
            loopBegin = section;
            loopEnd = loopBegin + 1;
        }else if( section == 5 ){
            loopBegin = 5;
            loopEnd = loopBegin;
        }else if(section == 14){
            loopBegin = 1;
            loopEnd = 4;
        }else if(section == 69){
            loopBegin = 6;
            loopEnd = 9;
        }else if(section == 10){
            loopBegin = 10;
            loopEnd = 12;
        }else{
            throw new CourseException(555,"未知节次！！");
        }

        //插入周次表与t_info的关联表
        for (Integer week : addCourseVo.getWeekList()) {
            for (int i = loopBegin; i <= loopEnd; i++) {
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
                infoSectionService.insertInfoSection(infoId,i);

                //出入用户表与t_info的关联表
                iUserInfoService.insertUserInfo(openId,infoId);
            }
        }

        //使缓存失效
        for (int i = 1; i <= 20; i++) {
            cache.invalidate("cour" + openId + ":" + i);
        }
        flag = true;
        return flag;
    }

    public static String getWeekStr(List<Integer> weekList){
        System.out.println("周次集合。。。。");
        System.out.println(weekList);
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

    @Override
    public boolean isConflict(String openId,List<Integer> weekList, int xingqi, int section) {
        for (Integer week : weekList) {
            int num = addCourseMapper.isConflict(openId, week, xingqi, section);
            if(num != 0){   //只要一个有冲突，直接阻止插入
                System.out.println("只要一个有冲突，直接阻止插入");
                return false;
            }
        }
        System.out.println("没有一个是冲突的，允许插入");
        return true;    //没有一个是冲突的，允许插入
    }

    public static String getSectionStr(int section){
        switch (section){
            case 1:return "[01-02节]";
            case 3:return "[03-04节]";
            case 14:return "[01-02-03-04节]";
            case 5:return "[05节]";
            case 6:return "[06-07节]";
            case 8:return "[08-09节]";
            case 69:return "[06-07-08-09节]";
            case 10:return "[10-11-12节]";
            default:return "未知节次";
        }
    }
}
