package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author CTC
 * @since 2021-12-10
 */
@Getter
@Setter
@TableName("t_failed_user")
public class FailedUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "openid", type = IdType.ASSIGN_ID)
    private String openid;

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("学号")
    private String no;

    @ApiModelProperty("密码")
    private String password;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;


}
