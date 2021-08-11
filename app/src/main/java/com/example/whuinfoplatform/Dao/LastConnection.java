package com.example.whuinfoplatform.Dao;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.whuinfoplatform.Entity.Last;

import org.json.JSONArray;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LastConnection {
    private RequestBody formBody;
    public static String URL = "http://122.9.144.219:8080/myServlet/";

    public void updateLastByUser(String user_id,String oppo_id,String last_id,okhttp3.Callback callback) {
        String Url=URL+"LastServlet";

        formBody = new FormBody.Builder()
                .add("user_id",user_id)
                .add("oppo_id", oppo_id)
                .add("last_id", last_id)
                .add("type","1")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryLastByUser(String user_id,String oppo_id,okhttp3.Callback callback) {
        String Url=URL+"LastServlet";

        formBody = new FormBody.Builder()
                .add("user_id",user_id)
                .add("oppo_id", oppo_id)
                .add("type","2")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void insertLastByUser(String user_id,String oppo_id,String last_id,okhttp3.Callback callback) {
        String Url=URL+"LastServlet";

        formBody = new FormBody.Builder()
                .add("user_id",user_id)
                .add("oppo_id", oppo_id)
                .add("last_id", last_id)
                .add("type","3")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    public void queryLastByUserId(String user_id,okhttp3.Callback callback) {
        String Url=URL+"LastServlet";

        formBody = new FormBody.Builder()
                .add("user_id",user_id)
                .add("type","4")
                .build();

        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().url(Url).post(formBody).build();

        client.newCall(request).enqueue(callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int parseJSONLastResponse(String json, List<Last> last){
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
                Last culast=new Last();
                culast.setId(jsonArray.getJSONObject(i).getInt("id"));
                culast.setOppo_id(jsonArray.getJSONObject(i).getInt("oppo_id"));
                culast.setUser_id(jsonArray.getJSONObject(i).getInt("user_id"));
                culast.setLast_id(jsonArray.getJSONObject(i).getInt("last_id"));
                last.add(culast);
            }
            return jsonArray.length();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}
