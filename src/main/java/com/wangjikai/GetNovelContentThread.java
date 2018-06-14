package com.wangjikai;

import com.wangjikai.po.Novel;
import com.wangjikai.po.NovelContent;
import com.wangjikai.po.NovelUrl;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author wang.
 * @date 2018/5/19.
 * Description: 获取小说内容的线程
 */
public class GetNovelContentThread implements Callable<List<NovelContent>> {

    @Resource
    private CloseableHttpClient httpClient;

    private List<NovelUrl> novelUrls;

    private int novelId;

    private String novelTitleCssQuery;

    private String novelContentCssQuery;

    private String charSet;

    @Override
    public List<NovelContent> call() throws Exception {
        List<NovelContent> novelContents = new ArrayList<>();
        for (NovelUrl url : novelUrls) {
            HttpGet novelContentGet = new HttpGet(url.getUrl());
            CloseableHttpResponse contentResponse;
            try {
                contentResponse = httpClient.execute(novelContentGet);
                HttpEntity entityContent = contentResponse.getEntity();
                Document contentDocument = Jsoup.parseBodyFragment(EntityUtils.toString(entityContent,charSet));
                EntityUtils.consume(entityContent);
                Element elementContent = contentDocument.selectFirst(novelContentCssQuery);
                NovelContent novelContent = new NovelContent();
                novelContent.setId(url.getMongoId());
                novelContent.setTitle(contentDocument.selectFirst(novelTitleCssQuery).text());
                novelContent.setContent(elementContent.html());
                novelContent.setNovelId(novelId);
                novelContents.add(novelContent);
                contentResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return novelContents;
    }

    public void setNovelUrls(List<NovelUrl> novelUrls) {
        this.novelUrls = novelUrls;
    }

    public void setNovelId(int novelId) {
        this.novelId = novelId;
    }

    public void setNovelTitleCssQuery(String novelTitleCssQuery) {
        this.novelTitleCssQuery = novelTitleCssQuery;
    }

    public void setNovelContentCssQuery(String novelContentCssQuery) {
        this.novelContentCssQuery = novelContentCssQuery;
    }

    public void setCharSet(String charSet) {
        this.charSet = charSet;
    }
}
