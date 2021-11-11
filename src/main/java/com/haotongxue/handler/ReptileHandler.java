package com.haotongxue.handler;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.haotongxue.entity.User;
import com.haotongxue.runnable.ReptileRunnable;
import com.haotongxue.service.IUserService;
import com.haotongxue.service.ReptileService;
import com.haotongxue.utils.LoginUtils;
import com.haotongxue.utils.UserContext;
import com.haotongxue.utils.WebClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ReptileHandler {

    @Autowired
    ReptileService reptileService;

    @Resource(name = "loginCache")
    LoadingCache<String,Object> cache;

    @Autowired
    IUserService userService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Transactional
    public void pa(WebClient webClient,String no,String password){
        String currentOpenid = UserContext.getCurrentOpenid();
        UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
        userUpdateWrapper.set("is_paing",1).eq("openid",currentOpenid);
        if (userService.update(userUpdateWrapper)){
            cache.invalidate(currentOpenid);
            executorService.execute(new ReptileRunnable(webClient,no,password));
            UpdateWrapper<User> userUpdateWrapperTwo = new UpdateWrapper<>();
            userUpdateWrapper.set("is_paing",0).eq("openid",currentOpenid);
            userService.update(userUpdateWrapperTwo);
        }
    }
}
