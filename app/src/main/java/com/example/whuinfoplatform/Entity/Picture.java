package com.example.whuinfoplatform.Entity;

import org.litepal.crud.DataSupport;

public class Picture extends DataSupport {
    private int id;
    private byte[] picture;

    public int getId() {
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public byte[] getPicture(){
        return picture;
    }
    public void setPicture(byte[] picture){
        this.picture=picture;
    }
}
