package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityCreateUserPromoteBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
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
            String nnm = binding.editNickname.getText().toString();
            String pw = binding.editPwd.getText().toString();
            String rnm = binding.editRealname.getText().toString();
            String id = binding.editStdid.getText().toString();
            if(nnm.equals("")||pw.equals("")||rnm.equals("")||id.equals("")){
                Toast.makeText(Create_User_promote_Activity.this,"请完善信息！",Toast.LENGTH_SHORT).show();
            }
            else {
                if(id.length()==13) {
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
                            Toast.makeText(Create_User_promote_Activity.this,"服务器连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            User user=new User();
                            userConnection.parseJSON(user,result);
                            Looper.prepare();
                            Toast.makeText(Create_User_promote_Activity.this,user.getResponse(),Toast.LENGTH_SHORT).show();
                            if(user.getCode()==101){
                                Intent intent = new Intent(Create_User_promote_Activity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            Looper.loop();
                        }
                    });
                }
                else
                    Toast.makeText(Create_User_promote_Activity.this, "学号必须为13位数字！", Toast.LENGTH_SHORT).show();
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