package com.haotongxue.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.haotongxue.entity.Book;
import com.haotongxue.entity.JsonResult;
import com.haotongxue.utils.HttpUtils;
import com.haotongxue.utils.ResultCode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description TODO
 * @date 2022/1/5 9:54
 */
@RestController
public class ISBNController {
    /**
     * 测试
     *
     * */
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public JSONObject test(String isbn) {
        String apiKey = "11819.4c27099d7a369c36907888aa7bd0e16e.85b4ca9c41cd50932198c63540dbc1b4";
//        String isbn = "9787109256019";
//        String uurl = "http://api.douban.com/book/subject/isbn/9787111298854?apikey=0df993c66c0c636e29ecbb5344252a4a ";
//        String url="https://api.douban.com/v2/book/isbn/:"+isbn;
//        String result = HttpUtils.post(url, "", "GBK");
//        String url1="http://jisuisbn.market.alicloudapi.com/isbn/query?isbn="+isbn;
//        String url3 = "http://feedback.api.juhe.cn/ISBN?sub=" + isbn + "&key="+"373798f2******7b610612";
//        https://api.jike.xyz/situ/book/isbn/9787302501459?apikey=11819.4c27099d7a369c36907888aa7bd0e16e.85b4ca9c41cd50932198c63540dbc1b4
        String url3 = "https://api.jike.xyz/situ/book/isbn/" + isbn + "?apikey=" + apiKey;
        String result1 = HttpUtils.get(url3, "GBK");
        JSONObject jsonObject = JSON.parseObject(result1);
        String jsonStr = jsonObject.get("data").toString();
        JSONObject datajsO = JSONObject.parseObject(jsonStr);
        System.out.println(jsonStr);
        System.out.println(datajsO.get("name"));
        return null;
    }
}
