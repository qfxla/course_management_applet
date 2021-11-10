package com.haotongxue.quartz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.entity.vo.TodayCourseVo;
import com.haotongxue.service.IInfoService;
import com.haotongxue.service.IUserService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zcj
 * @creat 2021-11-09-11:03
 */

//用于每节课推送消息
public class SubscribeCourseJob implements Job {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IInfoService iInfoService;
    @Resource(name = "todayCourseCache")
    private LoadingCache cache;

    int section; //节次
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //遍历所有的user表数据
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("is_pa",1).eq("subscribe",1);
        List<User> subs = iUserService.list(wrapper);  //所有爬完数据并且订阅课程推送的人
        for (User user : subs) {

            List<TodayCourseVo> todayCourse = (List<TodayCourseVo>)cache.get("tody" + user.getOpenid());//从缓存中获取每个人的今日课表

            for (TodayCourseVo todayCourseVo : todayCourse) {
                boolean push = false;
                for (Integer section : todayCourseVo.getSections()) {
                    if (section == this.section){
                        push = true;
                        break;
                    }
                }
                if (push){
                    //TODO 推送消息
                    break;
                }
            }
        }
    }

    public void setSection(int section) {
        this.section = section;
    }

}
