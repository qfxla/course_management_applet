package com.haotongxue.controller;


import com.haotongxue.service.IFailRateService;
import com.haotongxue.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2022-02-07
 */
@Api(tags = "挂科率查询")
@RestController
@RequestMapping("/failRate")
public class FailRateController {

    @Autowired
    IFailRateService failRateService;

    @ApiOperation("更新挂科率（平时禁止调用）")
    @PostMapping()
    public R refreshRate(){
        failRateService.refreshRate();
        return R.ok();
    }
}

