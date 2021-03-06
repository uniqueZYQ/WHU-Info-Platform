package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.example.whuinfoplatform.Adapter.srch_info_Adapter;
import com.example.whuinfoplatform.Dao.InfoConnection;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.srch_info;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivitySearchInfoPromoteBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class Search_Info_promote_Activity extends rootActivity{
    private ActivitySearchInfoPromoteBinding binding;
    private List<srch_info> srch_info_list=new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private srch_info_Adapter adapter;
    private int locid=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView(){
        binding=ActivitySearchInfoPromoteBinding.inflate(getLayoutInflater());
        adapter=new srch_info_Adapter(srch_info_list,locid);
        setContentView(binding.getRoot());
        Intent intent1=getIntent();
        locid=intent1.getIntExtra("id",0);
        init();
    }

    private void init(){
        Intent intent=getIntent();
        String kwd=intent.getStringExtra("kwd");
        int id=intent.getIntExtra("id",0);

        //List<Info> info = DataSupport.where("detail like ? or lesson like ?","%"+kwd+"%","%"+kwd+"%").order("send_date desc").find(Info.class);
        InfoConnection infoConnection=new InfoConnection();
        infoConnection.queryInfoByKwdConnection(kwd,new okhttp3.Callback(){
            @Override
            public void onFailure(Call call,IOException e){
                Looper.prepare();
                BToast.showText(Search_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
                Looper.loop();
            }

            @RequiresApi(api=Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call,Response response) throws IOException{
                String result=response.body().string();
                int n=infoConnection.parseJSONForInfoResponse(Search_Info_promote_Activity.this,result,id,srch_info_list);
                if(n==0){
                    showNoneInfo();
                    OtherOptions();
                }
                else if(n>0){
                    OtherOptions();
                }
                else if(n==-1){
                    Looper.prepare();
                    BToast.showText(Search_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
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
            StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            RecyclerView recyclerView=findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            swipeRefresh=findViewById(R.id.swiperrfresh);
            swipeRefresh.setColorSchemeResources(
                    android.R.color.holo_blue_light,
                    android.R.color.holo_purple);
            swipeRefresh.setOnRefreshListener(()->refresh_info(2000));
        });
    }

    private void refresh_info(int s){
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
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initClick(){
        super.initClick();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget(){
        super.initWidget();
        Intent intent=getIntent();
        String kwd=intent.getStringExtra("kwd");
        ActionBar actionBar=getSupportActionBar();
        if(kwd.length()>8){
            actionBar.setTitle("\""+kwd.substring(0,8)+"...\"???????????????");
        }
        else
            actionBar.setTitle("\""+kwd+"\"???????????????");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}