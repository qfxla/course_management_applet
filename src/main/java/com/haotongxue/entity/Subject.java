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
@TableName("t_subject")
@ApiModel(value = "Subject对象", description = "")
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "subject_id", type = IdType.ASSIGN_ID)
    private String subjectId;

    @ApiModelProperty("科目名称")
    private String subjectName;

    @ApiModelProperty("专业id")
    private String majorId;

    private String property;

    @TableLogic
    private Integer isDeleted;


}
