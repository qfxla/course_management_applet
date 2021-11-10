package com.haotongxue.mapper;

import com.haotongxue.entity.Info;
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
public interface InfoMapper extends BaseMapper<Info> {
    //获得该学生本周的info对象
    List<Info> getInfoByOpenidAndWeek(@Param("openid") String openid,
                               @Param("week") int week);

    //根据每个info查该info对应的节次
    List<Integer> getSectionByInfoId(@Param("infoId")String infoId);

    //根据每个info查该info对应的课程名
    String getCourseNameByInfoId(@Param("infoId")String infoId);

    //根据每个info查该info对应的教室
    String getClassRoomByInfoId(@Param("infoId")String infoId);

    //根据每个info查该info对应的老师列表(可能有多个)
    List<String> getTeacherListByInfoId(@Param("infoId")String infoId);


    //查今天是第几周
    Integer getWeekByToday();
}
