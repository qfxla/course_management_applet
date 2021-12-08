package com.haotongxue.entity.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zcj
 * @creat 2021-12-07-14:43
 */
@Data
public class SelectedVo {
    private static final long serialVersionUID = 1L;

    public String selectedId;

    public String selectedName;

    public Float selectedScore;

    public String bigName;

    public Integer bigId;

    public String smallName;

    public Integer smallId;
}
