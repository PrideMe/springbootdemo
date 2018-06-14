package com.wangjikai.po;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author 22717.
 * @date 2018/4/10.
 * Description:单本
 */
@Entity
@Getter
@Setter
public class Novel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int webSiteType;

    private String name;

    private String listUrl;

    private String author;

    private String picUrl;

    /**
     * 简介，要足够长
     */
    @Lob
    @Column(columnDefinition = "text")
    private String resume;

    /**
     * 小说的章节集合
     * 指定一对多的关系
     */
    @OneToMany(targetEntity = NovelUrl.class,mappedBy = "novel",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<NovelUrl> novelUrlList;

    @ManyToOne(targetEntity = NovelType.class,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "typeId",referencedColumnName = "id")
    private NovelType novelType;
}
