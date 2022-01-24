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
 * @since 2022-01-19
 */
@Getter
@Setter
@TableName("t_class")
@ApiModel(value = "Class对象", description = "")
public class Class implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "class_id", type = IdType.ASSIGN_ID)
    private String classId;

    private String name;

    private String majorId;


}
