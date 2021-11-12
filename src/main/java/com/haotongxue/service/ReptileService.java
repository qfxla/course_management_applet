package com.haotongxue.service;

import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;

public interface ReptileService {
    void pa(WebClient webClient ,String openid) throws IOException;
}
