package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.LocalLogin;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    private EditText id,pw;
    private LinearLayout layout,edit,wrong_promote;
    private Button startLogin,startCreateUser,showPw;
    private int wrong_time=0;
    private TextView second;
    private CheckBox checkBox;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window=getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);
        id=findViewById(R.id.edit_stdid);
        pw=findViewById(R.id.edit_pwd);
        layout=findViewById(R.id.main_activity_layout);
        wrong_promote=findViewById(R.id.wrong_promote);
        edit=findViewById(R.id.edit);
        showPw=findViewById(R.id.show_pw);
        second=findViewById(R.id.second);
        startLogin=findViewById(R.id.log_in);
        startCreateUser=findViewById(R.id.create_user);
        checkBox=findViewById(R.id.checkbox);

        pw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        id.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

        pw.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}

            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count){}

            @Override
            public void afterTextChanged(Editable s){
                if(s.length()>0){
                    for(int i=0;i<s.length();i++){
                        char c=s.charAt(i);
                        if(c>=0x4e00&&c<=0X9fff){ // ?????????????????????
                            // ?????????????????????????????????????????????????????????
                            s.delete(i,i+1);
                        }
                    }
                }
            }
        });

        id.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after){}

            @Override
            public void onTextChanged(CharSequence s,int start,int before,int count){}

            @Override
            public void afterTextChanged(Editable s){
                if(s.length()>0){
                    for(int i=0;i<s.length();i++){
                        char c=s.charAt(i);
                        if(!(c=='0'||c=='1'||c=='2'||c=='3'||c=='4'||c=='5'||c=='6'||c=='7'||c=='8'||c=='9')){
                            s.delete(i,i+1);
                        }
                    }
                }
            }
        });

        startCreateUser.setOnClickListener(v->{
            Intent intent=new Intent(MainActivity.this,Create_User_promote_Activity.class);
            startActivity(intent);
        });

        startLogin.setOnClickListener(v->{
            if(!(id.getText().toString().equals(""))&&!(pw.getText().toString().equals(""))){
                String currid=id.getText().toString();
                String currpw=pw.getText().toString();

                UserConnection userConnection=new UserConnection();
                userConnection.initLoginConnection(currid,currpw,new okhttp3.Callback(){

                    @Override
                    public void onFailure(Call call,IOException e){
                        Looper.prepare();
                        BToast.showText(MainActivity.this,"?????????????????????????????????????????????",false);
                        shake();
                        Looper.loop();
                    }

                    @RequiresApi(api=Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call call,Response response) throws IOException{
                        String result=response.body().string();
                        User user=new User();
                        userConnection.parseJSON(user,result);
                        if(user.getCode()==101){
                            if(checkBox.isChecked()){
                                LocalLogin localLogin=new LocalLogin();
                                localLogin.updateOrInsert(user.getId());
                            }
                            /*Intent intent=new Intent(MainActivity.this,Basic_Activity.class);
                            intent.putExtra("tmpnkn",user.getNickname());
                            intent.putExtra("tmpid",user.getId());
                            intent.putExtra("ref_nkn",1);*/
                            Intent intent=new Intent(MainActivity.this,Info_Hall_Activity.class);
                            intent.putExtra("id",user.getId());
                            startActivity(intent);
                            Looper.prepare();
                            BToast.showText(MainActivity.this,user.getRealname()+"???????????????",true);
                            MainActivity.this.finish();
                        }
                        else{
                            Looper.prepare();
                            shakeAndSetPwdBlank();
                        }
                        Looper.loop();
                    }
                });
            }
            else{
                BToast.showText(MainActivity.this,"???????????????????????????",false);
                Animation shake=AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_button);//???????????????????????????
                layout.startAnimation(shake);
            }
        });

        showPw.setOnTouchListener((v,event)->{
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    break;
                case MotionEvent.ACTION_UP:
                    pw.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
                    break;
                }
            return true;
        });
    }

    private void shakeAndSetPwdBlank(){
        runOnUiThread(()->{
            pw.setText("");
            BToast.showText(MainActivity.this,"????????????????????????",false);
            Animation shake=AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_button);//???????????????????????????
            //findViewById(R.id.bt).startAnimation(shake);  //?????????
            edit.startAnimation(shake);  //?????????
            pw.setBackgroundColor(getResources().getColor(R.color.light_red));
            id.setBackgroundColor(getResources().getColor(R.color.light_red));
        });
        if(++wrong_time==5){
            runOnUiThread(()->{
                wrong_promote.setVisibility(View.VISIBLE);
                startLogin.setVisibility(View.GONE);
                second.setText("60");
            });
            for(int i=60;i>=0;i--){
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
                int finalI=i;
                runOnUiThread(()->second.setText(String.valueOf(finalI)));
            }
            runOnUiThread(()->{
                wrong_promote.setVisibility(View.GONE);
                startLogin.setVisibility(View.VISIBLE);
            });
            wrong_time=0;
        }
        try{
            Thread.sleep(2500);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        runOnUiThread(()->{
            pw.setBackgroundColor(getResources().getColor(R.color.gray));
            id.setBackgroundColor(getResources().getColor(R.color.gray));
        });
    }

    private void shake(){
        runOnUiThread(()->{
            Animation shake=AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_button);//???????????????????????????
            //findViewById(R.id.bt).startAnimation(shake);  //?????????
            layout.startAnimation(shake);  //?????????
        });
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0){
            dialog();
            return true;
        }
        return true;
    }

    protected void dialog(){
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("?????????");
        dialog.setMessage("????????????WHU?????????");
        dialog.setCancelable(true);
        dialog.setPositiveButton("???",(dialog1,which)->ActivityCollector.finishAll());
        dialog.setNegativeButton("??????????????????",(dialog12,which)->{});
        dialog.show();
    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        id.setText("");
        pw.setText("");
        wrong_time=0;
    }
}

