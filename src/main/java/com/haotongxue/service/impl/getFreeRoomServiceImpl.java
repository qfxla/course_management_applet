package com.haotongxue.service.impl;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.haotongxue.entity.FreeRoom;
import com.haotongxue.entity.vo.TodayCourseVo;
import com.haotongxue.runnable.ReReptileRunnable;
import com.haotongxue.runnable.ReptileRunnable;
import com.haotongxue.service.IFreeRoomService;
import com.haotongxue.service.getFreeRoomService;
import com.haotongxue.utils.LoginUtils;
import com.haotongxue.utils.UserContext;
import com.haotongxue.utils.WebClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * @author zcj
 * @creat 2021-11-26-8:11
 */
@Slf4j
@Service
@EnableScheduling
public class getFreeRoomServiceImpl implements getFreeRoomService {

    @Autowired
    IFreeRoomService iFreeRoomService;

//    @Transactional(rollbackFor = Exception.class)
//    @Scheduled(initialDelay = 5000,fixedDelay = 300000)
    @Override
    public void paFreeRoom() throws IOException {
        WebClient webClient = WebClientUtils.getWebClient();
        HtmlPage afterLogin = LoginUtils.login(webClient,"202010244130","Zhku106133");
        log.info("ohpVk5VjCMQ9IZsZzfmwruWvhXeA" + "开始爬虫");
        HtmlPage page = null;
        try {
            page = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/kbcx/kbxx_classroom");
        } catch (IOException e) {
            e.printStackTrace();
        }
        HtmlSelect campus = page.getHtmlElementById("xqid");
        campus.setDefaultValue("1");
        HtmlSelect building = page.getHtmlElementById("jzwid");
        building.setDefaultValue("A01");

        Map<Integer, List<Integer>> map = getSectionMap();

        for (int i = 1;i < 23;i++){
            System.out.println(i);
            HtmlSelect startWeek = page.getHtmlElementById("zc1");
            startWeek.setDefaultValue(String.valueOf(i));
            HtmlSelect endWeek = page.getHtmlElementById("zc2");
            endWeek.setDefaultValue(String.valueOf(i));
            HtmlElement query = page.getHtmlElementById("btn_query");
            HtmlPage afterQuery = query.click();
            webClient.waitForBackgroundJavaScript(1000);
            DomElement tbody = afterQuery.getElementsByTagName("tbody").get(0);
            DomNodeList<HtmlElement> tr = tbody.getElementsByTagName("tr");
            for (HtmlElement htmlElement : tr) {   //每一个教师每一周的课
                DomNodeList<HtmlElement> td = htmlElement.getElementsByTagName("td");
                String name = td.get(0).asText();
                int xingqi = 0;
                int start = 0;
                for (int j = 1;j < td.size();j++){
                    start++;
                    if (j % 6 == 1){
                        xingqi ++;
                        start = 1;
                    }
                    if (td.get(j).asText().trim().equals("")){
                        List<Integer> sections = map.get(start);
                        for (Integer section : sections) {
                            FreeRoom freeRoom = new FreeRoom(UUID.randomUUID().toString(),i,section,xingqi,name,"海珠校区","教学楼");
//                            System.out.println(freeRoom);
                            iFreeRoomService.save(freeRoom);
                        }
                    }
                }
            }
        }
        System.out.println("我爬完了");
    }

    Map<Integer,List<Integer>> getSectionMap(){
        Map<Integer, List<Integer>> map = new HashMap<>();
        map.put(1,new ArrayList<Integer>(){{this.add(1);this.add(2);}});
        map.put(2,new ArrayList<Integer>(){{this.add(3);this.add(4);}});
        map.put(3,new ArrayList<Integer>(){{this.add(5);}});
        map.put(4,new ArrayList<Integer>(){{this.add(6);this.add(7);}});
        map.put(5,new ArrayList<Integer>(){{this.add(8);this.add(9);}});
        map.put(6,new ArrayList<Integer>(){{this.add(10);this.add(11);this.add(12);}});
        return map;
    }
}
