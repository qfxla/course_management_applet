package com.haotongxue.course_service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("t_week")
@ApiModel(value = "Week对象", description = "")
public class Week implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "week_id", type = IdType.AUTO)
    private Integer weekId;

    private LocalDateTime startTime;


}
