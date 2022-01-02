package com.haotongxue.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.Grade;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.mapper.GradeMapper;
import com.haotongxue.mapper.UserMapper;
import com.haotongxue.service.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService  {

    @Resource
    GradeMapper gradeMapper;

    @Resource
    UserMapper userMapper;

    @Resource(name = "GradeCache")
    LoadingRedisCache gradeCache;

    @Resource
    RedisTemplate<Object, Grade> redisTemplate;

    public int paGrade(String openid, WebClient webClient){
//    public int paGrade(){
//        WebClient webClient = getWebClient();
//        try {
//            LoginUtils.login(webClient, "202010244304", "Ctc779684470...");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        HtmlPage page = null;
        ArrayList<Grade> pushList = new ArrayList<>();
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
//        String ofOpenId = userMapper.getOfOpenidByOpenid(openid);
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
//                    if(gradeObj.getTerm().equals("2021-2022-1")){
//                        pushList.add(gradeObj);
//                    }
                    System.out.println(openid + "---" + "插入考试成功---" + subject);
                }
            }
        }
        System.out.println(openid + "---成绩总数：" + total);
//        String pushKey = "grade" + "==" +  ofOpenId;
//        gradeCache.put(pushKey,pushList);
        return total > 0 ? total : -1;
    }

    @Override
    public List<Grade> getGrade(String openId,int term){
        String termStr = "";
        switch (term){
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
            String key = "MyUnThisGrade" + openId + ":" + termStr;
            List<Grade> unThisGradeList = null;
            if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
                unThisGradeList = redisTemplate.opsForList().range(key,0,-1);
            }else{
                unThisGradeList = gradeMapper.selectList(new QueryWrapper<Grade>()
                        .eq("openid",openId).eq("term",termStr).orderByDesc("term").orderByDesc("create_time"));
                System.out.println(key + "==" + redisTemplate.getExpire(key));
                if(!(unThisGradeList == null || unThisGradeList.size() == 0)){
                    redisTemplate.opsForList().rightPushAll(key,unThisGradeList);
                    Boolean expire = redisTemplate.expire(key, 1, TimeUnit.DAYS);
                    assert expire != null;
                    System.out.println(redisTemplate.getExpire(key) + expire.toString());
                }
            }
            return unThisGradeList;
        }
    }
}