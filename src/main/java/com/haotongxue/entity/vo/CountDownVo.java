package com.haotongxue.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author zcj
 * @creat 2021-12-01-14:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class CountDownVo {
    private String id;

    private int countDownHour;

    private String name;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;
}
