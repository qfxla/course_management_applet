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
import com.haotongxue.service.ICountDownService;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
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
@RequestMapping("/countDown")
@Slf4j
public class CountDownController {

    @Autowired
    ICountDownService iCountDownService;

    @Autowired
    IUserService userService;

    @Resource
    CountDownMapper countDownMapper;

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
            int countDownHour = (int) ((start - now) / (1000 * 60 * 60) - 8);
            countDownVo.setCountDownHour(countDownHour);
        }
        List<CountDownVo> listVo = listVo1.stream().sorted(Comparator.comparing(CountDownVo::getCountDownHour)).collect(Collectors.toList());
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


//    @GetMapping("/insertRenWenCountDown")
//    public boolean insertRenWenCountDown(){
//        HashMap<String,String> zhuanYe = new HashMap<>();
////        zhuanYe.put("行管","11414");
//        zhuanYe.put("行管","11412");
//        zhuanYe.put("社工","11424");
//        zhuanYe.put("文管","11434");
//        List<RenWenCountDown> renWenList = null;
//        try {
//            renWenList = getRenWenList();
//        } catch (BiffException | IOException e) {
//            e.printStackTrace();
//        }
//
////20 19114142 03
//        int count = 0;
//        for (RenWenCountDown renWenCountDown : renWenList) {
//            String banJiStr = renWenCountDown.getBanJi();
//            String gradeClassNum = banJiStr.substring(2);
//            String grade = gradeClassNum.substring(0,2);
//            String classs = gradeClassNum.substring(2);
//            String arg = "";
//            if (banJiStr.contains("行管")) {
//                arg = grade + zhuanYe.get("行管") + classs;
//            }else if(banJiStr.contains("社工")){
//                arg = grade + zhuanYe.get("社工") + classs;
//            }else if(banJiStr.contains("文管")){
//                arg = grade + zhuanYe.get("文管") + classs;
//            }else{
//                throw new CourseException(555,"找不到该专业！");
//            }
//            if(!arg.equals("")){
//                List<String> openIdList = countDownMapper.getOpenIdByArg(arg);
//                for (String openId : openIdList) {
//                    if(countDownMapper.concludeInsert(openId) >= 0){
//                        count++;
//                        continue;
//                    }
//                    if(openIdList.size() <= 0){
//                        continue;
//                    }
//                    System.out.println("@@@@@@@@@@有考试，插！");
//                    CountDown countDown = new CountDown();
//                    countDown.setOpenid(openId);
//                    countDown.setName(renWenCountDown.getCourseName());
//                    countDown.setStartTime(renWenCountDown.getStartTime());
//                    countDown.setEndTime(renWenCountDown.getEndTime());
//                    countDown.setLocation(renWenCountDown.getLocation());
//                    countDownMapper.insert(countDown);
//                }
//            }
//        }
//        System.out.println("总人数：" + count);
////        for (RenWenCountDown renWenCountDown : renWenList) {
////            System.out.println(renWenCountDown);
////        }
//        return true;
//    }


//    public static List<RenWenCountDown> getRenWenList() throws BiffException, IOException {
//        List<RenWenCountDown> renWenCountDownList = new ArrayList<>();
//        Workbook workbook = Workbook.getWorkbook(new File("C:/renwencountdown.xls"));
//        Sheet sheet = workbook.getSheet(0);
//        int timeCol = 1;
//        int courseCol = 3;
//        int localCol = 4;
//        int banJiCol = 5;
//        int[] colArr = {1,3,4,5};
//        System.out.println(Arrays.toString(colArr));
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
//                            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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
}