package com.haotongxue.utils;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

public class WebClientUtils {
    public static WebClient getWebClient(){
        WebClient webClient = new WebClient();
        //配置webClient
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.waitForBackgroundJavaScript(3*1000);
        webClient.getCookieManager().setCookiesEnabled(true);
        return webClient;
    }
}
