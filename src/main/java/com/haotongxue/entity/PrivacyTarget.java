package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
@TableName("t_privacy_target")
@ApiModel(value = "PrivacyTarget对象", description = "")
public class PrivacyTarget implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    private String no;

    @ApiModelProperty("隐私设置的编号")
    private Integer privacySetting;

    @ApiModelProperty("目标no")
    private String targetNo;

    @TableLogic
    private Integer isDeleted;

    public PrivacyTarget() {
    }

    public PrivacyTarget(String no, Integer privacySetting, String targetNo) {
        this.no = no;
        this.privacySetting = privacySetting;
        this.targetNo = targetNo;
    }
}
