package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.PasswordEditEntity;
import com.haotongxue.entity.User;
import com.haotongxue.entity.WeChatLoginResponse;
import com.haotongxue.entity.dto.PushSettingDTO;
import com.haotongxue.entity.dto.WeChatLoginDTO;
import com.haotongxue.handler.ReptileHandler;
import com.haotongxue.service.EduLoginService;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

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
@Slf4j
public class UserController {

    @Autowired
    IUserService userService;

    @Resource(name = "loginCache")
    LoadingCache<String,Object> cache;

    @Autowired
    EduLoginService eduLoginService;

    @Autowired
    ReptileHandler reptileHandler;

    @ApiOperation(value = "微信登录")
    @PostMapping("/login")
    public R login(@RequestBody WeChatLoginDTO loginDTO) throws IOException {
        WeChatLoginResponse loginResponse = userService.getLoginResponse(loginDTO.getCode());
        String openid = loginResponse.getOpenid();
        UserContext.add(openid);
        //用来标记是否是快捷登录
        boolean isQuickLogin = false;
        //是否让用户重新刷新他的个人信息
        boolean isRefreshInfo = false;
        if (loginDTO.getPassword() == null && loginDTO.getNo() == null){
            isQuickLogin = true;
            if (!cache.asMap().containsKey(openid)){
                //每三天用户信息就要更新一次，起码确保头像是最新的
                isRefreshInfo = true;
            }
        }
        User user = (User) cache.get(openid);
        boolean isDoPa = true; //是否执行学校系统登录验证
        WebClient webClient = null;
        if (user == null){
            //快捷登录失败
            if (isQuickLogin){
                return R.error().code(ResultCode.QUICK_LOGIN_ERROR);
            }

            webClient = WebClientUtils.getWebClient();
            HtmlPage afterLogin = LoginUtils.login(webClient, loginDTO.getNo(), loginDTO.getPassword());

            if (afterLogin == null){
                return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
            }
            user = new User();
            BeanUtils.copyProperties(loginDTO,user);
            user.setOpenid(openid);
            //记得在爬虫完以后要设置这条数据失效
            user.setIsPa(0);
            user.setIsPaing(0);
            userService.save(user);
            cache.put(openid,user);
        }else {
            //如果为0，则爬虫还没执行成功
            isDoPa = user.getIsPa() == 0;
        }
        if (isDoPa && user.getIsPaing() == 0){
            log.info(openid+"开始爬虫");
            reptileHandler.pa(webClient,user.getNo(),user.getPassword());
        }
        String token = JwtUtils.generate(openid);
        if (isRefreshInfo){
            return R.ok().code(ResultCode.NEED_REFRESH_INFO)
                    .data("Authority",token)
                    .data("subscribe",user.getSubscribe() == 1)
                    .data("isConcern",!user.getUnionId().equals(""));
        }
        return R.ok()
                .data("Authority",token)
                .data("openid",openid)
                .data("subscribe",user.getSubscribe() == 1)
                .data("isConcern",!user.getUnionId().equals(""));
    }

    @ApiOperation("修改用户密码")
    @PostMapping("/authority")
    public R editPassword(@RequestBody PasswordEditEntity passwordEditEntity) throws IOException {
        String no = passwordEditEntity.getNo();
        String password = passwordEditEntity.getPassword();
        if (StringUtils.isEmpty(no) || StringUtils.isEmpty(password)){
            return R.error();
        }
        //验证账号和密码对不对
        WebClient webClient = WebClientUtils.getWebClient();
        HtmlPage afterLogin = LoginUtils.login(webClient, no, password);
        if (afterLogin == null){
            return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
        }
        String currentOpenid = UserContext.getCurrentOpenid();
        //使缓存失效
        cache.invalidate(currentOpenid);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("no",no).set("password",password).eq("openid",currentOpenid);
        userService.update(userUpdateWrapper);
        return R.ok();
    }

    @ApiOperation("查看用户推送设置")
    @GetMapping("/authority")
    public R isSubscribe(){
        String currentOpenid = UserContext.getCurrentOpenid();
        User user = (User) cache.get(currentOpenid);
        if (user == null){
            return R.error();
        }
        return R.ok().data("isSubscribe",user.getSubscribe());
    }

    @ApiOperation("查看用户是否关注了公众号")
    @GetMapping("/authority/confirmOfficial")
    public R confirmConcern(){
        String currentOpenid = UserContext.getCurrentOpenid();
        User user = (User) cache.get(currentOpenid);
        return user.getUnionId().equals("") ? R.error() : R.ok();
    }

    @ApiOperation("重新设置信息推送规则")
    @PostMapping("/authority/setting")
    public R pushSetting(@RequestBody PushSettingDTO pushSettingDTO){
        String currentOpenid = UserContext.getCurrentOpenid();
        cache.invalidate(currentOpenid);
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("subscribe",pushSettingDTO.getSubscribe());
        userUpdateWrapper.eq("openid",currentOpenid);
        userService.update(userUpdateWrapper);
        return R.ok();
    }
}

