package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.CountDown;
import com.haotongxue.entity.vo.CountDownVo;
import com.haotongxue.service.ICountDownService;
import com.haotongxue.service.ICourseService;
import com.haotongxue.utils.ConvertUtil;
import com.haotongxue.utils.R;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.management.Query;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

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
public class CountDownController {

    @Autowired
    ICountDownService iCountDownService;

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
            int countDownHour = (int) ((start - now) / (1000 * 60 * 60));
            countDownVo.setCountDownHour(countDownHour);
        }
        return R.ok().data("data",listVo);
    }
}

