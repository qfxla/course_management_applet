package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

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

