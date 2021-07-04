package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.whuinfoplatform.Adapter.my_info_Adapter;
import com.example.whuinfoplatform.Adapter.srch_info_Adapter;
import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.Entity.srch_info;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivitySearchInfoPromoteBinding;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class Search_Info_promote_Activity extends rootActivity {
    private ActivitySearchInfoPromoteBinding binding;
    private List<srch_info> srch_info_list = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private srch_info_Adapter adapter;
    private DB_USER dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding=ActivitySearchInfoPromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent1 = getIntent();
        int locid=intent1.getIntExtra("id",0);
        init();
        adapter = new srch_info_Adapter(Search_Info_promote_Activity.this,R.layout.srch_info_item,srch_info_list);
        ListView listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                srch_info srchinfo = srch_info_list.get(position);
                int infoid=srchinfo.getId();
                List<Info> info = DataSupport.where("id=?",String.valueOf(infoid)).find(Info.class);
                int ownerid=info.get(0).getOwner_id();
                int cuself=srchinfo.getSelf();
                String cuowner=srchinfo.getOwner();
                Intent intent = new Intent(Search_Info_promote_Activity.this,Srch_Info_details_Activity.class);
                intent.putExtra("id",infoid);
                intent.putExtra("locid",locid);
                intent.putExtra("owner",cuowner);
                intent.putExtra("self",cuself);
                intent.putExtra("ownerid",ownerid);
                startActivity(intent);
            }
        });
        /////
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
        /////

    }

    private void init(){
        String owner="";
        int self=0;
        Intent intent=getIntent();
        String kwd=intent.getStringExtra("kwd");
        int id=intent.getIntExtra("id",0);
        dbHelper = new DB_USER(this, "User.db", null, 7);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Info> info = DataSupport.where("detail like ? or lesson like ?","%"+kwd+"%","%"+kwd+"%").order("send_date desc").find(Info.class);
        for(int i=0;i<info.size();i++){
            String date=info.get(i).getSend_date();
            String form=" 信息类别："+(info.get(i).getForm()==1?"私人性-学术咨询信息":info.get(i).getForm()==2?"私人性-日常求助信息":info.get(i).getForm()==3?"私人性-物品出售信息":info.get(i).getForm()==4?"私人性-物品求购信息":info.get(i).getForm()==5?"组织性信息":"课程点评信息");
            String detail=" 内容："+info.get(i).getDetail();
            int owner_id=info.get(i).getOwner_id();
            if(owner_id==id) self=1;
            else self=0;
            Cursor cursor = db.rawQuery("select nickname from User where id=?", new String[]{String.valueOf(owner_id)}, null);
            if(cursor.moveToFirst()){
                if (cursor.getCount() != 0) {
                    if(self==0)owner=cursor.getString(cursor.getColumnIndex("nickname"));
                    else owner=cursor.getString(cursor.getColumnIndex("nickname"))+"(我)";
                }
            }
            cursor.close();
            int infoid=info.get(i).getId();
            srch_info srchinfo=new srch_info(infoid,date,form,detail,owner,self);
            srch_info_list.add(srchinfo);
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

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initClick() {
        super.initClick();

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        Intent intent = getIntent();
        String kwd=intent.getStringExtra("kwd");
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("\""+kwd+"\"的搜索结果");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}