package com.haotongxue.controller;


import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.entity.WeChatLoginResponse;
import com.haotongxue.entity.dto.WeChatLoginDTO;
import com.haotongxue.service.EduLoginService;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.JwtUtils;
import com.haotongxue.utils.R;
import com.haotongxue.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    LoadingCache<String,Object> cache;

    @Autowired
    EduLoginService eduLoginService;

    @ApiOperation(value = "微信登录")
    @PostMapping("/login")
    public R login(@RequestBody WeChatLoginDTO loginDTO){
        WeChatLoginResponse loginResponse = userService.getLoginResponse(loginDTO.getCode());
        String openid = loginResponse.getOpenid();
        User user = (User) cache.get("logi" + openid);
        boolean isDoPa = true; //是否执行校园网登录验证
        if (user == null){
            //快捷登录失败
            if (loginDTO.getNickName() == null){
                return R.error().code(ResultCode.QUICK_LOGIN_ERROR);
            }
            user = new User();
            user.setOpenid(loginResponse.getOpenid());
            user.setNickName(loginDTO.getNickName());
            user.setAvatarUrl(loginDTO.getAvatarUrl());
            user.setGender(loginDTO.getGender());
            userService.save(user);
            cache.put("logi"+openid,user);
        }else {
            isDoPa = user.getIsPa() == 1;
        }

        if (isDoPa){
            HtmlPage login = eduLoginService.login(loginDTO.getNo(), loginDTO.getPassward());
            if (login != null){
                System.out.println("执行爬虫");
            }else {
                R.error();
            }
        }
        String token = JwtUtils.generate(openid);
        return R.ok().data("Authority",token).data("openid",openid);
    }

}

