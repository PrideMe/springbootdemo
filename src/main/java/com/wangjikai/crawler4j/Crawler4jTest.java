package com.wangjikai.crawler4j;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author 22717.
 * @date 2018/4/3.
 * Description: crawler4j学习
 */
public class Crawler4jTest extends WebCrawler{

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");
    /**
     * 决定一个给定的URL是否应收访问
     * @param referringPage
     * @param url
     * @return
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && href.startsWith("http://www.ics.uci.edu/");
    }

    /**
     * 解析网页内容
     * @param page
     */
    @Override
    public void visit(Page page) {
        //获取url
        String url=page.getWebURL().getURL();
        System.out.println("url:"+url);
        //判断是否是html数据
        if(page.getParseData() instanceof HtmlParseData){
            //强制类型转换，获取html数据对象
            HtmlParseData htmlParseData= (HtmlParseData) page.getParseData();
            //获得页面纯文本
            String text=htmlParseData.getText();
            //获得页面html
            String html=htmlParseData.getHtml();
            //获取页面输出链接
            Set<WebURL> links=htmlParseData.getOutgoingUrls();

            System.out.println("纯文本长度: " + text.length());
            System.out.println("html长度: " + html.length());
            System.out.println("输出链接个数: " + links.size());
        }
    }

    public static void main(String[] args) throws Exception {
        //定义爬虫存储的位置
        String crawStorageFoler="E:/crawler";
        //定义爬虫线程7个
        int numberOfCrawlers=1;
        //定义爬虫配置
        CrawlConfig config=new CrawlConfig();
        //设置爬虫文件存储位置
        config.setCrawlStorageFolder(crawStorageFoler);
        //实例化页面获取器
        PageFetcher pageFetcher=new PageFetcher(config);
        //实例化爬虫机器人配置,比如可以设置user-agent
        RobotstxtConfig robotstxtconfig=new RobotstxtConfig();
        //实例化爬虫机器人对目标服务器的配置，每个网站都有一个robots.txt文件
        //规定了该网站哪些页面可以爬，哪些页面禁止爬，该类是对robots.txt规范的实现
        RobotstxtServer robotstxtServer=new RobotstxtServer(robotstxtconfig,pageFetcher);
        //实例化爬虫控制器
        CrawlController controller=new CrawlController(config,pageFetcher,robotstxtServer);
        //配置爬取种子页面，就是规定从哪里开始爬，可以配置多个种子页面
        controller.addSeed("http://www.bjsxt.com");


        //启动爬虫，爬虫从此刻开始执行爬虫任务
        controller.start(Crawler4jTest.class,numberOfCrawlers);
    }
}
