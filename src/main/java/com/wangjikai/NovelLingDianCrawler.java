package com.wangjikai;

import com.google.common.collect.Lists;
import com.wangjikai.po.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.persistence.GenerationType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 22717.
 * @date 2018/4/2.
 * Description: https://www.1200ksw.com/ 零点看书爬虫
 */
@Component
public class NovelLingDianCrawler {
    /**
     * mysql库
     */
    @Resource
    private NovelUrlRepository novelUrlRepository;

    @Resource
    private NovelRepository novelRepository;
    /**
     * mongo库
     */
    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private CloseableHttpClient httpClient;

    @Resource
    private ThreadPoolExecutor executorService;

    @Resource
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    private Novel novel;

    private String baseUrl;

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 爬取具体单本小说的详情页，解析章节列表存入集合中，然后入库
     * 入库效率比较高的集合数值大约在500-1000左右，小说章节目录完全符合
     * 注意，此页面不是立刻就显示的，需要等抓取完章节列表然后入库之后才进行显示
     * 需要存入缓存中，页面展示时从缓存中获取
     * 定时任务及时缓存章节列表即可，热点小说进行及时缓存，一般小说第一次访问进行缓存，避免浪费
     */
    public List<NovelUrl> crawlerList(Website website) throws Exception {

        String novelListCssQuery = website.getNovelListCssQuery();
        String charSet = website.getWebSiteCharSet();
        //网址主页
        String baseUrl = website.getWebSiteBaseUrl();
        String novelTitleCssQuery = website.getNovelTitleCssQuery();
        String novelContentCssQuery = website.getNovelContentCssQuery();
        boolean needBaseIndex = website.isNeedBaseIndex();
        String novelResumeCssQueryByContent = website.getNovelResumeCssQueryByContent();

        List<NovelUrl> returnList;
        //ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("", ExampleMatcher.GenericPropertyMatchers.startsWith());
        List<NovelUrl> dbNovelUrls = novelRepository.findById(novel.getId()).get().getNovelUrlList();
        String novelListUrl = novel.getListUrl();
        List<NovelUrl> novelUrls = new ArrayList<>();
        List<NovelUrl> toDBSave;
        HttpGet get = new HttpGet(novelListUrl);
        get.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        get.setHeader("Accept-Encoding", "gzip, deflate, br");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        get.setHeader("cache-control", "max-age=0");
        get.setHeader("upgrade-insecure-requests", "1");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36");
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity,charSet);
            Document document = Jsoup.parse(content);
            EntityUtils.consume(entity);
            Elements elements = document.select(novelListCssQuery);
            if (!StringUtils.isEmpty(novelResumeCssQueryByContent)) {
                String novelResumeContent = document.selectFirst(novelResumeCssQueryByContent).ownText();
                novel.setResume(novelResumeContent);
            }
            NovelUrl novelUrl;
            for (Element element : elements) {
                novelUrl = new NovelUrl();
                if (needBaseIndex) {
                    novelUrl.setUrl(baseUrl + element.select("a").attr("href"));
                } else {
                    novelUrl.setUrl(novelListUrl + element.select("a").attr("href"));
                }
                novelUrl.setChapterName(element.text());
                novelUrl.setMongoId(UUID.randomUUID().toString().replaceAll("\\-",""));
                novelUrl.setNovel(novel);
                novelUrls.add(novelUrl);
            }
        } catch (IOException e) {
            System.out.println("页面错误");
        }
        //关于hashCode和equals知识点：https://www.cnblogs.com/keyi/p/7119825.html
        //novelUrls.removeAll(dbNovelUrl);
        if (dbNovelUrls.size() > 0) {
            //数据库中已有章节列表，则取出最后一个章节
            NovelUrl novelUrlDBId = dbNovelUrls.get(dbNovelUrls.size()-1);
            int startPoint = novelUrls.indexOf(novelUrlDBId)+1;
            //subList属于前包含后不包含的列表，同时subList依赖于原始list
            toDBSave = novelUrls.subList(startPoint,novelUrls.size());
        } else {
            //数据库很干净
            toDBSave = novelUrls;
        }
        //章节入库。耗时操作
        novelUrlRepository.saveAll(toDBSave);
        dbNovelUrls.addAll(toDBSave);
        //novel.setNovelUrlList(novelUrls);

        //获取已经在mysql中的章节。
        returnList = novelRepository.findById(novel.getId()).get().getNovelUrlList();
        //此时先返回给前端，这是目录显示，后台再继续采集章节内容并存入mongo库
        //查找mongo中的所有与此小说有关的章节
        org.bson.Document queryObject = new org.bson.Document();
        org.bson.Document fieldsObject = new org.bson.Document();
        queryObject.put("novelId",novel.getId());
        fieldsObject.put("_id",true);
        Query query=new BasicQuery(queryObject,fieldsObject);
        List<NovelContent> mongoNovelContent = mongoTemplate.find(query,NovelContent.class);
        //mongo中已经保存的章节列表，mysql中保存着mongo的id属性
        List<NovelUrl> mongoSaved = new ArrayList<>();
        //获取所有mysql中存在的章节，然后去除mysql中存在却没有存入mongo中的数据
        List<NovelUrl> toMongoSave = new ArrayList<>();
        toMongoSave.addAll(returnList);
        if (mongoNovelContent.size() > 0) {
            //循环遍历出mysql中存在，mongo中也存在的章节
            for (NovelUrl dbNovelUrl : toMongoSave) {
                for (NovelContent novelContent : mongoNovelContent) {
                    if (dbNovelUrl.getMongoId().equals(novelContent.getId())) {
                        mongoSaved.add(dbNovelUrl);
                    }
                }
            }
            toMongoSave.removeAll(mongoSaved);
        }
        //List<NovelContent> novelContents = new ArrayList<>();
        if (toMongoSave.size() != 0) {
            //存入mongo。开始多线程，爬取内容页
            crawlerContentByThread(toMongoSave, novel.getId(),charSet, novelTitleCssQuery, novelContentCssQuery);
        }
        return returnList;
    }

    public Novel getNovel() {
        return novel;
    }

    public void setNovel(Novel novel) {
        this.novel = novel;
    }

    /**
     * 分割集合，构造好多线程需要的集合，开始进行多线程的执行
     */
    public void crawlerContentByThread(List<NovelUrl> toMongoSave, int novelId,String charSet, String novelTitleCssQuery, String novelContentCssQuery) throws Exception {
        List<List<NovelUrl>> partitionList = Lists.partition(toMongoSave,40);
        List<Future<List<NovelContent>>> futureList = new ArrayList<>();
        for (int i = 0; i < partitionList.size(); i++) {
            List<NovelUrl> novelUrls = partitionList.get(i);
            GetNovelContentThread getNovelContentThread = new GetNovelContentThread();
            getNovelContentThread.setNovelUrls(novelUrls);
            getNovelContentThread.setNovelId(novelId);
            getNovelContentThread.setNovelTitleCssQuery(novelTitleCssQuery);
            getNovelContentThread.setNovelContentCssQuery(novelContentCssQuery);
            getNovelContentThread.setCharSet(charSet);
            autowireCapableBeanFactory.autowireBean(getNovelContentThread);
            Future<List<NovelContent>> future = executorService.submit(getNovelContentThread);
            futureList.add(future);
        }

        for (Future<List<NovelContent>> listFuture : futureList) {
            listFuture.get();
        }

        List<NovelContent> novelContentList = new ArrayList<>();
        for (int i = 0; i < futureList.size(); i++) {
            List<NovelContent> listFuture = futureList.get(i).get();
            novelContentList.addAll(listFuture);
        }
        mongoTemplate.insert(novelContentList,NovelContent.class);
    }
}
