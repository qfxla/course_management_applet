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
 * @since 2021-12-06
 */
@Getter
@Setter
@TableName("t_small_kind")
@ApiModel(value = "SmallKind对象", description = "")
public class SmallKind implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "samll_id", type = IdType.AUTO)
    private Integer samllId;

    @ApiModelProperty("小类名")
    private String name;

    @ApiModelProperty("大类id")
    private Integer bigId;

    @ApiModelProperty("逻辑删除")
    @TableLogic
    private Boolean isDeleted;


}
