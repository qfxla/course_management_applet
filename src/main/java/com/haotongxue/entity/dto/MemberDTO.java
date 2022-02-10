package com.haotongxue.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @Author CTC
 * @Description
 * @Date 2022/2/9
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class MemberDTO {
    private String realName;
    private String[][] hasCourseArr;
}
