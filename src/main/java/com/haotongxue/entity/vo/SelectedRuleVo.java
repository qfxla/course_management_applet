package com.haotongxue.entity.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.boot.jackson.JsonObjectDeserializer;

import java.util.List;

/**
 * @author zcj
 * @creat 2021-12-08-9:18
 */
@Data
public class SelectedRuleVo {
    Integer collegeId;

    String bigName;

    Integer bigId;

    List<SmallKindVo> smallVo;

    String score;

    float iHave;
}
