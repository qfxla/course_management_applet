package com.haotongxue.mapper;

import com.haotongxue.entity.InfoClassroom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
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
public interface InfoClassroomMapper extends BaseMapper<InfoClassroom> {
//    @Delete("delete from t_info_classroom where info_id = #{infoId}")
    int deleteByInfoId(@Param("infoList") List<String> infoList);
}
