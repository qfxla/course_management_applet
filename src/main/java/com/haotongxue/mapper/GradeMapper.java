package com.haotongxue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haotongxue.entity.Grade;
import com.haotongxue.entity.vo.AddCourseVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description TODO
 * @date 2021/11/21 12:21
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
}
