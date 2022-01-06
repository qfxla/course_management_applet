package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * @Description TODO
 * @date 2022/1/5 10:48
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Book {
    private String name;
    private String code;
}
