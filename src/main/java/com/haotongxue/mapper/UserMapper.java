package com.haotongxue.mapper;

import com.haotongxue.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    int deleteByInfoId(@Param("openid") String openid);
    List<String> selectZeroPa();
    List<User> selectPaing();
    String getOfOpenidByOpenid(String openId);
    List<Integer> getHasCourseWeekList(String no,int xingqi, int section);

}
