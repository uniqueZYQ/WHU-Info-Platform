package com.example.whuinfoplatform.Dao;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Xml;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whuinfoplatform.Entity.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserConnection {

    private RequestBody formBody;
    public static String URL = "http://122.9.144.219:8080/myServlet/";

    public void initRegisterConnection(String stdid,String pwd,String realname,String nickname,byte[] picture,okhttp3.Callback callback) throws MalformedURLException {
        String Url=URL+"RegisterServlet";
        String s=new String(picture);
        formBody = new FormBody.Builder()
                .add("stdid",stdid)
                .add("pwd", pwd)
                .add("realname", realname)
                .add("nickname", nickname)
                .add("picture",s)
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public interface HttpCallbackListener{
        void onFinish(String response);

        void onError(Exception e);
    }

    /*public void uploadPictureForRegister(byte[] PostData,final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL u = null;
                HttpURLConnection con = null;
                InputStream inputStream = null;
                //尝试发送请求
                try {
                    u = new URL(URL+"RegisterServlet");
                    con = (HttpURLConnection) u.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.setUseCaches(false);
                    con.setRequestProperty("Content-Type", "application/octet-stream");
                    OutputStream outStream = con.getOutputStream();
                    outStream.write(PostData);
                    outStream.flush();
                    outStream.close();
                    //读取返回内容
                    inputStream = con.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    if(listener!=null){
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if(listener!=null){
                        listener.onError(e);
                    }
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();

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
