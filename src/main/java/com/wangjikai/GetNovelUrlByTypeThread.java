package com.wangjikai;

import com.wangjikai.po.Novel;
import com.wangjikai.po.NovelType;
import com.wangjikai.po.NovelTypeStartPage;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author 22717.
 * @date 2018/4/16.
 * Description:多线程请求url，根据类型,请求书名,作者的页面
 */
public class GetNovelUrlByTypeThread implements Callable<NovelTypeStartPage> {

    private NovelTypeStartPage novelTypeStartPage;

    @Resource
    private CloseableHttpClient httpClient;

    /**
     * 线程进度
     */
    private int progress;

    public int getProgress() {
        return progress;
    }

    public void setNovelTypeStartPage(NovelTypeStartPage novelTypeStartPage) {
        this.novelTypeStartPage = novelTypeStartPage;
    }

    public NovelTypeStartPage getNovelTypeStartPage() {
        return novelTypeStartPage;
    }

    @Override
    public NovelTypeStartPage call() throws Exception {
        int size = novelTypeStartPage.getAllUrls().size();
        String novelCssQuery = novelTypeStartPage.getNovelCssQuery();
        String novelNameCssQuery = novelTypeStartPage.getNovelNameCssQuery();
        String novelListUrlCssQuery = novelTypeStartPage.getNovelListUrlCssQuery();
        String novelPicUrlCssQuery = novelTypeStartPage.getNovelPicUrlCssQuery();
        String novelAuthorCssQuery = novelTypeStartPage.getNovelAuthorCssQuery();
        String novelResumeCssQueryByType = novelTypeStartPage.getNovelResumeCssQueryByType();
        String charSet = novelTypeStartPage.getCharSet();
        System.out.println(Thread.currentThread().getName()+"开始爬取"+" 数量："+size);
        HttpEntity entity = null;
        List<Novel> novels = new ArrayList<>();
        CloseableHttpResponse response = null;
        String content = null;
        HttpGet get;
        Document document;
        int temp = 0;
        for (String url : novelTypeStartPage.getAllUrls()) {
            progress = ++temp;
            get = new HttpGet(url);
            get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            get.setHeader("Accept-Encoding", "gzip, deflate");
            get.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
            get.setHeader("cache-control", "max-age=0");
            get.setHeader("upgrade-insecure-requests", "1");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
            try {
                response = httpClient.execute(get);
            } catch (IOException e) {
                System.out.println("响应失败！URL:" + url);
            }
            try {
                entity = response.getEntity();
            } catch (NullPointerException e) {
                System.out.println("解析URL出错" + url);
            }
            try {
                content = EntityUtils.toString(entity,charSet);
            } catch (IOException e) {
                System.out.println("转码执行出错！URL:" + url);
            }
            document = Jsoup.parse(content);
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                System.out.println("无法关闭entity！URL:" + url);
            }
            //书名板块div.fl_right .tt
            Elements elements = document.select(novelCssQuery);
            for (Element element : elements) {
                Novel novel = new Novel();
                String name = element.selectFirst(novelNameCssQuery).text();
                String listUrl = element.selectFirst(novelListUrlCssQuery).attr("href");
                if (!"/".equals(listUrl.substring(listUrl.length()-1))) {
                    listUrl += "/";
                }
                String pic = element.selectFirst(novelPicUrlCssQuery).attr("src");
                String author = element.selectFirst(novelAuthorCssQuery).ownText().replace("作者：","").replace("/","");
                //判断是否在获取小说分类里取得简介
                if (!StringUtils.isEmpty(novelResumeCssQueryByType)) {
                    String resume = element.selectFirst(novelResumeCssQueryByType).text();
                    novel.setResume(resume);
                }
                novel.setWebSiteType(novelTypeStartPage.getWebSiteType());
                novel.setName(name);
                novel.setNovelType(novelTypeStartPage.getNovelType());
                novel.setListUrl(listUrl);
                novel.setAuthor(author);
                novel.setPicUrl(pic);
                novels.add(novel);
            }
        }
        System.out.println(Thread.currentThread().getName()+"完成"+" 数量："+novelTypeStartPage.getAllUrls().size());
        NovelTypeStartPage novelTypeStartPageReturn = new NovelTypeStartPage();
        novelTypeStartPageReturn.setStartPageUrl(novelTypeStartPage.getStartPageUrl());
        novelTypeStartPageReturn.setNumberCssQuery(novelTypeStartPage.getNumberCssQuery());
        novelTypeStartPageReturn.setTypeNameCssQuery(novelTypeStartPage.getTypeNameCssQuery());
        novelTypeStartPageReturn.setCharSet(novelTypeStartPage.getCharSet());
        novelTypeStartPageReturn.setTypeName(novelTypeStartPage.getNovelType().getTypeName());
        novelTypeStartPageReturn.setNovelType(novelTypeStartPage.getNovelType());
        novelTypeStartPageReturn.setTotalPage(novelTypeStartPage.getTotalPage());
        novelTypeStartPageReturn.setNovelList(novels);
        return novelTypeStartPageReturn;
    }
}
