package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description TODO
 * @date 2021/12/31 20:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
@TableName("t_grade")
@ApiModel(value = "Grade对象", description = "考试成绩表")
public class Grade {

    @TableId(value = "id",type = IdType.AUTO)
    private int id;

    private String openid;

    private String term;

    private String subject;

    private String grade;

    private String property;

    private float score;

    private float gpa;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public Grade(String openid, String term, String subject, String grade, String property, float score, float gpa) {
        this.openid = openid;
        this.term = term;
        this.subject = subject;
        this.grade = grade;
        this.property = property;
        this.score = score;
        this.gpa = gpa;
    }
}

