package com.haotongxue.handler;

import com.gargoylesoftware.htmlunit.WebClient;
import com.haotongxue.service.ReptileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ReptileHandler {

    @Autowired
    ReptileService reptileService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public void pa(WebClient webClient){
        executorService.execute(() -> reptileService.pa(webClient));
    }
}
