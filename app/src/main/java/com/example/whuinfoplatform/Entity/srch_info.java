package com.example.whuinfoplatform.Entity;

public class srch_info {
    private String time;
    private String form;
    private String detail;
    private String owner;
    private int id;
    private int self;
    public srch_info(int id,String time,String form,String detail,String owner,int self){
        this.time = time;
        this.form = form;
        this.detail = detail;
        this.id = id;
        this.owner=owner;
        this.self=self;
    }
    public String getTime(){
        return time;
    }
    public String getForm(){
        return form;
    }
    public String getDetail(){
        return detail;
    }
    public String getOwner(){
        return owner;
    }
    public int getId(){
        return id;
    }
    public int getSelf(){
        return self;
    }
}
