package com.example.whuinfoplatform.Dao;

import com.example.whuinfoplatform.Entity.WebResponse;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CachedPictureConnection {
    private RequestBody formBody;
    public static String URL = "http://122.9.144.219:8080/myServlet/";

    public void addByUserCodeConnection(String user_code,okhttp3.Callback callback) {
        String Url=URL+"CachedPictureServlet";

        formBody = new FormBody.Builder()
                .add("user_code", user_code)
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }
}
