package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPersonalMessageBinding;
import com.example.whuinfoplatform.databinding.ActivityRenewPermsgPromteBinding;

public class Renew_Permsg_Promte_Activity extends rootActivity {
    private ActivityRenewPermsgPromteBinding binding;
    private DB_USER dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding= ActivityRenewPermsgPromteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData() {
        super.initData();
        String newnkn="";//test
        String newrnm="";//test
        String newstdid="";//test
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

        Intent intent1 = getIntent();
        String id=Integer.toString(intent1.getIntExtra("id",0));
        dbHelper = new DB_USER(this, "User.db", null, 6);
        SQLiteDatabase db1 = dbHelper.getWritableDatabase();
        Cursor cursor = db1.rawQuery("select nickname,realname,stdid from User where id=?", new String[]{id}, null);
        if(cursor.moveToFirst()){
            if (cursor.getCount() != 0) {
                newnkn=cursor.getString(cursor.getColumnIndex("nickname"));
                newrnm=cursor.getString(cursor.getColumnIndex("realname"));
                newstdid=cursor.getString(cursor.getColumnIndex("stdid"));
            }
        }
        cursor.close();
        binding.editNickname.setText(newnkn);
        binding.textStdidRnm.setText(newstdid+"-"+newrnm);
        //test
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.button1.setOnClickListener(v->{
            Intent intent1 = getIntent();
            String id=Integer.toString(intent1.getIntExtra("id",0));
            String newnkn=binding.editNickname.getText().toString();
            String newpwd=binding.editPwd.getText().toString();
            dbHelper = new DB_USER(this, "User.db", null, 6);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if((newnkn.equals(""))||(newpwd.equals(""))) {
                Toast.makeText(Renew_Permsg_Promte_Activity.this, "请填入完整信息！", Toast.LENGTH_SHORT).show();
            }
            else {
                db.execSQL("update User set nickname = ?,pwd = ? where id = ?", new String[]{newnkn, newpwd, id});
                Toast.makeText(Renew_Permsg_Promte_Activity.this, "修改成功，请重新登录！", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(Renew_Permsg_Promte_Activity.this, MainActivity.class);
                startActivity(intent2);

            }
        });
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("个人资料-修改");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}