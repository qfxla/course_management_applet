package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.entity.CountDown;
import com.haotongxue.entity.User;
import com.haotongxue.mapper.CountDownMapper;
import com.haotongxue.service.ICountDownService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.LoginUtils;
import com.haotongxue.utils.WebClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-12-01
 */
@Service
@Slf4j
public class CountDownServiceImpl extends ServiceImpl<CountDownMapper, CountDown> implements ICountDownService {

    @Autowired
    IUserService userService;

    @Override
    //@Scheduled(cron = "0 0 3 * * ?")
    public void refreshCountDown(){
        log.info("开始查询考试信息--->");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("openid","no","password");
        List<User> list = userService.list(userQueryWrapper);
        for (User user : list){
            executorService.execute(() -> {
                searchCountDown(user.getOpenid(),user.getNo(),user.getPassword());
            });
        }
        executorService.shutdown();
    }

    @Override
    public void searchCountDown(String userOpenid,String no,String password){
        WebClient webClient = WebClientUtils.getWebClient();
        HtmlPage loginPage = null;
        DomNodeList<DomElement> trList = null;
        try {
            loginPage = LoginUtils.login(webClient, no, password);
            HtmlElement testSchedule = loginPage.getHtmlElementById("NEW_XSD_KSBM_WDKS_KSAPCX");
            HtmlPage testPage = testSchedule.click();
            HtmlElement queryBtn = testPage.getHtmlElementById("btn_query");
            HtmlPage testQueryMsg = queryBtn.click();
            trList = testQueryMsg.getElementsByTagName("tr");
        } catch (IOException e) {
            //e.printStackTrace();
        }
        boolean loop = true;
        for (int i=1;i<trList.size() && loop;i++){
            CountDown countDown = new CountDown();
            countDown.setOpenid(userOpenid);
            DomElement domElement = trList.get(i);
            DomNodeList<HtmlElement> tdList = domElement.getElementsByTagName("td");
            if (tdList.get(0).getTextContent().contains("未")){
                loop = false;
                continue;
            }
            countDown.setName(tdList.get(4).asText());
            QueryWrapper<CountDown> wrapperTwo = new QueryWrapper<>();
            wrapperTwo.eq("openid",userOpenid).eq("name",countDown.getName());
            if (count(wrapperTwo) != 0){
                continue;
            }
            countDown.setLocation(tdList.get(7).asText());
            String startAndEndTime = tdList.get(6).asText();
            String[] split = startAndEndTime.split(" ");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(split[0], dateFormatter);
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String[] timeSplit = split[1].split("~");
            LocalTime startTime = LocalTime.parse(timeSplit[0], timeFormatter);
            LocalTime endTime = LocalTime.parse(timeSplit[1], timeFormatter);
            countDown.setStartTime(LocalDateTime.of(localDate,startTime));
            countDown.setEndTime(LocalDateTime.of(localDate,endTime));
            saveOrUpdate(countDown);
        }
    }
}
