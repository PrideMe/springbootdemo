package com.wangjikai.config;

import com.wangjikai.quartz.UpdateNovelJob;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Properties;

/**
 * @author wang.
 * @date 2018/5/29.
 * Description: 定时任务配置
 */
@Configuration
public class ScheduledConfig {

    @Resource
    private MyJobFactory myJobFactory;

    /**
     * 任务
     */
    @Bean(name = "jobDetail")
    public JobDetailFactoryBean secondJobDetail(UpdateNovelJob secondJob) {
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(secondJob.getClass());
        return jobDetailFactoryBean;
    }

    /**
     * 触发器
     */
    @Bean(name = "trigger")
    public CronTriggerFactoryBean secondTrigger(JobDetail jobDetail) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(jobDetail);
        trigger.setCronExpression("0 0/4 * * * ?");
        return trigger;
    }

    /**
     * 调度器
     */
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactoryBean(Trigger... triggers) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setStartupDelay(5);
        factory.setJobFactory(myJobFactory);
        //factory.setQuartzProperties(quartzProperties());
        factory.setTriggers(triggers);
        return factory;
    }

//    /**
//     * 配置信息
//     */
//    @Bean
//    public Properties quartzProperties() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }
}
