package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Getter
@Setter
@TableName("t_info")
@ApiModel(value = "Info对象", description = "")
public class Info implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("信息ID")
      @TableId(value = "info_id", type = IdType.ASSIGN_ID)
    private String infoId;

    @ApiModelProperty("星期几")
    private Integer xingqi;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;

}
