package com.haotongxue.mapper;

import com.haotongxue.entity.Selected;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haotongxue.entity.vo.SelectedVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.ParameterMetaData;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-12-06
 */
@Mapper
public interface SelectedMapper extends BaseMapper<Selected> {
    List<SelectedVo> myChoice(@Param("openid")String openid);
}
