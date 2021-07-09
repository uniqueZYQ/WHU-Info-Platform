package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.Person;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.whuinfoplatform.Adapter.my_info_Adapter;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPersonalCenterBinding;
import com.example.whuinfoplatform.databinding.MyInfoItemBinding;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Personal_Center_Activity extends rootActivity {

    private ActivityPersonalCenterBinding binding;
    private List<my_info> my_info_list = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private my_info_Adapter adapter;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding = ActivityPersonalCenterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        adapter = new my_info_Adapter(Personal_Center_Activity.this,R.layout.my_info_item,my_info_list);
        ListView listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                my_info myinfo = my_info_list.get(position);
                int infoid=myinfo.getId();
                Intent intent2 = getIntent();
                int owner_id=intent2.getIntExtra("id",0);
                Intent intent = new Intent(Personal_Center_Activity.this,My_Info_details_Activity.class);
                intent.putExtra("id",infoid);
                intent.putExtra("owner_id",owner_id);
                startActivity(intent);
            }
        });
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperrfresh);
        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_purple);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refresh_my_info();
            }
        });
    }

    private void init(){
        Intent intent=getIntent();
        id=intent.getIntExtra("id",0);
        List<Info> info = DataSupport.where("owner_id=?",String.valueOf(id)).order("send_date desc").find(Info.class);
        for(int i=0;i<info.size();i++){
            String date=info.get(i).getSend_date();
            String form=info.get(i).getForm()==1?"私人性-学术咨询信息":info.get(i).getForm()==2?"私人性-日常求助信息":info.get(i).getForm()==3?"私人性-物品出售信息":info.get(i).getForm()==4?"私人性-物品求购信息":info.get(i).getForm()==5?"组织性信息":"课程点评信息";
            String detail=info.get(i).getDetail();
            String answered=info.get(i).getAnswered()==0?"暂无响应":"已被响应";
            int infoid=info.get(i).getId();
            my_info myinfo=new my_info(infoid,date,form,detail,answered);
            my_info_list.add(myinfo);
        }
        if(my_info_list.size()==0){
            binding.none.setVisibility(View.VISIBLE);
        }
    }

    private void refresh_my_info(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    Thread.sleep(2000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread((new Runnable(){

                    @Override
                    public void run() {
                        adapter.clear();
                        init();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                }));
            }
        }).start();
    }

    private void refresh_my_info1(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                try{
                    Thread.sleep(1);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread((new Runnable(){

                    @Override
                    public void run() {
                        adapter.clear();
                        init();
                        adapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                }));
            }
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
        actionBar.setTitle("我发布的");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh_my_info1();
    }

}