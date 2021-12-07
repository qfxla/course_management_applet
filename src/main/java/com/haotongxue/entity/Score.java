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
 * @since 2021-12-07
 */
@Getter
@Setter
@TableName("t_score")
@ApiModel(value = "Score对象", description = "")
public class Score implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("学院id")
    private Integer collegeId;

    @ApiModelProperty("大类id")
    private Integer bigId;

    @ApiModelProperty("分数")
    private String score;


}
