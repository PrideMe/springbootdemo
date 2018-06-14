package com.wangjikai.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author 22717.
 * @date 2018/4/11.
 * Description:小说分类
 */
@Entity
@Getter
@Setter
public class NovelType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String typeName;
    @OneToMany(targetEntity = Novel.class,mappedBy = "novelType",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Novel> novels;

    @ManyToOne(targetEntity = Website.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "webSiteId",referencedColumnName = "id")
    private Website website;
}
