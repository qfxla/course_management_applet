package com.haotongxue.quartz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.entity.vo.TodayCourseVo;
import com.haotongxue.mapper.InfoMapper;
import com.haotongxue.service.IInfoService;
import com.haotongxue.service.IUserService;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zcj
 * @creat 2021-11-09-20:11
 */
public class WeekCourseCacheJob implements Job {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IInfoService iInfoService;
    @Autowired
    private InfoMapper infoMapper;
    @Resource(name = "courseCache")
    private LoadingCache cache;

    //缓存每周课表
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //遍历所有的user表数据
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("is_pa",1);
        List<User> users = iUserService.list(wrapper);  //所有爬完数据并且订阅课程推送的人
        for (User user : users) {
            Integer week = infoMapper.getWeekByToday();
            List<List> info = iInfoService.getInfo(user.getOpenid(), week);
            cache.put("cour",user.getOpenid() + ":" + week);
        }
    }
}
