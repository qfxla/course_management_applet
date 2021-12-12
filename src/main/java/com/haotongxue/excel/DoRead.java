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
        String fileName = "" + "demo.xlsx";
        DemoData demoData = new DemoData();
        EasyExcel.read(fileName,DemoData.class,new DemoDataListener(scoreService)).sheet().doRead();
    }
}
