package com.haotongxue.quartz;

import com.haotongxue.sentinel.RunNacosDataSource;
import com.haotongxue.service.IFailRateService;
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

    @Autowired
    private IFailRateService failRateService;

    @Override
    public void run(String... args) throws Exception {
        //studentStatusService.prepareES();
        //failRateService.prepareES();
        Schedule.insertFailedPa();
    }
}