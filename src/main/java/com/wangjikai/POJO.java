package com.wangjikai;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by jikai_wang on 2018/3/23.
 */
public class POJO {

    @NotNull
    @Size(max = 10,min = 5)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
