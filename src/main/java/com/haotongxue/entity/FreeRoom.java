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
 * 空闲教室表
 * </p>
 *
 * @author DJT
 * @since 2021-11-26
 */
@Getter
@Setter
@TableName("t_free_room")
@ApiModel(value = "FreeRoom对象", description = "空闲教室表")
public class FreeRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("周次")
    private Integer week;

    @ApiModelProperty("节次")
    private Integer section;

    @ApiModelProperty("星期")
    private Integer xingqi;

    @ApiModelProperty("课室名字")
    private String name;

    @ApiModelProperty("校区")
    private String campus;

    @ApiModelProperty("教学楼")
    private String building;

    public FreeRoom(String id, Integer week, Integer section, Integer xingqi, String name, String campus, String building) {
        this.id = id;
        this.week = week;
        this.section = section;
        this.xingqi = xingqi;
        this.name = name;
        this.campus = campus;
        this.building = building;
    }

    public FreeRoom() {
    }

    @Override
    public String toString() {
        return "FreeRoom{" +
                "id='" + id + '\'' +
                ", week=" + week +
                ", section=" + section +
                ", xingqi=" + xingqi +
                ", name='" + name + '\'' +
                ", campus='" + campus + '\'' +
                ", building='" + building + '\'' +
                '}';
    }
}
