package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.entity.*;
import com.haotongxue.entity.dto.BrowsingHistoryDTO;
import com.haotongxue.entity.dto.DetailPrivacySettingDTO;
import com.haotongxue.entity.vo.ClassifyVO;
import com.haotongxue.service.IBrowsingHistoryService;
import com.haotongxue.service.IPrivacySettingService;
import com.haotongxue.service.IPrivacyTargetService;
import com.haotongxue.service.IStudentStatusService;
import com.haotongxue.utils.GradeUtils;
import com.haotongxue.utils.R;
import com.haotongxue.utils.ResultCode;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
 * @since 2022-01-19
 */
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
    IPrivacyTargetService targetService;

    @Resource(name = "loginCache")
    LoadingRedisCache<User> loginCache;

    @Resource(name = "classifyCache")
    LoadingRedisCache<ClassifyVO> classifyCache;

    @Resource(name = "studentStatusCache")
    LoadingRedisCache<StudentStatus> studentStatusCache;


    /**
     * 获取用户默认分类
     * @return
     */
    @GetMapping("/authority/classifyMsg")
    public R getDefaultClassifyMsg(){
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

    @GetMapping("/student")
    public R getStudent(@RequestParam String grade,
                        @RequestParam String collegeId,
                        @RequestParam String majorId,
                        @RequestParam String classId){
        SearchHit[] hits;
        try {
            hits = studentStatusService.getStudent(grade, collegeId, majorId, classId);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok().data("list",hits);
    }

    @GetMapping("/studentByName")
    public R getStudentByFuzzySearch(@RequestParam String content){
        SearchHit[] hits;
        try {
            hits = studentStatusService.getStudentByFuzzySearch(content);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok().data("list",hits);
    }

    /**
     * 更改隐私设置
     * 1.公开 2.私密 3 只给谁看 4 不给谁看
     * @param setting
     * @return
     */
    @GetMapping("/authority/privacySetting")
    public R updatePrivacySetting(@RequestParam Integer setting){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        PrivacySetting privacySetting = new PrivacySetting();
        privacySetting.setNo(studentStatus.getNo());
        privacySetting.setSetting(setting);
        if (privacySettingService.saveOrUpdate(privacySetting)){
            return R.ok();
        }
        return R.error();
    }

    @ApiOperation("把某些人设为只给他们看或不给他们看")
    @PostMapping("/authority/detailPrivacySetting")
    public R detailPrivacySetting(@RequestBody DetailPrivacySettingDTO dto){
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
    public R deleteDetailPrivacySetting(@RequestParam @ApiParam("id") Integer id){
        if (targetService.removeById(id)){
            return R.ok();
        }
        return R.error();
    }

    @ApiOperation("查看只给谁看或不给谁看具体的人")
    @GetMapping("/authority/detailPrivacySetting")
    public R getDetailPrivacySetting(@RequestParam @ApiParam("隐私设置") Integer setting){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<PrivacyTarget> privacyTargetQueryWrapper = new QueryWrapper<>();
        privacyTargetQueryWrapper.eq("no",no);
        privacyTargetQueryWrapper.eq("privacy_setting",setting);
        List<PrivacyTarget> list = targetService.list(privacyTargetQueryWrapper);
        return R.ok().data("list",list);
    }

    @ApiOperation("添加浏览记录")
    @PutMapping("/authority/browsingHistory")
    public R insertBrowsingHistory(@RequestBody BrowsingHistoryDTO dto){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<BrowsingHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("read_no",no).eq("readed_no",dto.getNo());
        if (browsingHistoryService.count(wrapper) == 0){
            BrowsingHistory browsingHistory = new BrowsingHistory(no,dto.getNo());
            browsingHistoryService.save(browsingHistory);
        }
        return R.ok();
    }

    @ApiOperation("查看我的浏览记录")
    @GetMapping("/authority/browsingHistory/mine")
    public R getMyBrowsingHistory(){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<BrowsingHistory> wrapper = new QueryWrapper<>();
        wrapper.select("readed_no").eq("read_no",no);
        List<BrowsingHistory> list = browsingHistoryService.list(wrapper);
        String[] nos = new String[list.size()];
        for (int i=0;i<list.size();i++){
            nos[i] = list.get(i).getReadedNo();
        }
        SearchHit[] students;
        try {
            students = studentStatusService.getStudent(nos);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok().data("stu",students);
    }

    @ApiOperation("查看谁看过我")
    @GetMapping("/authority/browsingHistory/other")
    public R getWhoSeeMe(){
        String currentOpenid = UserContext.getCurrentOpenid();
        StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
        String no = studentStatus.getNo();
        QueryWrapper<BrowsingHistory> wrapper = new QueryWrapper<>();
        wrapper.select("read_no").eq("readed_no",no);
        List<BrowsingHistory> list = browsingHistoryService.list(wrapper);
        String[] nos = new String[list.size()];
        for (int i=0;i<list.size();i++){
            nos[i] = list.get(i).getReadedNo();
        }
        SearchHit[] students;
        try {
            students = studentStatusService.getStudent(nos);
        } catch (IOException e) {
            e.printStackTrace();
            return R.error();
        }
        return R.ok().data("stu",students);
    }

    @ApiOperation("删除所有的浏览记录")
    @DeleteMapping("/authority/browsingHistory")
    public R deleteBrowsingHistory(){
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
    public R hasAuthorityToSee(@RequestParam @ApiParam("对方的学号") Integer no){
        PrivacySetting privacySetting = privacySettingService.getById(no);
        Integer setting = privacySetting.getSetting();
        if (!setting.equals(2)){
            String currentOpenid = UserContext.getCurrentOpenid();
            StudentStatus studentStatus = studentStatusCache.get(currentOpenid);
            String targetNo = studentStatus.getNo();
            QueryWrapper<PrivacyTarget> wrapper = new QueryWrapper<>();
            if (setting.equals(3)){
                wrapper.eq("no",no).eq("target_no",targetNo).eq("privacy_setting",3);
                if (targetService.count(wrapper) != 0){
                    return R.ok();
                }
            }else if (setting.equals(4)){
                wrapper.eq("no",no).eq("target_no",targetNo).eq("privacy_setting",4);
                if (targetService.count(wrapper) == 0){
                    return R.ok();
                }
            }else {
                return R.ok();
            }
        }
        return R.ok().code(ResultCode.NO_AUTHORITY_TO_SEE);
    }
}

