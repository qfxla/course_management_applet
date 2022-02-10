package com.haotongxue.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.entity.BrowsingHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.vo.BrowsingHistoryVO;
import com.haotongxue.entity.vo.BrowsingHistoryVOList;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2022-01-21
 */
public interface IBrowsingHistoryService extends IService<BrowsingHistory> {

    /**
     *
     * @param wrapper wrapper需要设置create_time降序排序
     * @return
     */
    BrowsingHistoryVOList sliceByCreateTime(QueryWrapper<BrowsingHistory> wrapper,boolean isMyRead);
}
