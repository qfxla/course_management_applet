package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.entity.Grade;
import com.haotongxue.entity.GradePush;
import com.haotongxue.entity.User;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.GradeMapper;
import com.haotongxue.mapper.UserMapper;
import com.haotongxue.service.*;
import com.haotongxue.utils.LoginUtils;
import com.haotongxue.utils.WebClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
//@Transactional(rollbackFor = Exception.class)
@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService  {

    @Autowired
    GradeMapper gradeMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IUserService userService;

//    @Resource(name = "GradeCache")
//    LoadingRedisCache gradeCache;

//    @Autowired
//    RedisTemplate<String,Grade> redisTemplate;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
//    @Scheduled(fixedDelay = 100000)
    public int paGrade(String openid, WebClient webClient){
//    public void paGrade(){
        HtmlPage page = null;
//        WebClient webClient = null;
//        String openid = "ohpVk5TmJDKSy5Wm3rGAvLQnUneQ";
        try {
//            webClient = WebClientUtils.getWebClient();
//            try {
////                LoginUtils.login(webClient, "202010244331", "Zhku10422X");
//                LoginUtils.login(webClient, "202010244304", "Ctc779684470...");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            page = webClient.getPage("https://edu-admin.zhku.edu.cn/jsxsd/kscj/cjcx_list?kksj=2021-2022-1");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert page != null;
        DomElement dataList = page.getElementById("dataList");
        DomNodeList<HtmlElement> tr = dataList.getElementsByTagName("tr");
        boolean flag = true;
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
            String property = tdd.get(12).asText();
            float score = Float.parseFloat(scoreStr);
            float gpa = Float.parseFloat(gpaStr);
            DomNodeList<HtmlElement> a = element.getElementsByTagName("a");
            for (HtmlElement htmlElement : a) {
                String hrefStr = htmlElement.getAttribute("href");
                String url = "https://edu-admin.zhku.edu.cn" +  hrefStr.substring(hrefStr.indexOf("('") + 2, hrefStr.indexOf("',"));
                HtmlPage norPage = null;
                try {
                    norPage = webClient.getPage(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (norPage != null){
                    DomElement norList = norPage.getElementById("dataList");
                    DomNodeList<HtmlElement> norTr = norList.getElementsByTagName("tr");
                    for (HtmlElement norEle : norTr) {
                        DomNodeList<HtmlElement> norTd = norEle.getElementsByTagName("td");
                        if (norTd.size() == 8) {
                            String norGrade = norTd.get(1).asText();
                            String qimoGrade = norTd.get(5).asText();
                            String norRatioStr = norTd.get(2).asText();
                            String qimoRatioStr = norTd.get(6).asText();
                            if (!Objects.equals(norRatioStr, "") && !Objects.equals(qimoRatioStr, "") && norRatioStr!=null && qimoRatioStr != null) {
//                                String bili = qimoRatioStr.substring(0, qimoRatioStr.indexOf("0%")) + ":" + norRatioStr.substring(0, norRatioStr.indexOf("0%"));
                                if(norGrade == null || norGrade.equals("")){
                                    norGrade = "无";
                                }
                                if(qimoGrade == null || qimoGrade.equals("")){
                                    qimoGrade = "无";
                                }
                                Grade gradeObj = new Grade(openid,term,subject,grade,property,score,gpa,norGrade,qimoGrade,norRatioStr,qimoRatioStr);
                                QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
                                queryWrapper.eq("openid",openid);
                                queryWrapper.eq("subject",subject);
                                int count = gradeMapper.selectCount(queryWrapper);
                                if(count == 0){
                                    //数据库里这个人没有这个成绩
                                    int insert = gradeMapper.insert(gradeObj);
                                    if(insert == 1){
                                        System.out.println(openid + "---" + "插入成绩成功---" + subject);
                                    }else{
                                        log.info(openid +  "未能成功插入成绩");
                                    }
                                }else{
                                    //数据库里这个人有这个成绩
                                    queryWrapper.select("id");
                                    Grade gradeId = gradeMapper.selectOne(queryWrapper);
                                    gradeObj.setId(gradeId.getId());
                                    int updateNor = gradeMapper.updateById(gradeObj);
                                    if(updateNor == 1){
                                        System.out.println(openid + "---" + "更新平时分成功---" + subject);
                                    }else{
                                        log.info(openid +  "未能成功更新平时分");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Grade gradeObj = new Grade(openid,term,subject,grade,property,score,gpa);
            QueryWrapper<Grade> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid",openid);
            queryWrapper.eq("subject",subject);
            int count = gradeMapper.selectCount(queryWrapper);
            if(count == 0){
                //数据库里这个人没有这个成绩
                int insert = gradeMapper.insert(gradeObj);
                if(insert == 1){
                    System.out.println(openid + "---" + "插入成绩成功---" + subject);
                }else{
                    log.info(openid +  "未能成功插入成绩");
                }
            }
        }
        System.out.println(openid + "---成绩总数：" + total);
        return total > 0 ? total : -1;
    }

    @Override
    public void searchName(String userOpenid, WebClient webClient) {
        try {
            HtmlPage htmlPage = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/framework/xsMain.htmlx#");
            List<HtmlElement> userInfo = htmlPage.getByXPath("//div[@class='userInfo']");
            HtmlElement userInfoEle = userInfo.get(0);
            List<HtmlElement> name = userInfoEle.getByXPath("//p[@style='font-weight: 500;']");
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            String realName = name.get(0).asText();
            userUpdateWrapper.eq("openid", userOpenid).set("real_name", realName);
            userService.update(userUpdateWrapper);
            System.out.println("插入了姓名--->" + userOpenid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Grade> getGrade(String openId,int term){
        String termStr = "";
        switch (term){
            case 0:
                termStr = "allGrades";
                break;
            case 1:
                termStr = "2017-2018-2";
                break;
            case 2:
                termStr = "2017-2018-1";
                break;
            case 3:
                termStr = "2018-2019-2";
                break;
            case 4:
                termStr = "2018-2019-1";
                break;
            case 5:
                termStr = "2019-2020-2";
                break;
            case 6:
                termStr = "2019-2020-1";
                break;
            case 7:
                termStr = "2020-2021-2";
                break;
            case 8:
                termStr = "2020-2021-1";
                break;
            case 9:
                termStr = "2021-2022-2";
                break;
            case 10:
                termStr = "2021-2022-1";
                break;
            default:
                throw new CourseException(555,"请求参数错误");
        }
        if(term == 10){
            return gradeMapper.selectList(new QueryWrapper<Grade>()
                    .eq("openid",openId).eq("term",termStr).orderByDesc("term").orderByDesc("create_time"));
        }else{
            String key = "QueryUnThisGrade" + openId + ":" + termStr;
            List<Grade> unThisGradeList;
            if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
                unThisGradeList = redisTemplate.opsForList().range(key,0,-1);
//                unThisGradeList = redisTemplate.boundListOps(key).range(0,-1);
            }else{
                if(term != 0){
                    unThisGradeList = gradeMapper.selectList(new QueryWrapper<Grade>()
                            .eq("openid",openId).eq("term",termStr).orderByDesc("term").orderByDesc("create_time"));
                }else{
                    unThisGradeList = gradeMapper.selectList(new QueryWrapper<Grade>()
                            .eq("openid",openId).orderByDesc("term").orderByDesc("create_time"));
                }
                if(!(unThisGradeList == null || unThisGradeList.size() == 0)){
                    redisTemplate.opsForList().rightPushAll(key,unThisGradeList);
                    Boolean expire = redisTemplate.expire(key, 2, TimeUnit.DAYS);
                }
            }
            return unThisGradeList;
        }
    }
}