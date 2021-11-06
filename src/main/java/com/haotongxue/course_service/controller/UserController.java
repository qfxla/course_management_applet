package com.haotongxue.course_service.controller;


import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.course_service.entity.User;
import com.haotongxue.course_service.entity.WeChatLoginResponse;
import com.haotongxue.course_service.entity.dto.QuicklyWeChatLoginDTO;
import com.haotongxue.course_service.entity.dto.WeChatLoginDTO;
import com.haotongxue.course_service.entity.vo.UserLoginVO;
import com.haotongxue.course_service.service.IUserService;
import com.haotongxue.course_service.utils.JwtUtils;
import com.haotongxue.course_service.utils.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
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
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    LoadingCache<String,Object> cache;

    @ApiOperation(value = "微信登录")
    @PostMapping("/login")
    public R login(@RequestBody WeChatLoginDTO loginDTO){
        WeChatLoginResponse loginResponse = userService.getLoginResponse(loginDTO.getCode());
        User user = new User();
        user.setOpenid(loginResponse.getOpenid());
        user.setNickName(loginDTO.getNickName());
        user.setAvatarUrl(loginDTO.getAvatarUrl());
        user.setGender(loginDTO.getGender());
        userService.saveOrUpdate(user);
        String openid = user.getOpenid();

        //cache缓存openid只有2小时
        cache.put("logi"+openid,user);

        String token = JwtUtils.generate(openid);
        return R.ok().data("Authority",token).data("openid",openid);
    }

    @ApiOperation("快捷的微信登录")
    @PostMapping("/quicklyLogin")
    public R quicklyLogin(@RequestBody QuicklyWeChatLoginDTO quicklyWeChatLoginDTO){
        String openid = userService.quicklyLogin(quicklyWeChatLoginDTO);
        String token;
        if (cache.asMap().containsKey("logi"+openid)){
            token =  JwtUtils.generate(openid);
            User user = (User) cache.get("logi" + openid);
            UserLoginVO userLoginVO = new UserLoginVO();
            BeanUtils.copyProperties(user,userLoginVO);
            return token != null ? R.ok().data("Authority",token).data("userInfo",userLoginVO) : R.error();
        }
        return R.error();
    }
}

