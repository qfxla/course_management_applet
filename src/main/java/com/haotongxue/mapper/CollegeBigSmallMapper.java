package com.haotongxue.mapper;

import com.haotongxue.entity.CollegeBigSmall;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DJT
 * @since 2021-12-07
 */
@Mapper
public interface CollegeBigSmallMapper extends BaseMapper<CollegeBigSmall> {
    //查某个学院的无效选课
    public List<Integer> getInvalidSmallId(@Param("collegeId")Integer collegeId, @Param("grade")Integer grade);
}
