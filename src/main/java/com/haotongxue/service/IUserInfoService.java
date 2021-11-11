package com.haotongxue.service;

import com.haotongxue.entity.Info;
import com.haotongxue.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-07
 */
public interface IUserInfoService extends IService<UserInfo> {
    boolean insertUserInfo(String openId,String infoId);
}
