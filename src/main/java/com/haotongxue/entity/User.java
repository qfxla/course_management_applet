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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
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
 * @since 2021-11-06
 */
@Getter
@Setter
@TableName("t_user")
@ApiModel(value = "User对象", description = "")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "openid", type = IdType.ASSIGN_ID)
    private String openid;

    @ApiModelProperty("用户统一标识符(跨公众号和小程序)")
    private String unionId;

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("用户头像")
    private String avatarUrl;

    @ApiModelProperty("性别")
    private String gender;

    @ApiModelProperty("学号")
    private String no;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("是否爬完")
    private Integer isPa;

    @ApiModelProperty("是否正在爬")
    private Integer isPaing;

    private Integer isHaizhu;

    @ApiModelProperty("是否订阅推送")
    private Integer subscribe;

    @ApiModelProperty("用户自定义推送时间")
    private Integer minutes;

    @ApiModelProperty("用户真实姓名")
    private String realName;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;

}
