package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <p>
 * 
 * </p>
 *
 * @author DJT
 * @since 2021-12-01
 */
@Getter
@Setter
@TableName("t_count_down")
@ApiModel(value = "CountDown对象", description = "")
public class CountDown implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
      @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("openid")
    private String openid;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty("地点")
    private String location;

    @ApiModelProperty("逻辑删除")
    @TableLogic
    private Boolean isDeleted;

    @ApiModelProperty("创建时间")
      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Override
    public String toString() {
        return "CountDown{" +
                "id='" + id + '\'' +
                ", openid='" + openid + '\'' +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", isDeleted=" + isDeleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
