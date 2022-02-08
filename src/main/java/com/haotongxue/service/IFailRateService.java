package com.haotongxue.service;

import com.haotongxue.entity.FailRate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2022-02-07
 */
public interface IFailRateService extends IService<FailRate> {

    /**
     * 更新挂科率
     */
    void refreshRate();
}
