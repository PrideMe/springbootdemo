package com.wangjikai.quartz;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import com.wangjikai.*;
import com.wangjikai.po.Novel;
import com.wangjikai.po.NovelType;
import com.wangjikai.po.NovelTypeStartPage;
import com.wangjikai.po.Website;
import org.apache.http.impl.client.CloseableHttpClient;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;

/**
 * @author wang.
 * @date 2018/6/5.
 * Description: 定时去执行更新操作，获取网站最新数据
 */
@Component
public class UpdateNovelJob implements Job {

    @Resource
    private SpringbootdemoApplication application;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //System.out.println("定时任务执行失败！");
    }
}
