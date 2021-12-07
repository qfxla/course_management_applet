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

    private String selectedId;

    private String selectedName;

    private Float selectedScore;

    private String bigName;

    private String smallName;

}
