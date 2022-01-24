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
 * 
 * </p>
 *
 * @author DJT
 * @since 2022-01-19
 */
@Getter
@Setter
@TableName("t_privacy_setting")
@ApiModel(value = "PrivacySetting对象", description = "")
public class PrivacySetting implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "no", type = IdType.INPUT)
    private String no;

    @ApiModelProperty("1.公开 2.私密 3.部分可见 4.不给谁看")
    private Integer setting;


}
