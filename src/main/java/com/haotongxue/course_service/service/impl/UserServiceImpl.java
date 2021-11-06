package com.haotongxue.course_service.service.impl;

import com.alibaba.fastjson.JSON;
import com.haotongxue.course_service.entity.User;
import com.haotongxue.course_service.entity.WeChatLoginResponse;
import com.haotongxue.course_service.entity.dto.QuicklyWeChatLoginDTO;
import com.haotongxue.course_service.exceptionhandler.CourseException;
import com.haotongxue.course_service.mapper.UserMapper;
import com.haotongxue.course_service.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haotongxue.course_service.utils.WeChatUtil;
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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    WeChatUtil weChatUtil;

    @Autowired
    RestTemplate restTemplate;


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
    public String quicklyLogin(QuicklyWeChatLoginDTO quicklyWeChatLoginDTO) {
        WeChatLoginResponse loginResponse = getLoginResponse(quicklyWeChatLoginDTO.getCode());
        String openid = loginResponse.getOpenid();
        return openid;
    }
}
