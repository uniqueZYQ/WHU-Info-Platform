package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whuinfoplatform.Entity.SenseCheck;
import com.example.whuinfoplatform.databinding.ActivityInfoHallBinding;

public class Info_Hall_Activity extends rootActivity {
    private ActivityInfoHallBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding= ActivityInfoHallBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.startEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    String kwd = binding.startEditText.getText().toString();
                    SenseCheck senseCheck=new SenseCheck();
                    if(senseCheck.SenseCheckAllBlankOrNull(kwd)){
                        Intent intent = new Intent(Info_Hall_Activity.this, Search_Info_promote_Activity.class);
                        intent.putExtra("kwd",kwd);
                        Intent intent1=getIntent();
                        int id=intent1.getIntExtra("id",0);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    }
                    else{
                        binding.startEditText.setText("");
                        Toast.makeText(Info_Hall_Activity.this,"无法搜索无意义的内容！",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                else
                    return false;
            }
        });
        binding.startFindInfoActivity.setOnClickListener(v->{
            String kwd = binding.startEditText.getText().toString();
            boolean valid = false;
            for (int i = 0; i < kwd.length(); i++) {
                if (kwd.charAt(i) == '\0' || kwd.charAt(i) == '\n' || kwd.charAt(i) == ' ')
                    continue;
                else
                    valid = true;
            }
            if(valid){
                Intent intent = new Intent(Info_Hall_Activity.this, Search_Info_promote_Activity.class);
                intent.putExtra("kwd",kwd);
                Intent intent1=getIntent();
                int id=intent1.getIntExtra("id",0);
                intent.putExtra("id",id);
                startActivity(intent);
            }
            else{
                binding.startEditText.setText("");
                Toast.makeText(Info_Hall_Activity.this,"无法搜索无意义的内容！",Toast.LENGTH_SHORT).show();
            }
        });
        binding.startPublishInfoActivity.setOnClickListener(v->{
            Intent intent1=getIntent();
            int id=intent1.getIntExtra("id",0);
            Intent intent2 = new Intent(Info_Hall_Activity.this, Publish_Info_promote_Activity.class);
            intent2.putExtra("id",id);
            startActivity(intent2);
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("信息大厅");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}

