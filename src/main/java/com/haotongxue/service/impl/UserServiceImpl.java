package com.haotongxue.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.entity.User;
import com.haotongxue.entity.WeChatLoginResponse;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.handler.WatchIsPaingHandler;
import com.haotongxue.mapper.*;
import com.haotongxue.service.EduLoginService;
import com.haotongxue.service.ICountDownService;
import com.haotongxue.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    WeChatUtil weChatUtil;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    IUserService userService;

    @Autowired
    EduLoginService eduLoginService;

    @Autowired
    WatchIsPaingHandler watchIsPaingHandler;

    @Autowired
    ICountDownService iCountDownService;

    @Override
    public WeChatLoginResponse getLoginResponse(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid="+weChatUtil.getAppid()+"" +
                "&secret="+weChatUtil.getSecret()+"" +
                "&js_code="+code+"" +
                "&grant_type=authorization_code";
        String response = restTemplate.getForObject(url,String.class);
        WeChatLoginResponse loginResponse = JSON.parseObject(response, WeChatLoginResponse.class);
        Integer errcode = loginResponse.getErrcode();
        if (errcode != null && errcode != 0){
            CourseException courseException = new CourseException();
            courseException.setCode(20001);
            if (errcode == -1){
                courseException.setMsg("系统繁忙");
            }else if (errcode == 40029){
                courseException.setMsg("code无效");
            }else if (errcode == 45011){
                courseException.setMsg("频率限制，每个用户每分钟100次");
            }else if (errcode == 40226){
                courseException.setMsg("高风险等级用户，小程序登录拦截");
            }else {
                courseException.setMsg("code有误");
            }
            throw courseException;
        }
        return loginResponse;
    }

    @Override
    public void triggerSearchCountDown(String currentOpenid,WebClient webClient) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("no","password").eq("openid",currentOpenid);
        User user = userService.getOne(userQueryWrapper);
        log.info("----->"+currentOpenid+"新用户触发了查考试倒计时和选课");
        try {
            iCountDownService.searchOptionCourse(currentOpenid,webClient);
        }catch (Exception e){

        }
        try {
            iCountDownService.searchCountDown(currentOpenid,webClient);
        }catch (Exception e){

        }

    }

    @Override
    public boolean studentEvaluate(WebClient webClient) {
        try {
            HtmlPage htmlPage = webClient.getPage("http://edu-admin.zhku.edu.cn/jsxsd/xspj/xspj_find.do");
            List<HtmlElement> table = htmlPage.getByXPath("//table[@class='layui-table']");
            DomNode tbody = table.get(0).getFirstChild();
            DomNodeList<DomNode> childNodes = tbody.getChildNodes();
            for (int i=1;i<childNodes.size();i++){
                DomNode tr = childNodes.get(i);
                DomNodeList<DomNode> tdList = tr.getChildNodes();
                DomNode isEvaluate = tdList.get(tdList.size() - 2);
                if ("否".equals(isEvaluate.asText())){
                    HtmlElement gotoEvaluate = (HtmlElement) tdList.get(tdList.size() - 1);
                    HtmlPage evaluatePage = gotoEvaluate.click();
                    System.out.println(evaluatePage.asText());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
