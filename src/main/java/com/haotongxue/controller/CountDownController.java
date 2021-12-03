package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.CountDown;
import com.haotongxue.entity.User;
import com.haotongxue.entity.vo.CountDownVo;
import com.haotongxue.service.ICountDownService;
import com.haotongxue.service.ICourseService;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.ConvertUtil;
import com.haotongxue.utils.R;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.management.Query;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    ExecutorService executorService = Executors.newCachedThreadPool();

    @ApiOperation("获得登录用户的倒计时信息")
    @GetMapping("/authority/getCountDownMes")
    public R getCountDownMes(){
        String openid = UserContext.getCurrentOpenid();
        QueryWrapper<CountDown> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid).eq("is_deleted",0);
        List<CountDown> list = iCountDownService.list(wrapper);
        List<CountDownVo> listVo = ConvertUtil.convert(list, CountDownVo.class);
        for (CountDownVo countDownVo : listVo) {
            LocalDateTime startTime = countDownVo.getStartTime();
            long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            long start = startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            int countDownHour = (int) ((start - now) / (1000 * 60 * 60) - 8);
            countDownVo.setCountDownHour(countDownHour);
        }
        return R.ok().data("data",listVo);
    }


    @ApiOperation("触发一下查考试倒计时信息")
    @PostMapping("/authority/triCountDown")
    public R triggerSearchCountDown(){
        String currentOpenid = UserContext.getCurrentOpenid();
        QueryWrapper<CountDown> countDownQueryWrapper = new QueryWrapper<>();
        countDownQueryWrapper.eq("openid",currentOpenid);
        int count = iCountDownService.count(countDownQueryWrapper);
        if (count > 0){
            return R.ok();
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("no","password").eq("openid",currentOpenid);
        User user = userService.getOne(userQueryWrapper);
        log.info("----->"+currentOpenid+"触发了查考试倒计时");
        executorService.execute(() -> iCountDownService.searchCountDown(currentOpenid,user.getNo(),user.getPassword()));
        return R.ok();
    }
}

