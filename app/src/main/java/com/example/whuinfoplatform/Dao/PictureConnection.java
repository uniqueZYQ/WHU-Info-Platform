package com.example.whuinfoplatform.Dao;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.whuinfoplatform.Entity.WebResponse;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PictureConnection {
    private RequestBody formBody;
    public static String URL = "http://122.9.144.219:8080/myServlet/";

    public void initUploadConnection(String picture,okhttp3.Callback callback) {
        String Url=URL+"UploadPictureServlet";

        formBody = new FormBody.Builder()
                .add("picture",picture)
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseJSONForPictureResponse(WebResponse Response, String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            int code=jsonObject.getInt("code");
            String response=jsonObject.getString("response");
            int id=jsonObject.getInt("id");

            showResponseForInfoResponse(Response,code,response,id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showResponseForInfoResponse(WebResponse Response,int code,String response,int id){
        Response.setCode(code);
        Response.setResponse(response);
        Response.setId(id);
    }
}
