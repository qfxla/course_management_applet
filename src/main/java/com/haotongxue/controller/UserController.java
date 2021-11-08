package com.haotongxue.controller;


import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    LoadingCache<String,Object> cache;

    @Autowired
    EduLoginService eduLoginService;

    @ApiOperation(value = "微信登录")
    @PostMapping("/login")
    public R login(@RequestBody WeChatLoginDTO loginDTO) throws IOException {
        WeChatLoginResponse loginResponse = userService.getLoginResponse(loginDTO.getCode());
        String openid = loginResponse.getOpenid();
        User user = (User) cache.get("logi" + openid);
        boolean isDoPa = true; //是否执行学校系统登录验证
        if (user == null){
            //快捷登录失败
            if (loginDTO.getNickName() == null || loginDTO.getNo() == null || loginDTO.getPassword() == null){
                return R.error().code(ResultCode.QUICK_LOGIN_ERROR);
            }
            //执行官网的登录
            WebClient webClient = new WebClient();
            //配置webClient
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.waitForBackgroundJavaScript(3*1000);
            webClient.getCookieManager().setCookiesEnabled(true);

            //执行表单提交
            HtmlPage page = webClient.getPage("http://edu-admin.zhku.edu.cn");
            HtmlForm loginForm = page.getHtmlElementById("loginForm");
            HtmlInput username = loginForm.getInputByName("username");
            username.setValueAttribute(loginDTO.getNo());
            HtmlInput password = loginForm.getInputByName("password");
            password.setValueAttribute(loginDTO.getPassword());
            HtmlElement submit = page.getHtmlElementById("submit");
            HtmlPage afterClick = submit.click();
            webClient.waitForBackgroundJavaScript(1000);

            boolean isSuccessLogin = false;
            try {
                afterClick.getHtmlElementById("loginForm");
                //如果没有找到元素则抛出异常（证明loginForm1不是登录页，账号和密码正确）
            }catch (ElementNotFoundException notFoundException){
                notFoundException.printStackTrace();
                //标记登录成功
                isSuccessLogin = true;
            }

            if (!isSuccessLogin){
                return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
            }

            user = new User();
            BeanUtils.copyProperties(loginDTO,user);
            user.setOpenid(openid);
            userService.save(user);
            cache.put("logi"+openid,user);
        }else {
            //如果为0，则爬虫还没执行成功
            isDoPa = user.getIsPa() == 0;
        }
        if (isDoPa){
            System.out.println("执行爬虫");
        }
        String token = JwtUtils.generate(openid);
        return R.ok().data("Authority",token).data("openid",openid);
    }

}

