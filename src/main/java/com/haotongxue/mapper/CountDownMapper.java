package com.haotongxue.mapper;

import com.haotongxue.entity.CountDown;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-12-01
 */

@Repository
public interface CountDownMapper extends BaseMapper<CountDown> {
    List<String> getOpenIdByArg(String arg);
    int selectNum(String openId);
    int ifExist(String name,String openId);
    int deleteByOpenId(@Param("openId") String openId);
}
