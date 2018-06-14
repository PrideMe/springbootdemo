package com.wangjikai.po;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;

/**
 * @author 22717.
 * @date 2018/4/2.
 * Description:
 */
@Document
@Getter
@Setter
public class NovelContent implements Serializable {
    @Id
    private String id;
    private String title;
    private String content;
    private int novelId;
}