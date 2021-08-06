package com.example.whuinfoplatform.Activity;
import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.Last;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityBasicBinding;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class Basic_Activity extends rootActivity{
    private ActivityBasicBinding binding;
    private long exitTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }
    int id;
    String tmpnkn;
    @Override
    public void bindView() {
        binding=ActivityBasicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        tmpnkn=intent.getStringExtra("tmpnkn");
        id=intent.getIntExtra("tmpid",0);
    }

    @Override
    protected void initClick() {
        super.initClick();
       binding.startPersonalMessageActivity.setOnClickListener(v->{
            Intent intent = new Intent(Basic_Activity.this, Personal_Message_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });
       binding.startPersonalCenterActivity.setOnClickListener(v->{
            Intent intent = new Intent(Basic_Activity.this, Personal_Center_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });
        binding.startInfoHallActivity.setOnClickListener(v -> {
            Intent intent = new Intent(Basic_Activity.this,Info_Hall_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });
        binding.startMessageCenterActivity.setOnClickListener(v -> {
            Intent intent = new Intent(Basic_Activity.this, Message_Center_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });
        binding.changeUser.setOnClickListener(v -> {
            Intent intent = new Intent(Basic_Activity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle(tmpnkn);
        /*actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*here start mark show*/
        Connector.getDatabase();
        Intent intent = getIntent();
        id=intent.getIntExtra("tmpid",0);
        List<Msg> cumsg= DataSupport.where("obj_id=?",String.valueOf(id)).order("id desc").find(Msg.class);
        List<Last> culast=DataSupport.where("user_id=?",String.valueOf(id)).order("last_id desc").find(Last.class);
        if((cumsg.size()==0&&culast.size()==0));
        else if(cumsg.size()!=0&&culast.size()==0){
            //binding.startMessageCenterActivity.setText("消息中心\n[有未读信息]");
            binding.startMessageCenterActivity.setBackground(getResources().getDrawable(R.drawable.a2_2));
        }
        else if(cumsg.size()==0&&culast.size()!=0);
        else {
            if(cumsg.get(0).getId()>culast.get(0).getLast_id()){
                //binding.startMessageCenterActivity.setText("消息中心\n[有未读信息]");
                binding.startMessageCenterActivity.setBackground(getResources().getDrawable(R.drawable.a2_2));
            }
            else binding.startMessageCenterActivity.setBackground(getResources().getDrawable(R.drawable.a2));//binding.startMessageCenterActivity.setText("消息中心");
        }
        /*here end mark show*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ActivityCollector.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}