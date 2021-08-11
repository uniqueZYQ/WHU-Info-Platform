package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;

import com.example.whuinfoplatform.Adapter.my_msg_Adapter;
import com.example.whuinfoplatform.Dao.LastConnection;
import com.example.whuinfoplatform.Dao.MsgConnection;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.my_msg;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityMessageCenterBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Message_Center_Activity extends rootActivity {
    private ActivityMessageCenterBinding binding;
    private List<my_msg> my_msg_list = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private my_msg_Adapter adapter;
    int myid=0,oppo_id=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding= ActivityMessageCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        adapter = new my_msg_Adapter(Message_Center_Activity.this,R.layout.my_msg_item,my_msg_list);
    }

    private void initLast(String user_id,String oppo_id,String last_id,String oppo_name){
        LastConnection lastConnection=new LastConnection();
        lastConnection.updateLastByUser(user_id,oppo_id,last_id,new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                BToast.showText(Message_Center_Activity.this,"服务器连接失败，请检查网络设置",false);
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    if(!(jsonObject.getInt("code")==101)){
                        Looper.prepare();
                        BToast.showText(Message_Center_Activity.this,jsonObject.getString("response"),false);
                        Looper.loop();
                    }else{
                        Intent intent=new Intent(Message_Center_Activity.this,Chat_Window_Activity.class);
                        intent.putExtra("sub_id1",myid);
                        intent.putExtra("obj_id1",Integer.valueOf(oppo_id).intValue());
                        intent.putExtra("nickname",oppo_name);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    BToast.showText(Message_Center_Activity.this,"数据解析失败！",false);
                    Looper.loop();
                }
            }
        });
    }

    private void showNoMsg(){
        runOnUiThread(() -> binding.none.setVisibility(View.VISIBLE));
    }

    private void init(){
        my_msg_list.clear();
        Intent intent=getIntent();
        myid=intent.getIntExtra("id",0);
        //List<Msg> msg = DataSupport.where("sub_id=? or obj_id=?",String.valueOf(myid),String.valueOf(myid)).order("id desc").find(Msg.class);
        MsgConnection msgConnection=new MsgConnection();
        msgConnection.queryMsgAboutUserForOneUser(String.valueOf(myid), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                BToast.showText(Message_Center_Activity.this,"服务器连接失败，请检查网络设置",false);
                Looper.loop();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                int n=msgConnection.parseJSONMyMsgResponse(Message_Center_Activity.this,result,my_msg_list);
                if(n==-1){
                    Looper.prepare();
                    BToast.showText(Message_Center_Activity.this,"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }
                else if(n==0){
                    showNoMsg();
                }
                else{
                    OtherOptions();
                }
            }
        });
    }

    private void OtherOptions(){
        runOnUiThread(() -> {
            ListView listView=(ListView)findViewById(R.id.list_view);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                my_msg mymsg = my_msg_list.get(position);

                int msgid=mymsg.getId();
                String oppo_name=mymsg.getOppoName();

                MsgConnection msgConnection=new MsgConnection();
                msgConnection.queryMsgById(String.valueOf(msgid), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        BToast.showText(Message_Center_Activity.this,"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result=response.body().string();
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            if(jsonObject.getInt("code")==101){
                                if(myid==jsonObject.getInt("sub_id")){
                                    oppo_id=jsonObject.getInt("obj_id");
                                }
                                else{
                                    oppo_id=jsonObject.getInt("sub_id");
                                }
                                MsgConnection msgConnection1=new MsgConnection();
                                msgConnection1.queryMsgAboutUserByOneWay(String.valueOf(oppo_id),String.valueOf(myid), new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Looper.prepare();
                                        BToast.showText(Message_Center_Activity.this,"服务器连接失败，请检查网络设置",false);
                                        Looper.loop();
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String result=response.body().string();
                                        try {
                                            JSONArray jsonArray=new JSONArray(result);
                                            if(jsonArray.getJSONObject(0).getInt("code")==101){
                                                int last_id=jsonArray.getJSONObject(0).getInt("id");
                                                initLast(String.valueOf(myid),String.valueOf(oppo_id),String.valueOf(last_id),oppo_name);
                                            }
                                            else if(jsonArray.getJSONObject(0).getInt("code")==401){
                                                Looper.prepare();
                                                BToast.showText(Message_Center_Activity.this,jsonArray.getJSONObject(0).getString("response"),false);
                                                Looper.loop();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Looper.prepare();
                                            BToast.showText(Message_Center_Activity.this,"数据解析失败！",false);
                                            Looper.loop();
                                        }
                                    }
                                });
                            }
                            else{
                                Looper.prepare();
                                BToast.showText(Message_Center_Activity.this,jsonObject.getString("response"),false);
                                Looper.loop();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            BToast.showText(Message_Center_Activity.this,"数据解析失败！",false);
                            Looper.loop();
                        }
                    }
                });
            });

            swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperrfresh);
            swipeRefresh.setColorSchemeResources(
                    android.R.color.holo_blue_light,
                    android.R.color.holo_purple);
            swipeRefresh.setOnRefreshListener(() -> refresh_my_msg(2000));
        });
    }

    private void refresh_my_msg(int s){
        new Thread(() -> {
            try{
                Thread.sleep(s);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            runOnUiThread((() -> {
                adapter.clear();
                init();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }));
        }).start();
    }

    @Override
    protected void initClick() {
        super.initClick();
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("消息中心");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh_my_msg(1);
    }
}