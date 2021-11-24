package com.haotongxue.service.impl;


import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.haotongxue.entity.UserInfo;
import com.haotongxue.mapper.*;
import com.haotongxue.utils.UserContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.Info;
import com.haotongxue.entity.User;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.service.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileOutputStream;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReptileServiceImpl implements ReptileService, JavaScriptErrorListener {

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

    @Resource
    IUserService iUserService;

    @Resource(name = "loginCache")
    LoadingCache<String,Object> cache;

    @Resource
    InfoSectionMapper infoSectionMapper;

    @Resource
    InfoClassroomMapper infoClassroomMapper;

    @Resource
    InfoTeacherMapper infoTeacherMapper;

    @Resource
    InfoWeekMapper infoWeekMapper;

    @Resource
    InfoMapper infoMapper;

    @Resource
    InfoCourseMapper infoCourseMapper;

    @Resource
    UserInfoMapper userInfoMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pa(WebClient webClient,String currentOpenid) throws IOException {
        HtmlPage page = null;
        try {
            page = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/xskb/xskb_list.do");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String enter = "\n";
        String[] sectionIds = new String[6];


        DomElement timetable = page.getElementById("timetable");
        String timeTableXml = timetable.asXml();
        Document doc = Jsoup.parse(timeTableXml);
        Elements trs = doc.select("table").select("td");
        int count = 0;
        for (Element oneClass :trs.toggleClass("kbcontent")){
            if(count >= 42){
                break;
            }
            Element child = oneClass.child(0);
            String val = child.val();
            String sub = val.substring(0, 32);

            switch (count){
                case 6:
                    sectionIds[0] = sub;
                    break;
                case 13:
                    sectionIds[1] = sub;
                    break;
                case 20:
                    sectionIds[2] = sub;
                    break;
                case 27:
                    sectionIds[3] = sub;
                    break;
                case 34:
                    sectionIds[4] = sub;
                    break;
                case 41:
                    sectionIds[5] = sub;
                    break;
            }
            count++;
        }


//        sectionIds[0] = "A0510B4234BA451499C8DDE3AD796254";  //1-2节
//        sectionIds[1] = "CEEE5CA18F9546968B2478B34BECAF59";  //3-4节
//        sectionIds[2] = "13CC5FA094F34E519AFA1A151EC9676E";  //5节
//        sectionIds[3] = "FE4BF6D361F648CF902A89C879DF0A81";  //6-7节
//        sectionIds[4] = "DAB50D67E3684EE7AE7781D2DCA83158";  //8-9节
//        sectionIds[5] = "273908B0CB2248D2BF96D0CF529EB31F";  //10-12节



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



//        File file = new File(System.getProperty("user.dir")+"/123.txt");
//        FileWriter fileWriter = new FileWriter(file);

        for (int i = 0;i < 7;i++){     //星期一到星期日
            for (int j = 0;j <= 5;j++){     //sectionIds[0]到sectionIds[5]
                if(Thread.currentThread().isInterrupted()){
                    log.info("正常爬的线程不再执行。。。");
                    throw new CourseException(555,"正常爬的线程不再执行，回滚数据");
                }
                if(j == 2){     //由于第5节为空，略过
                    continue;
                }
                key = sectionIds[j] + "-" + (i+1) + "-2";
                if(page.getElementById(key) == null){
                    throw new NullPointerException("Key过期了！");
                }else{
                    domElements[i][j] = page.getElementById(key);
//                    fileWriter.append(page.asText());
//                    fileWriter.flush();
                }
                String course = domElements[i][j].asText();
                String[] temp;
//                int num = 0;
//                int index;

//                for (int g = 0; course.contains("----"); g = g + index) {
//                    index = course.indexOf("---");
//                    temp[num] = course.substring(0,index);
//                    course = course.substring(index+21);
//                    course = course.substring(index+22);
//                    num++;
//                }
                 temp = course.split("---------------------|----------------------");
                //System.out.println(Arrays.toString(temp));
                String[] courseInfo = new String[4];
                for (int k = 0;k < temp.length;k++) {
                    if(temp[k] == null || temp[k].equals("") || temp[k].equals(" ")){
                        continue;
                    }
                    if (temp[k].charAt(0) == '\n'){
                        temp[k] = temp[k].substring(1);
                    }
                    if(temp[k].indexOf(enter) == 1){
                        temp[k] = temp[k].substring(2);
                    }
                    ArrayList<Integer> weekList;
                    ArrayList<Integer> sectionList;
                    if(temp[k].contains("网络课")){
                        temp[k] = temp[k].substring(0,temp[k].indexOf(enter));
                        courseInfo[0] = temp[k];
                        weekList = null;
                        sectionList = null;
                    }else{
                        int idx,cnum = 0;
                        for(int h = 0; temp[k].contains(enter) && cnum <= 3;h = h+idx){
                            idx = temp[k].indexOf(enter);
                            courseInfo[cnum] = temp[k].substring(0,idx);
                            temp[k] = temp[k].substring(idx+1);
                            cnum++;
                        }
                        weekList = getWeekCount(courseInfo[2]);
                        sectionList = getSectionCount(courseInfo[2]);
                    }
                    int xingqiId = i+1;     //星期几

                    if(weekList == null || weekList.size() == 0){
                        String weekAndSectionStr = courseInfo[2];
                        String weekStr = getWeekStr(weekAndSectionStr);
                        String sectionStr = getSectionStr(weekAndSectionStr);

                        infoId = iinfoService.addCourseInfo(xingqiId,weekStr,sectionStr);
                        iUserInfoService.insertUserInfo(currentOpenid,infoId);
                        continue;
                    }

                    for (Integer weekId : weekList) {
                        for (Integer sectionId : sectionList) {
                            courseTotal++;
                            String weekStr = "";
                            String sectionStr = "";
                            String weekAndSectionStr = courseInfo[2];
                            if(weekAndSectionStr != null && !weekAndSectionStr.equals("") && !weekAndSectionStr.equals(" ")){
                                weekStr  = getWeekStr(weekAndSectionStr);
                                sectionStr = getSectionStr(weekAndSectionStr);
                            }else {
                                weekStr = "无";
                                sectionStr = "无";
                            }
                            infoId = iinfoService.addCourseInfo(xingqiId,weekStr,sectionStr);
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
                    log.info(currentOpenid+":"+courseTotal);
                }
            }
        }
        System.out.println(currentOpenid+"总课程数===" + courseTotal);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",currentOpenid);
        User user = iUserService.getById(currentOpenid);
        user.setIsPa(1);
        if (!iUserService.updateById(user)){
            CourseException courseException = new CourseException();
            courseException.setMsg("更新isPa失败");
            throw courseException;
        }
        cache.invalidate(currentOpenid);
    }

    public static String getWeekStr(String ws){
        if(ws == null){
            return "无";
        }
        String zSub = "";
        if(ws.contains("周") && !ws.contains("节")){  //只有周次
            return ws;  //直接返回周次
        }

        if(ws.contains("周")){
            int zhouIdx = ws.indexOf("周");
            zSub = ws.substring(0, zhouIdx + 2);
            return zSub;  //返回周次
        }

        return "无";     //只有节次 或者 都没有

    }

    public static String getSectionStr(String ws){
        if(ws == null){
            return "无";
        }
        String zSub = "";
        if(ws.contains("周")){   //拿到zSub
            int zhouIdx = ws.indexOf("周");
            zSub = ws.substring(0, zhouIdx + 2);
        }

        if(ws.contains("周") && ws.contains("节")){   //有周次和节次，只要节次
            int jieIdx = ws.indexOf("节");
            String jSub = ws.substring(zSub.length(),jieIdx+2);
            return jSub;
        }else if(!ws.contains("周") && ws.contains("节")){    //只有节次
            return ws;  //直接返回节次
        }
        return "无";     //只有周次  或者  都没有
    }


    public static ArrayList<Integer> getWeekCount(String weekAndSection){
        ArrayList<Integer> weekList = new ArrayList<>();
        int index;
        if(weekAndSection.contains("(双周)")){
            index = weekAndSection.indexOf("(双周)");
        }else if(weekAndSection.contains("(周)")){
            index = weekAndSection.indexOf("(周)");
        }else if(weekAndSection.contains("(单周)")){
            index = weekAndSection.indexOf("(单周)");
        }else{
            throw new CourseException(555,"周次这里要改Bug咯！");
        }
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
            System.out.println("可能要报错了。。");
            System.out.println(weekAndSection);
//            log.info("要报错了。。。。");
//            log.info(weekAndSection);
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


    @Override
    public void scriptException(HtmlPage page, ScriptException scriptException) {

    }

    @Override
    public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {

    }

    @Override
    public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {

    }

    @Override
    public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {

    }

    @Override
    public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {

    }
}
