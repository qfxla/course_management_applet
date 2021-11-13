package com.haotongxue.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zcj
 * @creat 2021-11-13-13:32
 */
@Data
@Accessors(chain = true)
public class CourseVo {

    private static final long serialVersionUID = 1L;
    String name;
    String classRoom;
    String teacher;

    public CourseVo() {
    }

    public CourseVo(String name, String classRoom, String teacher) {
        this.name = name;
        this.classRoom = classRoom;
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "CourseVo{" +
                "name='" + name + '\'' +
                ", classRoom='" + classRoom + '\'' +
                ", teacher='" + teacher + '\'' +
                '}';
    }
}
