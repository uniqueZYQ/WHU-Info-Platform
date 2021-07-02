package com.example.whuinfoplatform.Entity;

import org.litepal.crud.DataSupport;

public class last_show extends DataSupport {
    private int id;
    private int user_id;
    private int last_show_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLast_show_id() {
        return last_show_id;
    }
    public void setLast_show_id(int last_show_id){
        this.last_show_id = last_show_id;
    }

    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
