package com.haotongxue.entity.vo;

import lombok.Data;

/**
 * 每个科目对应某个学期的挂科率
 */
@Data
public class FailRateInTermVO {
    String term;
    Integer totalCount;
    Integer failCount;
    Double failRate;
}
