package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;

import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.SenseCheck;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityCreateUserPromoteBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import okhttp3.Call;
import okhttp3.Response;

public class Create_User_promote_Activity extends rootActivity {

    private ActivityCreateUserPromoteBinding binding;
    ImageView picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding=ActivityCreateUserPromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData() {
        super.initData();
        picture=binding.picture;
        picture.setImageResource(R.drawable.default_head);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void initClick() {
        super.initClick();
        binding.button1.setOnClickListener(v->{
            SenseCheck senseCheck=new SenseCheck();
            String nnm = binding.editNickname.getText().toString();
            String pw = binding.editPwd.getText().toString();
            String rnm = binding.editRealname.getText().toString();
            String id = binding.editStdid.getText().toString();
            if(nnm.equals("")||pw.equals("")||rnm.equals("")||id.equals("")){
                BToast.showText(Create_User_promote_Activity.this,"请完善信息！",false);
            }
            else {
                if(!senseCheck.SenseCheckAllBlankOrNull(nnm)){
                    BToast.showText(Create_User_promote_Activity.this,"昵称不能为无实义内容！",false);
                    binding.editNickname.setText("");
                }
                else if(id.length()==13) {
                    picture.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(picture.getDrawingCache());
                    picture.setDrawingCacheEnabled(false);
                    final ByteArrayOutputStream os = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    byte[] in=os.toByteArray();
                    String FileBuf = Base64.getEncoder().encodeToString(in);
                    UserConnection userConnection=new UserConnection();
                    userConnection.initRegisterConnection(id,pw,rnm,nnm,FileBuf,new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            BToast.showText(Create_User_promote_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            User user=new User();
                            userConnection.parseJSON(user,result);
                            Looper.prepare();
                            if(user.getCode()==101){
                                BToast.showText(Create_User_promote_Activity.this,user.getResponse(),true);
                                Intent intent = new Intent(Create_User_promote_Activity.this, MainActivity.class);
                                startActivity(intent);
                            }else{
                                BToast.showText(Create_User_promote_Activity.this,user.getResponse(),false);
                            }
                            Looper.loop();
                        }
                    });
                }
                else
                    BToast.showText(Create_User_promote_Activity.this,"学号必须为13位数字！",false);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("创建账号");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}