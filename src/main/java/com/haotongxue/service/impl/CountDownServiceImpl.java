package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.haotongxue.entity.CountDown;
import com.haotongxue.entity.Selected;
import com.haotongxue.entity.User;
import com.haotongxue.entity.UserSelected;
import com.haotongxue.mapper.CountDownMapper;
import com.haotongxue.service.ICountDownService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.service.ISelectedService;
import com.haotongxue.service.IUserSelectedService;
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


    @Autowired
    IUserService userService;

    @Autowired
    ISelectedService selectedService;

    @Autowired
    IUserSelectedService userSelectedService;

    public static final Object lock = new Object();

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

    @Override
    public void searchOptionCourse(String userOpenid, WebClient webClient) {
        try {
            HtmlPage htmlPage = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/xkgl/xsxkjgcx");
            HtmlElement query = htmlPage.getHtmlElementById("btn_query");
            HtmlSelect select = htmlPage.getHtmlElementById("xnxqid");
            int optionSize = select.getOptionSize();
            boolean loop = true;
            for (int i=1;i<optionSize && loop;i++){
                HtmlOption option = select.getOption(i);
                option.click();
                HtmlPage click = query.click();
                DomNodeList<DomElement> trList = click.getElementsByTagName("tr");
                for (int j=1;j<trList.size();j++){
                    DomElement domElement = trList.get(j);
                    DomNodeList<HtmlElement> tdList = domElement.getElementsByTagName("td");
                    if (tdList.get(0).getTextContent().contains("未")){
                        loop = false;
                        continue;
                    }
                    if ("任选".equals(tdList.get(6).asText()) && "公共课".equals(tdList.get(7).asText())){
                        String courseName = tdList.get(1).asText();
                        String score = tdList.get(5).asText();
                        QueryWrapper<Selected> selectedQueryWrapper = new QueryWrapper<>();
                        selectedQueryWrapper
                                .select("selected_id")
                                .eq("selected_name",courseName)
                                .eq("selected_score",score);
                        UserSelected userSelected = new UserSelected();
                        userSelected.setOpenid(userOpenid);
                        if (selectedService.count(selectedQueryWrapper) == 0){
                            synchronized (lock){
                                if (selectedService.count(selectedQueryWrapper) == 0){
                                    Selected selected = new Selected();
                                    selected.setSelectedName(courseName);
                                    selected.setSelectedScore(Float.valueOf(score));
                                    selectedService.save(selected);
                                    userSelected.setSelectedId(selected.getSelectedId());
                                    userSelectedService.save(userSelected);
                                }
                            }
                        }else {
                            Selected selectedOne = selectedService.getOne(selectedQueryWrapper);
                            QueryWrapper<UserSelected> userSelectedQueryWrapper = new QueryWrapper<>();
                            userSelectedQueryWrapper
                                    .eq("openid",userOpenid)
                                    .eq("selected_id",selectedOne.getSelectedId());
                            if (userSelectedService.count(userSelectedQueryWrapper) == 0){
                                userSelected.setSelectedId(selectedOne.getSelectedId());
                                userSelectedService.save(userSelected);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
