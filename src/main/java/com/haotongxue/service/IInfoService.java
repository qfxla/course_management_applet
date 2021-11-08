package com.haotongxue.service;

import com.haotongxue.entity.Info;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.vo.InfoVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface IInfoService extends IService<Info> {
    List<List> getInfo(int week) throws InterruptedException;
}
