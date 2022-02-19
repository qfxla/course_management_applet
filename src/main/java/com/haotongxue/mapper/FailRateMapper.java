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

    Integer countTotal(@Param("subject") String subject,
                   @Param("property") String property,
                   @Param("majorId") String majorId,
                   @Param("term") String term);

    Integer countFail(@Param("subject") String subject,
                  @Param("property") String property,
                  @Param("majorId") String majorId,
                  @Param("term") String term);
}
