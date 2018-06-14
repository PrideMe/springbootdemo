package com.wangjikai.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author 22717.
 * @date 2018/4/24.
 * Description:小说网站源
 */
@Entity
@Getter
@Setter
public class Website implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String webSiteName;

    private String webSiteBaseUrl;

    private int webSiteTypeNumber;

    private String webSiteUrlPrefix;

    private String webSiteUrlInfix;

    private String webSiteUrlSuffix;

    private String webSiteCharSet;

    private String webSiteFirstPageSuffix;

    private String webSiteNumberCssQuery;

    private String webSiteTypeNameCssQuery;

    private String novelCssQuery;

    private String novelNameCssQuery;

    private String novelListUrlCssQuery;

    private String novelPicUrlCssQuery;

    private String novelAuthorCssQuery;

    private String novelResumeCssQueryByType;

    private String novelResumeCssQueryByContent;

    /**
     * 在类型页穷举所有页面，如果大于分组数，则依据分组数进行分组
     */
    private int pageSplitSize;

    /**
     * 章节列表，单个小说的所有章节的列表
     */
    private String novelListCssQuery;

    /**
     * 获取小说标题的JSoup语法
     */
    private String novelTitleCssQuery;

    /**
     * 获取小说内容的JSoup语法
     */
    private String novelContentCssQuery;

    /**
     * 小说章节内容页的url拼接方式。只需要主页拼接、需要多层拼接
     */
    private boolean needBaseIndex;

    @OneToMany(targetEntity = NovelType.class,mappedBy = "website",cascade = CascadeType.ALL)
    private List<NovelType> novelTypes;
}
