package com.example.whuinfoplatform.Entity;

import android.content.Context;
import android.os.Looper;

import com.example.whuinfoplatform.Dao.UserConnection;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class UpdateLocalPicture{

    public void updateMyMsgOppoPicture(Context context,List<my_msg> msgList){
        for(int i=0;i<msgList.size();i++){
            List<LocalPicture> version=DataSupport.where("user_code=?",String.valueOf(msgList.get(i).getOppo_id())).select("picture_version").find(LocalPicture.class);
            if(version.size()!=0){
                UserConnection userConnection=new UserConnection();
                int finalI=i;
                userConnection.queryUserPicture(String.valueOf(msgList.get(i).getOppo_id()),String.valueOf(version.get(0).getPicture_version()), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call,IOException e){
                        Looper.prepare();
                        BToast.showText(context,"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call,Response response) throws IOException{
                        String result=response.body().string();
                        try{
                            JSONObject jsonObject=new JSONObject(result);
                            if((jsonObject.getInt("code")!=101)&&(jsonObject.getInt("code")!=102)){
                                Looper.prepare();
                                BToast.showText(context,jsonObject.getString("response"), false);
                                Looper.loop();
                            }
                            else if(jsonObject.getInt("code")==101){
                                LocalPicture localPicture=new LocalPicture();
                                localPicture.updateUserPicture(String.valueOf(msgList.get(finalI).getOppo_id()),jsonObject.getString("picture"),jsonObject.getInt("picture_version"));
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            Looper.prepare();
                            BToast.showText(context,"数据解析失败！",false);
                            Looper.loop();
                        }
                    }
                });
            }
            else{
                UserConnection userConnection=new UserConnection();
                int finalI=i;
                userConnection.queryUserPicture(String.valueOf(msgList.get(i).getOppo_id()),"-1", new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call,IOException e){
                        Looper.prepare();
                        BToast.showText(context,"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call,Response response) throws IOException{
                        String result=response.body().string();
                        try{
                            JSONObject jsonObject=new JSONObject(result);
                            if((jsonObject.getInt("code")!=101)&&(jsonObject.getInt("code")!=102)){
                                Looper.prepare();
                                BToast.showText(context,jsonObject.getString("response"),false);
                                Looper.loop();
                            }
                            else if(jsonObject.getInt("code")==101){
                                LocalPicture localPicture=new LocalPicture();
                               localPicture.updateUserPicture(String.valueOf(msgList.get(finalI).getOppo_id()),jsonObject.getString("picture"),jsonObject.getInt("picture_version"));
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            Looper.prepare();
                            BToast.showText(context,"数据解析失败！",false);
                            Looper.loop();
                        }
                    }
                });
            }
        }
    }

    public void updateInfoOwnerPicture(Context context,List<srch_info> old_infoList){
        List<Integer> infoList=new ArrayList<>();
        for(int i=0;i<old_infoList.size();i++){
            if(!infoList.contains(old_infoList.get(i).getOwner_id())){
                infoList.add(old_infoList.get(i).getOwner_id());
            }
        }
        for(int i=0;i<infoList.size();i++){
            List<LocalPicture> version=DataSupport.where("user_code=?",String.valueOf(infoList.get(i))).select("picture_version").find(LocalPicture.class);
            if(version.size()!=0){
                int finalI=i;
                UserConnection userConnection=new UserConnection();
                userConnection.queryUserPicture(String.valueOf(infoList.get(i)),String.valueOf(version.get(0).getPicture_version()), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call,IOException e){
                        Looper.prepare();
                        BToast.showText(context,"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call,Response response) throws IOException{
                        String result=response.body().string();
                        try{
                            JSONObject jsonObject=new JSONObject(result);
                            if((jsonObject.getInt("code")!=101)&&(jsonObject.getInt("code")!=102)){
                                Looper.prepare();
                                BToast.showText(context,jsonObject.getString("response"), false);
                                Looper.loop();
                            }
                            else if(jsonObject.getInt("code")==101){
                                LocalPicture localPicture=new LocalPicture();
                                localPicture.updateUserPicture(String.valueOf(infoList.get(finalI)),jsonObject.getString("picture"),jsonObject.getInt("picture_version"));
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            Looper.prepare();
                            BToast.showText(context,"数据解析失败！",false);
                            Looper.loop();
                        }
                    }
                });
            }
            else{
                int finalI=i;
                UserConnection userConnection=new UserConnection();
                userConnection.queryUserPicture(String.valueOf(infoList.get(i)),"-1",new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call,IOException e){
                        Looper.prepare();
                        BToast.showText(context,"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call,Response response) throws IOException{
                        String result=response.body().string();
                        try{
                            JSONObject jsonObject=new JSONObject(result);
                            if((jsonObject.getInt("code")!=101)&&(jsonObject.getInt("code")!=102)){
                                Looper.prepare();
                                BToast.showText(context,jsonObject.getString("response"),false);
                                Looper.loop();
                            }
                            else if(jsonObject.getInt("code")==101){
                                LocalPicture localPicture=new LocalPicture();
                                localPicture.updateUserPicture(String.valueOf(infoList.get(finalI)),jsonObject.getString("picture"),jsonObject.getInt("picture_version"));
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            Looper.prepare();
                            BToast.showText(context,"数据解析失败！",false);
                            Looper.loop();
                        }
                    }
                });
            }
        }
    }

    public void update(Context context, List<Integer> user_id_list){
        for(int i=0;i<user_id_list.size();i++){
            UserConnection userConnection=new UserConnection();
            int finalI=i;
            userConnection.queryUserInfo(String.valueOf(user_id_list.get(i)),new okhttp3.Callback(){
                @Override
                public void onFailure(Call call,IOException e){
                    Looper.prepare();
                    BToast.showText(context,"服务器连接失败，请检查网络设置", false);
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call,Response response) throws IOException{
                    String result=response.body().string();
                    try{
                        JSONObject jsonObject=new JSONObject(result);
                        if(jsonObject.getInt("code")!=101){
                            Looper.prepare();
                            BToast.showText(context,jsonObject.getString("response"),false);
                            Looper.loop();
                        }
                        else{
                            LocalPicture localPicture=new LocalPicture();
                            localPicture.setPicture(jsonObject.getString("picture"));
                            localPicture.updateAll("user_code=?",String.valueOf(user_id_list.get(finalI)));
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(context,"数据解析失败！",false);
                        Looper.loop();
                    }
                }
            });
        }
    }
}
