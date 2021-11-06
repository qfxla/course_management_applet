package com.haotongxue.service;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public interface EduLoginService {
    HtmlPage login(String no, String password);
}
