package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.whuinfoplatform.Adapter.my_info_Adapter;
import com.example.whuinfoplatform.Adapter.my_msg_Adapter;
import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.Last;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Entity.last_show;
import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.Entity.my_msg;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityBasicBinding;
import com.example.whuinfoplatform.databinding.ActivityMessageCenterBinding;
import com.example.whuinfoplatform.databinding.ActivityPersonalCenterBinding;
import com.example.whuinfoplatform.databinding.MyInfoItemBinding;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class Message_Center_Activity extends rootActivity {
    private ActivityMessageCenterBinding binding;
    private List<my_msg> my_msg_list = new ArrayList<>();
    private SwipeRefreshLayout swipeRefresh;
    private my_msg_Adapter adapter;
    private DB_USER dbHelper;
    int myid=0;
    int oppo_id=0;
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
        ListView listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                my_msg mymsg = my_msg_list.get(position);
                //click jumper
                int msgid=mymsg.getId();
                List<Msg> msg=DataSupport.where("id=?",String.valueOf(msgid)).find(Msg.class);
                if(msg.get(0).getSub_id()==myid){
                    oppo_id=msg.get(0).getObj_id();
                }
                else{
                    oppo_id=msg.get(0).getSub_id();
                }
                /*
                 * here start the mark show
                 */
                Connector.getDatabase();
                Last lastshow=new Last();
                lastshow.setUser_id(myid);
                lastshow.setOppo_id(oppo_id);
                List<Msg> lastmsg=DataSupport.where("sub_id=? and obj_id=?",String.valueOf(oppo_id),String.valueOf(myid)).order("id desc").find(Msg.class);
                if(lastmsg.size()==0);
                else {
                    int b = lastmsg.get(0).getId();
                    lastshow.setLast_id(b);
                    lastshow.updateAll("user_id=? and oppo_id=? and last_id<?", String.valueOf(myid), String.valueOf(oppo_id), String.valueOf(b));
                }
                /*
                 * here finish the mark show
                 */
                Intent intent=new Intent(Message_Center_Activity.this,Chat_Window_Activity.class);
                intent.putExtra("sub_id1",myid);
                intent.putExtra("obj_id1",oppo_id);
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
                refresh_my_msg();
            }
        });
    }

    private void init(){
        my_msg_list.clear();
        Intent intent=getIntent();
        List<Integer> oppo_hist_id=new ArrayList<>();
        myid=intent.getIntExtra("id",0);
        List<Msg> msg = DataSupport.where("sub_id=? or obj_id=?",String.valueOf(myid),String.valueOf(myid)).order("id desc").find(Msg.class);
        dbHelper = new DB_USER(this, "User.db", null, 7);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String lasttext="";
        for(int i=0;i<msg.size();i++){
            String last_time=msg.get(i).getTime();
            int recalled=msg.get(i).getRecalled();
            int msgid=msg.get(i).getId();
            String oppo_name="";
            String last_detail;
            if(recalled==1){
                if(myid==msg.get(i).getSub_id())
                    last_detail="你撤回了一条消息";
                else
                    last_detail="对方撤回了一条消息";
            }
            else
                last_detail=msg.get(i).getContent();
            int oppo_id=0;
            if(myid==msg.get(i).getSub_id()) {
                oppo_id=msg.get(i).getObj_id();
            }
            else oppo_id=msg.get(i).getSub_id();
            Cursor cursor = db.rawQuery("select nickname from User where id=?", new String[]{String.valueOf(oppo_id)}, null);
            if(cursor.moveToFirst()){
                if (cursor.getCount() != 0) {
                    oppo_name=cursor.getString(cursor.getColumnIndex("nickname"));
                }
            }
            cursor.close();
            if(!oppo_hist_id.contains(oppo_id)){
                List<Msg> msg1=DataSupport.where("obj_id=? and sub_id=?",String.valueOf(myid),String.valueOf(oppo_id)).order("id desc").find(Msg.class);
                if(msg1.size()!=0) {
                    int p1 = msg1.get(0).getId();
                    List<Last> last = DataSupport.where("user_id=? and oppo_id=?", String.valueOf(myid),String.valueOf(oppo_id)).order("last_id desc").find(Last.class);
                    if(last.size()==0){
                        Connector.getDatabase();
                        Last lastshow=new Last();
                        lastshow.setUser_id(myid);
                        lastshow.setOppo_id(oppo_id);
                        lastshow.setLast_id(0);
                        lastshow.save();
                        List<Last> last1 = DataSupport.where("user_id=? and oppo_id=?", String.valueOf(myid),String.valueOf(oppo_id)).order("last_id desc").find(Last.class);
                        int p2 = last1.get(0).getLast_id();
                        int size = 0;
                        if (p1 > p2) {
                            for (int ii = 0; ii < msg1.size(); ii++) {
                                if (msg1.get(ii).getId() > p2) size++;
                            }
                        }
                        if(size!=0){
                            if(size>9){
                                lasttext="[9+未读]";
                            }
                            else
                                lasttext="["+String.valueOf(size) + "未读]";
                        }
                        else lasttext="";
                    }
                    else {
                        int p2 = last.get(0).getLast_id();
                        int size = 0;
                        if (p1 > p2) {
                            for (int ii = 0; ii < msg1.size(); ii++) {
                                if (msg1.get(ii).getId() > p2) size++;
                            }
                        }
                        if (size != 0) {
                            if(size>9){
                                lasttext="[9+未读]";
                            }
                            else
                                lasttext = "[" + String.valueOf(size) + "未读]";
                        } else lasttext = "";
                    }
                }
                my_msg mymsg=new my_msg(msgid,oppo_id,oppo_name,last_time,last_detail,lasttext);
                my_msg_list.add(mymsg);
                oppo_hist_id.add(oppo_id);
            }

        }
        if(my_msg_list.size()==0){
            binding.none.setVisibility(View.VISIBLE);
        }
    }

    private void refresh_my_msg(){
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

    private void refresh_my_msg1(){
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
        actionBar.setTitle("消息中心");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh_my_msg1();
    }
}