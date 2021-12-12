package com.haotongxue.service;

import com.haotongxue.entity.Score;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.excel.DemoData;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-12-07
 */
public interface IScoreService extends IService<Score> {

    void saveExcel(List<DemoData> cachedDataList, int collegeId,int grade);
}
