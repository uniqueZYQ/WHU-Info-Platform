package com.example.whuinfoplatform.Dao;

import android.os.Build;
import androidx.annotation.RequiresApi;

import com.example.whuinfoplatform.Entity.User;

import org.json.JSONObject;
import java.util.Base64;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserConnection {

    private RequestBody formBody;
    public static String URL = "http://122.9.144.219:8080/myServlet/";

    public void initRegisterConnection(String stdid,String pwd,String realname,String nickname,String picture,okhttp3.Callback callback) {
        String Url=URL+"RegisterServlet";

        formBody = new FormBody.Builder()
                .add("stdid",stdid)
                .add("pwd", pwd)
                .add("realname", realname)
                .add("nickname", nickname)
                .add("picture",picture)
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryUserInfo(String id,okhttp3.Callback callback) {
        String Url=URL+"QueryUserServlet";
        formBody=new FormBody.Builder()
                .add("id",id)
                .build();
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void renewUserPicture(String id,String picture,okhttp3.Callback callback) {
        String Url=URL+"renewUserPictureServlet";
        formBody=new FormBody.Builder()
                .add("id",id)
                .add("picture",picture)
                .build();
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void renewUserInfo(String id,String pwd,String nickname,okhttp3.Callback callback) {
        String Url=URL+"renewUserServlet";
        formBody=new FormBody.Builder()
                .add("id",id)
                .add("pwd",pwd)
                .add("nickname",nickname)
                .build();
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void initLoginConnection(String stdid,String pwd,okhttp3.Callback callback) {
        String Url=URL+"LoginServlet";
        formBody = new FormBody.Builder()
                .add("stdid",stdid)
                .add("pwd",pwd)
                .build();
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseJSON(User user, String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            int id=jsonObject.getInt("id");
            String nickname=jsonObject.getString("nickname");
            String realname=jsonObject.getString("realname");
            int code=jsonObject.getInt("code");
            String stdid=jsonObject.getString("stdid");
            String response=jsonObject.getString("response");
            /*
            图片加载方案：
            以字节流的base64编码为String传输并存储，接收时服务器端以String传输，在此处解码
            --2021.7.25 0:54 CityGhost
             */
            String ss=jsonObject.getString("picture");
            byte[] picture = {};
            picture = Base64.getDecoder().decode(ss);
            showResponse(user,code,id,nickname,realname,response,stdid,picture);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showResponse(User user,int code,int id,String nickname,String realname,String response,String stdid,byte[] picture){
        user.setCode(code);
        user.setId(id);
        user.setNickname(nickname);
        user.setRealname(realname);
        user.setResponse(response);
        user.setStdid(stdid);
        user.setPicture(picture);
    }
}
