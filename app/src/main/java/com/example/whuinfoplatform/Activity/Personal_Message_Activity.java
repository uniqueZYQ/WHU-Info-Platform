package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.BottomNavigation;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.LocalLogin;
import com.example.whuinfoplatform.Entity.LocalPicture;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPersonalMessageBinding;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class Personal_Message_Activity extends rootActivity implements View.OnClickListener{
    private ActivityPersonalMessageBinding binding;
    private int id=0,type1=1;
    private Dialog mCameraDialog;
    private Bitmap bit;
    private long exitTime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    @Override
    public void bindView(){
        binding=ActivityPersonalMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigation bottomNavigation=new BottomNavigation();
        bottomNavigation.init(binding.navigationBar,Personal_Message_Activity.this,4);
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    @Override
    protected void initData(){
        super.initData();
        Intent intent=getIntent();
        id=intent.getIntExtra("id",0);
        UserConnection userConnection=new UserConnection();
        List<LocalPicture> localPictures=DataSupport.where("user_code=?",String.valueOf(id)).find(LocalPicture.class);
        if(localPictures.size()!=0){
            byte[] in=Base64.getDecoder().decode(localPictures.get(0).getPicture());
            userConnection.queryUserInfoWithoutPicture(String.valueOf(id),new okhttp3.Callback(){

                @Override
                public void onFailure(Call call,IOException e){
                    Looper.prepare();
                    BToast.showText(Personal_Message_Activity.this,"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }

                @RequiresApi(api=Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call call,Response response) throws IOException{
                    String result=response.body().string();
                    User user=new User();
                    userConnection.parseJSON(user,result);
                    Looper.prepare();
                    if(user.getCode()==101){
                        String nkn=user.getNickname();
                        String rnm=user.getRealname();
                        String stdid=user.getStdid();
                        bit=BitmapFactory.decodeByteArray(in,0,in.length);
                        showResult(stdid,nkn,rnm,bit);
                    }
                    Looper.loop();
                }
            });
        }
        else{
            userConnection.queryUserInfo(String.valueOf(id),new okhttp3.Callback(){

                @Override
                public void onFailure(Call call,IOException e){
                    Looper.prepare();
                    BToast.showText(Personal_Message_Activity.this,"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }

                @RequiresApi(api=Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call call,Response response) throws IOException{
                    String result=response.body().string();
                    User user=new User();
                    userConnection.parseJSON(user,result);
                    Looper.prepare();
                    if(user.getCode()==101){
                        String nkn=user.getNickname();
                        String rnm=user.getRealname();
                        String stdid=user.getStdid();
                        byte[] in=user.getPicture();
                        bit=BitmapFactory.decodeByteArray(in,0,in.length);
                        showResult(stdid,nkn,rnm,bit);
                        LocalPicture localPicture=new LocalPicture();
                        localPicture.userPictureAddToLocal(id,Base64.getEncoder().encodeToString(in),user.getPicture_version());
                    }
                    Looper.loop();
                }
            });
        }
    }

    @Override
    protected void initClick(){
        super.initClick();
        binding.buttonUpgrade.setOnClickListener(v->{
            Intent intent=new Intent(Personal_Message_Activity.this,Renew_Permsg_promote_Activity.class);
            intent.putExtra("id",id);
            intent.putExtra("type",1);
            startActivity(intent);
        });
        binding.buttonUpload.setOnClickListener(v->{
            type1=0;
            setDialog();
        });
        binding.picture.setOnClickListener(v->{
            EnlargePicture enlargePicture=new EnlargePicture();
            enlargePicture.EnlargePicture(Personal_Message_Activity.this,bit,false);
        });
        binding.changeUser.setOnClickListener(v->{
            DataSupport.deleteAll(LocalLogin.class);
            Intent intent=new Intent(Personal_Message_Activity.this,MainActivity.class);
            startActivity(intent);
            this.finish();
        });
    }

    private void showResult(String stdid,String nkn,String rnm,Bitmap bit){
        runOnUiThread(()->{
            binding.picture.setImageBitmap(bit);
            binding.textNickname.setText("昵称:"+nkn);
            binding.textRealname.setText("真实姓名:"+rnm);
            binding.textStdid.setText("学号:"+stdid);
        });
    }

    private void setDialog() {
        LinearLayout root=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.bottom_dialog,null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(this);
        root.findViewById(R.id.btn_open_camera).setOnClickListener(this);
        mCameraDialog=new Dialog(this, R.style.BottomDialog);
        mCameraDialog.setContentView(root);
        Window dialogWindow=mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        //dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp=dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x=0; // 新位置X坐标
        lp.y=0; // 新位置Y坐标
        lp.width=getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0,0);
        lp.height=root.getMeasuredHeight();
        lp.alpha=9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget(){
        super.initWidget();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("  个人资料");
    }

    @Override
    public void onClick(View view){
        Intent intent;
        switch(view.getId()){
            case R.id.btn_choose_img:
                //选择照片按钮
                intent=new Intent(Personal_Message_Activity.this,Renew_Permsg_promote_Activity.class);
                intent.putExtra("id",id);
                intent.putExtra("type",2);
                startActivity(intent);
                break;
            case R.id.btn_open_camera:
                //拍照按钮
                intent=new Intent(Personal_Message_Activity.this,Renew_Permsg_promote_Activity.class);
                intent.putExtra("id",id);
                intent.putExtra("type",3);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause(){
        if(type1==0)
            if(mCameraDialog.isShowing()) mCameraDialog.cancel();
        super.onPause();
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    @Override
    protected void onResume(){
        super.onResume();
        initData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime)>2000){
                BToast.showText(Personal_Message_Activity.this,"再按一次退出程序",true);
                exitTime=System.currentTimeMillis();
            }
            else{
                ActivityCollector.finishAll();
                ActivityCollector.removeActivity(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}