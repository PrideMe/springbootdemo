package com.wangjikai.po;

import lombok.Getter;
import lombok.Setter;

/**
 * @author wang.
 * @date 2018/5/8.
 * Description: 描述线程状态
 */
@Getter
@Setter
public class Progress {
    private int size;
    private int currentProgress;
    private String startPageUrl;
}
