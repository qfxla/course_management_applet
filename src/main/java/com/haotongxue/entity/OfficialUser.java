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
 * @since 2021-11-10
 */
@Getter
@Setter
@TableName("t_official_user")
@ApiModel(value = "OfficialUser对象", description = "")
public class OfficialUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户的标识，对当前公众号唯一")
      @TableId(value = "openid", type = IdType.ASSIGN_ID)
    private String openid;

    @ApiModelProperty("用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。")
    private String subscribe;

    @ApiModelProperty("用户的昵称")
    private String nickname;

    @ApiModelProperty("用户的性别，值为1时是男性，值为2时是女性，值为0时是未知")
    private String sex;

    @ApiModelProperty("用户所在城市")
    private String city;

    @ApiModelProperty("用户所在国家")
    private String country;

    @ApiModelProperty("用户所在省份")
    private String province;

    @ApiModelProperty("用户的语言，简体中文为zh_CN")
    private String language;

    @ApiModelProperty("用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。")
    private String headimgurl;

    @ApiModelProperty("用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间")
    private LocalDateTime subscribeTime;

    @ApiModelProperty("只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。")
    private String unionid;

    @ApiModelProperty("公众号运营者对粉丝的备注，公众号运营者可在微信公众平台用户管理界面对粉丝添加备注")
    private String remark;

    @ApiModelProperty("用户所在的分组ID（兼容旧的用户分组接口）")
    private String groupid;

    @ApiModelProperty("用户被打上的标签ID列表")
    private String tagidList;

    @ApiModelProperty("返回用户关注的渠道来源，ADD_SCENE_SEARCH 公众号搜索，ADD_SCENE_ACCOUNT_MIGRATION 公众号迁移，ADD_SCENE_PROFILE_CARD 名片分享，ADD_SCENE_QR_CODE 扫描二维码，ADD_SCENE_PROFILE_LINK 图文页内名称点击，ADD_SCENE_PROFILE_ITEM 图文页右上角菜单，ADD_SCENE_PAID 支付后关注，ADD_SCENE_WECHAT_ADVERTISEMENT 微信广告，ADD_SCENE_OTHERS 其他")
    private String subscribeScene;

    @ApiModelProperty("二维码扫码场景（开发者自定义）")
    private String qrScene;

    @ApiModelProperty("二维码扫码场景描述（开发者自定义）")
    private String qrSceneStr;

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
