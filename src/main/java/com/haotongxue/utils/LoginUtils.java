package com.haotongxue.utils;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;

public class LoginUtils {
    public static HtmlPage login(WebClient webClient,String no,String psw) throws IOException {
        //执行表单提交
        HtmlPage page = webClient.getPage("http://edu-admin.zhku.edu.cn");
        HtmlForm loginForm = page.getHtmlElementById("loginForm");
        HtmlInput username = loginForm.getInputByName("username");
        username.setValueAttribute(no);
        HtmlInput password = loginForm.getInputByName("password");
        password.setValueAttribute(psw);
        HtmlElement submit = page.getHtmlElementById("submit");
        HtmlPage afterClick = submit.click();
        webClient.waitForBackgroundJavaScript(1000);
        try {
            afterClick.getHtmlElementById("loginForm");
            //如果没有找到元素则抛出异常（证明loginForm1不是登录页，账号和密码正确）
        }catch (ElementNotFoundException notFoundException){
            //notFoundException.printStackTrace();
            //标记登录成功
            return afterClick;
        }
        return null;
    }
}