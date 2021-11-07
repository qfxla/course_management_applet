package com.haotongxue.controller;


import com.haotongxue.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@RestController
@CrossOrigin
@Slf4j
@Api(tags = "课程表信息")
@RequestMapping("/info")
public class InfoController {

    @ApiOperation(value = "获得课程表信息")
    @GetMapping("/getInfo")
    public R getInfo(@RequestParam(value = "week",required = false,defaultValue = "0")int week){
        if (week == 0){//调用本周的课表


        }else {//调用所选周次的课表

        }
        return null;
    }

}

