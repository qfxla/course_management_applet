package com.haotongxue.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DemoData {

    @ExcelProperty(index = 0)
    private String bigKind;

    @ExcelProperty(index = 1)
    private String smallKind;

    @ExcelProperty(index = 2)
    private String lowScore;
}
