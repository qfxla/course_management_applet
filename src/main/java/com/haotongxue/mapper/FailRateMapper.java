package com.haotongxue.mapper;

import com.haotongxue.entity.FailRate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2022-02-07
 */
public interface FailRateMapper extends BaseMapper<FailRate> {

    int countTotal(@Param("subject") String subject,
                   @Param("property") String property,
                   @Param("majorId") String majorId,
                   @Param("term") String term);

    int countFail(@Param("subject") String subject,
                  @Param("property") String property,
                  @Param("majorId") String majorId,
                  @Param("term") String term);
}
