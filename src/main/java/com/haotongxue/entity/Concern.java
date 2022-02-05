package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
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
 * @since 2022-02-05
 */
@Getter
@Setter
@TableName("t_concern")
@ApiModel(value = "Concern对象", description = "")
public class Concern implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "concern_id", type = IdType.ASSIGN_ID)
    private String concernId;

    @ApiModelProperty("关注人学号")
    private String no;

    @ApiModelProperty("被关注人学号")
    private String concernedNo;

    @TableLogic
    private Integer isDeleted;


}
