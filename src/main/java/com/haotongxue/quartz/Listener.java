package com.haotongxue.quartz;

import com.haotongxue.service.IStudentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author zcj
 * @creat 2021-11-09-12:28
 */
@Component
public class Listener implements CommandLineRunner {
    @Autowired
    private Schedule Schedule;

    @Autowired
    private IStudentStatusService studentStatusService;

    @Override
    public void run(String... args) throws Exception {
        //studentStatusService.prepareES();
        Schedule.updateWeekCourse();  //服务启动调用每周更新课表的定时器
        Schedule.insertFailedPa();
    }
}
