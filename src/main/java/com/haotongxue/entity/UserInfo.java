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
 * @since 2021-11-07
 */
@Getter
@Setter
@TableName("t_user_info")
@ApiModel(value = "UserInfo对象", description = "")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;


    private String openid;

    @TableId(value = "info_id", type = IdType.ASSIGN_ID)
    private String infoId;

      @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

      @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;


}
