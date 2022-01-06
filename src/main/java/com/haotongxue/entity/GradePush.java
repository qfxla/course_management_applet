package com.haotongxue.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @Description TODO
 * @date 2022/1/2 12:40
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class GradePush {
    private String ofOpenid;
    private String subject;
    private String property;
}
