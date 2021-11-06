package com.haotongxue.course_service.service;

import com.haotongxue.course_service.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.course_service.entity.WeChatLoginResponse;
import com.haotongxue.course_service.entity.dto.QuicklyWeChatLoginDTO;

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

    String quicklyLogin(QuicklyWeChatLoginDTO quicklyWeChatLoginDTO);
}
