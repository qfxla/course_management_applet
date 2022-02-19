package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2022-02-18
 */
@Getter
@Setter
@TableName("t_course_plus")
@ApiModel(value = "CoursePlus对象", description = "")
public class CoursePlus implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("学生学号")
    private String no;

    @ApiModelProperty("课程名称")
    private String courseName;

    @ApiModelProperty("学分")
    private String score;

    @ApiModelProperty("课程属性")
    private String property;

    @ApiModelProperty("上课时间")
    private String time;

    @ApiModelProperty("上课地点")
    private String local;

    @ApiModelProperty("节次（0~5），0代表一到二节课，2仅代表第五节")
    private String section;

    @ApiModelProperty("星期几（0~6）")
    private String dayOfWeek;

    /**
     * 周次
     */
    private String week;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
