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
 * @since 2021-12-06
 */
@Getter
@Setter
@TableName("t_big_small_kind")
@ApiModel(value = "BigSmallKind对象", description = "")
public class BigSmallKind implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("大类id")
    private Integer bigId;

    @ApiModelProperty("小类id")
    private Integer samllId;

    @ApiModelProperty("逻辑删除")
    private Boolean idDeleted;


}
