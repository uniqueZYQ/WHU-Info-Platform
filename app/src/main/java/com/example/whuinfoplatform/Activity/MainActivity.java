package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private EditText id;
    private EditText pw;
    private LinearLayout layout,edit;
    Button startLogin;
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
        layout = (LinearLayout) findViewById(R.id.main_activity_layout);
        edit = (LinearLayout) findViewById(R.id.edit);
        startLogin = (Button) findViewById(R.id.log_in);
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

        startCreateUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Create_User_promote_Activity.class);
            startActivity(intent);
        });
        startLogin.setOnClickListener(v -> {
            if(!(id.getText().toString().equals(""))&&!(pw.getText().toString().equals(""))) {
                String currid = id.getText().toString();
                String currpw = pw.getText().toString();

                UserConnection userConnection=new UserConnection();
                userConnection.initLoginConnection(currid,currpw,new okhttp3.Callback(){

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        BToast.showText(MainActivity.this, "服务器连接失败，请检查网络设置", false);
                        shake();
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
                            BToast.showText(MainActivity.this, user.getRealname()+"登录成功！", true);
                            Looper.loop();
                        }
                        else{
                            Looper.prepare();
                            shakeAndSetPwdBlank();
                            Looper.loop();
                        }
                    }
                });
            }
            else{
                BToast.showText(MainActivity.this, "请输入完整的信息！", false);
                Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_button);//给组件播放动画效果
                layout.startAnimation(shake);
            }
        });
    }

    private void shakeAndSetPwdBlank(){
        runOnUiThread(() -> {
            pw.setText("");
            BToast.showText(MainActivity.this, "学号或密码错误！", false);
            Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_button);//给组件播放动画效果
            //findViewById(R.id.bt).startAnimation(shake);  //写法一
            edit.startAnimation(shake);  //写法二
            pw.setBackgroundColor(getResources().getColor(R.color.light_red));
            id.setBackgroundColor(getResources().getColor(R.color.light_red));
        });
        try{
            Thread.sleep(2500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        runOnUiThread(() -> {
            pw.setBackgroundColor(getResources().getColor(R.color.gray));
            id.setBackgroundColor(getResources().getColor(R.color.gray));
        });
    }

    private void shake(){
        runOnUiThread(() -> {
            Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_button);//给组件播放动画效果
            //findViewById(R.id.bt).startAnimation(shake);  //写法一
            layout.startAnimation(shake);  //写法二
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
        dialog.setPositiveButton("是", (dialog1, which) -> ActivityCollector.finishAll());
        dialog.setNegativeButton("不，我再想想", (dialog12, which) -> {});
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

