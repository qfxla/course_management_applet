package com.haotongxue.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zcj
 * @creat 2021-11-09-12:04
 */
@Component
public class Schedule {
    StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();

    //缓存每个学生的今日课表，便于推送
    protected void updateTodayCourse() throws SchedulerException {
        Scheduler scheduler = stdSchedulerFactory.getScheduler();
        //创建JobDetail实例，并与SubscribeCourseSchedule类绑定
        JobDetail jobDetail = JobBuilder.newJob(TodayCourseCacheJob.class)
                .withIdentity("getTodayCourse","group1").build();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "trigger1")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?"))
                .build();

        scheduler.scheduleJob(jobDetail, cronTrigger);
    }


    //每周更新每个学生的每周课表
    protected void updateWeekCourse() throws SchedulerException {
        Scheduler scheduler = stdSchedulerFactory.getScheduler();
        //创建JobDetail实例，并与SubscribeCourseSchedule类绑定
        JobDetail jobDetail = JobBuilder.newJob(WeekCourseCacheJob.class)
                .withIdentity("getWeekCourseCache","group2").build();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("trigger2", "trigger2")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 ? * MON"))
                .build();

        scheduler.scheduleJob(jobDetail, cronTrigger);
    }
}
