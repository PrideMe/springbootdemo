package com.wangjikai.jd;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by jikai_wang on 2018/3/28.
 * 模拟登陆京东
 */
public class JDShoping {
    private String url = "https://passport.jd.com/uc/login?ltype=logout";
    private String logInurl = "https://passport.jd.com/uc/loginService";
    private String AuthImgSrc = "";
    private Map<String, String> paramMap=null;
    private static String LOGIN_NAME = "15628810640";  //"15898917930"
    private static String LOGIN_PSW = "wangjikai159";  //Wang@15820091357
    private static String AuthCodeImgPath = "E:/test/验证码.png";
    private static String AuthCookiePath = "E:/test/"+LOGIN_NAME+"cookies";

    public List<String> doLogin() throws Exception {
        File file = new File(AuthCookiePath);
        BasicCookieStore cookieStore = null;
        List<String> list = new ArrayList<>();
        if (file.exists()) {
            //即使cookie存在，过了网站的session有效期本地登录的cookie也可能过期
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            cookieStore = (BasicCookieStore) objectInputStream.readObject();
            //进入购物车
            RequestBuilder builderCart = RequestBuilder.get().setUri("https://cart.jd.com/cart.action");
            setHeadersForCart(builderCart);
            HttpUriRequest getCart = builderCart.build();
            CloseableHttpClient closeableHttpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            CloseableHttpResponse responseCart = closeableHttpClient.execute(getCart);
            HttpEntity entityCart = responseCart.getEntity();
            Document documentCart = Jsoup.parse(EntityUtils.toString(entityCart));
            //判断一下session是否失效
            Elements elements = documentCart.select("div.item-form .item-msg .p-name > a");
            System.err.println("购物车信息");
            list.add("从E盘加载cookie然后登陆。。。");
            for (Element element : elements) {
                System.err.println(element.text());
                list.add(element.text());
            }
            responseCart.close();
            closeableHttpClient.close();
            return list;
        } else {
            cookieStore = new BasicCookieStore();
        }
        CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = client.execute(get);
        String rs = "";
        try {
            HttpEntity entity = response.getEntity();
            String content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            Document document = Jsoup.parse(content);
            paramMap = getMapFromDoc(document);
            AuthImgSrc = "https:"+getImgsrcFromDoc(document);
            //获取验证码图片 并保存到指定路径
            RequestBuilder builder = RequestBuilder.get().setUri(new URI(AuthImgSrc + "&yys=" + System.currentTimeMillis()));
            setHeradersForAuthImg(builder);
            HttpUriRequest getAuthCode = builder.build();
            CloseableHttpResponse response3 = client.execute(getAuthCode);
            try {
                HttpEntity httpEntity = response3.getEntity();
                FileUtils.copyInputStreamToFile(httpEntity.getContent(), new File(AuthCodeImgPath));
                EntityUtils.consume(httpEntity);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response3.close();
            }
            URI uri = uri = new URI(logInurl + "?uuid=" + paramMap.get("uuid") + "&ltype=logout" + "&r=" + Math.random() + "&version=2018");
            RequestBuilder builder2 = RequestBuilder.post().setUri(uri);
            setHeadersForLogIn(builder2);
            System.out.println("输入验证码,并以回车结束");
            Scanner in = new Scanner(System.in);
            File file1 = new File(AuthCodeImgPath);
            file1.deleteOnExit();
            String code = in.next();
            paramMap.put("authcode", code);
            //POST 参数
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                builder2.addParameter(entry.getKey(), entry.getValue());
            }
            HttpUriRequest login = builder2.build();
            CloseableHttpResponse response4 = client.execute(login);
            try {
                HttpEntity loginEntity = response4.getEntity();
                rs = EntityUtils.toString(loginEntity);
                EntityUtils.consume(loginEntity);
            } finally {
                response4.close();
            }
            //进入购物车
            RequestBuilder builderCart = RequestBuilder.get().setUri("https://cart.jd.com/cart.action");
            setHeadersForCart(builderCart);
            HttpUriRequest getCart = builderCart.build();
            CloseableHttpResponse responseCart = HttpClients.custom().setDefaultCookieStore(cookieStore).build().execute(getCart);
            HttpEntity entityCart = responseCart.getEntity();
            Document documentCart = Jsoup.parse(EntityUtils.toString(entityCart));
            Elements elements = documentCart.select("div.item-form .item-msg .p-name > a");
            System.out.println("购物车信息");
            list.add("重新登陆。。。");
            for (Element element : elements) {
                System.err.println(element.text());
                list.add(element.text());
            }
        } finally {
            response.close();
            client.close();
        }
//        for(Cookie cookie:cookieStore.getCookies()){
//            System.out.println(String.format("cookie键:%s,cookie值:%s,cookie路径:%s",cookie.getName(),cookie.getValue(),cookie.getPath()));
//        }
//        System.out.println("状态："+rs);
        if (rs.contains("emptyAuthcode") || rs.contains("302")) {
            System.err.println("登录失败!!验证码有问题");
        } else {
            if (!file.exists()) {
                //将cookie持久化到文件中
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
                objectOutputStream.writeObject(cookieStore);
                objectOutputStream.close();
            }
            return list;
        }
        return list;
    }

    public static void main(String[] args) throws Exception {
        JDShoping shoping = new JDShoping();
        System.out.println(shoping.doLogin());
    }

    public static Map<String,String> getMapFromDoc(Document doc) {
        Map<String, String> map = new HashMap<>();
        Elements e = doc.getElementsByTag("form");
        Elements ele = e.select("input");
        for (Element element : ele) {
            map.put(element.attr("name"), element.attr("value"));
        }
        map.put("loginname", LOGIN_NAME);
        map.put("loginpwd", LOGIN_PSW);
        map.put("nloginpwd", LOGIN_PSW);
        return map;
    }

    public static String getImgsrcFromDoc(Document doc) {
        Elements e = doc.getElementsByTag("img");
        for (Element element : e) {
            if (!"".equals(element.attr("src2"))) {
                return element.attr("src2");
            }
        }
        return "";
    }

    /**请求图片验证码的请求头**/
    public static void setHeradersForAuthImg(RequestBuilder builder){
        builder.setHeader("Accept", "image/webp,*/*;q=0.8");
        builder.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        builder.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        builder.setHeader("Connection", "keep-alive");
        builder.setHeader("Host", "authcode.jd.com");
        builder.setHeader("Referer", "https://passport.jd.com/uc/login?ltype=logout");//必须。因为没弄这个浪费了很长时间
        builder.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");
    }

    /**登录的请求头**/
    public static void setHeadersForLogIn(RequestBuilder builder){
        builder.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        builder.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        builder.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
        builder.setHeader("Connection", "keep-alive");
        builder.setHeader("Host", "passport.jd.com");
        builder.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");
    }

    /**购物车的请求头**/
    public static void setHeadersForCart(RequestBuilder builder){
        builder.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        builder.setHeader("Accept-Encoding", "gzip, deflate, br");
        builder.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        builder.setHeader("Connection", "keep-alive");
        builder.setHeader("Host", "cart.jd.com");
        builder.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36");
    }
}
