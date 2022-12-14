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
    String infoId;
    String name;
    String classRoom;
    String teacher;
    String weekStr;
    String sectionStr;

    public CourseVo() {
    }

    public CourseVo(String infoId,String name, String classRoom, String teacher, String weekStr, String sectionStr) {
        this.infoId = infoId;
        this.name = name;
        this.classRoom = classRoom;
        this.teacher = teacher;
        this.weekStr = weekStr;
        this.sectionStr = sectionStr;
    }

    @Override
    public String toString() {
        return "CourseVo{" +
                "infoId='" + infoId +
                "name='" + name + '\'' +
                ", classRoom='" + classRoom + '\'' +
                ", teacher='" + teacher + '\'' +
                ", weekStr='" + weekStr + '\'' +
                ", sectionStr='" + sectionStr + '\'' +
                '}';
    }
}
