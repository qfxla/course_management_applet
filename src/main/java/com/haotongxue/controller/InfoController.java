package com.haotongxue.controller;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.entity.vo.AddCourseVo;
import com.haotongxue.mapper.InfoMapper;
import com.haotongxue.service.AddCourseService;
import com.haotongxue.service.IInfoService;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.haotongxue.utils.*;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@RestController@CrossOrigin
@Slf4j
@Api(tags = "课程表信息")
@RequestMapping("/authority/info")
public class InfoController {
    @Autowired
    private IInfoService iInfoService;

    @Autowired
    private InfoMapper infoMapper;

    @Resource(name = "loginCache")
    LoadingCache<String,Object> userCache;

    @Resource(name = "weekCache")
    LoadingCache<String,Object> weekCache;

    @Resource(name = "courseCache")
    LoadingCache<String,Object> courseCache;

    @Autowired
    AddCourseService addCourseService;



    @ApiOperation(value = "判断是否爬完")
    @GetMapping("/successPa")
    public R successPa(){
        String openId = UserContext.getCurrentOpenid();
        User user = (User) userCache.get(openId);
        if (user == null){
            return R.error().message("没有用户");
        }
        return user.getIsPa() == 1? R.ok().message("爬取成功") : R.error().message("尚未爬取成功");
    }

    @ApiOperation(value = "获得课程表信息")
    @GetMapping("/getInfo")
    public R getInfo(@RequestParam(value = "week",required = false,defaultValue = "0")int week) throws InterruptedException {
        String openId = UserContext.getCurrentOpenid();
        List<List> timeTables = (List<List>)courseCache.get("cour" + openId + ":" + week);
//        List<List> timeTables = iInfoService.getInfo(openId, week);
        return timeTables != null?R.ok().data("timeTables",timeTables) : R.error();
    }

    @ApiOperation("自定义添加课程")
    @PostMapping("/addCourse")
    public R addCourse(@RequestBody AddCourseVo addCourseVo){
        String openId = UserContext.getCurrentOpenid();
        boolean flag = addCourseService.addCourse(openId, addCourseVo);
        return flag?R.ok():R.error();
    }

    @ApiOperation(value = "重新爬取课程表数据")
    @GetMapping("/updateCourseData")
    public R updateCourseData() throws IOException {
        String openId = UserContext.getCurrentOpenid();
        boolean b = iInfoService.updateCourseData();
        for (int i = 1;i <= 20;i++){
            courseCache.invalidate("cour" + openId + ":" + i);
        }
        //删除缓存
        if (b){
            return R.ok();
        }
        return R.error();
    }
}