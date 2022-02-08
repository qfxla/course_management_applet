package com.haotongxue.mapper;

import com.haotongxue.entity.FailRate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2022-02-07
 */
public interface FailRateMapper extends BaseMapper<FailRate> {

    int countTotal(String subject, String property, String term);
}
