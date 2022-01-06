package com.haotongxue.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @Description TODO
 * @date 2022/1/5 9:56
 */

@AllArgsConstructor
@ToString
@Data
public class JsonResult {
    Integer code;
    String msg;
    Map<String, Object> map;

}
