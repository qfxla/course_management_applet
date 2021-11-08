package com.haotongxue.mapper;

import com.haotongxue.entity.Info;
import com.haotongxue.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-11-07
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {


}
