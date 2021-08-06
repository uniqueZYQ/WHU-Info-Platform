package com.example.whuinfoplatform.Entity;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.example.whuinfoplatform.Dao.CachedPictureConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/*图片本地缓存*/
public class LocalPicture extends DataSupport {
    private int id;
    private int code;
    private int user_code;
    private int chat_code;
    private String response;
    private String picture;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getChat_code() {
        return chat_code;
    }

    public void setChat_code(int chat_code) {
        this.chat_code = chat_code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public int getUser_code() {
        return user_code;
    }

    public void setUser_code(int user_code) {
        this.user_code = user_code;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setPicture(String picture) {
        this.picture = picture;
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
        localPicture.save();
    }

    public void userPictureAddToLocal(Context context,int user_code, String picture){
        Connector.getDatabase();
        LocalPicture localPicture=new LocalPicture();
        localPicture.setPicture(picture);
        localPicture.setResponse("");
        localPicture.setCode(0);
        localPicture.setUser_code(user_code);
        localPicture.setChat_code(0);
        localPicture.save();
        CachedPictureConnection cachedPictureConnection=new CachedPictureConnection();
        cachedPictureConnection.addByUserCodeConnection(String.valueOf(user_code), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(context,"服务器连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    if(jsonObject.getInt("code")!=101){
                        Looper.prepare();
                        Toast.makeText(context,jsonObject.getString("response"),Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(context,"数据解析失败！",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    public void chatPictureAddToLocal(int chat_code,String picture){
        Connector.getDatabase();
        LocalPicture localPicture=new LocalPicture();
        localPicture.setPicture(picture);
        localPicture.setResponse("");
        localPicture.setCode(0);
        localPicture.setUser_code(0);
        localPicture.setChat_code(chat_code);
        localPicture.save();
    }
}
