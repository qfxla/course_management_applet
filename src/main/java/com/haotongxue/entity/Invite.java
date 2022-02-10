package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @Author CTC
 * @Description
 * @Date 2022/2/10
 */

@TableName("t_invite")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Invite {

    private static final long serialVersionUID = 1L;

    @TableId(value = "invite_id", type = IdType.AUTO)
    private Integer inviteId;

    private String xlsId;

    private String no;

    private Integer status;

}
