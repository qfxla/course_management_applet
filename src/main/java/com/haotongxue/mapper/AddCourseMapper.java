package com.haotongxue.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haotongxue.entity.Classroom;
import com.haotongxue.entity.vo.AddCourseVo;

/**
 * @Description TODO
 * @date 2021/11/21 12:21
 */
public interface AddCourseMapper extends BaseMapper<AddCourseVo> {
    int isConflict(String openid,int week,int xingqi,int section);
}
