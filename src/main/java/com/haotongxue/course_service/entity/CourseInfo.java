package com.haotongxue.course_service.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
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
 * @since 2021-11-06
 */
@Getter
@Setter
@TableName("t_course_info")
@ApiModel(value = "CourseInfo对象", description = "")
public class CourseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "course_id", type = IdType.ASSIGN_ID)
    private String courseId;

    private String infoId;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;


}
