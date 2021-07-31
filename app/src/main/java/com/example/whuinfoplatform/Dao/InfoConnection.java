package com.example.whuinfoplatform.Dao;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.whuinfoplatform.Entity.MyInformation;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.Entity.WebResponse;
import com.example.whuinfoplatform.Entity.my_info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Base64;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class InfoConnection {
    private RequestBody formBody;
    public static String URL = "http://122.9.144.219:8080/myServlet/";

    public void initRegisterConnection(String owner_id,String send_date,String answered,String form,String fd_form,String help_form,String price,String date,
                                       String place,String lesson,String score,String detail,String reward,String picture1,String picture2,
                                       String picture3,String picture4,String placeId,okhttp3.Callback callback) {
        String Url=URL+"PublishInfoServlet";

        formBody = new FormBody.Builder()
                .add("owner_id",owner_id)
                .add("send_date", send_date)
                .add("answered", answered)
                .add("form", form)
                .add("fd_form",fd_form)
                .add("help_form",help_form)
                .add("price", price)
                .add("date", date)
                .add("place", place)
                .add("lesson",lesson)
                .add("score",score)
                .add("detail", detail)
                .add("reward", reward)
                .add("picture1", picture1)
                .add("picture2",picture2)
                .add("picture3",picture3)
                .add("picture4", picture4)
                .add("placeId", placeId)
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryMyInfoConnection(String id,okhttp3.Callback callback) {
        String Url=URL+"QueryMyInfoServlet";

        formBody = new FormBody.Builder()
                .add("id",id)
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void parseJSONForInfoResponse(WebResponse Response, String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            int code=jsonObject.getInt("code");
            String response=jsonObject.getString("response");

            showResponseForInfoResponse(Response,code,response);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int parseJSONForMyInfoResponse(String json, List<my_info> my_info_list){
        try{
            //JSONObject jsonObject=new JSONObject(json);
            /*int code=jsonObject.getInt("code");
            String response=jsonObject.getString("response");*/
            JSONArray jsonArray=new JSONArray(json);
            /*for(int i=0;i<jsonArray.length();i++){
                jsonArray.getJSONObject(0).
            }*/
            for(int i=0;i<jsonArray.length();i++){
                String date=jsonArray.getJSONObject(i).getString("send_date");
                String form=
                        jsonArray.getJSONObject(i).getInt("form")==1?"私人性-学术咨询信息":jsonArray.getJSONObject(i).getInt("form")==2?"私人性-日常求助信息":
                        jsonArray.getJSONObject(i).getInt("form")==3?"私人性-物品出售信息":jsonArray.getJSONObject(i).getInt("form")==4?"私人性-物品求购信息":
                        jsonArray.getJSONObject(i).getInt("form")==5?"组织性信息":"课程点评信息";
                String detail=jsonArray.getJSONObject(i).getString("detail");
                String answered=jsonArray.getJSONObject(i).getInt("answered")==0?"暂无响应":"已被响应";
                int infoid=jsonArray.getJSONObject(i).getInt("id");
                my_info myinfo=new my_info(infoid,date,form,detail,answered);
                my_info_list.add(myinfo);
            }
            return jsonArray.length();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    private void showResponseForInfoResponse(WebResponse Response,int code,String response){
        Response.setCode(code);
        Response.setResponse(response);
    }
}
