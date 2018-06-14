package com.wangjikai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

/**
 * @author wang.
 * @date 2018/5/3.
 * Description: 线程池
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public ThreadPoolExecutor executorService() {
        //无界队列
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

        //自定义线程池
        return new ThreadPoolExecutor(16,1000,60
                , TimeUnit.SECONDS,queue,new ThreadPoolExecutor.AbortPolicy());
    }
}
