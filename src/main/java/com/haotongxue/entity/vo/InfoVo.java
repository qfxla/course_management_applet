package com.haotongxue.entity.vo;

import lombok.Data;

/**
 * @author zcj
 * @creat 2021-11-06-19:51
 */
@Data
public class InfoVo {
    private static final long serialVersionUID = 1L;

    private String infoId;

    private String course;

    private String classroom;

    private String teacher;

    private Integer section;

    private Integer week;

    private Integer xingqi;

}
