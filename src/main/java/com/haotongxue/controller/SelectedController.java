package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.CollegeBigSmall;
import com.haotongxue.entity.Selected;
import com.haotongxue.entity.User;
import com.haotongxue.entity.vo.SelectedRuleVo;
import com.haotongxue.entity.vo.SelectedVo;
import com.haotongxue.entity.vo.SmallKindVo;
import com.haotongxue.mapper.SelectedMapper;
import com.haotongxue.service.ICollegeBigSmallService;
import com.haotongxue.service.ISelectedService;
import com.haotongxue.utils.R;
import com.haotongxue.utils.ResultCode;
import com.haotongxue.utils.UserContext;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-12-06
 */
@RestController
@RequestMapping("/selected")
public class SelectedController {
    @Autowired
    ISelectedService iSelectedService;
    @Autowired
    SelectedMapper selectedMapper;
    @Autowired
    ICollegeBigSmallService iCollegeBigSmallService;
    @Resource(name = "loginCache")
    LoadingCache loginCache;

    @ApiOperation("获得该学院的规则及自己的得分")
    @GetMapping("/authority/myChoice")
    public R myChoice(@RequestParam("collegeId")int collegeId){
        //这个人的选课
        String openid = UserContext.getCurrentOpenid();
//        String openid = "ohpVk5VjCMQ9IZsZzfmwruWvhXeA";
        User user = (User)loginCache.get(openid);
        if (Integer.valueOf(user.getNo().substring(2,4)) < 20){
            return R.error().code(ResultCode.NO_TARGET);
        }

        List<SelectedVo> selectedVoList = selectedMapper.myChoice(openid);
        List<SelectedRuleVo> ruleList = selectedMapper.rule(collegeId);
        for (SelectedRuleVo rule : ruleList) {
            List<SmallKindVo> smallKindVos = selectedMapper.ruleSmallKind(rule.getCollegeId(), rule.getBigId());
            rule.setSmallVo(smallKindVos);

            int iHave = 0;
            //根据小类判断自己的所选科目是不是归在这里面的,是的话这个大类的得分就加
            for (SmallKindVo smallKindVo : smallKindVos) {
                for (SelectedVo selectedVo : selectedVoList) {
                    if (smallKindVo.getSmallId() == selectedVo.getSmallId()){
                        iHave += selectedVo.getSelectedScore();
                    }
                }
            }
            rule.setIHave(iHave);
        }

        if (ruleList.size() == 0){
            return R.error().data("msg","暂无数据");
        }
        return R.ok().data("rule",ruleList);
    }



    @ApiOperation("根据大类获得自己的选课")
    @GetMapping("/authority/getSelectedByBigId")
    public R getSelectedByBigId(@Param("bigId")int bigId,@Param("collegeId")int collegeId){
        String openid = UserContext.getCurrentOpenid();
//        String openid = "ohpVk5VjCMQ9IZsZzfmwruWvhXeA";

        User user = (User)loginCache.get(openid);
        if (Integer.valueOf(user.getNo().substring(2,4)) < 20){
            return R.error().code(ResultCode.NO_TARGET);
        }

        List<SelectedVo> myChoiceList = selectedMapper.myChoice(openid);
        if (myChoiceList.size() == 0){
            return R.error().data("msg","暂无数据");
        }
        QueryWrapper<CollegeBigSmall> wrapper = new QueryWrapper<>();
        wrapper.select("small_id").eq("big_id",bigId).eq("college_id",collegeId);
        List<CollegeBigSmall> needSmallId = iCollegeBigSmallService.list(wrapper);
        List<SelectedVo> sameSelectedVo = new ArrayList<>();
        for (CollegeBigSmall collegeBigSmall : needSmallId) {
            for (SelectedVo myChoice : myChoiceList) {
                if (collegeBigSmall.getSmallId() == myChoice.getSmallId()){
                    sameSelectedVo.add(myChoice);
                }
            }
        }

        return R.ok().data("msg",sameSelectedVo);
    }
}

