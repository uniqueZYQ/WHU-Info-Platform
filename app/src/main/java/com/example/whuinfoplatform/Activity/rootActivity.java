package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityRootBinding;

public abstract  class rootActivity extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_root);
        bindView();
        initToolBar();
        initData();
        initClick();
        initWidget();
    }
    public abstract void  bindView();//抽象函数
    /**
     * 用来定义各种click事件
     */
    protected void initClick(){
        toolbar.setNavigationOnClickListener(v->{
            finish();
        });
    }
    private void initToolBar(){
        toolbar=findViewById(R.id.toolbar);
        if(toolbar!=null)
            setSupportActionBar(toolbar);
    }
    /**
     * 用来初始化各种数据
     */
    protected void initData(){
    }
    /**
     * 用来初始化窗口
     */
    protected void initWidget(){

    }

}