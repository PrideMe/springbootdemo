package com.wangjikai;

import com.wangjikai.po.NovelType;
import com.wangjikai.po.NovelTypeStartPage;
import com.wangjikai.po.Website;
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
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author wang.
 * @date 2018/5/3.
 * Description: 获取小说每种类型的总页数
 */
public class GetTypeTotalPageNumberThread implements Callable<NovelTypeStartPage>{
    @Resource
    private CloseableHttpClient httpClient;

    private NovelTypeStartPage novelTypeStartPage;

    private Website website;

    @Override
    public NovelTypeStartPage call() throws Exception {
        String novelTypeStartPageStartUrl  = novelTypeStartPage.getStartPageUrl();
        List<NovelType> novelTypes = website.getNovelTypes();
        HttpGet get = new HttpGet(novelTypeStartPageStartUrl);
        get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        get.setHeader("Accept-Encoding", "gzip, deflate");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        get.setHeader("cache-control", "max-age=0");
        get.setHeader("upgrade-insecure-requests", "1");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
        CloseableHttpResponse response;
        String content;
        HttpEntity entity;
        Document document;
        try {
            response = httpClient.execute(get);
            entity = response.getEntity();
        } catch (IOException e) {
            return null;
        }
        if (entity != null) {
            try {
                content = EntityUtils.toString(entity,novelTypeStartPage.getCharSet());
            } catch (IOException e) {
                return null;
            } finally {
                try {
                    EntityUtils.consume(entity);
                } catch (IOException e) {
                    System.out.println("请求异常");
                }
            }
        } else {
            return null;
        }
        if (!StringUtils.isEmpty(content)) {
            document = Jsoup.parse(content);
        } else {
            return null;
        }
        //获取页数
        Elements elements = document.select(novelTypeStartPage.getNumberCssQuery());
        Element element = elements.get(0);
        String maxPageNumber = element.text();
        //获取类型名称
        Elements typeNameElements = document.select(novelTypeStartPage.getTypeNameCssQuery());
        NovelType novelType = novelTypeStartPage.getNovelType();
        //1、网站会显示分类名称，爬取配置时已配置类型
        //2、网站会显示分类名称，爬取配置时未配置类型
        //3、网站不显示分类名称，爬取配置时未配置类型
        //4、网站不显示分类名称，爬取配置时已配置类型
        if (typeNameElements.size() > 0 && novelType == null) {
            Element typeNameElement = typeNameElements.get(0);
            //ownText()方法避免获取此元素下的其他子元素,屏蔽掉除中文外的其他字符
            String typeName = typeNameElement.ownText().replaceAll("[^\\u4e00-\\u9fa5]","");
            novelTypeStartPage.setTypeName(typeName);
            NovelType novelType1 = null;
            synchronized (novelTypes) {
                for (NovelType type : novelTypes) {
                    if (type.getTypeName().equals(typeName)) {
                        novelType1 = type;
                    }
                }
            }
            if (novelType1 == null) {
                novelType1 = new NovelType();
                novelType1.setTypeName(typeName);
                novelType1.setWebsite(website);
            }
            novelTypeStartPage.setNovelType(novelType1);
        } else {
            if (novelType == null) {
                String warn = "解析分类名称失败" + novelTypeStartPageStartUrl;
                novelTypeStartPage.setTypeName(warn);
                NovelType type = new NovelType();
                type.setTypeName(warn);
                novelTypeStartPage.setNovelType(type);
            }
        }
        novelTypeStartPage.setTotalPage(Integer.valueOf(maxPageNumber));
        return novelTypeStartPage;
    }

    public void setNovelTypeStartPage(NovelTypeStartPage novelTypeStartPage) {
        this.novelTypeStartPage = novelTypeStartPage;
    }

    public void setWebsite(Website website) {
        this.website = website;
    }
}
