package com.haotongxue.controller;


import com.haotongxue.entity.BigKind;
import com.haotongxue.service.IBigKindService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-12-07
 */
@RestController
@RequestMapping("/zkCourse/bigKind")
public class BigKindController {
    @Resource
    IBigKindService iBigKindService;

//    @GetMapping("/insertBigKind")
//    public boolean insertBigKind() throws BiffException, IOException {
//        Workbook workbook = Workbook.getWorkbook(new File("C:/selected.xls"));
//        Sheet sheet = workbook.getSheet(0);
//        Set<String> bigSet = new HashSet<>();
//        int c = 7;
//        for (int i = 0; i < sheet.getRows(); i++) {
//            Cell cell = sheet.getCell(c,i);
//            String smallStr = cell.getContents();
//            if(smallStr == null || i == 1 || smallStr.equals("")){
//                continue;
//            }
//            bigSet.add(smallStr);
//        }
//        System.out.println(bigSet);
//        for (String bigKindStr : bigSet) {
//            BigKind bigKind = new BigKind();
//            bigKind.setName(bigKindStr);
//            boolean saveFlag = iBigKindService.save(bigKind);
//            if(!saveFlag){
//                return false;
//            }
//        }
//        return true;
//    }
}

