package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.CollegeBigSmall;
import com.haotongxue.entity.Selected;
import com.haotongxue.entity.User;
import com.haotongxue.entity.vo.SelectedRuleVo;
import com.haotongxue.entity.vo.SelectedVo;
import com.haotongxue.entity.vo.SmallKindVo;
import com.haotongxue.mapper.CollegeBigSmallMapper;
import com.haotongxue.mapper.SelectedMapper;
import com.haotongxue.service.ICollegeBigSmallService;
import com.haotongxue.service.ISelectedService;
import com.haotongxue.service.impl.InfoServiceImpl;
import com.haotongxue.utils.*;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.apache.catalina.realm.UserDatabaseRealm;
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



    @ApiOperation("获得选课信息")
    @GetMapping("/authority/myChoice")
//    @GetMapping("/myChoice")
    public R myChoice(@RequestParam(name = "collegeId",required = false)Integer collegeId) throws InterruptedException {
        String openid = UserContext.getCurrentOpenid();
//        String openid = "ohpVk5VjCMQ9IZsZzfmwruWvhXeA";  //20级
//        String openid = "ohpVk5QKsZVJG94dMLCsLeF-ETwY";  //20级 一个有无效选修的人
//        String openid = "ohpVk5a0g0sadhdZyBdftaBOG-Q4";  //19级信科
//        String openid = "ohpVk5Rh5rn6OnSQtlr75pPKXgDE";//19级轻工  专业2不在数据库中
//        String openid = "ohpVk5XJf6d8pl7uH0hieZ4OJslM";//19级轻工  专业1在数据库中
//        String openid = "ohpVk5UDTRzfNkG-iy2uoCR86VZ0";
//        String openid = "ohpVk5a7-Hye0Es2iTtuk0lnzIGI";
//        String openid = "ohpVk5VonsNaR3o9rSwJCWD3H-Zw";
        User user = (User)loginCache.get(openid);


        /*
        * 本来打算19级不行，现在可以了*/
//        if (Integer.valueOf(user.getNo().substring(2,4)) < 20){
//            return R.error().code(ResultCode.NO_TARGET);
//        }

        //如果没有传参，默认为null
        if (collegeId == null){
            collegeId = WhichCollege.getCollegeId(user.getNo());
        }

        int grade = WhichGrade.whichGrade(user.getNo());

        List<SelectedRuleVo> ruleList = iSelectedService.getSelected(collegeId, openid,grade, user.getNo());
        if (ruleList == null || ruleList.size() == 0){
            return R.error().data("msg","暂无数据");
        }


        //无效选课invalidSelected
        List<SelectedVo> invalidSelected = iSelectedService.getInvalidSelected(collegeId, openid,grade);

        return R.ok().data("rule",ruleList).data("invalidSelected",invalidSelected);
    }

}

