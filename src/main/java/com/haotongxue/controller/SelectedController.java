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
import com.haotongxue.service.impl.InfoServiceImpl;
import com.haotongxue.utils.R;
import com.haotongxue.utils.ResultCode;
import com.haotongxue.utils.UserContext;
import com.haotongxue.utils.WhichCollege;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    private static Logger logger = LoggerFactory.getLogger(SelectedController.class);
    @Autowired
    ISelectedService iSelectedService;
    @Autowired
    SelectedMapper selectedMapper;
    @Autowired
    ICollegeBigSmallService iCollegeBigSmallService;
    @Resource(name = "loginCache")
    LoadingCache loginCache;
    @Resource(name = "selectedCache")
    LoadingCache selectedCache;



    @ApiOperation("获得选课信息")
    @GetMapping("/authority/myChoice")
//    @GetMapping("/myChoice")
    public R myChoice(@RequestParam(name = "collegeId",required = false)Integer collegeId) throws InterruptedException {
        String openid = UserContext.getCurrentOpenid();
//        String openid = "ohpVk5XeOL8loKJw1rTBvQ7C4ygI";
        User user = (User)loginCache.get(openid);
        if (Integer.valueOf(user.getNo().substring(2,4)) < 20){
            return R.error().code(ResultCode.NO_TARGET);
        }

        //如果没有传参，默认为null
        if (collegeId == null){
            collegeId = WhichCollege.getCollegeId(user.getNo());
        }

//        List<SelectedRuleVo> ruleList = (List<SelectedRuleVo>)selectedCache.get("selected:" + collegeId + ":" + openid);
        List<SelectedRuleVo> ruleList = iSelectedService.getSelected(collegeId, openid);
        if (ruleList == null || ruleList.size() == 0){
            return R.error().data("msg","暂无数据");
        }
        return R.ok().data("rule",ruleList);
    }

}

