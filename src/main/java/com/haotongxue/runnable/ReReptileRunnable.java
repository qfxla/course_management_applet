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
import org.springframework.dao.TransientDataAccessResourceException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
@Slf4j
public class ReReptileRunnable implements Runnable{

    private WebClient webClient;

    private String no;

    private String password;

    private String currentOpenid;

    private ReptileService reptileService = GetBeanUtil.getBean(ReptileService.class);

    private IUserService userService = GetBeanUtil.getBean(IUserService.class);

    private Thread paThread;

    public ReReptileRunnable(WebClient webClient, String no, String password, String currentOpenid) {
        this.webClient = webClient;
        this.no = no;
        this.password = password;
        this.currentOpenid = currentOpenid;
    }

    public void noticeRePa(Thread paThread){
        this.paThread = paThread;
    }

    @SneakyThrows
    @Override
    public void run(){
        boolean rePa = false;
        Thread.sleep(2*1000*60);
        if(userService.getById(currentOpenid).getIsPaing() == 1){
            rePa = true;
        }
        if(rePa){
            log.info("正常爬超过2分钟，得重爬了。。。。。。");
            Thread.currentThread().setName(currentOpenid + "===重爬的线程");
            try {
                Thread.sleep(2000);
                paThread.interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            try{
//                paThread.stop();
//            }catch (Exception exception){
//                exception.printStackTrace();
//            }
            log.info("正常爬的线程的名字---" + paThread.getName());
//            log.info("已将正常爬的线程杀死");
//            Thread.currentThread().wait();
//            log.info("重爬的线程已被唤醒，开始重爬");
            if (webClient == null){
                webClient = WebClientUtils.getWebClient();
                try {
                    HtmlPage afterLogin = LoginUtils.login(webClient, no, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            reptileService.pa(webClient,currentOpenid);
            UpdateWrapper<User> userUpdateWrapperTwo = new UpdateWrapper<>();
            userUpdateWrapperTwo.set("is_paing",0).eq("openid",currentOpenid);
            userService.update(userUpdateWrapperTwo);
        }
    }
}
