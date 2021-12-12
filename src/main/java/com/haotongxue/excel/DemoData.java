package com.haotongxue.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DemoData {

    @ExcelProperty("课程类别")
    private String bigKind;

    @ExcelProperty("课程模块")
    private String smallKind;

    @ExcelProperty("最低应修学分")
    private String lowScore;
}
