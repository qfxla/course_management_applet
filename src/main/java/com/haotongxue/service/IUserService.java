package com.haotongxue.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.haotongxue.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haotongxue.entity.WeChatLoginResponse;
import com.haotongxue.utils.R;

import java.util.concurrent.ExecutorService;

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

    void triggerSearchCountDown(String currentOpenid, WebClient webClient);

    boolean studentEvaluate(WebClient webClient);
}
