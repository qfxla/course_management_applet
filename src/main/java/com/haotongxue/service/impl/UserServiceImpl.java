package com.haotongxue.service.impl;

import com.alibaba.fastjson.JSON;
import com.haotongxue.entity.User;
import com.haotongxue.entity.WeChatLoginResponse;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.handler.WatchIsPaingHandler;
import com.haotongxue.mapper.*;
import com.haotongxue.service.EduLoginService;
import com.haotongxue.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.utils.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

}
