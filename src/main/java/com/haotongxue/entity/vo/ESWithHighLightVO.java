package com.haotongxue.entity.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ESWithHighLightVO {
    Map<String,Object> source;
    Map<String, String> highLight;
}
