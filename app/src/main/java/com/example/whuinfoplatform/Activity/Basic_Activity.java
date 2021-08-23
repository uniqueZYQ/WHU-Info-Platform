package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;

import com.example.whuinfoplatform.Dao.LastConnection;
import com.example.whuinfoplatform.Dao.MsgConnection;
import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.Last;
import com.example.whuinfoplatform.Entity.LocalLogin;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.databinding.ActivityBasicBinding;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class Basic_Activity extends rootActivity{
    private ActivityBasicBinding binding;
    private long exitTime=0;
    private int id;
    private String tmpnkn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    public void bindView(){
        binding=ActivityBasicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData(){
        super.initData();
        Intent intent=getIntent();
        tmpnkn=intent.getStringExtra("tmpnkn");
        id=intent.getIntExtra("tmpid",0);
    }

    @Override
    protected void initClick(){
        super.initClick();

        binding.startPersonalMessageActivity.setOnClickListener(v->{
            Intent intent=new Intent(Basic_Activity.this,Personal_Message_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });

        binding.startPersonalCenterActivity.setOnClickListener(v->{
            Intent intent=new Intent(Basic_Activity.this,Personal_Center_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });

        binding.startInfoHallActivity.setOnClickListener(v->{
            Intent intent=new Intent(Basic_Activity.this,Info_Hall_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });

        binding.startMessageCenterActivity.setOnClickListener(v->{
            Intent intent=new Intent(Basic_Activity.this,Message_Center_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });

        binding.changeUser.setOnClickListener(v->{
            DataSupport.deleteAll(LocalLogin.class);
            Intent intent=new Intent(Basic_Activity.this,MainActivity.class);
            startActivity(intent);
            this.finish();
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget(){
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(tmpnkn);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        // must store the new intent unless getIntent() will return the old one
        setIntent(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        /*here start mark show*/
        Intent intent = getIntent();
        id=intent.getIntExtra("tmpid",0);
        //List<Msg> cumsg= DataSupport.where("obj_id=?",String.valueOf(id)).order("id desc").find(Msg.class);
        List<Msg> cumsg=new ArrayList<>();
        List<Last> culast=new ArrayList<>();
        MsgConnection msgConnection=new MsgConnection();
        msgConnection.queryMsgAboutUserForObjUser(String.valueOf(id),new okhttp3.Callback(){
            @Override
            public void onFailure(Call call,IOException e){
                Looper.prepare();
                BToast.showText(Basic_Activity.this,"服务器连接失败，请稍后再试",false);
                Looper.loop();
            }

            @RequiresApi(api=Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call,Response response) throws IOException{
                String result=response.body().string();
                int n=msgConnection.parseJSONMsgResponse(result,0,cumsg);
                if(n<0){
                    Looper.prepare();
                    BToast.showText(Basic_Activity.this,"服务器连接失败，请稍后再试",false);
                    Looper.loop();
                }
                else{
                    LastConnection lastConnection=new LastConnection();
                    lastConnection.queryLastByUserId(String.valueOf(id),new okhttp3.Callback(){
                        @Override
                        public void onFailure(Call call,IOException e){
                            Looper.prepare();
                            BToast.showText(Basic_Activity.this,"服务器连接失败，请稍后再试",false);
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call,Response response) throws IOException{
                            String result=response.body().string();
                            int n=lastConnection.parseJSONLastResponse(result,culast);
                            if(n<0){
                                Looper.prepare();
                                BToast.showText(Basic_Activity.this,"服务器连接失败，请稍后再试",false);
                                Looper.loop();
                            }
                            else{
                                doUI(cumsg,culast);
                            }
                        }
                    });
                }
            }
        });
    }

    private void doUI(List<Msg> cumsg,List<Last> culast){
       runOnUiThread(()->{
           if((cumsg.size()==0&&culast.size()==0));
           else if(cumsg.size()!=0&&culast.size()==0){
               binding.lastSignal.setVisibility(View.VISIBLE);
           }
           else if(cumsg.size()==0&&culast.size()!=0);
           else {
               if(cumsg.get(0).getId()>culast.get(0).getLast_id())
                   binding.lastSignal.setVisibility(View.VISIBLE);
               else
                   binding.lastSignal.setVisibility(View.GONE);
           }
       });
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                BToast.showText(Basic_Activity.this,"再按一次退出程序",true);
                exitTime=System.currentTimeMillis();
            }
            else {
                ActivityCollector.finishAll();
                ActivityCollector.removeActivity(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //ActivityCollector.removeActivity(this);
    }
}