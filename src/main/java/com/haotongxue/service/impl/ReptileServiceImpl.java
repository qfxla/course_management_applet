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
            //?????????~????????????1-2~7-2
            if (page == null) {
                throw new CourseException(555, "page?????????");
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
                    if (j == 2) {     //?????????5??????????????????
                        continue;
                    }
                    key = sectionIds[j] + "-" + (i + 1) + "-2";
                    if (page.getElementById(key) == null) {
                        throw new NullPointerException("Key????????????");
                    } else {
                        domElements[i][j] = page.getElementById(key);
                    }
                    String course = domElements[i][j].asText();
                    if (course.length() < 5) {
                        switchFlag++;
                    }
                    if (switchFlag >= 42) {
                        log.info("???????????????????????????");
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
            for (int i = 0; i < 7; i++) {     //?????????????????????
                for (int j = 0; j <= 5; j++) {     //sectionIds[0]???sectionIds[5]
                    if (Thread.currentThread().isInterrupted()) {
                        log.info("???????????????????????????????????????");
                        throw new CourseException(555, "?????????????????????????????????????????????");
                    }
                    if (j == 2) {     //?????????5??????????????????
                        continue;
                    }
                    key = sectionIds[j] + "-" + (i + 1) + "-2";
                    if (page.getElementById(key) == null) {
                        throw new NullPointerException("Key????????????");
                    } else {
                        domElements[i][j] = page.getElementById(key);
                    }
                    String course = domElements[i][j].asText();
                    if (course.length() < 5) {
                        switchFlag++;
                    }
                    if (switchFlag >= 42) {
                        throw new CourseException(555, "????????????????????????????????????");
                    }
                    String[] temp;
                    temp = course.split("---------------------|----------------------");
                    //System.out.println(Arrays.toString(temp));
                    String[] courseInfo = new String[4];
                    for (int k = 0; k < temp.length; k++) {
//                        if(temp[k].contains("???????????????")){
//                            temp[k] = temp[k].substring(0,temp[k].indexOf("???????????????"));
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
                        if (temp[k].contains("?????????")) {
                            log.info("?????????????????????");
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
                            if (courseInfo[1].contains("??????") || courseInfo[1].contains("?????????")
                                    || courseInfo[1].contains("??????") || courseInfo[1].contains("??????")
                                    || courseInfo[1].contains("?????????") || courseInfo[1].contains("??????")
                                    || courseInfo[1].contains("?????????") || courseInfo[1].contains("??????")
                                    || courseInfo[1].contains("??????") || courseInfo[1].contains("?????????")
                                    || courseInfo[1].contains("??????") || courseInfo[1].contains("??????")
                            ) {
                                courseInfo[0] = courseInfo[0] + courseInfo[1];
                                courseInfo[1] = courseInfo[2];
                                courseInfo[2] = courseInfo[3];
                                courseInfo[3] = "";
                            }
                            if (courseInfo[1].contains("(???)")){
                                courseInfo[3] = courseInfo[2];
                                courseInfo[2] = courseInfo[1];
                                courseInfo[1] = "??????";
                            }
                            if (courseInfo[3].contains(":")) {
                                courseInfo[3] = "???";
                            }
                            weekList = getWeekCount(courseInfo[2]);
                            if(weekList.get(0) == -99){
                                System.out.println(courseInfo[2]);
                                throw new CourseException(555,"??????????????????bug??????");
                            }
                            sectionList = getSectionCount(courseInfo[2]);
                        }
                        int xingqiId = i + 1;     //?????????

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
                                    weekStr = "???";
                                    sectionStr = "???";
                                }
                                infoId = iinfoService.addCourseInfo(xingqiId, weekStr, sectionStr);
                                iUserInfoService.insertUserInfo(currentOpenid, infoId);
                                String courseId;
                                Integer teacherId;
                                Integer classroomId;
//                                synchronized (insertLock){
                                String courseName = courseInfo[0];      //?????????
                                courseId = iCourseService.addCourse(courseName); //????????????t_course
                                String teacherName = courseInfo[1];     //?????????
                                teacherId = iTeacherService.addTeacher(teacherName);  //????????????t_teacher
                                String classroomName = courseInfo[3];   //?????????
                                classroomId = iClassroomService.addClassroom(classroomName);    //????????????t_classroom
//                                }

                                //??????????????????t_info????????????
                                iInfoCourseService.insertInfoCourse(infoId, courseId);

                                //??????????????????t_info????????????
                                iInfoTeacherService.insertInfoTeacher(infoId, teacherId);

                                //??????????????????t_info????????????
                                iInfoClassroomService.insertInfoClassroom(infoId, classroomId);

                                //??????????????????t_info????????????
                                iInfoWeekService.insertInfoWeek(infoId, weekId);

                                //??????????????????t_info????????????
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
                courseException.setMsg("??????isPa??????");
                throw courseException;
            }
            cache.invalidate(currentOpenid);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }
        //????????????????????????????????????
//        String xueHao = user.getNo();
//        if(xueHao != null){
//            String arg = xueHao.substring(4,9);
//            if(!(arg.equals("11414") || arg.equals("11424") || arg.equals("11434") || arg.equals("11412"))){
//                //??????????????????????????????????????????
//                cache.invalidate(currentOpenid);
//                return;
//            }else{
//                List<RenWenCountDown> renWenList = null;
//                try {
//                    renWenList = getRenWenList();
//                } catch (BiffException | IOException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(currentOpenid + "----" + "????????????????????????????????????????????????????????????");
//                String myGrade = xueHao.substring(2,4);
//                String myZhuanYe = xueHao.substring(5,7);
//                String myBanji = xueHao.substring(9,10);
//                System.out.println("??????===================" + xueHao);
//                for (RenWenCountDown renWenCountDown : renWenList) {
//                    String name = renWenCountDown.getCourseName();
//                    LocalDateTime startTime = renWenCountDown.getStartTime();
//                    LocalDateTime endTime = renWenCountDown.getEndTime();
//                    String location = renWenCountDown.getLocation();
//
//                    String banJiStr = renWenCountDown.getBanJi();
//                    String gradeClassNum = banJiStr.substring(2);
//                    String zhuanye = "";
//                    if(banJiStr.contains("??????")){
//                        zhuanye = "14";
//                    }else if(banJiStr.contains("??????")){
//                        zhuanye = "24";
//                    }else if(banJiStr.contains("??????")){
//                        zhuanye = "34";
//                    }else{
//                        throw new CourseException(555,"??????????????????");
//                    }
//                    String grade = gradeClassNum.substring(0,2);
//                    String classs = gradeClassNum.substring(2);
//                    if(!myBanji.equals(classs) || !myGrade.equals(grade) || !arg.substring(3,5).equals(zhuanye)){
//                        continue;
//                        //??????????????????
//                    }
//                    //?????????????????????????????????????????????
//                    //?????????
//                    CountDown newCountDown = new CountDown();
//                    newCountDown.setOpenid(currentOpenid);
//                    newCountDown.setName(name);
//                    newCountDown.setStartTime(startTime);
//                    newCountDown.setEndTime(endTime);
//                    newCountDown.setLocation(location);
//                    int insert = countDownMapper.insert(newCountDown);
//                    if (insert == 1) {
//                        System.out.println(newCountDown);
//                        System.out.println(arg + "----" + currentOpenid + "?????????????????????????????????????????????");
//                    } else {
//                        throw new CourseException(555, arg + "----" + currentOpenid + "???????????????????????????????????????");
//                    }
//                }
//                cache.invalidate(currentOpenid);
//                return;
//            }
//        }else{
//            throw new CourseException(555,"??????????????????????????????????????????");
//        }
    }

    public static String getWeekStr(String ws){
        if(ws == null){
            return "???";
        }
        String zSub = "";
        if(ws.contains("???") && !ws.contains("???")){  //????????????
            return ws;  //??????????????????
        }

        if(ws.contains("???")){
            int zhouIdx = ws.indexOf("???");
            zSub = ws.substring(0, zhouIdx + 2);
            return zSub;  //????????????
        }
        return "???";     //???????????? ?????? ?????????
    }

    public static String getSectionStr(String ws){
        if(ws == null){
            return "???";
        }
        String zSub = "";
        if(ws.contains("???")){   //??????zSub
            int zhouIdx = ws.indexOf("???");
            zSub = ws.substring(0, zhouIdx + 2);
        }

        if(ws.contains("???") && ws.contains("???")){   //?????????????????????????????????
            int jieIdx = ws.indexOf("???");
            String jSub = ws.substring(zSub.length(),jieIdx+2);
            return jSub;
        }else if(!ws.contains("???") && ws.contains("???")){    //????????????
            return ws;  //??????????????????
        }
        return "???";     //????????????  ??????  ?????????
    }


    public static ArrayList<Integer> getWeekCount(String weekAndSection){
        ArrayList<Integer> weekList = new ArrayList<>();
        int index;
        if(weekAndSection.contains("(???)")){
            index = weekAndSection.indexOf("(???)");
        }else if(weekAndSection.contains("(??????)")){
            index = weekAndSection.indexOf("(??????)");
        }else if(weekAndSection.contains("(??????)")){
            index = weekAndSection.indexOf("(??????)");
        }else{
            throw new CourseException(555,"??????????????????Bug??????");
        }
        if(index == -1){
            return new ArrayList<>();
        }
        String subWeek  = weekAndSection.substring(0, index);     //1-3,5,15,18
        String[] weekArr = new String[10];
        int idx = subWeek.indexOf(",");     //1???3
        int num = 0,n = 0;
        while (subWeek.contains(",")){
            weekArr[num] = subWeek.substring(0,idx);    //??????????????????????????????????????????
            subWeek = subWeek.substring(idx+1);   //????????????
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
        int end = weekAndSection.indexOf("???");
        if(end == -1){
            System.out.println("????????????????????????");
            System.out.println(weekAndSection);
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
                throw new CourseException(555,"sectionId?????????");
            }
        }

        return sectionIds;
    }

//    public static List<RenWenCountDown> getRenWenList() throws BiffException, IOException {
//        List<RenWenCountDown> renWenCountDownList = new ArrayList<>();
////        String filePath = File.separator + "myFile" +File.separator +  "renwencountdown.xls";     //Linux
////        String filePath = "C:/renwencountdown.xls";     //Windows??????
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
//                        int index = timeStr.lastIndexOf("???");
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
//                        throw new CourseException(555,"????????????case");
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
