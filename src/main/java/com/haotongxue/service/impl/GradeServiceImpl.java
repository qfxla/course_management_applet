package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.CountDown;
import com.haotongxue.entity.Grade;
import com.haotongxue.entity.vo.AddCourseVo;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.AddCourseMapper;
import com.haotongxue.mapper.CountDownMapper;
import com.haotongxue.mapper.GradeMapper;
import com.haotongxue.service.*;
import com.haotongxue.utils.LoginUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.haotongxue.utils.WebClientUtils.getWebClient;

@Service
@Transactional(rollbackFor = Exception.class)
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService  {

    @Resource
    GradeMapper gradeMapper;

    public int paGrade(String openid, WebClient webClient){
//    public int paGrade(){
//        WebClient webClient = getWebClient();
        try {
            LoginUtils.login(webClient, "202010244304", "Ctc779684470...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HtmlPage page = null;
        try {
            page = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/kscj/cjcx_list");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert page != null;
        DomElement dataList = page.getElementById("dataList");
        DomNodeList<HtmlElement> tr = dataList.getElementsByTagName("tr");
        boolean flag = true;
//        String openid = "测试";
        int total = (tr.size()-1);
        for (HtmlElement element : tr) {
            if(flag){
                flag = false;
                continue;
            }
            DomNodeList<HtmlElement> tdd = element.getElementsByTagName("td");
            String term = tdd.get(1).asText();
            String subject = tdd.get(3).asText();
            String grade = tdd.get(4).asText();
            String scoreStr = tdd.get(6).asText();
            String gpaStr = tdd.get(8).asText();
            float score = Float.parseFloat(scoreStr);
            float gpa = Float.parseFloat(gpaStr);
            String property = tdd.get(12).asText();
            Grade gradeObj = new Grade(openid,term,subject,grade,property,score,gpa);
            QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid",openid);
            queryWrapper.eq("subject",subject);
            int count = gradeMapper.selectCount(queryWrapper);
            if(count == 0){
                int insert = gradeMapper.insert(gradeObj);
                if(insert == 1){
                    System.out.println(openid + "---" + "插入考试成功---" + subject);
                }
            }
        }
        System.out.println("考试总数：" + total);
        return total > 0 ? total : -1;
    }
}
