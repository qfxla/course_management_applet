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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public int paGrade(String openid, WebClient webClient){
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
        int total = (tr.size()-1);
//        ArrayList<GradePush> pushList = new ArrayList<>();
//        String ofOpenid = userMapper.getOfOpenidByOpenid(openid);
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
                //数据库里这个人没有这个成绩
                int insert = gradeMapper.insert(gradeObj);
                if(insert == 1){
//                    if(term.equals("2021-2022-1")){
//                        if(ofOpenid != null){
//                            GradePush gradePush = new GradePush(ofOpenid, subject, property);
//                            pushList.add(gradePush);
//                        }
//                    }
                    System.out.println(openid + "---" + "插入成绩成功---" + subject);
                }else{
                    log.info(openid +  "未能成功插入成绩");
                }
            }
        }
        System.out.println(openid + "---成绩总数：" + total);

//        String pushKey = "GradesPush";
//        if(pushList.size() == 0){
//            //没有本学期的新成绩，不操作redis
//            return total > 0 ? total : -1;
//        }
//        if(Boolean.TRUE.equals(redisTemplate.hasKey(pushKey))){
//            for (GradePush gradePush : pushList) {
//                if(gradePush != null){
//                    List<GradePush> cacheList = redisTemplate.opsForList().range(pushKey, 0, -1);
//                    for (GradePush pushCache : cacheList) {
//                        if(!(gradePush.getOfOpenid().equals(pushCache.getOfOpenid()) &&
//                                gradePush.getSubject().equals(pushCache.getSubject()))){
//                            Long num = redisTemplate.opsForList().rightPush(pushKey, gradePush);
//                            log.info("插入" + gradePush.getSubject() + "，pushCacheList中有" + num + "个元素，" + pushKey + "的过期时间为：" + redisTemplate.getExpire(pushKey));
//                        }else{
//                            System.out.println("缓存list中已有同一个人的同一科目，不加");
//                        }
//                    }
//                }
//            }
//        }else {
//            if(pushList.size() != 0){
//                redisTemplate.opsForList().rightPushAll(pushKey,pushList);
//                redisTemplate.expire(pushKey,23,TimeUnit.HOURS);
//            }
//        }
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