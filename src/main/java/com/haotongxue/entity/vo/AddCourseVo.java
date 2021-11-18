package com.haotongxue.entity.vo;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author CTC
 * @Description TODO
 * @date 2021/11/18 10:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Accessors(chain = true)
public class AddCourseVo {

    private String courseName;

    private String classRoom;

    private String teacherName;

    private int section;

    private List<Integer> weekList;

    private int xingqi;
}
