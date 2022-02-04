package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class BrowsingHistoryVOList {
    private List<BrowsingHistoryVO> list = new LinkedList<>();
}
