package com.haotongxue.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description TODO
 * @date 2021/12/7 11:08
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RenWenCountDown {

    private String courseName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private String banJi;
}
