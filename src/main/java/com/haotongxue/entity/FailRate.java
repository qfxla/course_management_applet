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
 * @since 2022-02-07
 */
@Getter
@Setter
@TableName("t_fail_rate")
@ApiModel(value = "FailRate对象", description = "")
public class FailRate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "fail_id", type = IdType.ASSIGN_ID)
    private String failId;

    private String term;

    @ApiModelProperty("科目id")
    private String subjectId;

    @ApiModelProperty("参考人数")
    private Integer totalCount;

    @ApiModelProperty("挂科人数")
    private Integer failCount;

    @ApiModelProperty("挂科率")
    private String failRate;

    @TableLogic
    private Integer isDeleted;


}
