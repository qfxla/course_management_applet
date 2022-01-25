package com.haotongxue.service.impl;


import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.mapper.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.entity.User;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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

    @Resource
    UserMapper userMapper;

//    @Resource(name = "loginCache")
//    LoadingCache<String,Object> cache;

    @Resource(name = "loginCache")
    LoadingRedisCache cache;

    @Resource
    CountDownMapper countDownMapper;

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
        boolean isSwitch = false;
        HtmlPage page = null;
        try {
            page = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/xskb/xskb_list.do");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            String enter = "\n";
            String key = "";
            int courseTotal = 0;
            //星期一~星期日：1-2~7-2
            if (page == null) {
                throw new CourseException(555, "page为空！");
            }

            DomElement[][] domElements = new DomElement[7][6];
            DomElement xnxq01id = page.getElementById("xnxq01id");
            DomNodeList<HtmlElement> options = xnxq01id.getElementsByTagName("option");
            HtmlElement htmlElement = options.get(0);
            HtmlPage click = null;
            try {
                click = htmlElement.click();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert click != null;
            String[] sectionIds = getSectionId(click);
            page = click;

            int switchFlag = 0;
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j <= 5; j++) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Thread.currentThread().isInterrupted()");
                    }
                    if (j == 2) {     //由于第5节为空，略过
                        continue;
                    }
                    key = sectionIds[j] + "-" + (i + 1) + "-2";
                    if (page.getElementById(key) == null) {
                        throw new NullPointerException("Key过期了！");
                    } else {
                        domElements[i][j] = page.getElementById(key);
                    }
                    String course = domElements[i][j].asText();
                    if (course.length() < 5) {
                        switchFlag++;
                    }
                    if (switchFlag >= 42) {
                        log.info("发现他是白云课程表");
                        isSwitch = true;
                        break;
                    }
                }
            }

            if (isSwitch) {
                DomElement kbjcmsid = page.getElementById("kbjcmsid");
                DomNodeList<HtmlElement> baiyunOpt = kbjcmsid.getElementsByTagName("option");
                HtmlElement baiyunEle = baiyunOpt.get(1);
                HtmlPage baiyunClick = null;
                try {
                    baiyunClick = baiyunEle.click();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert baiyunClick != null;
                sectionIds = getSectionId(baiyunClick);
                page = baiyunClick;
            }
            String infoId;
            switchFlag = 0;
            webClient.getCurrentWindow().getJobManager().removeAllJobs();
            webClient.close();
            for (int i = 0; i < 7; i++) {     //星期一到星期日
                for (int j = 0; j <= 5; j++) {     //sectionIds[0]到sectionIds[5]
                    if (Thread.currentThread().isInterrupted()) {
                        log.info("正常爬的线程不再执行。。。");
                        throw new CourseException(555, "正常爬的线程不再执行，回滚数据");
                    }
                    if (j == 2) {     //由于第5节为空，略过
                        continue;
                    }
                    key = sectionIds[j] + "-" + (i + 1) + "-2";
                    if (page.getElementById(key) == null) {
                        throw new NullPointerException("Key过期了！");
                    } else {
                        domElements[i][j] = page.getElementById(key);
                    }
                    String course = domElements[i][j].asText();
                    if (course.length() < 5) {
                        switchFlag++;
                    }
                    if (switchFlag >= 42) {
                        throw new CourseException(555, "发现他海珠白云均为空课表");
                    }
                    String[] temp;
                    temp = course.split("---------------------|----------------------");
                    //System.out.println(Arrays.toString(temp));
                    String[] courseInfo = new String[4];
                    for (int k = 0; k < temp.length; k++) {
//                        if(temp[k].contains("通知单编号")){
//                            temp[k] = temp[k].substring(0,temp[k].indexOf("通知单编号"));
//                        }
                        if (temp[k] == null || temp[k].equals("") || temp[k].equals(" ")) {
                            continue;
                        }
                        if (temp[k].charAt(0) == '\n') {
                            temp[k] = temp[k].substring(1);
                        }
                        if (temp[k].indexOf(enter) == 1) {
                            temp[k] = temp[k].substring(2);
                        }
                        ArrayList<Integer> weekList;
                        ArrayList<Integer> sectionList;
                        if (temp[k].contains("网络课")) {
                            log.info("有网络课。。。");
                            temp[k] = temp[k].substring(0, temp[k].indexOf(enter));
                            courseInfo[0] = temp[k];
                            weekList = null;
                            sectionList = null;
                        } else {
                            int idx, cnum = 0;
                            for (int h = 0; temp[k].contains(enter) && cnum <= 3; h = h + idx) {
                                idx = temp[k].indexOf(enter);
                                courseInfo[cnum] = temp[k].substring(0, idx);
                                temp[k] = temp[k].substring(idx + 1);
                                cnum++;
                            }
                            if (courseInfo[1].contains("篮球") || courseInfo[1].contains("匹克球")
                                    || courseInfo[1].contains("网球") || courseInfo[1].contains("排球")
                                    || courseInfo[1].contains("羽毛球") || courseInfo[1].contains("足球")
                                    || courseInfo[1].contains("乒乓球") || courseInfo[1].contains("健美")
                                    || courseInfo[1].contains("舞蹈") || courseInfo[1].contains("跆拳道")
                                    || courseInfo[1].contains("瑜伽") || courseInfo[1].contains("排舞")
                            ) {
                                courseInfo[0] = courseInfo[0] + courseInfo[1];
                                courseInfo[1] = courseInfo[2];
                                courseInfo[2] = courseInfo[3];
                                courseInfo[3] = "";
                            }
                            if (courseInfo[1].contains("(周)")){
                                courseInfo[3] = courseInfo[2];
                                courseInfo[2] = courseInfo[1];
                                courseInfo[1] = "未知";
                            }
                            if (courseInfo[3].contains(":")) {
                                courseInfo[3] = "无";
                            }
                            weekList = getWeekCount(courseInfo[2]);
                            if(weekList.get(0) == -99){
                                System.out.println(courseInfo[2]);
                                throw new CourseException(555,"周次这里要改bug咯！");
                            }
                            sectionList = getSectionCount(courseInfo[2]);
                        }
                        int xingqiId = i + 1;     //星期几

                        if (weekList == null || weekList.size() == 0) {
                            String weekAndSectionStr = courseInfo[2];
                            String weekStr = getWeekStr(weekAndSectionStr);
                            String sectionStr = getSectionStr(weekAndSectionStr);

                            infoId = iinfoService.addCourseInfo(xingqiId, weekStr, sectionStr);
                            iUserInfoService.insertUserInfo(currentOpenid, infoId);
                            continue;
                        }

                        for (Integer weekId : weekList) {
                            for (Integer sectionId : sectionList) {
                                courseTotal++;
                                String weekStr = "";
                                String sectionStr = "";
                                String weekAndSectionStr = courseInfo[2];
                                if (!weekAndSectionStr.equals("") && !weekAndSectionStr.equals(" ")) {
                                    weekStr = getWeekStr(weekAndSectionStr);
                                    sectionStr = getSectionStr(weekAndSectionStr);
                                } else {
                                    weekStr = "无";
                                    sectionStr = "无";
                                }
                                infoId = iinfoService.addCourseInfo(xingqiId, weekStr, sectionStr);
                                iUserInfoService.insertUserInfo(currentOpenid, infoId);
                                String courseId;
                                Integer teacherId;
                                Integer classroomId;
//                                synchronized (insertLock){
                                String courseName = courseInfo[0];      //课程名
                                courseId = iCourseService.addCourse(courseName); //添加课程t_course
                                String teacherName = courseInfo[1];     //教师名
                                teacherId = iTeacherService.addTeacher(teacherName);  //添加教师t_teacher
                                String classroomName = courseInfo[3];   //教室名
                                classroomId = iClassroomService.addClassroom(classroomName);    //添加教室t_classroom
//                                }

                                //插入课程表与t_info的关联表
                                iInfoCourseService.insertInfoCourse(infoId, courseId);

                                //插入教师表与t_info的关联表
                                iInfoTeacherService.insertInfoTeacher(infoId, teacherId);

                                //插入教室表与t_info的关联表
                                iInfoClassroomService.insertInfoClassroom(infoId, classroomId);

                                //插入周次表与t_info的关联表
                                iInfoWeekService.insertInfoWeek(infoId, weekId);

                                //插入节次表与t_info的关联表
                                infoSectionService.insertInfoSection(infoId, sectionId);
                            }
                        }
                        log.info(currentOpenid + ":" + courseTotal);
                    }
                }
            }
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid", currentOpenid);
            User user = userMapper.selectOne(queryWrapper);
            user.setIsPa(1);
            if (!iUserService.updateById(user)) {
                CourseException courseException = new CourseException();
                courseException.setMsg("更新isPa失败");
                throw courseException;
            }
                        cache.invalidate(currentOpenid);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }
        //课程爬完，开始爬人文考试
//        String xueHao = user.getNo();
//        if(xueHao != null){
//            String arg = xueHao.substring(4,9);
//            if(!(arg.equals("11414") || arg.equals("11424") || arg.equals("11434") || arg.equals("11412"))){
//                //不是人文学院的人，结束该方法
//                cache.invalidate(currentOpenid);
//                return;
//            }else{
//                List<RenWenCountDown> renWenList = null;
//                try {
//                    renWenList = getRenWenList();
//                } catch (BiffException | IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(currentOpenid + "----" + "人文学生，触发了爬考试倒计时。。。。。。");
//                String myGrade = xueHao.substring(2,4);
//                String myZhuanYe = xueHao.substring(5,7);
//                String myBanji = xueHao.substring(9,10);
//                System.out.println("学号===================" + xueHao);
//                for (RenWenCountDown renWenCountDown : renWenList) {
//                    String name = renWenCountDown.getCourseName();
//                    LocalDateTime startTime = renWenCountDown.getStartTime();
//                    LocalDateTime endTime = renWenCountDown.getEndTime();
//                    String location = renWenCountDown.getLocation();
//
//                    String banJiStr = renWenCountDown.getBanJi();
//                    String gradeClassNum = banJiStr.substring(2);
//                    String zhuanye = "";
//                    if(banJiStr.contains("行管")){
//                        zhuanye = "14";
//                    }else if(banJiStr.contains("社工")){
//                        zhuanye = "24";
//                    }else if(banJiStr.contains("文管")){
//                        zhuanye = "34";
//                    }else{
//                        throw new CourseException(555,"没有这个专业");
//                    }
//                    String grade = gradeClassNum.substring(0,2);
//                    String classs = gradeClassNum.substring(2);
//                    if(!myBanji.equals(classs) || !myGrade.equals(grade) || !arg.substring(3,5).equals(zhuanye)){
//                        continue;
//                        //换下一个考试
//                    }
//                    //必须保证同班且同年级才能加考试
//                    //直接加
//                    CountDown newCountDown = new CountDown();
//                    newCountDown.setOpenid(currentOpenid);
//                    newCountDown.setName(name);
//                    newCountDown.setStartTime(startTime);
//                    newCountDown.setEndTime(endTime);
//                    newCountDown.setLocation(location);
//                    int insert = countDownMapper.insert(newCountDown);
//                    if (insert == 1) {
//                        System.out.println(newCountDown);
//                        System.out.println(arg + "----" + currentOpenid + "刚进来的人文学生，有考试，插！");
//                    } else {
//                        throw new CourseException(555, arg + "----" + currentOpenid + "刚进来的人文学生，插不进了");
//                    }
//                }
//                cache.invalidate(currentOpenid);
//                return;
//            }
//        }else{
//            throw new CourseException(555,"爬人文考试这里，学号居然为空");
//        }
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
        if(weekAndSection.contains("(周)")){
            index = weekAndSection.indexOf("(周)");
        }else if(weekAndSection.contains("(单周)")){
            index = weekAndSection.indexOf("(单周)");
        }else if(weekAndSection.contains("(双周)")){
            index = weekAndSection.indexOf("(双周)");
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

    public static String[] getSectionId(HtmlPage page){
        DomElement timetable = page.getElementById("timetable");
        String timeTableXml = timetable.asXml();
        Document doc = Jsoup.parse(timeTableXml);
        String[] sectionIds = new String[6];
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
        for (String sectionId : sectionIds) {
            if(sectionId.length() < 32){
                throw new CourseException(555,"sectionId不全！");
            }
        }

        return sectionIds;
    }

//    public static List<RenWenCountDown> getRenWenList() throws BiffException, IOException {
//        List<RenWenCountDown> renWenCountDownList = new ArrayList<>();
////        String filePath = File.separator + "myFile" +File.separator +  "renwencountdown.xls";     //Linux
////        String filePath = "C:/renwencountdown.xls";     //Windows本地
//        String filePath = "src/renwencountdown.xls";     //Windows
//        Workbook workbook = Workbook.getWorkbook(new File(filePath));
//        Sheet sheet = workbook.getSheet(0);
//        int timeCol = 1;
//        int courseCol = 3;
//        int localCol = 4;
//        int banJiCol = 5;
//        int[] colArr = {1,3,4,5};
//        ArrayList<Integer> colList = new ArrayList<>();
//        colList.add(timeCol);
//        colList.add(courseCol);
//        colList.add(localCol);
//        colList.add(banJiCol);
//        for (int i = 4; i < sheet.getRows(); i++) {
//            RenWenCountDown renWenCountDown = new RenWenCountDown();
//            for (int j = 0; j < colList.size(); j++) {
//                int nowCol = colArr[j];
//                switch (nowCol) {
//                    case 1:
//                        Cell timeCell = sheet.getCell(nowCol, i);
//                        String timeStr = timeCell.getContents();
//                        if (timeStr == null || timeStr.equals("")) {
//                            continue;
//                        }
//                        String dateStr = timeStr.substring(0, 10);
//                        int index = timeStr.lastIndexOf("）");
//                        String sub = timeStr.substring(index + 1);
//                        String[] startEnd = sub.split("-");
//                        String startTime = dateStr + " " + startEnd[0];
//                        String endTime = dateStr + " " + startEnd[1];
//                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//                        LocalDateTime localStart = LocalDateTime.parse(startTime, formatter);
//                        LocalDateTime localEnd = LocalDateTime.parse(endTime, formatter);
//                        renWenCountDown.setStartTime(localStart);
//                        renWenCountDown.setEndTime(localEnd);
//                        break;
//                    case 3:
//                        Cell courseCell = sheet.getCell(nowCol, i);
//                        String courseStr = courseCell.getContents();
//                        renWenCountDown.setCourseName(courseStr);
//                        break;
//                    case 4:
//                        Cell locationCell = sheet.getCell(nowCol, i);
//                        String locationStr = locationCell.getContents();
//                        renWenCountDown.setLocation(locationStr);
//                        break;
//                    case 5:
//                        Cell banJiCell = sheet.getCell(nowCol, i);
//                        String banJiStr = banJiCell.getContents();
//                        renWenCountDown.setBanJi(banJiStr);
//                        renWenCountDownList.add(renWenCountDown);
//                        break;
//                    default:
//                        throw new CourseException(555,"没有这个case");
//                }
//            }
//        }
//        workbook.close();
//        renWenCountDownList.remove(75);
//        return renWenCountDownList;
//    }


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
