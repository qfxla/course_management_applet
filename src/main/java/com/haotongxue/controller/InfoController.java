package com.haotongxue.controller;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.service.IInfoService;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.R;
import com.haotongxue.utils.ResultCode;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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
@RequestMapping("/authority/info")
public class InfoController {
    @Autowired
    private IInfoService iInfoService;
    @Autowired
    private IUserService iUserService;

    @Resource(name = "courseCache")
    LoadingCache<String,Object> cache;

    @ApiOperation(value = "获得课程表信息")
    @GetMapping("/getInfo")
    public R getInfo(@RequestParam(value = "week",required = false,defaultValue = "0")int week) throws InterruptedException {

        System.out.println(UserContext.getCurrentOpenid());
        String openId = UserContext.getCurrentOpenid();
        User user = iUserService.getById(openId);
        if (user.getIsPa() == 0){
            return R.error().code(ResultCode.COURSE_IMPORT_UNFINISHED);
        }

        List<List> timeTables = (List<List>)cache.get("cour" + openId + ":" + week);

        return timeTables != null?R.ok().data("timeTables",timeTables) : R.error();
    }

}

