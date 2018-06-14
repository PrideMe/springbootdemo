package com.wangjikai.proxy;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * @author 22717.
 * @date 2018/4/3.
 * Description: httpclient设置代理访问IP测试
 */
public class IPproxy {
    public static void main(String[] args) throws IOException {
        //设置代理IP、端口、协议（请分别替换）
        HttpHost proxy = new HttpHost("220.171.77.174", 8060, "http");
        //把代理设置到请求配置
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
        //实例化CloseableHttpClient对象
        CloseableHttpClient closeableHttpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        //访问目标地址
        HttpGet httpGet = new HttpGet("http://www.baidu.com");
        //请求返回
        CloseableHttpResponse response = closeableHttpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            System.out.println("请求成功！");
        }
    }
}
