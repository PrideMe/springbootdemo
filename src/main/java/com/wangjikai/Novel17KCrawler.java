package com.wangjikai;

import com.wangjikai.po.NovelContent;
import com.wangjikai.po.NovelUrl;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 22717.
 * @date 2018/4/1.
 * Description: http://www.17k.com/ 17K小说网爬虫
 */
@Component
public class Novel17KCrawler {

    /**
     * mysql库
     */
    @Resource
    private NovelUrlRepository novelUrlRepository;
    /**
     * mongo库
     */
    @Resource
    private MongoTemplate mongoTemplate;

    private static String url = "http://www.17k.com/";

    public static void main(String[] args) throws IOException {
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
//        HttpGet get = new HttpGet(url);
//        CloseableHttpResponse response = client.execute(get);
//        HttpEntity entityCart = response.getEntity();
//        Document documentCart = Jsoup.parse(EntityUtils.toString(entityCart));
//        Element element = documentCart.select("dl.BZZD_TOP").first();
//        Elements tabs = element.select(".top");
//        for (Element tab : tabs) {
//            Elements bookNames = tab.select("li a");
//            for (Element bookName : bookNames) {
//                System.out.println(bookName.text()+" "+bookName.attr("href"));
//            }
//            System.out.println();
//        }
//        response.close();
//        client.close();
    }

    public List<String> aaa() throws IOException {
        List<String> stringList = new ArrayList<>();
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpGet get = new HttpGet("http://www.17k.com/list/1859126.html");
        CloseableHttpResponse response = client.execute(get);
        HttpEntity entity = response.getEntity();
        Document document = Jsoup.parse(EntityUtils.toString(entity));
        EntityUtils.consume(entity);
        Elements elements = document.select("dl.Volume");
        stringList.add(document.select("h1.Title").text());
        //Elements tabs = element.select(".top");
        System.out.println(document.select("h1.Title").text());
        for (Element element : elements) {
            Elements spans = element.select("dt span");
            for (Element span : spans) {
                System.out.print(span.text()+" ");
                stringList.add(span.text());
            }
            Elements chapters = element.select("dd a");
            for (Element chapter : chapters) {
                NovelUrl novelUrl = new NovelUrl();
                novelUrl.setChapterName(chapter.text());
                novelUrl.setUrl("http://www.17k.com"+chapter.attr("href"));
                //判断是否有新增章节，存在则存库。每一本书的章节页的章节数组成集合，与数据库中的做对比。
                novelUrlRepository.save(novelUrl);
                //使用消息队列，将小说的url数据发送给MQ，然后再在消费端进行小说内容的爬取
                HttpGet novelContentGet = new HttpGet("http://www.17k.com"+chapter.attr("href"));
                CloseableHttpResponse contentResponse = client.execute(novelContentGet);
                HttpEntity entityContent = contentResponse.getEntity();
                Document contentDocument = Jsoup.parse(EntityUtils.toString(entityContent));
                EntityUtils.consume(entityContent);
                Element elementContent = null;
                if (chapter.select("span").hasClass("vip")) {
                    //System.out.println(chapter.text()+"VIP");
                    elementContent = contentDocument.selectFirst("div.content");
                    stringList.add("<a href="+"\"http://www.17k.com"+chapter.attr("href")+"\" target=\"_blank\">"+chapter.text()+"</a></span><span style=\"color:red\">VIP");
                } else {
                    //System.out.println(chapter.text());
                    elementContent = contentDocument.selectFirst("div.content .p");
                    stringList.add("<a href="+"\"http://www.17k.com"+chapter.attr("href")+"\" target=\"_blank\">"+chapter.text()+"</a>");
                    elementContent.select("qrcode").remove();
                }
                NovelContent novelContent = new NovelContent();
                novelContent.setTitle(chapter.text());
                novelContent.setContent(elementContent.text());
                mongoTemplate.insert(novelContent);
                contentResponse.close();
            }
            System.out.println();
        }
        response.close();
        client.close();
        return stringList;
    }
}
