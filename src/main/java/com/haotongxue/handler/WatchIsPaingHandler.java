package com.haotongxue.handler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.haotongxue.runnable.ReReptileRunnable;
import com.haotongxue.runnable.ReptileRunnable;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.UserContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @Description TODO
 * @date 2021/11/23 22:51
 */

@Component
public class WatchIsPaingHandler {


    private ExecutorService watchIsPaPool = Executors.newCachedThreadPool();

//    @Resource
//    IUserService iUserService;

    public void watchIsPa(ReReptileRunnable reReptileRunnable){

        watchIsPaPool.execute(reReptileRunnable);
    }
}
