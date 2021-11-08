package com.haotongxue.controller;


import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.service.IInfoService;
import com.haotongxue.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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
    @Autowired
    private IInfoService iInfoService;

    @Autowired
    LoadingCache<String,Object> cache;

    @ApiOperation(value = "获得课程表信息")
    @GetMapping("/getInfo")
    public R getInfo(@RequestParam(value = "week",required = false,defaultValue = "0")int week) throws InterruptedException {
        //如果该用户未导入成功
        //....


        List<List> cacheResult = (List<List>)cache.get("cour" + week);
        if (cacheResult != null){
            return R.ok().data("timeTables",cacheResult);
        }


        List<List> timeTables;
        if (week == 0){//调用本周的课表.....
            //判断今天是第几周

            timeTables = null;
        }else {//调用所选周次的课表
            timeTables = iInfoService.getInfo(week);
        }
        return R.ok().data("timeTables",timeTables);
    }

}

