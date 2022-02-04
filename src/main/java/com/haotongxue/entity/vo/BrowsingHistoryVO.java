package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class BrowsingHistoryVO {
    private String date;
    private List<ESVO> esvoList;
}
