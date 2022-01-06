package com.haotongxue.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

    /**
     * get
     *
     * @param url
     *            String
     * @param charset
     *            String
     * @return String
     */
    public static String get(String url, String charset) {
        if (charset == null) {
            charset = HTTP.UTF_8;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try{
            HttpGet httpget = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, charset);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (response != null){
                    response.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * getForJisu
     *
     * @param url
     *            String
     * @param charset
     *            String
     * @return String
     */
    public static String getForJisu(String url, String charset) {
        if (charset == null) {
            charset = HTTP.UTF_8;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try{
            HttpGet httpget = new HttpGet(url);
            httpget.addHeader("key", "value");
            CloseableHttpResponse response = httpClient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, charset);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (response != null){
                    response.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * postForString
     *
     * @param url
     *            String
     * @param content
     *            content
     * @param charset
     *            String
     * @return String
     */
    public static String post(String url, String content, String charset) {
        if (charset == null) {
            charset = HTTP.UTF_8;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            if (content != null) {
                httpPost.setEntity(new StringEntity(content, charset));
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, charset);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (response != null){
                    response.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * postForMap
     *
     * @param url
     *            String
     * @param map
     *            HashMap 提交表单的键值对
     * @param charset
     *            String
     * @return String
     */
    public static String post(String url, HashMap<String, String> map, String charset) {
        if (charset == null) {
            charset = HTTP.UTF_8;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<>();
            if (map != null) {
                Iterator it = map.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    nvps.add(new BasicNameValuePair(key, map.get(key)));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));
            // 设置header信息   指定报文头【Content-type】、【User-Agent】
            //httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            //httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            // 执行请求操作，并拿到结果（同步阻塞）
            CloseableHttpResponse response = httpClient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, charset);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (response != null){
                    response.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 关闭连接,释放资源
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}