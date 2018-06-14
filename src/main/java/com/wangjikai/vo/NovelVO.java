package com.wangjikai.vo;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author wang.
 * @date 2018/6/14.
 * Description:视图层对象
 */
@Getter
@Setter
public class NovelVO implements Serializable {

    private String name;

    private String author;

    private String picUrl;

    private String resume;

    private String typeName;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("author", author)
                .add("picUrl", picUrl)
                .add("resume", resume)
                .add("typeName", typeName)
                .toString();
    }
}
