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

    protected void insertFailedPa() throws SchedulerException {
        Scheduler scheduler = stdSchedulerFactory.getScheduler();
        JobDetail jobDetail = JobBuilder.newJob(FailedPaJob.class).withIdentity("paFailed","paFailedGroup").build();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("failedPaTrigger","failedPaTrigger")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 3 * * ?"))
                .build();
        scheduler.scheduleJob(jobDetail,cronTrigger);
    }
}
