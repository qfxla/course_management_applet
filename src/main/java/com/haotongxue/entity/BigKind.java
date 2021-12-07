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
 * @since 2021-12-07
 */
@Getter
@Setter
@TableName("t_big_kind")
@ApiModel(value = "BigKind对象", description = "")
public class BigKind implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "big_id", type = IdType.AUTO)
    private Integer bigId;

    @ApiModelProperty("大类名")
    private String name;

    @ApiModelProperty("逻辑删除")
    @TableLogic
    private Boolean isDeleted;



}