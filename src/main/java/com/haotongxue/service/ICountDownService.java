package com.haotongxue.service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.entity.CountDown;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DJT
 * @since 2021-12-01
 */
public interface ICountDownService extends IService<CountDown> {
    void searchCountDown(String userOpenid, WebClient webClient);
}
