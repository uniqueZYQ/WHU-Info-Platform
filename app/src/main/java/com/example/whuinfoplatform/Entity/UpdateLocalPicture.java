package com.example.whuinfoplatform.Entity;

import android.content.Context;
import android.os.Looper;

import com.example.whuinfoplatform.Dao.UserConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class UpdateLocalPicture {

    public void getMyMsgOppoIdList(Context context,List<my_msg> msgList){
        for(int i=0;i<msgList.size();i++){
            UserConnection userConnection=new UserConnection();
            int finalI = i;
            userConnection.queryUserInfo(String.valueOf(msgList.get(i).getOppo_id()), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    BToast.showText(context,"服务器连接失败，请检查网络设置", false);
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        if(jsonObject.getInt("code")!=101){
                            Looper.prepare();
                            BToast.showText(context,jsonObject.getString("response"), false);
                            Looper.loop();
                        }
                        else{
                            LocalPicture localPicture=new LocalPicture();
                            localPicture.setPicture(jsonObject.getString("picture"));
                            localPicture.updateAll("user_code=?",String.valueOf(msgList.get(finalI).getOppo_id()));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(context,"数据解析失败！", false);
                        Looper.loop();
                    }
                }
            });
        }
    }

    public void getInfoOwnerIdList(Context context,List<srch_info> old_infoList){
        List<Integer> infoList=new ArrayList<Integer>();
        for(int i=0;i<old_infoList.size();i++){
            if(!infoList.contains(old_infoList.get(i).getOwner_id())){
                infoList.add(old_infoList.get(i).getOwner_id());
            }
        }
        for(int i=0;i<infoList.size();i++){
            UserConnection userConnection=new UserConnection();
            int finalI = i;
            userConnection.queryUserInfo(String.valueOf(infoList.get(i)), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    BToast.showText(context,"服务器连接失败，请检查网络设置", false);
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        if(jsonObject.getInt("code")!=101){
                            Looper.prepare();
                            BToast.showText(context,jsonObject.getString("response"), false);
                            Looper.loop();
                        }
                        else{
                            LocalPicture localPicture=new LocalPicture();
                            localPicture.setPicture(jsonObject.getString("picture"));
                            localPicture.updateAll("user_code=?",String.valueOf(infoList.get(finalI)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(context,"数据解析失败！", false);
                        Looper.loop();
                    }
                }
            });
        }
    }

    public void update(Context context, List<Integer> user_id_list){
        for(int i=0;i<user_id_list.size();i++){
            UserConnection userConnection=new UserConnection();
            int finalI = i;
            userConnection.queryUserInfo(String.valueOf(user_id_list.get(i)), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    BToast.showText(context,"服务器连接失败，请检查网络设置", false);
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        if(jsonObject.getInt("code")!=101){
                            Looper.prepare();
                            BToast.showText(context,jsonObject.getString("response"), false);
                            Looper.loop();
                        }
                        else{
                            LocalPicture localPicture=new LocalPicture();
                            localPicture.setPicture(jsonObject.getString("picture"));
                            localPicture.updateAll("user_code=?",String.valueOf(user_id_list.get(finalI)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(context,"数据解析失败！", false);
                        Looper.loop();
                    }
                }
            });
        }
    }
}
