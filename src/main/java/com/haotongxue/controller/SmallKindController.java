package com.haotongxue.controller;


import com.haotongxue.service.ISmallKindService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DJT
 * @since 2021-12-07
 */
@RestController
@RequestMapping("/smallKind")
public class SmallKindController {
    @Resource
    ISmallKindService iSmallKindService;

//    @GetMapping("/insertSmallKind")
//    public boolean insertSmallKind() throws BiffException, IOException {
//        Workbook workbook = Workbook.getWorkbook(new File("C:/selected.xls"));
//        Sheet sheet = workbook.getSheet(0);
//        Set<String> smallSet = new HashSet<>();
//        int c = 8;
//        for (int i = 0; i < sheet.getRows(); i++) {
//            Cell cell = sheet.getCell(c,i);
//            String smallStr = cell.getContents();
//            if(smallStr == null || i == 1 || smallStr.equals("")){
//                continue;
//            }
//            smallSet.add(smallStr);
//        }
//        System.out.println(smallSet);
//        for (String smallKindStr : smallSet) {
//            SmallKind smallKind = new SmallKind();
//            smallKind.setName(smallKindStr);
//            boolean saveFlag = iSmallKindService.save(smallKind);
//            if(!saveFlag){
//                return false;
//            }
//        }
//        return true;
//    }
}

