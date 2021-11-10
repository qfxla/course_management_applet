package com.haotongxue.quartz;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
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
import java.util.List;

/**
 * @author zcj
 * @creat 2021-11-09-19:41
 */

//定时缓存每个学生每天课程
public class TodayCourseCacheJob implements Job {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IInfoService iInfoService;
    @Resource(name = "todayCourseCache")
    private LoadingCache cache;

    //每天2点缓存今日课表，那12点到2点用的不就是昨天的课表了吗？
    //答：因为这个今日课表的接口是给消息推送用的，推送不用12点到2点这段时间
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //遍历所有的user表数据
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("is_pa",1).eq("subscribe",1);
        List<User> subs = iUserService.list(wrapper);  //所有爬完数据并且订阅课程推送的人
        for (User user : subs) {
            List<TodayCourseVo> todayCourse = iInfoService.getTodayCourse(user.getOpenid());  //应该是从缓存中获取
            cache.put("tody" + user.getOpenid(),todayCourse);
        }
    }
}
