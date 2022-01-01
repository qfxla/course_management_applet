package com.haotongxue.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.*;
import com.haotongxue.entity.vo.AddCourseVo;
import com.haotongxue.mapper.InfoMapper;
import com.haotongxue.mapper.InfoWeekMapper;
import com.haotongxue.service.*;
import com.haotongxue.service.impl.InfoTeacherServiceImpl;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.haotongxue.utils.*;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
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
@RequestMapping("/zkCourse/authority/info")
public class InfoController {
    @Autowired
    private IInfoService iInfoService;

    @Autowired
    private InfoMapper infoMapper;

//    @Resource(name = "loginCache")
//    LoadingCache<String,Object> userCache;

    @Resource(name = "loginCache")
    LoadingRedisCache userCache;

//    @Resource(name = "weekCache")
//    LoadingCache<String,Object> weekCache;

    @Resource(name = "weekCache")
    LoadingRedisCache weekCache;

//    @Resource(name = "courseCache")
//    LoadingCache<String,Object> courseCache;

    @Resource(name = "courseCache")
    LoadingRedisCache courseCache;

    @Autowired
    AddCourseService addCourseService;
    @Autowired
    IUserInfoService iUserInfoService;
    @Autowired
    IInfoClassroomService iInfoClassroomService;
    @Autowired
    IInfoCourseService iInfoCourseService;
    @Autowired
    IInfoTeacherService iInfoTeacherService;
    @Autowired
    IInfoWeekService iInfoWeekService;
    @Autowired
    IInfoSectionService infoSectionService;
    @Autowired
    InfoWeekMapper infoWeekMapper;



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
    public R updateCourseData() throws Exception {
        String openId = UserContext.getCurrentOpenid();
        boolean b = iInfoService.updateCourseData();
        //删除缓存
        for (int i = 1;i <= 20;i++) {
            courseCache.invalidate("cour" + openId + ":" + i);
        }
        if (b){
            return R.ok();
        }
        return R.error();
    }

    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "删除某个课程")
    @GetMapping("/deleteCourse")
    public R deleteCourse(@RequestParam(value = "infoIdList") String list){
//        JSONArray obj = JSON.parseArray(list);
//        List<String> infoIdList = new ArrayList<>();
//        if (obj.size() > 0) {
//
//            for (int i = 0; i < obj.size(); i++) {
//                infoIdList.add((String) obj.get(i));
//            }
//        }
        String[] infoIdList = list.split(",");
        for (String infoId : infoIdList) {
            UserInfo userInfo = iUserInfoService.list(new QueryWrapper<UserInfo>().eq("info_id", infoId)).get(0);
            iUserInfoService.remove(new QueryWrapper<UserInfo>().eq("info_id",infoId));
            iInfoService.removeById(infoId);
            iInfoClassroomService.remove(new QueryWrapper<InfoClassroom>().eq("info_id",infoId));
            iInfoCourseService.remove(new QueryWrapper<InfoCourse>().eq("info_id",infoId));
            iInfoTeacherService.remove(new QueryWrapper<InfoTeacher>().eq("info_id",infoId));
            infoSectionService.remove(new QueryWrapper<InfoSection>().eq("info_id",infoId));
            InfoWeek infoWeek = infoWeekMapper.selectById(infoId);
            iInfoWeekService.remove(new QueryWrapper<InfoWeek>().eq("info_id",infoId));
            courseCache.invalidate("cour" + userInfo.getOpenid() + ":" + infoWeek.getWeekId());
        }

        return R.ok();
    }
}