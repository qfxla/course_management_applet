package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.*;
import com.haotongxue.entity.dto.BrowsingHistoryDTO;
import com.haotongxue.entity.dto.ConcernDTO;
import com.haotongxue.entity.dto.DetailPrivacySettingDTO;
import com.haotongxue.entity.vo.*;
import com.haotongxue.service.*;
import com.haotongxue.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2022-01-19
 */
@Api(tags = "觅他接口")
@RestController
@RequestMapping("/zkCourse/mita")
public class MiTaController {

    @Autowired
    IStudentStatusService studentStatusService;

    @Autowired
    IPrivacySettingService privacySettingService;

    @Autowired
    IBrowsingHistoryService browsingHistoryService;

    @Autowired
    IConcernService concernService;

    @Autowired
    IPrivacyTargetService targetService;

    @Resource(name = "loginCache")
    LoadingRedisCache<User> loginCache;

    @Resource(name = "classifyCache")
    LoadingRedisCache<ClassifyVO> classifyCache;

    @Resource(name = "studentStatusCache")
    LoadingRedisCache<StudentStatus> studentStatusCache;

    @Resource(name = "privacySettingCache")
    LoadingRedisCache<PrivacySetting> privacySettingCache;

    @Resource(name = "concernCache")
    LoadingRedisCache<Concern> concernCache;

    @Resource(name = "isConcernCache")
    LoadingRedisCache<Concern> isConcernCache;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    /**
     * 获取用户默认分类
     * @return
     */
    @ApiOperation("获取全部分类")
    @GetMapping("/authority/classifyMsg")
    public R getDefaultClassifyMsg(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusService.getById(currentOpenid);
        User user = loginCache.get(currentOpenid);
        String grade = GradeUtils.getGrade(user.getNo());
        int collegeId = studentStatus.getCollegeId();
        String majorId = studentStatus.getMajorId();
        String classId = studentStatus.getClassId();
        ClassifyVO classifyVO = classifyCache.get("");
        return R.ok()
                .data("grade",grade)
                .data("collegeId",collegeId)
                .data("majorId",majorId)
                .data("classId",classId)
                .data("classifyVO",classifyVO);
    }

    @ApiOperation("条件查询学生")
    @GetMapping("/authority/student")
    public R getStudent(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                        @RequestParam String grade,
                        @RequestParam String collegeId,
                        @RequestParam String majorId,
                        @RequestParam String classId,
                        @RequestParam Integer currentPage){
        List<IsConcernVO> list;
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        try {
            list = studentStatusService.getStudent(grade, collegeId, majorId, classId, currentPage,studentStatus.getNo());
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok().data("list",list);
    }

    @ApiOperation("模糊查询学生")
    @GetMapping("/authority/studentByName")
    public R getStudentByFuzzySearch(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                                     @RequestParam String content,
                                     @RequestParam Integer currentPage){
        List<IsConcernVO> list;
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        try {
            list = studentStatusService.getStudentByFuzzySearch(content,currentPage,studentStatus.getNo());
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok().data("list",list);
    }

    /**
     * 更改隐私设置
     * 1.公开 2.私密 3 只给谁看 4 不给谁看
     * @param setting
     * @return
     */
    @ApiOperation("更改隐私设置：1.公开 2.私密 3 只给谁看 4 不给谁看")
    @GetMapping("/authority/privacySetting")
    public R updatePrivacySetting(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                                  @RequestParam Integer setting){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        PrivacySetting privacySetting = new PrivacySetting();
        String no = studentStatus.getNo();
        privacySetting.setNo(no);
        privacySetting.setSetting(setting);
        if (privacySettingService.saveOrUpdate(privacySetting)){
            privacySettingCache.invalidate(no);
            return R.ok();
        }
        return R.error();
    }

    @ApiOperation("获取当前的隐私设置")
    @GetMapping("/authority/getPrivacySetting")
    public R getPrivacySetting(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        PrivacySetting privacySetting = privacySettingCache.get(studentStatus.getNo());
        Integer returnSetting;
        if (privacySetting == null){
            returnSetting = 1;
        }else {
            returnSetting = privacySetting.getSetting();
        }
        return R.ok().data("setting",returnSetting);
    }

    @ApiOperation("把某些人设为只给他们看或不给他们看")
    @PostMapping("/authority/detailPrivacySetting")
    public R detailPrivacySetting(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                                  @RequestBody DetailPrivacySettingDTO dto){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        List<String> list = dto.getList();
        int setting = dto.getSetting();
        for (String targetNo : list){
            QueryWrapper<PrivacyTarget> privacyTargetQueryWrapper = new QueryWrapper<>();
            privacyTargetQueryWrapper.eq("no",no);
            privacyTargetQueryWrapper.eq("target_no",targetNo);
            privacyTargetQueryWrapper.eq("privacy_setting",setting);
            if (targetService.count(privacyTargetQueryWrapper) == 0){
                PrivacyTarget privacyTarget = new PrivacyTarget(no,setting,targetNo);
                targetService.save(privacyTarget);
            }
        }
        return R.ok();
    }

    @ApiOperation("删除只给谁看或不给谁看")
    @DeleteMapping("/authority/detailPrivacySetting")
    public R deleteDetailPrivacySetting(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                                        @RequestParam @ApiParam("目标学号") String targetNo,
                                        @RequestParam @ApiParam("对应的隐私设置") Integer setting){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<PrivacyTarget> privacyTargetQueryWrapper = new QueryWrapper<>();
        privacyTargetQueryWrapper
                .eq("no",no)
                .eq("target_no",targetNo)
                .eq("privacy_setting",setting);
        if (targetService.remove(privacyTargetQueryWrapper)){
            return R.ok();
        }
        return R.error();
    }

    @ApiOperation("查看只给谁看或不给谁看具体的人")
    @GetMapping("/authority/detailPrivacySetting")
    public R getDetailPrivacySetting(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                                     @RequestParam @ApiParam("隐私设置") Integer setting){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<PrivacyTarget> privacyTargetQueryWrapper = new QueryWrapper<>();
        privacyTargetQueryWrapper
                .select("target_no")
                .eq("no",no)
                .eq("privacy_setting",setting);
        List<PrivacyTarget> list = targetService.list(privacyTargetQueryWrapper);
        if (list.isEmpty()){
            return R.error().message("没有数据");
        }
        String[] nos = new String[list.size()];
        for (int i=0;i<list.size();i++){
            nos[i] = list.get(i).getTargetNo();
        }
        List<ESVO> student;
        try {
            student = studentStatusService.getStudent(nos);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error().message("捕获ES异常");
        }
        return R.ok().data("list",student);
    }

    @ApiOperation("添加浏览记录")
    @PutMapping("/authority/browsingHistory")
    public R insertBrowsingHistory(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                                   @RequestBody BrowsingHistoryDTO dto){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<BrowsingHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("read_no",no)
                .eq("readed_no",dto.getNo())
                .orderByDesc("create_time")
                .last("limit 1");
        BrowsingHistory browsingHistory = browsingHistoryService.getOne(wrapper);
        if (browsingHistory != null){
            LocalDate localDate = browsingHistory.getCreateTime().toLocalDate();
            LocalDate now = LocalDate.now();
            if (now.equals(localDate)){
                return R.ok();
            }
        }
        browsingHistory = new BrowsingHistory(no,dto.getNo());
        if (browsingHistoryService.save(browsingHistory)){
            redisTemplate.opsForValue().set("visted"+no,"");
        }
        return R.ok();
    }

    @ApiOperation("是否有访客")
    @GetMapping("/authority/hasBrowsingHistory")
    public R hasBrowsingHistory(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        if (redisTemplate.hasKey("visted"+no)){
            redisTemplate.delete("visted"+no);
            return R.ok().message("有访客");
        }
        return R.error().message("没有访客");
    }

    @ApiOperation("查看我的浏览记录")
    @GetMapping("/authority/browsingHistory/mine")
    public R getMyBrowsingHistory(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<BrowsingHistory> wrapper = new QueryWrapper<>();
        wrapper.select("readed_no","create_time").eq("read_no",no).orderByDesc("create_time");
        BrowsingHistoryVOList browsingHistoryVOList =  browsingHistoryService.sliceByCreateTime(wrapper,true);
        return R.ok().data("stu",browsingHistoryVOList);
    }

    @ApiOperation("查看谁看过我")
    @GetMapping("/authority/browsingHistory/other")
    public R getWhoSeeMe(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<BrowsingHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("readed_no",no);
        BrowsingHistoryVOList browsingHistoryVOList =  browsingHistoryService.sliceByCreateTime(wrapper,false);
        return R.ok().data("stu",browsingHistoryVOList);
    }

    @ApiOperation("删除所有的浏览记录")
    @DeleteMapping("/authority/browsingHistory")
    public R deleteBrowsingHistory(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<BrowsingHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("read_no",no);
        if (browsingHistoryService.remove(wrapper)){
            R.ok();
        }
        return R.error();
    }

    @ApiOperation("查看是否有权限看课表")
    @GetMapping("/authority/hasAuthorityToSee")
    public R hasAuthorityToSee(@RequestHeader String Authority,
                               @RequestParam @ApiParam("对方的学号") String no){
        PrivacySetting privacySetting = privacySettingService.getById(no);
        Integer setting;
        if (privacySetting == null){
            setting = 1;
        }else {
            setting = privacySetting.getSetting();
        }
        boolean hasAuthority = false;
        if (setting.equals(1)){
            hasAuthority = true;
        }else if (!setting.equals(2)){
            String currentOpenid = UserContext.getCurrentOpenid();
            StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
            String targetNo = studentStatus.getNo();
            QueryWrapper<PrivacyTarget> wrapper = new QueryWrapper<>();
            if (setting.equals(3)){
                wrapper.eq("no",no).eq("target_no",targetNo).eq("privacy_setting",3);
                if (targetService.count(wrapper) != 0){
                    hasAuthority = true;
                }
            }else if (setting.equals(4)){
                wrapper.eq("no",no).eq("target_no",targetNo).eq("privacy_setting",4);
                if (targetService.count(wrapper) == 0){
                    hasAuthority = true;
                }
            }
        }
        if (hasAuthority){
            QueryWrapper<StudentStatus> studentStatusQueryWrapper = new QueryWrapper<>();
            studentStatusQueryWrapper.select("openid").eq("no",no).last("limit 1");
            StudentStatus one = studentStatusService.getOne(studentStatusQueryWrapper);
            return R.ok().data("Authority", JwtUtils.generate(one.getOpenid())).message("可以访问");
        }
        return R.ok().code(ResultCode.NO_AUTHORITY_TO_SEE).message("没有权限");
    }

    @ApiOperation("关注")
    @PutMapping("/authority/concern")
    public R concern(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                     @RequestBody ConcernDTO concernDTO){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        String concernedNo = concernDTO.getConcernedNo();
        QueryWrapper<Concern> wrapper = new QueryWrapper<>();
        wrapper.eq("no",no).eq("concerned_no",concernedNo);
        if (concernService.count(wrapper) == 0){
            Concern concern = new Concern();
            concern.setNo(no);
            concern.setConcernedNo(concernedNo);
            if (concernService.save(concern)){
                isConcernCache.put(no+concernedNo,concern);
                concernCache.invalidate(no);
                return R.ok();
            }
        }
        return R.error().message("关注失败或重复关注");
    }

    @ApiOperation("取消关注")
    @DeleteMapping("/authority/concern")
    public R cancelConcern(@RequestHeader @ApiParam("传Authority（测试用）") String Authority,
                           @RequestParam @ApiParam("对方的学号") String targetNo){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<Concern> concernQueryWrapper = new QueryWrapper<>();
        concernQueryWrapper.eq("no",no).eq("concerned_no",targetNo);
        if (concernService.remove(concernQueryWrapper)){
            isConcernCache.invalidate(no+targetNo);
            concernCache.invalidate(no);
            return R.ok();
        }
        return R.error().message("取消关注失败，可能还没关注！");
    }

    @ApiOperation("获取关注列表")
    @GetMapping("authority/concern")
    public R getConcernList(@RequestHeader @ApiParam("传Authority（测试用）") String Authority){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        List<Concern> list = concernCache.getForList(no);
        if (list.isEmpty()){
            return R.error().message("关注列表为空");
        }
        String[] nos = new String[list.size()];
        for (int i=0;i<list.size();i++){
            nos[i] = list.get(i).getConcernedNo();
        }
        List<ESVO> student;
        try {
            student = studentStatusService.getStudent(nos);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error().message("捕获到ES异常");
        }
        return R.ok().data("list",student);
    }
}

