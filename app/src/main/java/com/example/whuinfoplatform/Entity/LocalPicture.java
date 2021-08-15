package com.example.whuinfoplatform.Entity;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

/*图片本地缓存*/
public class LocalPicture extends DataSupport{
    private int id;
    private int code;
    private int user_code;
    private int chat_code;
    private String response;
    private String picture;
    private int picture_version;

    public int getPicture_version() {
        return picture_version;
    }
    public void setPicture_version(int picture_version) {
        this.picture_version=picture_version;
    }
    public void setId(int id) {
        this.id=id;
    }
    public int getId() {
        return id;
    }
    public int getChat_code() {
        return chat_code;
    }
    public void setChat_code(int chat_code) {
        this.chat_code=chat_code;
    }
    public void setCode(int code) {
        this.code=code;
    }
    public int getCode() {
        return code;
    }
    public int getUser_code() {
        return user_code;
    }
    public void setUser_code(int user_code) {
        this.user_code=user_code;
    }
    public String getResponse() {
        return response;
    }
    public void setResponse(String response) {
        this.response=response;
    }
    public void setPicture(String picture) {
        this.picture=picture;
    }
    public String getPicture() {
        return picture;
    }

    public void infoPictureAddToLocal(int code,String picture){
        Connector.getDatabase();
        LocalPicture localPicture=new LocalPicture();
        localPicture.setPicture(picture);
        localPicture.setResponse("");
        localPicture.setCode(code);
        localPicture.setUser_code(0);
        localPicture.setChat_code(0);
        localPicture.setPicture_version(0);
        localPicture.save();
    }

    public void updateUserPicture(String user_code,String picture,int picture_version){
        Connector.getDatabase();
        List<LocalPicture> localPictures=DataSupport.where("user_code=?",user_code).select("picture_version").find(LocalPicture.class);
        if(localPictures.size()!=0){
            LocalPicture localPicture=new LocalPicture();
            localPicture.setPicture(picture);
            localPicture.setPicture_version(picture_version);
            localPicture.updateAll("user_code=?",user_code);
        }
        else{
            LocalPicture localPicture=new LocalPicture();
            localPicture.userPictureAddToLocal(Integer.valueOf(user_code).intValue(),picture,picture_version);
        }
    }

    public void userPictureAddToLocal(int user_code,String picture,int picture_version){
        Connector.getDatabase();
        LocalPicture localPicture=new LocalPicture();
        localPicture.setPicture(picture);
        localPicture.setResponse("");
        localPicture.setCode(0);
        localPicture.setUser_code(user_code);
        localPicture.setChat_code(0);
        localPicture.setPicture_version(picture_version);
        localPicture.save();
    }

    public void chatPictureAddToLocal(int chat_code,String picture){
        Connector.getDatabase();
        LocalPicture localPicture=new LocalPicture();
        localPicture.setPicture(picture);
        localPicture.setResponse("");
        localPicture.setCode(0);
        localPicture.setUser_code(0);
        localPicture.setChat_code(chat_code);
        localPicture.setPicture_version(0);
        localPicture.save();
    }
}
