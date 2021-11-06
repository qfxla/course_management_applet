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
@TableName("t_info_classroom")
@ApiModel(value = "InfoClassroom对象", description = "")
public class InfoClassroom implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "info_id", type = IdType.ASSIGN_ID)
    private String infoId;

      @TableId(value = "classroom_id", type = IdType.ASSIGN_ID)
    private Integer classroomId;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;


}
