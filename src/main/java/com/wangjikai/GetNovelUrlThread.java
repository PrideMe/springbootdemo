package com.wangjikai;

import com.wangjikai.po.Novel;
import com.wangjikai.po.NovelErrorUrl;
import com.wangjikai.po.NovelType;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 22717.
 * @date 2018/4/12.
 * Description: 多线程请求url
 */
public class GetNovelUrlThread implements Runnable {

    private List<String> urls;

    @Resource
    private NovelRepository novelRepository;

    @Resource
    private NovelErrorUrlRepository novelErrorUrlRepository;

//    public static void main(String[] args) {
//        List<String> urls = new ArrayList<>();
//        urls.add("https://www.1200ksw.com/html/43/43370");
//        GetNovelUrlThread getNovelUrlThread = new GetNovelUrlThread();
//        getNovelUrlThread.setUrls(urls);
//        getNovelUrlThread.run();
//    }

    @Override
    public void run() {
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpEntity entity = null;
        List<Novel> novels = new ArrayList<>();
        List<NovelErrorUrl> error = new ArrayList<>();
        for (String url : urls) {
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse response = null;
            try {
                response = client.execute(get);
            } catch (IOException e) {
                System.out.println("response执行出错！");
            }
            entity = response.getEntity();
            String content = null;
            try {
                content = EntityUtils.toString(entity,"GBK");
            } catch (IOException e) {
                System.out.println("转码执行出错！");
            }
            Document document = Jsoup.parse(content);
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                System.out.println("无法关闭entity！");
            }
            Elements elements = document.select("div.introduce h1");
            Elements resume = document.select("div.introduce p.jj");
            Elements typeName = document.select("div.ymdz a:matchesOwn([小][说]$)");
            String title = elements.text();
            if (!StringUtils.isEmpty(title)) {
                Novel novel = new Novel();
                NovelType type = new NovelType();
                type.setTypeName(typeName.text());
                novel.setListUrl(url);
                novel.setName(title);
                novel.setResume(resume.html());
                //novel.setNovelType(type);
                novels.add(novel);
                System.out.println(Thread.currentThread().getName()+url+title);
            } else {
                //错误页面
                NovelErrorUrl errorUrl = new NovelErrorUrl();
                errorUrl.setUrl(url);
                error.add(errorUrl);
            }
            try {
                response.close();
            } catch (IOException e) {
                System.out.println("response关闭出错！");
            }
        }
        novelRepository.saveAll(novels);
        //novelErrorUrlRepository.saveAll(error);
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
