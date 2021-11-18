package com.haotongxue.mapper;

import com.haotongxue.entity.InfoSection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Mapper
public interface InfoSectionMapper extends BaseMapper<InfoSection> {
    @Delete("delete from t_info_section where info_id = #{infoId}")
    int deleteByInfoId(@Param("infoId") String infoId);
}
