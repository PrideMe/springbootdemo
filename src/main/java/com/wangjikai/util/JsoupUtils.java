package com.wangjikai.util;

import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.util.StringUtils;

/**
 * Created by jikai_wang on 2018/3/16.
 * jsoup工具类，处理html数据
 */
public class JsoupUtils {
    //用户内容过滤规则白名单
    private final static Whitelist user_content_filter = Whitelist.relaxed();
    static {
        user_content_filter.addTags("embed","object","param","span","div");
        user_content_filter.addAttributes(":all", "style", "class", "id", "name");
        user_content_filter.addAttributes("object", "width", "height","classid","codebase");
        user_content_filter.addAttributes("param", "name", "value");
        user_content_filter.addAttributes("embed", "src","quality","width","height","allowFullScreen","allowScriptAccess","flashvars","name","type","pluginspage");
    }

    public static String cleanUserInputHTMLContent(String html) {
        if (StringUtils.isEmpty(html)) return "";
        return Jsoup.clean(html,user_content_filter);
    }

    public static void main(String[] args) {
        //System.out.println(JsoupUtils.cleanUserInputHTMLContent("\" onclick=alert('111') //"));
        //String string = "' onclick=alert('111') //";
        //System.out.println(StringEscapeUtils.escapeHtml4(string));
        String a = "wjk";
        String v = a.intern();
        System.out.println(a == v);
    }
}
