package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zcj
 * @creat 2021-12-08-16:33
 */
@Data
public class SmallKindVo {
    Integer smallId;

    String smallName;

    List<SelectedVo> selectedVoList;
}
