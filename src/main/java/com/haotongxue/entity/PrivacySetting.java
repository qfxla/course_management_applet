package com.haotongxue.entity;

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
 * @since 2022-01-01
 */
@Getter
@Setter
@TableName("p_privacy_setting")
@ApiModel(value = "PrivacySetting对象", description = "")
public class PrivacySetting implements Serializable {

    private static final long serialVersionUID = 1L;

    private String openid;

    @ApiModelProperty("1.公开 2.私密 3.部分可见 4.不给谁看")
    private Boolean setting;


}
