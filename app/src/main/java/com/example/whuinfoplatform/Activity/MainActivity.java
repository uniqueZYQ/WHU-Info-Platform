package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.R;

import org.litepal.crud.DataSupport;

public class MainActivity extends AppCompatActivity {
    private DB_USER dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        EditText id = (EditText) findViewById(R.id.edit_stdid);
        EditText pw = (EditText) findViewById(R.id.edit_pwd);
        id.setText("");
        pw.setText("");
        Button startLogin = (Button) findViewById(R.id.log_in);
        Button startCreateUser = (Button) findViewById(R.id.create_user);
        dbHelper = new DB_USER(this, "User.db", null, 7);
        pw.addTextChangedListener(new TextWatcher() {
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

        startCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Create_User_promote_Activity.class);
                startActivity(intent);
            }
        });
        startLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(id.getText().toString().equals(""))&&!(pw.getText().toString().equals(""))) {
                    Boolean ret = false;
                    String tmpnkn="";
                    String tmprnm="";
                    int tmpid=0;
                    String currid = id.getText().toString();
                    String currpw = pw.getText().toString();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = db.rawQuery("select stdid,pwd,realname,nickname,id from User where stdid=? and pwd=?", new String[]{currid, currpw}, null);
                    if(cursor.moveToFirst()){
                        if (cursor.getCount() != 0) {
                            ret = true;
                            tmprnm=cursor.getString(cursor.getColumnIndex("realname"));
                            tmpnkn=cursor.getString(cursor.getColumnIndex("nickname"));
                            tmpid=cursor.getInt(cursor.getColumnIndex("id"));
                        }
                    }
                    cursor.close();
                    if (ret == true) {
                        Toast.makeText(MainActivity.this, tmprnm+"登录成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, Basic_Activity.class);
                        intent.putExtra("tmpnkn",tmpnkn);
                        intent.putExtra("tmpid",tmpid);
                        //Toast.makeText(MainActivity.this, currid, Toast.LENGTH_SHORT).show();//test;
                        startActivity(intent);
                        id.setText("");
                        pw.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "学号或密码错误！", Toast.LENGTH_SHORT).show();
                        pw.setText("");
                    }
                }
                else
                    Toast.makeText(MainActivity.this, "请输入完整的信息！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            dialog();
            return true;
        }
        return true;
    }

    protected void dialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("未登录");
        dialog.setMessage("确定退出WHU平台？");
        dialog.setCancelable(true);
        dialog.setPositiveButton("是",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCollector.finishAll();
            }
        });
        dialog.setNegativeButton("不，我再想想",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}

