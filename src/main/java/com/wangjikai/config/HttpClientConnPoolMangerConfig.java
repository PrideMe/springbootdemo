package com.wangjikai.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author 22717.
 * @date 2018/4/25.
 * Description: httpClient 连接池
 */
@Configuration
public class HttpClientConnPoolMangerConfig {
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager
                = new PoolingHttpClientConnectionManager(60, TimeUnit.SECONDS);
        //最大连接数
        poolingHttpClientConnectionManager.setMaxTotal(500);
        //路由基数
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(25);
        return poolingHttpClientConnectionManager;
    }

    @Bean
    public CloseableHttpClient httpClient(){
        BasicCookieStore cookieStore = new BasicCookieStore();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(15000)
                .setConnectionRequestTimeout(15000)
                .setSocketTimeout(15000).build();
        return HttpClients.custom().setDefaultCookieStore(cookieStore)
                .setConnectionManager(poolingHttpClientConnectionManager())
                .setDefaultRequestConfig(requestConfig).build();
    }
}
