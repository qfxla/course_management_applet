package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2022-01-01
 */
@Getter
@Setter
@TableName("p_major")
@ApiModel(value = "Major对象", description = "")
public class Major implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("专业编号")
      @TableId(value = "major", type = IdType.ASSIGN_ID)
    private Integer major;

    @ApiModelProperty("学院")
    private String college;

    @ApiModelProperty("专业名称")
    private String majorName;


}
