package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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
 * @since 2021-12-06
 */
@Getter
@Setter
@TableName("t_selected")
@ApiModel(value = "Selected对象", description = "")
public class Selected implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "selected_id", type = IdType.ASSIGN_ID)
    private String selectedId;

    private String selectedName;

    private Float selectedScore;

    @TableLogic
    private Integer isDeleted;

    @Version
    private Integer version;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
