package com.haotongxue.service;

import com.haotongxue.entity.Info;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.vo.InfoVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface IInfoService extends IService<Info> {
    //首页课表展示
    List<List> getInfo(String openId,int week);


    //获得今天的课表（02点调用存入缓存），便于消息推送
    List getTodayCourse(String openId);   //map的key为一次课的第一小节，value为课程详细信息

    //插入课程信息
    String addCourseInfo(int week,String weekStr,String sectionStr);

    //插入周次和节次的字符串
    int insertInfo(Info info);

    //重新获取课程表数据
    boolean updateCourseData();
}
