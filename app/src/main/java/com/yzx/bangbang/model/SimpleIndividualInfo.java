package com.yzx.bangbang.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/25.
 */
public class SimpleIndividualInfo implements Serializable {
    public String name;
    public int id;
    public SimpleIndividualInfo(int id,String name){
        this.id = id;
        this.name = name;
    }
}
