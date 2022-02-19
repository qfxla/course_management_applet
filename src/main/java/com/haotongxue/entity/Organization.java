package com.haotongxue.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;



/**
 * <p>
 *
 * </p>
 *
 * @author DJT
 * @since 2021-12-07
 */
@Data
@TableName("t_organization")
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Organization对象", description = "")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "xls_id")
    private String xlsId;

    private String name;

    private String no;

    private int ackNum;

    private int totalNum;

    private int status;

}
