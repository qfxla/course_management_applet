package com.haotongxue.excel;

import com.alibaba.excel.EasyExcel;
import com.haotongxue.service.IScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DoRead implements CommandLineRunner {

    @Autowired
    IScoreService scoreService;

    @Override
    public void run(String... args) throws Exception {
//        int collegeId = 13;
//        String fileName = "D:\\跳蚤小程序\\选课资料\\各个学院选修excel\\16版\\" +collegeId+".xlsx";
//        EasyExcel.read(fileName,DemoData.class,new DemoDataListener(scoreService,collegeId,16)).sheet().doRead();
    }
}
