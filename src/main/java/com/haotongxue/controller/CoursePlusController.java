package com.haotongxue.controller;


import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.CoursePlus;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.utils.R;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2022-02-18
 */
@Api(tags = "课表查询（新）")
@RestController
@RequestMapping("/coursePlus")
public class CoursePlusController {

    @Resource(name = "coursePlusCache")
    LoadingRedisCache<List<CoursePlus[]>> coursePlusCache;

    @Resource(name = "studentStatusCache")
    LoadingRedisCache<StudentStatus> studentStatusCache;

    @ApiOperation("获取学生课表")
    @GetMapping("/authority")
    public R getCourse(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                       @RequestParam @ApiParam("周次") String week){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        if (studentStatus == null){
            return R.error().message("学籍表为空");
        }
        List<CoursePlus[]> coursePluses = coursePlusCache.get(studentStatus.getNo() + ":" + week);
        return R.ok().data("list",coursePluses);
    }
}

