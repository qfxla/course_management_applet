package com.haotongxue.service;

import com.haotongxue.entity.Selected;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.vo.SelectedRuleVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-12-06
 */
public interface ISelectedService extends IService<Selected> {
    List<SelectedRuleVo> getSelected(int collegeId,String openid) throws InterruptedException;
}
