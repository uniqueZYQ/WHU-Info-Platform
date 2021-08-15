package com.example.whuinfoplatform.Entity;

public class srch_info{
    private String time;
    private String form;
    private String detail;
    private String owner;
    private int owner_id;
    private int id;
    private int self;
    private int first_picture;
    private int views;

    public srch_info(int id,String time,String form,String detail,String owner,int owner_id,int self,int first_picture,int views){
        this.time=time;
        this.form=form;
        this.detail=detail;
        this.id=id;
        this.owner=owner;
        this.self=self;
        this.owner_id=owner_id;
        this.views=views;
        this.first_picture=first_picture;
    }

    public int getViews(){
        return views;
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
    public int getOwner_id() {
        return owner_id;
    }
    public int getId(){
        return id;
    }
    public int getSelf(){
        return self;
    }
    public int getFirst_picture() {
        return first_picture;
    }
}
