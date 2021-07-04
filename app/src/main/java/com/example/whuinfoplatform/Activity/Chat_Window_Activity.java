package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Adapter.MsgAdapter;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityChatWindowBinding;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Chat_Window_Activity extends rootActivity {
    private DB_USER dbHelper;
    private List<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private ActivityChatWindowBinding binding;
    String owner="";
    int sub_id=0;
    int obj_id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding=ActivityChatWindowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    @Override
    protected void initData() {
        Intent intent = getIntent();
        sub_id=intent.getIntExtra("sub_id",0);
        if(sub_id==0) sub_id=intent.getIntExtra("sub_id1",0);
        obj_id=intent.getIntExtra("obj_id",0);
        if(obj_id==0) obj_id=intent.getIntExtra("obj_id1",0);
        dbHelper=new DB_USER(this,"User.db",null,7);
        dbHelper.getWritableDatabase();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where id=?", new String[]{String.valueOf(obj_id)}, null);
        if(cursor.moveToFirst()) {
            if (cursor.getCount() != 0) {
                owner=cursor.getString(cursor.getColumnIndex("nickname"));
            }
        }
        cursor.close();
        inputText = (EditText) findViewById(R.id.inputText);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        adapter = new MsgAdapter(msgList);
        binding.objnm.setText(owner);
        List<Msg> targetMsg= DataSupport.where("sub_id=? and obj_id=? or sub_id=? and obj_id=? ",String.valueOf(sub_id),String.valueOf(obj_id),String.valueOf(obj_id),String.valueOf(sub_id)).find(Msg.class);
        for(int i=0;i<targetMsg.size();i++){
            Msg cumsg=new Msg();
            cumsg.setTime(targetMsg.get(i).getTime());
            cumsg.setObj_id(targetMsg.get(i).getObj_id());
            cumsg.setSub_id(targetMsg.get(i).getSub_id());
            cumsg.setContent(targetMsg.get(i).getContent());
            if(sub_id==targetMsg.get(i).getSub_id())cumsg.setType(1);
            else cumsg.setType(0);
            msgList.add(cumsg);
        }
    }

    @Override
    protected void initClick() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                long timecurrentTimeMillis = System.currentTimeMillis();
                SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                String time = sdfTwo.format(timecurrentTimeMillis);
                if (!"".equals(content)) {
                    Msg msg = new Msg();
                    msg.setContent(content);
                    msg.setTime(time);
                    msg.setType(1);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");
                }
                Connector.getDatabase();
                Msg addmsg = new Msg();
                addmsg.setType(1);
                addmsg.setContent(content);
                addmsg.setSub_id(sub_id);
                addmsg.setObj_id(obj_id);
                addmsg.setTime(time);
                addmsg.save();
            }
        });
    }

    @Override
    protected void initWidget() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        msgRecyclerView.setAdapter(adapter);
    }

}