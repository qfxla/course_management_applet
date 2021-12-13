package com.haotongxue.mapper;

import com.haotongxue.entity.UserSelected;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-12-06
 */
@Mapper
public interface UserSelectedMapper extends BaseMapper<UserSelected> {
    int deleteByOpenId(@Param("openId") String openId);
}
