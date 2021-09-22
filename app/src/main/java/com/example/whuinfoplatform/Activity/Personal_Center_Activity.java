package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.example.whuinfoplatform.Adapter.my_info_Adapter;
import com.example.whuinfoplatform.Dao.InfoConnection;
import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.BottomNavigation;
import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPersonalCenterBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class Personal_Center_Activity extends rootActivity {
    private ActivityPersonalCenterBinding binding;
    private List<my_info> my_info_list=new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private my_info_Adapter adapter;
    private int id;
    private long exitTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    public void bindView(){
        binding=ActivityPersonalCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigation bottomNavigation=new BottomNavigation();
        bottomNavigation.init(binding.navigationBar,Personal_Center_Activity.this,3);
        adapter=new my_info_Adapter(Personal_Center_Activity.this,R.layout.my_info_item,my_info_list);
        init();
    }

    private void init(){
        Intent intent=getIntent();
        id=intent.getIntExtra("id",0);

        InfoConnection connection=new InfoConnection();
        connection.queryMyInfoConnection(String.valueOf(id),new okhttp3.Callback(){
            @Override
            public void onFailure(Call call,IOException e){
                Looper.prepare();
                BToast.showText(Personal_Center_Activity.this,"服务器连接失败，请检查网络设置",false);
                Looper.loop();
            }

            @RequiresApi(api=Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call,Response response) throws IOException{
                String result=response.body().string();
                int n=connection.parseJSONForMyInfoResponse(result,my_info_list);
                if(n>0){
                    OtherOptions();
                }
                else if(n==0){
                    showNoneInfo();
                    OtherOptions();
                }
                else{
                    Looper.prepare();
                    BToast.showText(Personal_Center_Activity.this,"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }
            }
        });
    }

    private void showNoneInfo(){
        runOnUiThread(()->binding.none.setVisibility(View.VISIBLE));
    }

    private void OtherOptions(){
        runOnUiThread(()->{
            ListView listView=findViewById(R.id.list_view);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent,view,position,id)->{
                my_info myinfo=my_info_list.get(position);
                int infoid=myinfo.getId();
                Intent intent2=getIntent();
                int owner_id=intent2.getIntExtra("id",0);
                Intent intent=new Intent(Personal_Center_Activity.this,My_Info_details_Activity.class);
                intent.putExtra("id",infoid);
                intent.putExtra("owner_id",owner_id);
                startActivity(intent);
            });
            swipeRefresh=findViewById(R.id.swiperrfresh);
            swipeRefresh.setColorSchemeResources(
                    android.R.color.holo_blue_light,
                    android.R.color.holo_purple);
            swipeRefresh.setOnRefreshListener(()->refresh_my_info(2000));
        });
    }

    private void refresh_my_info(int s){
        new Thread(()->{
            try{
                Thread.sleep(s);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            runOnUiThread((()->{
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
    protected void initWidget(){
        super.initWidget();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("  我发布的");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        refresh_my_info(1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                BToast.showText(Personal_Center_Activity.this,"再按一次退出程序",true);
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