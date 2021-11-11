package com.haotongxue.service;

import com.haotongxue.entity.InfoWeek;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface IInfoWeekService extends IService<InfoWeek> {
    boolean insertInfoWeek(String infoId,Integer weekId);
}
