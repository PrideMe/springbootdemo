package com.wangjikai.listener;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * Created by jikai_wang on 2018/3/19.
 * spring boot 事件监听器
 */
public class MyApplicationEnvironmentPrepareEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent applicationEnvironmentPreparedEvent) {
        SpringApplication application = applicationEnvironmentPreparedEvent.getSpringApplication();
        //application.setBannerMode(Banner.Mode.OFF);
        System.out.println("spring boot环境准备");
    }
}
