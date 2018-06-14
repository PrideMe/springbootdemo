package com.wangjikai;

import com.google.common.collect.Lists;
import com.wangjikai.po.Novel;
import com.wangjikai.po.NovelType;
import com.wangjikai.po.NovelTypeStartPage;
import com.wangjikai.po.Website;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 22717.
 * @date 2018/4/10.
 * Description:整站爬取策略
 * 爬取网站：https://www.1200ksw.com/
 */
@Component
public class AllNovelCrawler {
    @Resource
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    @Resource
    private ThreadPoolExecutor executorService;

    private int splitSize = 300;

    public void setSplitSize(int splitSize) {
        this.splitSize = splitSize;
    }

    private List<GetNovelUrlByTypeThread> getNovelUrlByTypeThreads;

    List<Future<NovelTypeStartPage>> futureList;

    public List<GetNovelUrlByTypeThread> getGetNovelUrlByTypeThreads() {
        return getNovelUrlByTypeThreads;
    }

    //运行标志
    private boolean runFlag;

    public boolean isRunFlag() {
        return runFlag;
    }

    public void setRunFlag(boolean runFlag) {
        this.runFlag = runFlag;
    }

    //返回Future对象集合，判断线程是否完成
    public List<Future<NovelTypeStartPage>> getFutureList() {
        return futureList;
    }

    /**
     * 整站策略。零点看书网书目比较有规律，代码拼凑
     * 初期代码！
     */
    public void crawlerAllNovelByNumber() throws IOException {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i <= 55; i++) {
            for (int j = 1; j <= 999; j++) {
                // 0 代表前面补充0
                // 4 代表长度为4
                // d 代表参数为正数型
                String str = String.format("%03d", j);
                String url = "https://www.1200ksw.com/html/"+String.valueOf(i)+"/"+String.valueOf(i)+str+"/";
                if (0 == i) {
                    url = "https://www.1200ksw.com/html/"+String.valueOf(i)+"/"+String.valueOf(j)+"/";
                }
                stringList.add(url);
            }
        }
        //无界队列
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        //自定义线程池
        ExecutorService executorService = new ThreadPoolExecutor(16,1000,60
                ,TimeUnit.SECONDS,queue,new ThreadPoolExecutor.AbortPolicy());
        List<List<String>> splitList = Lists.partition(stringList,splitSize);
        for (List<String> list : splitList) {
            GetNovelUrlThread getNovelUrlThread = new GetNovelUrlThread();
            autowireCapableBeanFactory.autowireBean(getNovelUrlThread);
            getNovelUrlThread.setUrls(list);
            executorService.execute(getNovelUrlThread);
        }
        executorService.shutdown();
    }

    /**
     * 整站策略。零点看书网首先获取指定的类型小说，再获取该类型下的所有书目
     * https://www.1200ksw.com/s9/2.html
     * s1:850
     * s2:105
     * s3:463
     * s4:141
     * s5:89
     * s6:98
     * s7:49
     * s8:1
     * s9:26
     * 获取每个类型下说有书目的目录
     */
    public List<NovelTypeStartPage> crawlerAllNovelByType(List<NovelTypeStartPage> list) throws Exception {
        runFlag = true;
        //List<List<NovelTypeStartPage>> allType = new ArrayList<>();
        //多个分组集合，分组 >= 类别
        List<NovelTypeStartPage> allGroup = new ArrayList<>(list.size());
        //根据类别、每个类别的总页数 组装【获取每种类型下的所有页面的URL】列表
        //将页数多的list进行分组，分组依据用户自定义+系统默认（300）
        for (int i = 1; i <= list.size(); i++) {
            NovelTypeStartPage novelTypeStartPage = list.get(i-1);
            int pageNumber = novelTypeStartPage.getTotalPage();

            String baseUrl = novelTypeStartPage.getBaseUrl();
            String urlPrefix = novelTypeStartPage.getUrlPrefix();
            String urlInfix = novelTypeStartPage.getUrlInfix();
            String urlSuffix = novelTypeStartPage.getUrlSuffix();

            NovelType novelType = new NovelType();
            novelType.setTypeName(novelTypeStartPage.getTypeName());
            //novelTypeStartPage.setNovelType(novelType);

            //判断是否达到分组要求,进行分组
            if (pageNumber > splitSize) {
                int group = pageNumber/splitSize + 1;
                for (int i1 = 1; i1 <= group; i1++) {
                    int prefix = (i1 - 1) * splitSize;
                    int count = pageNumber - (i1 -1) * splitSize;

                    List<String> urls = new ArrayList<>();
                    if (count > splitSize){
                        for (int j = 1; j <= splitSize; j++) {
                            String url = baseUrl + urlPrefix + String.valueOf(i)+ urlInfix +String.valueOf(j + prefix)+ urlSuffix;
                            urls.add(url);
                        }
                    } else {
                        for (int j = 1; j <= count; j++) {
                            String url = baseUrl + urlPrefix + String.valueOf(i)+ urlInfix +String.valueOf(j + prefix)+ urlSuffix;
                            urls.add(url);
                        }
                    }
                    NovelTypeStartPage newNovelTypeStartPage = novelTypeStartPage.clone();
                    newNovelTypeStartPage.setAllUrls(urls);
                    allGroup.add(newNovelTypeStartPage);
                }
            }else {
                List<String> urls = new ArrayList<>();
                for (int j = 1; j <= pageNumber; j++) {
                    String url = baseUrl + urlPrefix+ String.valueOf(i)+ urlInfix +String.valueOf(j)+ urlSuffix;
                    urls.add(url);
                }
                novelTypeStartPage.setAllUrls(urls);
                allGroup.add(novelTypeStartPage);
            }
        }
        futureList = new ArrayList<>(allGroup.size());
        getNovelUrlByTypeThreads = new ArrayList<>();
        for (NovelTypeStartPage startPage : allGroup) {
            GetNovelUrlByTypeThread byTypeThread = new GetNovelUrlByTypeThread();
            autowireCapableBeanFactory.autowireBean(byTypeThread);
            byTypeThread.setNovelTypeStartPage(startPage);
            Future<NovelTypeStartPage> future = executorService.submit(byTypeThread);
            //将每个线程对象放入集合，方便获取进度
            getNovelUrlByTypeThreads.add(byTypeThread);
            futureList.add(future);
        }

        List<NovelTypeStartPage> futureGroupList = new ArrayList<>();
        //线程阻塞获取线程返回结果
        for (Future<NovelTypeStartPage> novelTypeStartPageFuture : futureList) {
            NovelTypeStartPage novelTypeStartPage = novelTypeStartPageFuture.get();
            futureGroupList.add(novelTypeStartPage);
            //已经完成的从列表中剔除
//            if (novelTypeStartPageFuture.isDone()) {
//                getNovelUrlByTypeThreads.remove(novelTypeStartPageFuture);
//            }
        }
        //将【分组后获取到的结果】汇总到【原始未分组】的集合中，根据分组后对象中包含的类型添加
        for (NovelTypeStartPage novelTypeStartPage : list) {
            NovelType novelType = novelTypeStartPage.getNovelType();
            List<Novel> novels = new ArrayList<>();
            for (NovelTypeStartPage typeStartPage : futureGroupList) {
                if (typeStartPage.getNovelType() == novelType) {
                    novels.addAll(typeStartPage.getNovelList());
                }
            }
            novelTypeStartPage.setNovelList(novels);
        }
        //入库完成后将此线程的队列置空
        //getNovelUrlByTypeThreads = null;
        return list;
    }

    public static void main(String[] args) throws IOException {
//        AllNovelCrawler crawler = new AllNovelCrawler();
//        List<NovelTypeStartPage> list = crawler.aaa();
//        crawler.crawlerAllNovelByType(list);

//        String url = "http://www.2yt.org/class2";
//        String charset = "GBK";
//        String cssQuery = "#pagelink.pagelink a.last";
//        AllNovelCrawler allNovelCrawler = new AllNovelCrawler();
//        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager
//                = new PoolingHttpClientConnectionManager(60, TimeUnit.SECONDS);
//        //最大连接数
//        poolingHttpClientConnectionManager.setMaxTotal(500);
//        //路由基数
//        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(10);
//        BasicCookieStore cookieStore = new BasicCookieStore();
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setConnectionManager(poolingHttpClientConnectionManager)
//                .setDefaultCookieStore(cookieStore).build();
//        List<Long> list = new ArrayList<>();
//        for (int i = 1; i < 11; i++) {
//            String uri = url+"-"+ i+".html";
//            long l1 = System.currentTimeMillis();
//            int maxPageNumber = allNovelCrawler.getTotalPage(httpClient,uri,charset,cssQuery);
//            System.out.println("查询耗时" + (System.currentTimeMillis() - l1));
//            System.out.println("最大页数："+ maxPageNumber);
//            list.add(System.currentTimeMillis() - l1);
//        }
//        System.out.println(list);
    }

    /**
     * 从前台传递过来需要爬的类型起始页面、爬取规则、定时等，或者是否爬到最后一页
     * 获取从起始页开始，当前起始页所在的类别的最大页数，以及当前起始页的类别名称
     */
    public List<NovelTypeStartPage> crawlerTypePage(List<NovelTypeStartPage> list, Website website) throws Exception {
        //单线程耗时约在300-500ms之间
        //多线程则为第一次300，第二次在45-56ms之间
        List<Future<NovelTypeStartPage>> futureList = new ArrayList<>();
        //将list分别交给多线程去处理每个类型的小说有多少页
        //多线程每个线程获取一个类型的url，从连接池中获取连接，减少三次握手环节
//        for (NovelTypeStartPage startPage : list) {
//            GetTypeTotalPageNumberThread getTypeTotalPageNumberThread = new GetTypeTotalPageNumberThread();
//            autowireCapableBeanFactory.autowireBean(getTypeTotalPageNumberThread);
//            getTypeTotalPageNumberThread.setNovelTypeStartPage(startPage);
//            getTypeTotalPageNumberThread.setWebsite(website);
//            Future<NovelTypeStartPage> novelTypeStartPageFuture = executorService.submit(getTypeTotalPageNumberThread);
//            futureList.add(novelTypeStartPageFuture);
//        }
//        for (Future<NovelTypeStartPage> novelTypeStartPageFuture : futureList) {
//            //get方法获取执行结果，此方法会产生阻塞，会等到任务执行完成才返回
//            novelTypeStartPageFuture.get();
//        }

        for (int i = 0; i < list.size(); i++) {
            GetTypeTotalPageNumberThread getTypeTotalPageNumberThread = new GetTypeTotalPageNumberThread();
            autowireCapableBeanFactory.autowireBean(getTypeTotalPageNumberThread);
            getTypeTotalPageNumberThread.setNovelTypeStartPage(list.get(i));
            getTypeTotalPageNumberThread.setWebsite(website);
            Future<NovelTypeStartPage> novelTypeStartPageFuture = executorService.submit(getTypeTotalPageNumberThread);
            futureList.add(novelTypeStartPageFuture);
        }
        for (Future<NovelTypeStartPage> novelTypeStartPageFuture : futureList) {
            //get方法获取执行结果，此方法会产生阻塞，会等到任务执行完成才返回
            novelTypeStartPageFuture.get();
        }
        return list;
    }
}
