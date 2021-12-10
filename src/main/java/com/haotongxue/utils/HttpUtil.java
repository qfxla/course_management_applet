package com.haotongxue.utils;

/**
 * @Description CTC
 * @date 2021/11/24 16:29
 */

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * http工具
 *
 */
public final class HttpUtil {
    /**
     * 模拟http协议发送get请求
     **/
    public static String sendGetRequest(String url) {
        String result = "";
        InputStream in = null;

        HttpURLConnection connection = null;

        try {
            URL httpUrl = new URL(url);
            connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                in = connection.getInputStream();

                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                int n = 0;
                byte[] datas = new byte[2048];

                while ((n = in.read(datas)) != -1) {
                    bs.write(datas, 0, n);
                }

                bs.flush();
                result = new String(bs.toByteArray(), "utf-8");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                connection.disconnect();
            } catch (Exception ex) {
            }
        }

        return result;
    }

    /**
     * 模拟http协议发送post请求
     **/
    public static String sendPostRequest(String url, String param) {
        InputStream in = null;
        String result = "";
        HttpURLConnection connection = null;

        try {
            URL httpUrl = new URL(url);
            connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.getOutputStream().write(param.getBytes("utf-8"));
            connection.getOutputStream().flush();

            if (connection.getResponseCode() == 200) {
                in = connection.getInputStream();

                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                int n = 0;
                byte[] datas = new byte[2048];

                while ((n = in.read(datas)) != -1) {
                    bs.write(datas, 0, n);
                }

                bs.flush();
                result = new String(bs.toByteArray(), "utf-8");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                connection.disconnect();
            } catch (Exception ex) {
            }
        }

        return result;
    }

    /**
     * 普通post文件上传
     */
    public static String upLoadAttachment(String url, String fileName, File file) {
        String result = null;

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL httpUrl = new URL(url);
            connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            String bound = "----------" + System.currentTimeMillis();
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + bound);

            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(bound);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"" + fileName +
                    "\";filename=\"" + file.getName() + "\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");

            byte[] data = sb.toString().getBytes("utf-8");
            OutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(data);

            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];

            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }

            in.close();

            byte[] foot = ("\r\n--" + bound + "--\r\n").getBytes("utf-8"); // 定义最后数据分隔线
            out.write(foot);
            out.flush();
            out.close();

            InputStream inn = connection.getInputStream();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            int n = 0;
            byte[] datas = new byte[2048];

            while ((n = inn.read(datas)) != -1) {
                bs.write(datas, 0, n);
            }

            bs.flush();
            result = new String(bs.toByteArray(), "utf-8");

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                connection.disconnect();
            } catch (Exception ex) {
            }
        }

        return null;
    }

    /**
     * post文件上传 - 使用InputStream输入流(例如MultipartFile从前端获取文件后转发给其他的服务器)
     */
    public static String upLoadAttachment(String url,String keyName, String fileName, InputStream fins) {
        String result = null;

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL httpUrl = new URL(url);
            connection = (HttpURLConnection) httpUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);

            String bound = "----------" + System.currentTimeMillis();
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + bound);

            StringBuilder sb = new StringBuilder();
            sb.append("--");
            sb.append(bound);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"" + keyName +
                    "\";filename=\"" + fileName + "\"\r\n");
            sb.append("Content-Type:application/octet-stream\r\n\r\n");

            byte[] data = sb.toString().getBytes("utf-8");
            OutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(data);

            DataInputStream in = new DataInputStream(fins);
            int bytes = 0;
            byte[] bufferOut = new byte[1024];

            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }

            in.close();

            byte[] foot = ("\r\n--" + bound + "--\r\n").getBytes("utf-8"); // 定义最后数据分隔线
            out.write(foot);
            out.flush();
            out.close();

            InputStream inn = connection.getInputStream();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            int n = 0;
            byte[] datas = new byte[2048];

            while ((n = inn.read(datas)) != -1) {
                bs.write(datas, 0, n);
            }

            bs.flush();
            result = new String(bs.toByteArray(), "utf-8");

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                connection.disconnect();
            } catch (Exception ex) {
            }
        }

        return null;
    }
}