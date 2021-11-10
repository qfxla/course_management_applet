package com.haotongxue.service.impl;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.haotongxue.service.EduLoginService;
import com.haotongxue.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;

@Service
public class EduLoginServiceImpl implements EduLoginService{


    @Autowired
    WebClient webClient;

    /**
     * 获取指定网页实体
     * @param url
     * @return
     */
    public HtmlPage getHtmlPage(String url){
        //调用此方法时加载WebClient
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        HtmlPage page=null;
        try{
            // 获取指定网页实体
            page = (HtmlPage) webClient.getPage(url);
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return page;
    }

    @Override
    public HtmlPage login(String no, String password) {
        // 获取指定网页实体
        HtmlPage page = getHtmlPage("http://edu-admin.zhku.edu.cn");
        HtmlForm loginForm = page.getForms().get(0);
        HtmlTextInput username = loginForm.getInputByName("username");
        username.setValueAttribute(no);
        HtmlPasswordInput passwordEle = loginForm.getInputByName("password");
        passwordEle.setValueAttribute(password);
        HtmlElement submit = page.getHtmlElementById("submit");
        HtmlPage click = null;
        try {
            click = submit.click();
        }catch (Exception e){
            e.printStackTrace();
            HtmlImage vcodeimg = (HtmlImage) click.getElementById("vcodeimg");
            if (vcodeimg != null){
                ImageReader imageReader;
                BufferedImage read;
                try {
                    imageReader = vcodeimg.getImageReader();
                    read = imageReader.read(0);
                    String property = System.getProperty("user.dir");
                    File file = new File(property+"hhhhh");
                    ImageIO.write(read,"jpg",file);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            return null;
        }
        return click;
    }
}
