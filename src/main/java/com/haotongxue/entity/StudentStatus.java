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
@TableName("t_student_status")
@ApiModel(value = "StudentStatus对象", description = "")
public class StudentStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "openid", type = IdType.INPUT)
    private String openid;

    private Integer collegeId;

    private String classId;

    private String majorId;

    private String name;

    private String sex;

    @ApiModelProperty("学号")
    private String no;


}
