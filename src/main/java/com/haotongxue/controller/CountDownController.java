package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.entity.CountDown;
import com.haotongxue.entity.RenWenCountDown;
import com.haotongxue.entity.User;
import com.haotongxue.entity.vo.CountDownVo;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.CountDownMapper;
import com.haotongxue.mapper.UserMapper;
import com.haotongxue.service.ICountDownService;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.*;
import io.swagger.annotations.ApiOperation;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-12-01
 */
@RestController
@RequestMapping("/zkCourse/countDown")
@Slf4j
public class CountDownController {

    @Autowired
    ICountDownService iCountDownService;

    @Autowired
    IUserService userService;

    @Resource
    CountDownMapper countDownMapper;

    @Resource
    UserMapper userMapper;

    @ApiOperation("获得登录用户的倒计时信息")
    @GetMapping("/authority/getCountDownMes")
    public R getCountDownMes(){
        String openid = UserContext.getCurrentOpenid();
        QueryWrapper<CountDown> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid).eq("is_deleted",0).gt("start_time",new Date());
//        wrapper.eq("openid",openid).eq("is_deleted",0);
        List<CountDown> list = iCountDownService.list(wrapper);
        List<CountDownVo> listVo1 = ConvertUtil.convert(list, CountDownVo.class);
        for (CountDownVo countDownVo : listVo1) {
            LocalDateTime startTime = countDownVo.getStartTime();
            long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long start = startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
//            int countDownHour = (int) ((start - now) / (1000 * 60 * 60) - 8);
            int countDownHour = (int) ((start - now) / (1000 * 60 * 60));
            countDownVo.setCountDownHour(countDownHour);
        }
        List<CountDownVo> listVo = listVo1.stream().sorted(Comparator.comparing(CountDownVo::getCountDownHour)).collect(Collectors.toList());
        return R.ok().data("data",listVo);
    }


    @ApiOperation("获得过去的考试信息")
    @GetMapping("/authority/getPastMes")
    public R getPastMes(){
        String openid = UserContext.getCurrentOpenid();
        QueryWrapper<CountDown> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid).eq("is_deleted",0).lt("start_time",new Date());
        List<CountDown> list = iCountDownService.list(wrapper);
        List<CountDownVo> listVo1 = ConvertUtil.convert(list, CountDownVo.class);
        List<CountDownVo> listVo = listVo1.stream().sorted(Comparator.comparing(CountDownVo::getEndTime).reversed()).collect(Collectors.toList());
        return R.ok().data("data",listVo);
    }

    @ApiOperation("触发一下查考试倒计时信息")
    @PostMapping("/authority/triCountDown")
    public R triggerSearchCountDown(){
//        String currentOpenid = UserContext.getCurrentOpenid();
//        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.select("no","password").eq("openid",currentOpenid);
//        User user = userService.getOne(userQueryWrapper);
//        log.info("----->"+currentOpenid+"新用户触发了查考试倒计时");
//        WebClient webClient = WebClientUtils.getWebClient();
//        HtmlPage login = null;
//        try {
//            login = LoginUtils.login(webClient, user.getNo(), user.getPassword());
//            if (login == null){
//                return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
//            }
//            executorService.execute(()->{
//                iCountDownService.searchOptionCourse(currentOpenid,webClient);
//                iCountDownService.searchCountDown(currentOpenid,webClient);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return R.ok();
    }

    @GetMapping("/insertRenWenCountDown")
    @Transactional(rollbackFor = Exception.class)
    public int insertRenWenCountDown(@RequestParam(value = "xueHao",required = false) String xueHao){
        List<RenWenCountDown> renWenList = null;
        try {
            renWenList = getRenWenList();
        } catch (BiffException | IOException e) {
            e.printStackTrace();
        }
        if(xueHao != null){
            String arg = xueHao.substring(4,9);
            if(!(arg.equals("11414") || arg.equals("11424") || arg.equals("11434") || arg.equals("11412"))){
                return 0;
            }
            String openid = UserContext.getCurrentOpenid();
            System.out.println(openid + "----" + "人文学生，触发了爬考试倒计时。。。。。。");
            String myGrade = xueHao.substring(2,4);
            String myZhuanYe = xueHao.substring(5,7);
            String myBanji = xueHao.substring(9,10);
            System.out.println("学号===================" + xueHao);
            for (RenWenCountDown renWenCountDown : renWenList) {
                String name = renWenCountDown.getCourseName();
                LocalDateTime startTime = renWenCountDown.getStartTime();
                LocalDateTime endTime = renWenCountDown.getEndTime();
                String location = renWenCountDown.getLocation();

                String banJiStr = renWenCountDown.getBanJi();
                String gradeClassNum = banJiStr.substring(2);
                String zhuanye = "";
                if(banJiStr.contains("行管")){
                    zhuanye = "14";
                }else if(banJiStr.contains("社工")){
                    zhuanye = "24";
                }else if(banJiStr.contains("文管")){
                    zhuanye = "34";
                }else{
                    throw new CourseException(555,"没有这个专业");
                }
                String grade = gradeClassNum.substring(0,2);
                String classs = gradeClassNum.substring(2);
                if(!myBanji.equals(classs) || !myGrade.equals(grade) || !arg.substring(3,5).equals(zhuanye)){
                    continue;
                    //换下一个考试
                }
                //必须保证同班且同年级才能加考试
                //直接加
                CountDown newCountDown = new CountDown();
                newCountDown.setOpenid(openid);
                newCountDown.setName(name);
                newCountDown.setStartTime(startTime);
                newCountDown.setEndTime(endTime);
                newCountDown.setLocation(location);
                int insert = countDownMapper.insert(newCountDown);
                if (insert == 1) {
                    System.out.println(newCountDown);
                    System.out.println(arg + "----" + openid + "刚进来的人文学生，有考试，插！");
                } else {
                    throw new CourseException(555, arg + "----" + openid + "刚进来的人文学生，插不进了");
                }
            }
            return 1;
        }
//20 19114142 03
        int count = 0;
        int addCount = 0;
        assert renWenList != null;
        for (int i = 1; i <= 3; i++) {
            /////////////////////////////////////////////////////
//            if(i!=1){
//                continue;
//            }
            /////////////////////////////////////////////////////
            String arg = "114" + i + "4";
            /////////////////////////////////////////////////////
//            String arg = "114" + i + "2";
            /////////////////////////////////////////////////////
            System.out.println("@@@@@@@@@@@@@@@" + arg);
            List<String> openIdList = countDownMapper.getOpenIdByArg(arg);
            for (String openid : openIdList) {
                count++;
                boolean conFlag = false;
                QueryWrapper<CountDown> isBai = new QueryWrapper<>();
                isBai.eq("openid",openid);
                List<CountDown> countDowns = countDownMapper.selectList(isBai);
                for (CountDown oldCountDown : countDowns) {
                    if (oldCountDown.getLocation().contains("白")) {
                        System.out.println(openid + "---------@@@@@@@@@@@@@@@@@有一个白云");
                        conFlag = true;
                        break;
                        //换下一个openid
                    }
                }
                if(conFlag){
                    continue;
                    //换下一个openid
                }
                QueryWrapper<User> oneUser = new QueryWrapper<>();
                oneUser.eq("openid",openid);
                User user = userMapper.selectOne(oneUser);
                String no = user.getNo();
                String myGrade = no.substring(2,4);
                String myZhuanYe = no.substring(5,7);
                String myBanji = no.substring(9,10);
                /////////////////////////////////////////////////////
//                if(Integer.parseInt(myBanji) <= 3){
//                    continue;
//                    //换下一个openid
//                }
                /////////////////////////////////////////////////////
                for (RenWenCountDown renWenCountDown : renWenList) {
                    String name = renWenCountDown.getCourseName();
                    LocalDateTime startTime = renWenCountDown.getStartTime();
                    LocalDateTime endTime = renWenCountDown.getEndTime();
                    String location = renWenCountDown.getLocation();

                    String banJiStr = renWenCountDown.getBanJi();
                    String gradeClassNum = banJiStr.substring(2);
                    String zhuanye = "";
                    if(banJiStr.contains("行管")){
                        zhuanye = "14";
                    }else if(banJiStr.contains("社工")){
                        zhuanye = "24";
                    }else if(banJiStr.contains("文管")){
                        zhuanye = "34";
                    }else{
                        throw new CourseException(555,"没有这个专业");
                    }
                    String grade = gradeClassNum.substring(0,2);
                    String classs = gradeClassNum.substring(2);
                    if(!myBanji.equals(classs) || !myGrade.equals(grade) || !arg.substring(3,5).equals(zhuanye)){
                        continue;
                        //换下一个考试
                    }
                    //必须保证同班且同年级同专业才能加考试
                    QueryWrapper<CountDown> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("openid",openid);
                    int integer = countDownMapper.selectCount(queryWrapper);
                    if(integer == 0){
                        //一节课都没有，加！
                        System.out.println(openid + "#########################################################一节课都没有，加！");
                        CountDown newCountDown = new CountDown();
                        newCountDown.setOpenid(openid);
                        newCountDown.setName(name);
                        newCountDown.setStartTime(startTime);
                        newCountDown.setEndTime(endTime);
                        newCountDown.setLocation(location);
                        int insert = countDownMapper.insert(newCountDown);
                        if (insert == 1) {
                            System.out.println(newCountDown);
                            System.out.println("0000----" + arg + "----" + openid + "有考试，插！");
                            addCount++;
                            //加了一个考试，换下一个考试
                        } else {
                            throw new CourseException(555, arg + "----" + openid + "插不进了");
                        }
                    }else{
                        QueryWrapper<CountDown> exist = new QueryWrapper<>();
                        exist.eq("openid",openid).eq("name",name);
                        //看这个openid下的这节课是否存在
                        int num = countDownMapper.ifExist(name,openid);
                        if(num < 1){
                            //没有这个考试，加
                            CountDown newCountDown = new CountDown();
                            newCountDown.setOpenid(openid);
                            newCountDown.setName(name);
                            newCountDown.setStartTime(startTime);
                            newCountDown.setEndTime(endTime);
                            newCountDown.setLocation(location);
                            int insert = countDownMapper.insert(newCountDown);
                            if (insert == 1) {
                                System.out.println(newCountDown.toString());
                                System.out.println(arg + "----" + openid + "有考试，插！");
                                addCount++;
                            } else {
                                throw new CourseException(555, arg + "----" + openid + "插不进了");
                            }
                        }
                    }
                }
            }
        }
        System.out.println("总人数：" + count);
        System.out.println("刚加考试的总数：" + addCount);
        return addCount;
    }


    @GetMapping("/insertRenWenCountDown3")
    @Transactional(rollbackFor = Exception.class)
    public int insertRenWenCountDown3(@RequestParam(value = "xueHao",required = false) String xueHao){
        List<RenWenCountDown> renWenList = null;
        try {
            renWenList = getRenWenList();
        } catch (BiffException | IOException e) {
            e.printStackTrace();
        }
        if(xueHao != null){
            String arg = xueHao.substring(4,9);
            if(!(arg.equals("11414") || arg.equals("11424") || arg.equals("11434") || arg.equals("11412"))){
                return 0;
            }
            String openid = UserContext.getCurrentOpenid();
            System.out.println(openid + "----" + "人文学生，触发了爬考试倒计时。。。。。。");
            String myGrade = xueHao.substring(2,4);
            String myZhuanYe = xueHao.substring(5,7);
            String myBanji = xueHao.substring(9,10);
            System.out.println("学号===================" + xueHao);
            for (RenWenCountDown renWenCountDown : renWenList) {
                String name = renWenCountDown.getCourseName();
                LocalDateTime startTime = renWenCountDown.getStartTime();
                LocalDateTime endTime = renWenCountDown.getEndTime();
                String location = renWenCountDown.getLocation();

                String banJiStr = renWenCountDown.getBanJi();
                String gradeClassNum = banJiStr.substring(2);
                String zhuanye = "";
                if(banJiStr.contains("行管")){
                    zhuanye = "14";
                }else if(banJiStr.contains("社工")){
                    zhuanye = "24";
                }else if(banJiStr.contains("文管")){
                    zhuanye = "34";
                }else{
//                    int[] arr = new Integer(){1,2,3};       //错
//                    int[] arr = new int(){1,2,3};       //错
//                    int[] arr = new int[]{1,2,3};       //对
                    throw new CourseException(555,"没有这个专业");
                }
                String grade = gradeClassNum.substring(0,2);
                String classs = gradeClassNum.substring(2);
                if(!myBanji.equals(classs) || !myGrade.equals(grade) || !arg.substring(3,5).equals(zhuanye)){
                    continue;
                    //换下一个考试
                }
                //必须保证同班且同年级同专业才能加考试
                CountDown newCountDown = new CountDown();
                newCountDown.setOpenid(openid);
                newCountDown.setName(name);
                newCountDown.setStartTime(startTime);
                newCountDown.setEndTime(endTime);
                newCountDown.setLocation(location);
                int insert = countDownMapper.insert(newCountDown);
                if (insert == 1) {
                    System.out.println(newCountDown);
                    System.out.println(arg + "----" + openid + "刚进来的人文学生，有考试，插！");
                } else {
                    throw new CourseException(555, arg + "----" + openid + "刚进来的人文学生，插不进了");
                }
            }
            return 1;
        }
//20 19114142 03
        int count = 0;
        int addCount = 0;
        assert renWenList != null;
        for (int i = 1; i <= 3; i++) {
            /////////////////////////////////////////////////////
            if(i!=1){
                continue;
            }
            /////////////////////////////////////////////////////
//            String arg = "114" + i + "4";
            /////////////////////////////////////////////////////
            String arg = "114" + i + "2";
            /////////////////////////////////////////////////////
            System.out.println("@@@@@@@@@@@@@@@" + arg);
            List<String> openIdList = countDownMapper.getOpenIdByArg(arg);
            for (String openid : openIdList) {
                count++;
                boolean conFlag = false;
                QueryWrapper<CountDown> isBai = new QueryWrapper<>();
                isBai.eq("openid",openid);
                List<CountDown> countDowns = countDownMapper.selectList(isBai);
                for (CountDown oldCountDown : countDowns) {
                    if (oldCountDown.getLocation().contains("白")) {
                        System.out.println(openid + "---------@@@@@@@@@@@@@@@@@有一个白云");
                        conFlag = true;
                        break;
                        //换下一个openid
                    }
                }
                if(conFlag){
                    continue;
                    //换下一个openid
                }
                QueryWrapper<User> oneUser = new QueryWrapper<>();
                oneUser.eq("openid",openid);
                User user = userMapper.selectOne(oneUser);
                String no = user.getNo();
                String myGrade = no.substring(2,4);
                String myZhuanYe = no.substring(5,7);
                String myBanji = no.substring(9,10);
                /////////////////////////////////////////////////////
                if(Integer.parseInt(myBanji) <= 3){
                    continue;
                    //换下一个openid
                }
                /////////////////////////////////////////////////////
                for (RenWenCountDown renWenCountDown : renWenList) {
                    String name = renWenCountDown.getCourseName();
                    LocalDateTime startTime = renWenCountDown.getStartTime();
                    LocalDateTime endTime = renWenCountDown.getEndTime();
                    String location = renWenCountDown.getLocation();

                    String banJiStr = renWenCountDown.getBanJi();
                    String gradeClassNum = banJiStr.substring(2);
                    String zhuanye = "";
                    if(banJiStr.contains("行管")){
                        zhuanye = "14";
                    }else if(banJiStr.contains("社工")){
                        zhuanye = "24";
                    }else if(banJiStr.contains("文管")){
                        zhuanye = "34";
                    }else{
                        throw new CourseException(555,"没有这个专业");
                    }
                    String grade = gradeClassNum.substring(0,2);
                    String classs = gradeClassNum.substring(2);
                    if(!myBanji.equals(classs) || !myGrade.equals(grade) || !arg.substring(3,5).equals(zhuanye)){
                        continue;
                        //换下一个考试
                    }
                    //必须保证同班且同年级才能加考试
                    QueryWrapper<CountDown> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("openid",openid);
                    int integer = countDownMapper.selectCount(queryWrapper);
                    if(integer == 0){
                        //一节课都没有，加！
                        System.out.println(openid + "#########################################################一节课都没有，加！");
                        CountDown newCountDown = new CountDown();
                        newCountDown.setOpenid(openid);
                        newCountDown.setName(name);
                        newCountDown.setStartTime(startTime);
                        newCountDown.setEndTime(endTime);
                        newCountDown.setLocation(location);
                        int insert = countDownMapper.insert(newCountDown);
                        if (insert == 1) {
                            System.out.println(newCountDown);
                            System.out.println("0000----" + arg + "----" + openid + "有考试，插！");
                            addCount++;
                            //加了一个考试，换下一个考试
                        } else {
                            throw new CourseException(555, arg + "----" + openid + "插不进了");
                        }
                    }else{
                        QueryWrapper<CountDown> exist = new QueryWrapper<>();
                        exist.eq("openid",openid).eq("name",name);
                        //看这个openid下的这节课是否存在
                        int num = countDownMapper.ifExist(name,openid);
                        if(num < 1){
                            //没有这个考试，加
                            CountDown newCountDown = new CountDown();
                            newCountDown.setOpenid(openid);
                            newCountDown.setName(name);
                            newCountDown.setStartTime(startTime);
                            newCountDown.setEndTime(endTime);
                            newCountDown.setLocation(location);
                            int insert = countDownMapper.insert(newCountDown);
                            if (insert == 1) {
                                System.out.println(newCountDown.toString());
                                System.out.println(arg + "----" + openid + "有考试，插！");
                                addCount++;
                            } else {
                                throw new CourseException(555, arg + "----" + openid + "插不进了");
                            }
                        }
                    }
                }
            }
        }
        System.out.println("总人数：" + count);
        System.out.println("刚加考试的总数：" + addCount);
        return addCount;
    }


    public static List<RenWenCountDown> getRenWenList() throws BiffException, IOException {
        List<RenWenCountDown> renWenCountDownList = new ArrayList<>();
//        String filePath = File.separator + "myFile" +File.separator +  "renwencountdown.xls";
        String filePath = "C:/renwencountdown.xls";
        Workbook workbook = Workbook.getWorkbook(new File(filePath));
        Sheet sheet = workbook.getSheet(0);
        int timeCol = 1;
        int courseCol = 3;
        int localCol = 4;
        int banJiCol = 5;
        int[] colArr = {1,3,4,5};
        ArrayList<Integer> colList = new ArrayList<>();
        colList.add(timeCol);
        colList.add(courseCol);
        colList.add(localCol);
        colList.add(banJiCol);
        for (int i = 4; i < sheet.getRows(); i++) {
            RenWenCountDown renWenCountDown = new RenWenCountDown();
            for (int j = 0; j < colList.size(); j++) {
                int nowCol = colArr[j];
                switch (nowCol) {
                    case 1:
                        Cell timeCell = sheet.getCell(nowCol, i);
                        String timeStr = timeCell.getContents();
                        if (timeStr == null || timeStr.equals("")) {
                            continue;
                        }
                        String dateStr = timeStr.substring(0, 10);
                        int index = timeStr.lastIndexOf("）");
                        String sub = timeStr.substring(index + 1);
                        String[] startEnd = sub.split("-");
                        String startTime = dateStr + " " + startEnd[0];
                        String endTime = dateStr + " " + startEnd[1];
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                        LocalDateTime localStart = LocalDateTime.parse(startTime, formatter);
                        LocalDateTime localEnd = LocalDateTime.parse(endTime, formatter);
                        renWenCountDown.setStartTime(localStart);
                        renWenCountDown.setEndTime(localEnd);
                        break;
                    case 3:
                        Cell courseCell = sheet.getCell(nowCol, i);
                        String courseStr = courseCell.getContents();
                        renWenCountDown.setCourseName(courseStr);
                        break;
                    case 4:
                        Cell locationCell = sheet.getCell(nowCol, i);
                        String locationStr = locationCell.getContents();
                        renWenCountDown.setLocation(locationStr);
                        break;
                    case 5:
                        Cell banJiCell = sheet.getCell(nowCol, i);
                        String banJiStr = banJiCell.getContents();
                        renWenCountDown.setBanJi(banJiStr);
                        renWenCountDownList.add(renWenCountDown);
                        break;
                    default:
                        throw new CourseException(555,"没有这个case");
                }
            }
        }
        workbook.close();
        renWenCountDownList.remove(75);
        return renWenCountDownList;
    }
}