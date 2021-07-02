package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityCreateUserPromoteBinding;

public class Create_User_promote_Activity extends rootActivity {
    private DB_USER dbHelper;
    private ActivityCreateUserPromoteBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper=new DB_USER(this,"User.db",null,6);
    }

    @Override
    public void bindView() {
        binding=ActivityCreateUserPromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData() {
        super.initData();
        binding.editPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if (c >= 0x4e00 && c <= 0X9fff) { // 根据字节码判断
                            // 如果是中文，则清除输入的字符，否则保留
                            s.delete(i,i+1);
                        }
                    }
                }
            }
        });
        binding.editRealname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    for (int i = 0; i < s.length(); i++) {
                        char c = s.charAt(i);
                        if (!(c >= 0x4e00 && c <= 0X9fff)) { // 根据字节码判断
                            // 如果不是中文，则清除输入的字符，否则保留
                            s.delete(i,i+1);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.button1.setOnClickListener(v->{
            String nnm = binding.editNickname.getText().toString();
            String pw = binding.editPwd.getText().toString();
            String rnm = binding.editRealname.getText().toString();
            String id = binding.editStdid.getText().toString();
            if(nnm.equals("")||pw.equals("")||rnm.equals("")||id.equals("")){
                Toast.makeText(Create_User_promote_Activity.this,"请完善信息！",Toast.LENGTH_SHORT).show();
            }
            else {
                if(id.length()==13) {
                    dbHelper.getWritableDatabase();
                    Boolean ret = false;
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("nickname", nnm);
                    values.put("pwd", pw);
                    values.put("realname", rnm);
                    values.put("stdid", id);
                    Cursor cursor = db.rawQuery("select id from User where stdid=?", new String[]{id}, null);
                    if(cursor.moveToFirst()) {
                        if (cursor.getCount() != 0) {
                            ret=true;
                        }
                    }
                    cursor.close();
                    if(!ret){
                        db.insert("User", null, values);
                        Toast.makeText(Create_User_promote_Activity.this, "创建成功！", Toast.LENGTH_SHORT).show();
                        values.clear();
                        Intent intent = new Intent(Create_User_promote_Activity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(Create_User_promote_Activity.this, "该学号对应账号已经存在！", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(Create_User_promote_Activity.this, "学号必须为13位数字！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("创建账号");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}