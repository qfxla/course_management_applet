package com.haotongxue.service;

import com.haotongxue.entity.vo.AddCourseVo;

import java.util.List;

/**
 * @Description TODO
 * @date 2021/11/18 10:38
 */
public interface AddCourseService {

    public boolean addCourse(String openId,AddCourseVo addCourseVo);
    public boolean isConflict(String openId,List<Integer> weekList,int xingqi,int section);
}
