package com.haotongxue.service;

import com.haotongxue.entity.FailRate;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.vo.ESVO;

import java.io.IOException;
import java.util.List;

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

    List<ESVO> getSubjectFail(String collegeId, String majorId, String subjectId, Integer currentPage) throws IOException;

    void prepareES();
}
