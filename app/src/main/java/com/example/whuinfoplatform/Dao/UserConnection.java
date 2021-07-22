package com.example.whuinfoplatform.Dao;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whuinfoplatform.Entity.User;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserConnection {

    private RequestBody formBody;
    public static String URL = "http://122.9.144.219:8080/myServlet/";

   /* public void initRegisterConnection(String stdid,String pwd,String realname,String nickname){
        String Url=URL+"RegisterServlet";
        formBody = new FormBody.Builder()
                .add("stdid",stdid)
                .add("pwd", pwd)
                .add("realname", realname)
                .add("nickname", nickname)
                .build();
        requestUsingOkHttp(Url,new okhttp3.Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                parseJSON(result);
            }
        });
    }*/

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

    public void parseJSON(User user,String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            int id=jsonObject.getInt("id");
            String nickname=jsonObject.getString("nickname");
            String realname=jsonObject.getString("realname");
            int code=jsonObject.getInt("code");
            String stdid=jsonObject.getString("stdid");
            String response=jsonObject.getString("response");
            showResponse(user,code,id,nickname,realname,response,stdid);
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private void showResponse(User user,int code,int id,String nickname,String realname,String response,String stdid){
        user.setCode(code);
        user.setId(id);
        user.setNickname(nickname);
        user.setRealname(realname);
        user.setResponse(response);
        user.setStdid(stdid);
    }

}
