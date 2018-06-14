package com.wangjikai.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author 22717.
 * @date 2018/4/2.
 * Description:
 */
@Entity
@Getter
@Setter
public class NovelUrl implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String url;
    private String chapterName;
    private String mongoId;
    @Column(columnDefinition = "timestamp")
    private Date savedTime;

    @ManyToOne(targetEntity = Novel.class,cascade = CascadeType.ALL)
    @JoinColumn(name="novel_id",referencedColumnName = "id")
    private Novel novel;

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (!(o instanceof NovelUrl)) {return false;}
        NovelUrl novelUrl = (NovelUrl) o;
        return Objects.equals(url, novelUrl.url) &&
                Objects.equals(chapterName, novelUrl.chapterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, chapterName);
    }
}
