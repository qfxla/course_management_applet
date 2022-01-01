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
@TableName("p_privacy_target")
@ApiModel(value = "PrivacyTarget对象", description = "")
public class PrivacyTarget implements Serializable {

    private static final long serialVersionUID = 1L;

    private String openid;

    @ApiModelProperty("隐私设置的编号")
    private Boolean privacySetting;

    @ApiModelProperty("目标openid")
    private String target;


}
