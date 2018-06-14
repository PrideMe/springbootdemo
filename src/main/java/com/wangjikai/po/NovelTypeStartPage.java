package com.wangjikai.po;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wang.
 * @date 2018/5/4.
 * Description: 起始页配置
 */
@Getter
@Setter
public class NovelTypeStartPage implements Serializable,Cloneable {
    private int webSiteType;
    private String startPageUrl;
    private String endPageUrl;
    private String baseUrl;
    private String urlPrefix;
    private String urlInfix;
    private String urlSuffix;
    private String numberCssQuery;
    private String typeNameCssQuery;
    private String novelCssQuery;
    private String novelNameCssQuery;
    private String novelListUrlCssQuery;
    private String novelPicUrlCssQuery;
    private String novelAuthorCssQuery;
    private String novelResumeCssQueryByType;
    private String novelResumeCssQueryByContent;
    private String charSet;
    private String quartzTime;
    private String typeName;
    private int totalPage;
    private List<String> allUrls;
    private NovelType novelType;
    private List<Novel> novelList = new ArrayList<>();

    @Override
    public NovelTypeStartPage clone() throws CloneNotSupportedException {
        return (NovelTypeStartPage)super.clone();
    }
}
