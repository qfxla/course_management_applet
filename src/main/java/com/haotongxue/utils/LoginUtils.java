package com.haotongxue.utils;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.haotongxue.exceptionhandler.CourseException;

import java.io.IOException;
import java.util.List;

public class LoginUtils {
    public static HtmlPage login(WebClient webClient,String no,String psw) throws Exception {
        //执行表单提交
        HtmlPage page = webClient.getPage("http://edu-admin.zhku.edu.cn");
        HtmlForm loginForm = page.getHtmlElementById("loginForm");
        HtmlInput username = loginForm.getInputByName("username");
        username.setValueAttribute(no);
        HtmlInput password = loginForm.getInputByName("password");
        password.setValueAttribute(psw);
        HtmlElement submit = page.getHtmlElementById("submit");
        HtmlPage afterClick = submit.click();
        //System.out.println(afterClick.asXml());
        try {
            afterClick.getHtmlElementById("loginForm");
            //如果没有找到元素则抛出异常（证明loginForm1不是登录页，账号和密码正确）
        }catch (ElementNotFoundException notFoundException){
            //notFoundException.printStackTrace();
            webClient.waitForBackgroundJavaScript(5000);
            List<Object> byXPath = afterClick.getByXPath("//div[@class='edu-container edu-bg-balck']");
            if (byXPath.size()>0){
                //标记登录成功
                return afterClick;
            }
            throw new CourseException(440,"教务网的问题");
        }
        List<HtmlElement> byXPath = afterClick.getByXPath("//div[@class='tips']");
        DomNodeList<HtmlElement> span = byXPath.get(0).getElementsByTagName("span");
        if (span != null && !span.isEmpty()){
            //System.out.println(firstChild.asText());
            if ("Invalid credentials.".equals(span.get(0).asText())){
                throw new CourseException(400,"账号或密码错误");
            }
        }
        throw new CourseException(440,"教务网的问题");
    }
}