package com.haotongxue.runnable;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.entity.User;
import com.haotongxue.service.IUserService;
import com.haotongxue.service.ReptileService;
import com.haotongxue.utils.GetBeanUtil;
import com.haotongxue.utils.LoginUtils;
import com.haotongxue.utils.WebClientUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@Slf4j
public class ReptileRunnable implements Runnable{

    private WebClient webClient;

    private String no;

    private String password;

    private String currentOpenid;

    public boolean paFlag = true;

    private ReReptileRunnable reptileRunnable;

    private ReptileService reptileService = GetBeanUtil.getBean(ReptileService.class);

    private IUserService userService = GetBeanUtil.getBean(IUserService.class);

    public ReptileRunnable(WebClient webClient, String no, String password,String currentOpenid,ReReptileRunnable reptileRunnable) {
        this.webClient = webClient;
        this.no = no;
        this.password = password;
        this.currentOpenid = currentOpenid;
        this.reptileRunnable = reptileRunnable;
    }

    @SneakyThrows
    @Override
    public void run() {
//        if(!Thread.currentThread().isInterrupted()){
        Thread.currentThread().setName(currentOpenid + "===正常爬的线程");
        reptileRunnable.noticeRePa(Thread.currentThread());
        if (webClient == null){
            webClient = WebClientUtils.getWebClient();
            try {
                HtmlPage afterLogin = LoginUtils.login(webClient, no, password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            reptileService.pa(webClient,currentOpenid);
            UpdateWrapper<User> userUpdateWrapperTwo = new UpdateWrapper<>();
            userUpdateWrapperTwo.set("is_paing",0).eq("openid",currentOpenid);
            userService.update(userUpdateWrapperTwo);
        } catch (Exception e){
            log.info("爬虫失败，继续爬倒计时和选课");
        }
        userService.triggerSearchCountDown(currentOpenid,webClient);
        webClient.close();
    }
}
