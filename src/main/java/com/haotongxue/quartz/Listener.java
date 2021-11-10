package com.haotongxue.quartz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

/**
 * @author zcj
 * @creat 2021-11-09-12:28
 */
public class Listener implements CommandLineRunner {
    @Autowired
    private Schedule Schedule;

    @Override
    public void run(String... args) throws Exception {
        Schedule.updateTodayCourse();   //服务启动调用课程推送定时器
    }
}
