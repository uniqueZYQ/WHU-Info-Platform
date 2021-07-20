package com.example.whuinfoplatform.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Adapter.MsgAdapter;
import com.example.whuinfoplatform.Entity.Picture;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityChatWindowBinding;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Chat_Window_Activity extends rootActivity {
    private DB_USER dbHelper;
    private List<Msg> msgList=new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private ActivityChatWindowBinding binding;
    String owner=new String();
    int sub_id=0;
    int obj_id=0;
    private boolean upload=false;
    private Dialog mCameraDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.WHITE);
        }
    }

    @Override
    public void bindView() {
        binding=ActivityChatWindowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        Intent intent = getIntent();
        sub_id=intent.getIntExtra("sub_id",0);
        if(sub_id==0) sub_id=intent.getIntExtra("sub_id1",0);
        obj_id=intent.getIntExtra("obj_id",0);
        if(obj_id==0) obj_id=intent.getIntExtra("obj_id1",0);
        dbHelper=new DB_USER(this,"User.db",null,7);
        dbHelper.getWritableDatabase();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from User where id=?", new String[]{String.valueOf(obj_id)}, null);
        if(cursor.moveToFirst()) {
            if (cursor.getCount() != 0) {
                owner=cursor.getString(cursor.getColumnIndex("nickname"));
            }
        }
        cursor.close();
        inputText = (EditText) findViewById(R.id.inputText);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        adapter = new MsgAdapter(msgList);
        binding.objnm.setText(owner);
        List<Msg> targetMsg= DataSupport.where("sub_id=? and obj_id=? or sub_id=? and obj_id=? ",String.valueOf(sub_id),String.valueOf(obj_id),String.valueOf(obj_id),String.valueOf(sub_id)).find(Msg.class);
        for(int i=0;i<targetMsg.size();i++){
            Msg cumsg=new Msg();
            cumsg.setTime(targetMsg.get(i).getTime());
            cumsg.setObj_id(targetMsg.get(i).getObj_id());
            cumsg.setSub_id(targetMsg.get(i).getSub_id());
            cumsg.setContent(targetMsg.get(i).getContent());
            cumsg.setPicture(targetMsg.get(i).getPicture());
            cumsg.setRecalled(targetMsg.get(i).getRecalled());
            if(sub_id==targetMsg.get(i).getSub_id())cumsg.setType(1);
            else cumsg.setType(0);
            msgList.add(cumsg);
        }
        binding.send.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // 设置按钮圆角率为30
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
            }
        });
        binding.send.setClipToOutline(true);
    }

    @Override
    protected void initClick() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                boolean valid = false;
                for (int i = 0; i < content.length(); i++) {
                    if (content.charAt(i) == '\0' || content.charAt(i) == '\n' || content.charAt(i) == ' ')
                        continue;
                    else
                        valid = true;
                }
                if (valid) {
                    long timecurrentTimeMillis = System.currentTimeMillis();
                    SimpleDateFormat sdfTwo = new SimpleDateFormat("YYYY年MM月dd日 HH:mm", Locale.getDefault());
                    String time = sdfTwo.format(timecurrentTimeMillis);
                    Msg msg = new Msg();
                    msg.setContent(content);
                    msg.setTime(time);
                    msg.setType(1);
                    msg.setSub_id(sub_id);
                    msg.setObj_id(obj_id);
                    msg.setRecalled(0);
                    msg.setPicture(0);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                    inputText.setText("");
                    Connector.getDatabase();
                    Msg addmsg = new Msg();
                    addmsg.setType(1);
                    addmsg.setContent(content);
                    addmsg.setSub_id(sub_id);
                    addmsg.setObj_id(obj_id);
                    addmsg.setPicture(0);
                    addmsg.setRecalled(0);
                    addmsg.setTime(time);
                    addmsg.save();
                } else {
                    inputText.setText("");
                    Toast.makeText(Chat_Window_Activity.this, "不能发送无意义的内容!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.sendPicture.setOnClickListener(v->{
            upload=true;
            setDialog();
        });
    }

    @Override
    protected void initWidget() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        msgRecyclerView.setTag(1);
        msgRecyclerView.setAdapter(adapter);
        msgRecyclerView.scrollToPosition(msgList.size() - 1);//最下方显示最新消息
        binding.send.setVisibility(View.GONE);
        binding.sendPicture.setVisibility(View.VISIBLE);
        binding.inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!String.valueOf(s).equals("")) {
                    binding.send.setVisibility(View.VISIBLE);
                    binding.sendPicture.setVisibility(View.GONE);
                }
                else{
                    binding.send.setVisibility(View.GONE);
                    binding.sendPicture.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setDialog() {
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(v -> {
            Intent intent = new Intent(Chat_Window_Activity.this,Upload_Picture_promote_Activity.class);
            intent.putExtra("chat",1);
            intent.putExtra("type",2);
            startActivity(intent);
        });
        root.findViewById(R.id.btn_open_camera).setOnClickListener(v -> {
            Intent intent = new Intent(Chat_Window_Activity.this,Upload_Picture_promote_Activity.class);
            intent.putExtra("chat",1);
            intent.putExtra("type",3);
            startActivity(intent);
        });
        Button btn_choose_img=root.findViewById(R.id.btn_choose_img);
        Button btn_open_camera=root.findViewById(R.id.btn_open_camera);
        btn_choose_img.setText("从相册中选择图片");
        btn_open_camera.setText("拍摄图片");
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
    protected void onPause() {
        super.onPause();
        if(upload)
            if(mCameraDialog.isShowing()) mCameraDialog.cancel();
    }

    /**
     *
     * 重写此方法，加上setIntent(intent);否则在onResume里面得不到intent
     * @param intent intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume(){
        super.onResume();
        Intent intent=getIntent();
        int picture=intent.getIntExtra("picture_id",0);
        if(picture!=0){
            long timecurrentTimeMillis = System.currentTimeMillis();
            SimpleDateFormat sdfTwo = new SimpleDateFormat("YYYY年MM月dd日 HH:mm", Locale.getDefault());
            String time = sdfTwo.format(timecurrentTimeMillis);
            Msg msg = new Msg();
            msg.setContent("");
            msg.setTime(time);
            msg.setType(1);
            msg.setSub_id(sub_id);
            msg.setObj_id(obj_id);
            msg.setRecalled(0);
            msg.setPicture(picture);
            msgList.add(msg);
            adapter.notifyItemInserted(msgList.size() - 1);
            msgRecyclerView.scrollToPosition(msgList.size() - 1);
            inputText.setText("");
            Connector.getDatabase();
            Msg addmsg = new Msg();
            addmsg.setType(1);
            addmsg.setContent("");
            addmsg.setSub_id(sub_id);
            addmsg.setObj_id(obj_id);
            addmsg.setPicture(picture);
            addmsg.setRecalled(0);
            addmsg.setTime(time);
            addmsg.save();
        }
    }
}