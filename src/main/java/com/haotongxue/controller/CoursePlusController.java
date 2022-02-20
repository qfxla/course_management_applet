package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.CoursePlus;
import com.haotongxue.entity.StudentStatus;
import com.haotongxue.entity.dto.AddCourseDTO;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.openfeign.RemoteReptileCalling;
import com.haotongxue.service.ICoursePlusService;
import com.haotongxue.utils.R;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
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

    @Autowired
    ICoursePlusService coursePlusService;

    @Resource(name = "studentStatusCache")
    LoadingRedisCache<StudentStatus> studentStatusCache;

    @Autowired
    RemoteReptileCalling remoteReptileCalling;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

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

    @ApiOperation("刷新课程")
    @PutMapping("/authority")
    public R refreshCourse(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        remoteReptileCalling.getCourseBySchoolWebsite(currentOpenid);
        return R.ok();
    }

    @ApiOperation("添加课程")
    @PostMapping("/authority")
    public R addCourse(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                       @RequestBody AddCourseDTO addCourseDTO){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        String courseName = addCourseDTO.getCourseName();
        String dayOfWeek = addCourseDTO.getDayOfWeek();
        List<String> weeks = addCourseDTO.getWeeks();
        String section = addCourseDTO.getSection();
        if (StringUtils.isEmpty(courseName)
                || StringUtils.isEmpty(dayOfWeek)
                || StringUtils.isEmpty(section)
                || weeks.isEmpty()){
            return R.error().message("信息有缺漏");
        }
        QueryWrapper<CoursePlus> coursePlusQueryWrapper = new QueryWrapper<>();
        for (String week : addCourseDTO.getWeeks()){
            CoursePlus coursePlus = new CoursePlus();
            coursePlus.setNo(no);
            coursePlus.setCourseName(addCourseDTO.getCourseName());
            coursePlus.setLocal(addCourseDTO.getLocal());
            coursePlus.setSection(section);
            coursePlus.setCourseName(courseName);
            coursePlus.setWeek(week);
            coursePlus.setDayOfWeek(dayOfWeek);
            coursePlusQueryWrapper
                    .eq("no",no)
                    .eq("week",week).eq("day_of_week",dayOfWeek).eq("section",section);
            if (coursePlusService.count(coursePlusQueryWrapper) == 0){
                if (!coursePlusService.save(coursePlus)){
                    throw new CourseException(444,"插入数据库失败！");
                }
                coursePlusCache.invalidate(no+":"+week);
            }else {
                return R.error().message("课程有冲突");
            }
        }
        return R.ok();
    }

    @ApiOperation("删除课程")
    @DeleteMapping("/authority")
    public R deleteCourse(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                          @RequestParam @ApiParam("课程的id") String id){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        coursePlusService.removeById(id);
        for (int i=1;i<=20;i++){
            coursePlusCache.invalidate(no+":"+i);
        }
        return R.ok();
    }

    @ApiOperation("是否爬课成功")
    @GetMapping("/authority/isSuccess")
    public R isReptileCourseSuccess(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        if (redisTemplate.hasKey("courseSuc:"+no)){
            return R.ok();
        }
        return R.error().message("爬取课程失败！");
    }

}

