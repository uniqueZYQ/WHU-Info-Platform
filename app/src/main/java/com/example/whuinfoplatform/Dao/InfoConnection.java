package com.example.whuinfoplatform.Dao;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.UpdateLocalPicture;
import com.example.whuinfoplatform.Entity.WebResponse;
import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.Entity.srch_info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class InfoConnection{
    private RequestBody formBody;
    private int i,self=0;
    private String owner;
    public static String URL="http://122.9.144.219:8080/myServlet/";

    public void initRegisterConnection(String owner_id,String send_date,String answered,String form,String fd_form,String help_form,String price,String date,
                                       String place,String lesson,String score,String detail,String reward,String picture1,String picture2,
                                       String picture3,String picture4,String placeId,okhttp3.Callback callback){
        String Url=URL+"PublishInfoServlet";

        formBody=new FormBody.Builder()
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

    public void updateMyInfoConnection1(String id,String fd_form,String reward,String detail,String send_date,okhttp3.Callback callback){
        String Url=URL+"updateMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("fd_form",fd_form)
                .add("detail",detail)
                .add("send_date",send_date)
                .add("reward",reward)
                .add("type","1")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void updateMyInfoConnection2(String id,String help_form,String reward,String detail,String send_date,okhttp3.Callback callback){
        String Url=URL+"updateMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("help_form",help_form)
                .add("detail",detail)
                .add("send_date",send_date)
                .add("reward",reward)
                .add("type","2")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void updateMyInfoConnection3(String id,String price,String detail,String send_date,okhttp3.Callback callback){
        String Url=URL+"updateMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("price",price)
                .add("detail",detail)
                .add("send_date",send_date)
                .add("type","3")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void updateMyInfoConnection4(String id,String reward,String place,String date,String detail,String placeId,String send_date,okhttp3.Callback callback){
        String Url=URL+"updateMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("reward",reward)
                .add("place",place)
                .add("date",date)
                .add("placeId",placeId)
                .add("detail",detail)
                .add("send_date",send_date)
                .add("type","4")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void updateMyInfoConnection5(String id,String score,String lesson,String detail,String send_date,okhttp3.Callback callback){
        String Url=URL+"updateMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("score",score)
                .add("lesson",lesson)
                .add("detail",detail)
                .add("send_date",send_date)
                .add("type","5")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryMyInfoConnection(String id,okhttp3.Callback callback){
        String Url=URL+"QueryMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","0")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryMyInfoDetailConnection(String id,okhttp3.Callback callback){
        String Url=URL+"QueryMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","1")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void deleteMyInfoConnection(String id,okhttp3.Callback callback){
        String Url=URL+"QueryMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","2")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryInfoByKwdConnection(String kwd,okhttp3.Callback callback){
        String Url=URL+"QueryMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("kwd",kwd)
                .add("type","3")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryInfoByIdConnection(String id,okhttp3.Callback callback){
        String Url=URL+"QueryMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","1")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void answerInfoConnection(String id,okhttp3.Callback callback){
        String Url=URL+"QueryMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","4")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryInfoByIdAndAddViewsConnection(String id,okhttp3.Callback callback){
        String Url=URL+"QueryMyInfoServlet";

        formBody=new FormBody.Builder()
                .add("id",id)
                .add("type","5")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    public void parseJSONForInfoResponse(WebResponse Response, String json){
        try{
            JSONObject jsonObject=new JSONObject(json);
            int code=jsonObject.getInt("code");
            String response=jsonObject.getString("response");
            showResponseForInfoResponse(Response,code,response);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    public int parseJSONForMyInfoResponse(String json,List<my_info> my_info_list){
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
                String date=jsonArray.getJSONObject(i).getString("send_date");
                String form=jsonArray.getJSONObject(i).getInt("form")==1?"私人性-学术咨询信息":
                            jsonArray.getJSONObject(i).getInt("form")==2?"私人性-日常求助信息":
                            jsonArray.getJSONObject(i).getInt("form")==3?"私人性-物品出售信息":
                            jsonArray.getJSONObject(i).getInt("form")==4?"私人性-物品求购信息":
                            jsonArray.getJSONObject(i).getInt("form")==5?"组织性信息":"课程点评信息";
                String detail;
                if(jsonArray.getJSONObject(i).getInt("form")!=6)
                    detail=jsonArray.getJSONObject(i).getString("detail");
                else
                    detail=jsonArray.getJSONObject(i).getString("lesson")+":"+jsonArray.getJSONObject(i).getString("detail");
                String answered=jsonArray.getJSONObject(i).getInt("answered")==0?"暂无响应":"已被响应";
                int infoid=jsonArray.getJSONObject(i).getInt("id");
                my_info myinfo=new my_info(infoid,date,form,detail,answered);
                my_info_list.add(myinfo);
            }
            return jsonArray.length();
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    public int parseJSONForInfoResponse(Context context,String json,int id,List<srch_info> srch_info_list){
        try{
            JSONArray jsonArray=new JSONArray(json);
            for(i=0;i<jsonArray.length();i++){
                int code=jsonArray.getJSONObject(i).getInt("code");
                if(code==102){
                    return 0;
                }
                else if(code!=101&&code!=103){
                    return -1;
                }
                String date=jsonArray.getJSONObject(i).getString("send_date");
                String form=jsonArray.getJSONObject(i).getInt("form")==1?"私人性-学术咨询信息":
                            jsonArray.getJSONObject(i).getInt("form")==2?"私人性-日常求助信息":
                            jsonArray.getJSONObject(i).getInt("form")==3?"私人性-物品出售信息":
                            jsonArray.getJSONObject(i).getInt("form")==4?"私人性-物品求购信息":
                            jsonArray.getJSONObject(i).getInt("form")==5?"组织性信息":"课程点评信息";
                String detail;
                int views=jsonArray.getJSONObject(i).getInt("views");
                if(jsonArray.getJSONObject(i).getInt("form")!=6)
                    detail=jsonArray.getJSONObject(i).getString("detail");
                else
                    detail=jsonArray.getJSONObject(i).getString("lesson")+":"+jsonArray.getJSONObject(i).getString("detail");
                int owner_id=jsonArray.getJSONObject(i).getInt("owner_id");
                owner=jsonArray.getJSONObject(i).getString("owner_nickname");
                if(owner_id==id){
                    self=1;
                }
                else{
                    self=0;
                }
                int first_picture=jsonArray.getJSONObject(i).getInt("picture1");
                int infoid=jsonArray.getJSONObject(i).getInt("id");
                srch_info srchinfo=new srch_info(infoid,date,form,detail,owner,owner_id,self,first_picture,views);
                srch_info_list.add(srchinfo);
            }
            //更新信息所有者头像
            UpdateLocalPicture updateLocalPicture=new UpdateLocalPicture();
            updateLocalPicture.updateInfoOwnerPicture(context,srch_info_list);
            return jsonArray.length();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    public void parseJSONForMyInfoDetailResponse(Info myInformation,String json) throws JSONException{
        JSONObject jsonObject=new JSONObject(json);
        myInformation.setAnswered(jsonObject.getInt("answered"));
        myInformation.setCode(jsonObject.getInt("code"));
        myInformation.setDate(jsonObject.getString("date"));
        myInformation.setDetail(jsonObject.getString("detail"));
        myInformation.setFd_form(jsonObject.getInt("fd_form"));
        myInformation.setForm(jsonObject.getInt("form"));
        myInformation.setHelp_form(jsonObject.getInt("help_form"));
        myInformation.setId(jsonObject.getInt("id"));
        myInformation.setLesson(jsonObject.getString("lesson"));
        myInformation.setOwner_id(jsonObject.getInt("owner_id"));
        myInformation.setPicture1(jsonObject.getInt("picture1"));
        myInformation.setPicture2(jsonObject.getInt("picture2"));
        myInformation.setPicture3(jsonObject.getInt("picture3"));
        myInformation.setPicture4(jsonObject.getInt("picture4"));
        myInformation.setPlace(jsonObject.getString("place"));
        myInformation.setPlaceId(jsonObject.getString("placeId"));
        myInformation.setPrice(jsonObject.getDouble("price"));
        myInformation.setResponse(jsonObject.getString("response"));
        myInformation.setReward(jsonObject.getDouble("reward"));
        myInformation.setScore(jsonObject.getInt("score"));
        myInformation.setSend_date(jsonObject.getString("send_date"));
    }

    private void showResponseForInfoResponse(WebResponse Response,int code,String response){
        Response.setCode(code);
        Response.setResponse(response);
    }
}

