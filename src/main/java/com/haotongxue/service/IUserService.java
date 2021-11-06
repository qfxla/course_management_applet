package com.haotongxue.service;

import com.haotongxue.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.WeChatLoginResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-11-06
 */
public interface IUserService extends IService<User> {

    WeChatLoginResponse getLoginResponse(String code);
}
