package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPersonalCenterBinding;
import com.example.whuinfoplatform.databinding.ActivityPersonalMessageBinding;

public class Personal_Message_Activity extends rootActivity {
    private ActivityPersonalMessageBinding binding;
    private DB_USER dbHelper;
    int id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding=ActivityPersonalMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        id=intent.getIntExtra("id",0);
        String tmpnkn="null";
        String tmprnm="null";
        String tmpstdid="null";
        dbHelper = new DB_USER(this, "User.db", null, 6);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select stdid,realname,nickname from User where id=?", new String[]{Integer.toString(id)}, null);
        if(cursor.moveToFirst()){
            if (cursor.getCount() != 0) {
                tmpnkn=cursor.getString(cursor.getColumnIndex("nickname"));
                tmprnm=cursor.getString(cursor.getColumnIndex("realname"));
                tmpstdid=Long.toString(cursor.getLong(cursor.getColumnIndex("stdid")));
            }
            else
                Toast.makeText(Personal_Message_Activity.this,"读取失败",Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        binding.textNickname.setText("昵称:"+tmpnkn);
        binding.textRealname.setText("真实姓名:"+tmprnm);
        binding.textStdid.setText("学号:"+tmpstdid);
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.buttonUpgrade.setOnClickListener(v->{
            Intent intent = new Intent(Personal_Message_Activity.this,Renew_Permsg_Promte_Activity.class);
            intent.putExtra("id",id);
            startActivity(intent);
        });
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("个人资料");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}