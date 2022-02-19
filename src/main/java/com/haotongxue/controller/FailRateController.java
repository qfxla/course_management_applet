package com.haotongxue.controller;


import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.vo.ESVO;
import com.haotongxue.entity.vo.FailRateClassifyVO;
import com.haotongxue.service.IFailRateService;
import com.haotongxue.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

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

    @Resource(name = "failRateClassifyCache")
    LoadingRedisCache<FailRateClassifyVO> failRateClassifyCache;

    @ApiOperation("更新挂科率（平时禁止调用）")
    @PostMapping()
    public R refreshRate(){
        failRateService.refreshRate();
        failRateService.prepareES();
        return R.ok();
    }

    @ApiOperation("获取分类信息")
    @GetMapping("/classify")
    public R getFailRateClassifyCache(){
        FailRateClassifyVO failRateClassifyVO = failRateClassifyCache.get("");

        return R.ok().data("classify",failRateClassifyVO);
    }

    @ApiOperation("条件查询挂科率")
    @GetMapping()
    public R getSubjectFailRate(@RequestParam @ApiParam("学院id") String collegeId,
                                @RequestParam @ApiParam("专业id") String majorId,
                                @RequestParam @ApiParam("专业id") String subjectId,
                                @RequestParam @ApiParam("当前页") Integer currentPage){
        List<ESVO> subjectFail;
        try {
            subjectFail = failRateService.getSubjectFail(collegeId, majorId, subjectId, currentPage);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error().message("捕获异常");
        }
        return R.ok().data("list",subjectFail);
    }
}