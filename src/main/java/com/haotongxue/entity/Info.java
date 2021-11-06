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
 * @since 2021-11-06
 */
@Getter
@Setter
@TableName("t_info")
@ApiModel(value = "Info对象", description = "")
public class Info implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("信息ID")
      @TableId(value = "info_id", type = IdType.ASSIGN_ID)
    private String infoId;

    @ApiModelProperty("课程ID")
    private Integer courseId;

    @ApiModelProperty("教室ID")
    private Integer classroomId;

    @ApiModelProperty("教师ID")
    private Integer teacherId;

    @ApiModelProperty("节次ID")
    private Integer sectionId;

    @ApiModelProperty("周次ID")
    private Integer weekId;

    @ApiModelProperty("星期几")
    private Integer xingqi;


}
