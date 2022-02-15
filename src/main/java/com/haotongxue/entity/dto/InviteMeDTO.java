package com.haotongxue.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author CTC
 * @Description
 * @Date 2022/2/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InviteMeDTO {
    private String mainerName;
    private String orgName;
    private int status;
}
