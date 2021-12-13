package com.haotongxue.excel;

import com.alibaba.excel.EasyExcel;
import com.haotongxue.service.IScoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DoRead implements CommandLineRunner {

    @Autowired
    IScoreService scoreService;

    @Override
    public void run(String... args) throws Exception {
//        for (int collegeId = 1;collegeId <= 16;collegeId++){
//            log.info("第"+collegeId+"个学院----->");
//            String fileName = "D:\\跳蚤小程序\\选课资料\\各个学院选修excel\\16版\\" +collegeId+".xlsx";
//            EasyExcel.read(fileName,DemoData.class,new DemoDataListener(scoreService,collegeId,16)).sheet().doRead();
//        }
    }
}
