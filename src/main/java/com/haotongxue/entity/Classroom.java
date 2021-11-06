package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
@Getter
@Setter
@TableName("t_classroom")
@ApiModel(value = "Classroom对象", description = "")
public class Classroom implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "classromm_id", type = IdType.AUTO)
    private Integer classrommId;

    private String location;


}
