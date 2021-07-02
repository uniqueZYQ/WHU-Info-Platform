package com.example.whuinfoplatform.Entity;

public class my_msg {
    private String last_time;
    private String last_detail;
    private String oppo_name;
    private int id;
    private String last;
    public my_msg(int id,String oppo_name,String last_time,String last_detail,String last){
        this.last_time = last_time;
        this.last_detail = last_detail;
        this.id = id;
        this.oppo_name = oppo_name;
        this.last=last;
    }
    public String getLastTime(){
        return last_time;
    }
    public String getLastDetail(){
        return last_detail;
    }
    public String getOppoName(){
        return oppo_name;
    }
    public int getId(){
        return id;
    }
    public String getLast(){
        return last;
    }
    public void setLast(String last){
        this.last = last;
    }
}
