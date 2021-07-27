package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
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
import android.os.Looper;
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
import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.R;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText id;
    private EditText pw;
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
        id = (EditText) findViewById(R.id.edit_stdid);
        pw = (EditText) findViewById(R.id.edit_pwd);
        Button startLogin = (Button) findViewById(R.id.log_in);
        Button startCreateUser = (Button) findViewById(R.id.create_user);

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
                    String currid = id.getText().toString();
                    String currpw = pw.getText().toString();

                    UserConnection userConnection=new UserConnection();
                    userConnection.initLoginConnection(currid,currpw,new okhttp3.Callback(){

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            Toast.makeText(MainActivity.this,"服务器连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            User user=new User();
                            userConnection.parseJSON(user,result);
                            if(user.getCode()==101){
                                Intent intent = new Intent(MainActivity.this, Basic_Activity.class);
                                intent.putExtra("tmpnkn",user.getNickname());
                                intent.putExtra("tmpid",user.getId());
                                startActivity(intent);
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, user.getRealname()+"登录成功！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else{
                                setPwdBlank();
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, "学号或密码错误！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(MainActivity.this, "请输入完整的信息！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPwdBlank(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pw.setText("");
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

    @Override
    protected void onResume(){
        super.onResume();
        id.setText("");
        pw.setText("");
    }
}

