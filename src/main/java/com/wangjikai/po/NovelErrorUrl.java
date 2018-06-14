package com.wangjikai.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author 22717.
 * @date 2018/4/12.
 * Description:
 */
//@Entity
@Getter
@Setter
public class NovelErrorUrl implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String url;
}
