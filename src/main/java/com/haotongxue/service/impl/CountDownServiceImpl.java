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

    @Override
    public void searchCountDown(String userOpenid, WebClient webClient) {
        DomNodeList<DomElement> trList = null;
        try {
            HtmlPage testPage = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/xsks/xsksap_query");
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
            wrapperTwo
                    .select("id")
                    .eq("openid",userOpenid)
                    .eq("name",countDown.getName());
            CountDown one = null;
            try {
                one = getOne(wrapperTwo);
            }catch (Exception e){
                log.info(userOpenid+"---->报错");
            }
            if (one != null){
                countDown.setId(one.getId());
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
            //log.info(userOpenid+"-->插入或更新考试倒计时一条");
            saveOrUpdate(countDown);
        }
    }
}
