package com.haotongxue.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.Selected;
import com.haotongxue.entity.vo.SelectedVo;
import com.haotongxue.mapper.SelectedMapper;
import com.haotongxue.service.ISelectedService;
import com.haotongxue.utils.R;
import com.haotongxue.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    ISelectedService iSelectedService;
    @Autowired
    SelectedMapper selectedMapper;

    @GetMapping("authority/myChoice")
    public R myChoice(){
        String openid = UserContext.getCurrentOpenid();
        List<SelectedVo> selectedVoList = selectedMapper.myChoice(openid);
        for (SelectedVo selectedVo : selectedVoList) {
            System.out.println(selectedVo);
        }
        return null;
    }
}

