package com.wangjikai.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;

/**
 * @author wang.
 * @date 2018/6/5.
 * Description: 配置spring的bean注入到quartz的job中
 */
@Component
public class MyJobFactory extends SpringBeanJobFactory {
    @Resource
    private AutowireCapableBeanFactory capableBeanFactory;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        capableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
