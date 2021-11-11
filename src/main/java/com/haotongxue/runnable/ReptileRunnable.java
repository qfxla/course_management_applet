package com.haotongxue.runnable;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.haotongxue.service.ReptileService;
import com.haotongxue.utils.GetBeanUtil;
import com.haotongxue.utils.LoginUtils;
import com.haotongxue.utils.WebClientUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

public class ReptileRunnable implements Runnable{

    private WebClient webClient;

    private String no;

    private String password;

    private ReptileService reptileService = GetBeanUtil.getBean(ReptileService.class);

    public ReptileRunnable(WebClient webClient, String no, String password) {
        this.webClient = webClient;
        this.no = no;
        this.password = password;
    }

    @Override
    public void run() {
        if (webClient == null){
            webClient = WebClientUtils.getWebClient();
            try {
                HtmlPage afterLogin = LoginUtils.login(webClient, no, password);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reptileService.pa(webClient);
    }
}
