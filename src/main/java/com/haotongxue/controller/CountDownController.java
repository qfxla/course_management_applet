package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.Html;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.benmanes.caffeine.cache.Cache;
import com.haotongxue.entity.CountDown;
import com.haotongxue.entity.User;
import com.haotongxue.entity.vo.CountDownVo;
import com.haotongxue.service.ICountDownService;
import com.haotongxue.service.ICourseService;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.management.Query;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-12-01
 */
@RestController
@RequestMapping("/countDown")
@Slf4j
public class CountDownController {

    @Autowired
    ICountDownService iCountDownService;

    @Autowired
    IUserService userService;

//    @Resource(name = "countDownCache")
//    Cache<String,String> cache;

    ExecutorService executorService = Executors.newCachedThreadPool();

    @ApiOperation("获得登录用户的倒计时信息")
    @GetMapping("/authority/getCountDownMes")
    public R getCountDownMes(){
        String openid = UserContext.getCurrentOpenid();
        QueryWrapper<CountDown> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid).eq("is_deleted",0).gt("start_time",new Date());
        List<CountDown> list = iCountDownService.list(wrapper);
        List<CountDownVo> listVo1 = ConvertUtil.convert(list, CountDownVo.class);
        for (CountDownVo countDownVo : listVo1) {
            LocalDateTime startTime = countDownVo.getStartTime();
            long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long start = startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            int countDownHour = (int) ((start - now) / (1000 * 60 * 60) - 8);
            countDownVo.setCountDownHour(countDownHour);
        }
        List<CountDownVo> listVo = listVo1.stream().sorted(Comparator.comparing(CountDownVo::getCountDownHour)).collect(Collectors.toList());
        return R.ok().data("data",listVo);
    }


    @ApiOperation("触发一下查考试倒计时信息")
    @PostMapping("/authority/triCountDown")
    public R triggerSearchCountDown(){
        String currentOpenid = UserContext.getCurrentOpenid();
//        if (cache.asMap().containsKey(currentOpenid)){
//            return R.ok();
//        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("no","password").eq("openid",currentOpenid);
        User user = userService.getOne(userQueryWrapper);
        log.info("----->"+currentOpenid+"触发了查考试倒计时");
        WebClient webClient = WebClientUtils.getWebClient();
        HtmlPage login = null;
        try {
            login = LoginUtils.login(webClient, user.getNo(), user.getPassword());
            if (login == null){
                return R.error().code(ResultCode.NO_OR_PASSWORD_ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        HtmlPage finalLogin = login;
        executorService.execute(() -> iCountDownService.searchCountDown(currentOpenid, finalLogin));
        //cache.put(currentOpenid,"");
        return R.ok();
    }
}