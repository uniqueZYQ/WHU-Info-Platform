package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPersonalCenterBinding;
import com.example.whuinfoplatform.databinding.ActivityPersonalMessageBinding;
import com.example.whuinfoplatform.databinding.ActivityRenewPermsgPromteBinding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Personal_Message_Activity extends rootActivity implements View.OnClickListener{
    private ActivityPersonalMessageBinding binding;
    private DB_USER dbHelper;
    int id=0,type1=1;
    Dialog mCameraDialog;

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
        String tmpnkn="";
        String tmprnm="";
        String tmpstdid="";
        dbHelper = new DB_USER(this, "User.db", null, 7);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select stdid,realname,nickname,picture from User where id=?", new String[]{Integer.toString(id)}, null);
        if(cursor.moveToFirst()){
            if (cursor.getCount() != 0) {
                tmpnkn=cursor.getString(cursor.getColumnIndex("nickname"));
                tmprnm=cursor.getString(cursor.getColumnIndex("realname"));
                tmpstdid=Long.toString(cursor.getLong(cursor.getColumnIndex("stdid")));
                byte[] in = cursor.getBlob(cursor.getColumnIndex("picture"));
                Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                binding.picture.setImageBitmap(bit);
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
            intent.putExtra("type",1);
            startActivity(intent);
        });
        binding.buttonUpload.setOnClickListener(v->{
            type1=0;
            setDialog();
        });
    }

    private void setDialog() {
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(this);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
        mCameraDialog = new Dialog(this, R.style.BottomDialog);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        //dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("个人资料");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_choose_img:
                //选择照片按钮
                intent = new Intent(Personal_Message_Activity.this,Renew_Permsg_Promte_Activity.class);
                intent.putExtra("id",id);
                intent.putExtra("type",2);
                startActivity(intent);
                break;
            case R.id.btn_open_camera:
                //拍照按钮
                intent = new Intent(Personal_Message_Activity.this,Renew_Permsg_Promte_Activity.class);
                intent.putExtra("id",id);
                intent.putExtra("type",3);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        if(type1==0)
            if(mCameraDialog.isShowing()) mCameraDialog.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}