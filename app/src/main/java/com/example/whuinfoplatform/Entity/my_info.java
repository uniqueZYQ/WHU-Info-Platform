package com.example.whuinfoplatform.Entity;

public class my_info{
    private String time;
    private String form;
    private String detail;
    private String answered;
    private int id;

    public my_info(int id,String time,String form,String detail,String answered){
        this.time=time;
        this.form=form;
        this.detail=detail;
        this.answered=answered;
        this.id=id;
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
    public String getAnswered(){
        return answered;
    }
    public int getId(){
        return id;
    }
}
