package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.BottomNavigation;
import com.example.whuinfoplatform.Entity.SenseCheck;
import com.example.whuinfoplatform.databinding.ActivityInfoHallBinding;

public class Info_Hall_Activity extends rootActivity {
    private ActivityInfoHallBinding binding;
    private long exitTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    public void bindView(){
        binding=ActivityInfoHallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigation bottomNavigation=new BottomNavigation();
        bottomNavigation.init(binding.navigationBar,Info_Hall_Activity.this,2);
    }

    @Override
    protected void initClick(){
        super.initClick();
        binding.startEditText.setOnEditorActionListener((v,actionId,event)->{
            if(actionId==EditorInfo.IME_ACTION_SEARCH){
                String kwd=binding.startEditText.getText().toString();
                SenseCheck senseCheck=new SenseCheck();
                if(senseCheck.SenseCheckAllBlankOrNull(kwd)){
                    Intent intent=new Intent(Info_Hall_Activity.this,Search_Info_promote_Activity.class);
                    intent.putExtra("kwd",kwd);
                    Intent intent1=getIntent();
                    int id=intent1.getIntExtra("id",0);
                    intent.putExtra("id",id);
                    startActivity(intent);
                }
                else{
                    binding.startEditText.setText("");
                    BToast.showText(Info_Hall_Activity.this,"?????????????????????????????????",false);
                }
                return true;
            }
            else
                return false;
        });
        binding.delete.setOnClickListener(v->binding.startEditText.setText(""));
        binding.startFindInfoActivity.setOnClickListener(v->{
            String kwd=binding.startEditText.getText().toString();
            SenseCheck senseCheck=new SenseCheck();
            if(senseCheck.SenseCheckAllBlankOrNull(kwd)){
                Intent intent=new Intent(Info_Hall_Activity.this,Search_Info_promote_Activity.class);
                intent.putExtra("kwd",kwd);
                Intent intent1=getIntent();
                int id=intent1.getIntExtra("id",0);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            else{
                binding.startEditText.setText("");
                BToast.showText(Info_Hall_Activity.this,"?????????????????????????????????",false);
            }
        });
        binding.startPublishInfoActivity.setOnClickListener(v->{
            Intent intent=new Intent(Info_Hall_Activity.this,Publish_Info_promote_Activity.class);
            intent.putExtra("id",getIntent().getIntExtra("id",0));
            startActivity(intent);
        });
    }

    @Override
    protected void initData(){
        super.initData();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget(){
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("  ????????????");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                BToast.showText(Info_Hall_Activity.this,"????????????????????????",true);
                exitTime=System.currentTimeMillis();
            }
            else{
                ActivityCollector.finishAll();
                ActivityCollector.removeActivity(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}

