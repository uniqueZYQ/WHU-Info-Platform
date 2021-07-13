package com.example.whuinfoplatform.Entity;

import org.litepal.crud.DataSupport;

public class Msg extends DataSupport {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    private String content;
    private int type;
    private int sub_id;
    private int obj_id;
    private String time;
    private int id;
    private int picture;

    public int getSub_id(){
        return sub_id;
    }
    public int getObj_id(){
        return obj_id;
    }
    public String getTime(){
        return time;
    }
    public String getContent(){
        return content;
    }
    public int getType(){
        return type;
    }
    public int getId(){return id;}
    public int getPicture(){return picture;}

    public void setContent(String content) {
        this.content = content;
    }
    public void setObj_id(int obj_id) {
        this.obj_id = obj_id;
    }
    public void setSub_id(int sub_id) {
        this.sub_id = sub_id;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setPicture(int picture) {
        this.picture = picture;
    }
    public void setId(int id) {
        this.id = id;
    }
}
