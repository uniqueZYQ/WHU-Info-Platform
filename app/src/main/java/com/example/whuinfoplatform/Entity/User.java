package com.example.whuinfoplatform.Entity;

public class User{
    private int code;
    private String response;
    private int id;
    private String pwd;
    private String nickname;
    private String realname;
    private String stdid;
    private byte[] picture;
    private int picture_version;

    public void setPicture_version(int picture_version) {
        this.picture_version=picture_version;
    }
    public int getPicture_version() {
        return picture_version;
    }
    public void setCode(int code) {
        this.code=code;
    }
    public int getCode() {
        return code;
    }
    public void setResponse(String response) {
        this.response=response;
    }
    public String getResponse() {
        return response;
    }
    public void setId(int id) {
        this.id=id;
    }
    public int getId() {
        return id;
    }
    public void setPwd(String pwd) {
        this.pwd=pwd;
    }
    public String getPwd() {
        return pwd;
    }
    public void setNickname(String nickname) {
        this.nickname=nickname;
    }
    public String getNickname() {
        return nickname;
    }
    public void setRealname(String realname) {
        this.realname=realname;
    }
    public String getRealname() {
        return realname;
    }
    public void setStdid(String stdid) {
        this.stdid=stdid;
    }
    public String getStdid() {
        return stdid;
    }
    public void setPicture(byte[] picture) {
        this.picture=picture;
    }
    public byte[] getPicture() {
        return picture;
    }
}
