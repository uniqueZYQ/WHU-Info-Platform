package com.example.whuinfoplatform.Dao;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Entity.UpdateLocalPicture;
import com.example.whuinfoplatform.Entity.my_msg;

import org.json.JSONArray;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MsgConnection{
    private RequestBody formBody;
    public static String URL="http://122.9.144.219:8080/myServlet/";

    public void queryMsgAboutUser(String sub_id,String obj_id,okhttp3.Callback callback){
        String Url=URL+"MsgServlet";

        formBody=new FormBody.Builder()
                .add("sub_id",sub_id)
                .add("obj_id", obj_id)
                .add("type","1")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryMsgById(String id,okhttp3.Callback callback){
        String Url=URL+"MsgServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","2")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryMsgAboutUserByOneWay(String sub_id,String obj_id,okhttp3.Callback callback){
        String Url=URL+"MsgServlet";

        formBody=new FormBody.Builder()
                .add("sub_id",sub_id)
                .add("obj_id", obj_id)
                .add("type","3")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryMsgAboutUserForOneUser(String id,okhttp3.Callback callback){
        String Url=URL+"MsgServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","4")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryMsgAboutUserForObjUser(String obj_id,okhttp3.Callback callback){
        String Url=URL+"MsgServlet";

        formBody=new FormBody.Builder()
                .add("obj_id",obj_id)
                .add("type","5")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void recallMsgById(String id,okhttp3.Callback callback){
        String Url=URL+"MsgServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","6")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void sendMsgPicture(String sub_id,String obj_id,String picture,String time,okhttp3.Callback callback){
        String Url=URL+"MsgServlet";

        formBody=new FormBody.Builder()
                .add("sub_id",sub_id)
                .add("obj_id",obj_id)
                .add("picture",picture)
                .add("time",time)
                .add("type","7")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void sendMsgWithoutPicture(String sub_id,String obj_id,String content,String time,okhttp3.Callback callback){
        String Url=URL+"MsgServlet";

        formBody=new FormBody.Builder()
                .add("sub_id",sub_id)
                .add("obj_id",obj_id)
                .add("content",content)
                .add("time",time)
                .add("type","8")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    public int parseJSONMsgResponse(String json,int sub_id,List<Msg> msgList){
        try{
            JSONArray jsonArray=new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
                /*无论是无历史发布信息还是只有一条历史发布信息，返回的length都是1，在此做出判断*/
                int code=jsonArray.getJSONObject(i).getInt("code");
                if(code==102){
                    return 0;
                }
                else if(code!=101){
                    return -1;
                }
                Msg cumsg=new Msg();
                cumsg.setTime(jsonArray.getJSONObject(i).getString("time"));
                cumsg.setObj_id(jsonArray.getJSONObject(i).getInt("obj_id"));
                cumsg.setSub_id(jsonArray.getJSONObject(i).getInt("sub_id"));
                cumsg.setContent(jsonArray.getJSONObject(i).getString("content"));
                cumsg.setPicture(jsonArray.getJSONObject(i).getInt("picture"));
                cumsg.setRecalled(jsonArray.getJSONObject(i).getInt("recalled"));
                cumsg.setId(jsonArray.getJSONObject(i).getInt("id"));
                cumsg.setOppo_nickname(jsonArray.getJSONObject(i).getString("oppo_nickname"));
                if(sub_id==jsonArray.getJSONObject(i).getInt("sub_id"))cumsg.setType(1);
                else cumsg.setType(0);
                msgList.add(cumsg);
            }
            return jsonArray.length();
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    public int parseJSONMyMsgResponse(Context context,String json,List<my_msg> myMsgList){
        try{
            JSONArray jsonArray=new JSONArray(json);
            for(int i=0;i<jsonArray.length();i++){
                /*无论是无历史发布信息还是只有一条历史发布信息，返回的length都是1，在此做出判断*/
                int code=jsonArray.getJSONObject(i).getInt("code");
                if(code==102){
                    return 0;
                }
                else if(code!=101){
                    return -1;
                }
                else{
                    my_msg cumsg=new my_msg(jsonArray.getJSONObject(i).getInt("id")
                            ,jsonArray.getJSONObject(i).getInt("oppo_id")
                            ,jsonArray.getJSONObject(i).getString("oppo_name")
                            ,jsonArray.getJSONObject(i).getString("last_time")
                            ,jsonArray.getJSONObject(i).getString("last_detail")
                            ,jsonArray.getJSONObject(i).getString("last"));
                    myMsgList.add(cumsg);
                }
            }
            //更新对方头像信息
            UpdateLocalPicture updateLocalPicture=new UpdateLocalPicture();
            updateLocalPicture.updateMyMsgOppoPicture(context,myMsgList);
            return jsonArray.length();
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}
