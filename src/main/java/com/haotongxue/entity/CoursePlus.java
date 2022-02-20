package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
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

    @ApiModelProperty("星期几（1~6）")
    private String dayOfWeek;

    /**
     * 周次
     */
    private String week;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;


}
