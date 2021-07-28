package com.example.whuinfoplatform.Entity;

import org.litepal.crud.DataSupport;

public class LocalPicture extends DataSupport {
    private int id;
    private int code;
    private String picture;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPicture() {
        return picture;
    }
}
