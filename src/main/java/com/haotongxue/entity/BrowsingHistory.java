package com.haotongxue.entity;

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
 * @since 2022-01-21
 */
@Getter
@Setter
@TableName("t_browsing_history")
@ApiModel(value = "BrowsingHistory对象", description = "")
public class BrowsingHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String readNo;

    private String readedNo;

    @TableLogic
    private Integer isDeleted;

    public BrowsingHistory() {
    }

    public BrowsingHistory(String readNo, String readedNo) {
        this.readNo = readNo;
        this.readedNo = readedNo;
    }
}
