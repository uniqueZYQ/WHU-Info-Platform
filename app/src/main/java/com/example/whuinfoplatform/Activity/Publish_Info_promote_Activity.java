package com.example.whuinfoplatform.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.example.whuinfoplatform.DB.DB_INFO;*/
import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.Picture;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPublishInfoPromoteBinding;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Publish_Info_promote_Activity extends rootActivity {
    private ActivityPublishInfoPromoteBinding binding;
    private int id=0,id1=0,id2=0,pos_fd=0,pos_help=0,pos_score=0,form=-1,picture_count=0;
    private double reward=0,price=0;
    private Dialog mCameraDialog;
    private boolean upload=false;
    private ArrayList<Integer> pictureList=new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding=ActivityPublishInfoPromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.calendar.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        id=intent.getIntExtra("id",0);
        binding.infoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((int)id){
                    case 1:{//私人性信息
                        id1=1;
                        binding.calendar.setVisibility(View.GONE);
                        binding.persInfoType.setVisibility(View.VISIBLE);
                        binding.editPlace.setVisibility(View.GONE);
                        binding.editDate.setVisibility(View.GONE);
                        binding.editScore.setVisibility(View.GONE);
                        binding.editCommobj.setVisibility(View.GONE);
                        binding.persInfoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch ((int)id){
                                    case 1:{//学术咨询
                                        id2=1;
                                        binding.persInfoFdType.setVisibility(View.VISIBLE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editReward.setVisibility(View.VISIBLE);
                                        binding.editPrice.setVisibility(View.GONE);
                                        form=1;
                                        break;
                                    }
                                    case 2:{//日常求助
                                        id2=2;
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.VISIBLE);
                                        binding.editReward.setVisibility(View.VISIBLE);
                                        binding.editPrice.setVisibility(View.GONE);
                                        form=2;
                                        break;
                                    }
                                    case 3:{/*物品求购*/
                                        id2=3;
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editPrice.setVisibility(View.VISIBLE);
                                        binding.editReward.setVisibility(View.GONE);
                                        form=3;
                                        break;
                                    }
                                    case 4:{//物品出售
                                        id2=4;
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editPrice.setVisibility(View.VISIBLE);
                                        binding.editReward.setVisibility(View.GONE);
                                        form=4;
                                        break;
                                    }
                                    default:{
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editPrice.setVisibility(View.GONE);
                                        binding.editReward.setVisibility(View.GONE);
                                    }

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) { }
                        });
                        break;
                    }
                    case 2:{//组织性信息
                        id1=2;
                        id2=1;
                        binding.persInfoType.setVisibility(View.GONE);
                        binding.persInfoFdType.setVisibility(View.GONE);
                        binding.persInfoHelpType.setVisibility(View.GONE);
                        binding.editPrice.setVisibility(View.GONE);
                        binding.editReward.setVisibility(View.VISIBLE);
                        binding.editDate.setVisibility(View.VISIBLE);
                        binding.editPlace.setVisibility(View.VISIBLE);
                        binding.editScore.setVisibility(View.GONE);
                        binding.editCommobj.setVisibility(View.GONE);
                        form=5;
                        break;
                    }
                    case 3:{//点评信息
                        id1=3;
                        id2=1;
                        binding.calendar.setVisibility(View.GONE);
                        binding.persInfoType.setVisibility(View.GONE);
                        binding.persInfoFdType.setVisibility(View.GONE);
                        binding.persInfoHelpType.setVisibility(View.GONE);
                        binding.editPrice.setVisibility(View.GONE);
                        binding.editReward.setVisibility(View.GONE);
                        binding.editDate.setVisibility(View.GONE);
                        binding.editPlace.setVisibility(View.GONE);
                        binding.editScore.setVisibility(View.VISIBLE);
                        binding.editCommobj.setVisibility(View.VISIBLE);
                        form=6;
                        break;
                    }
                    default:
                        binding.calendar.setVisibility(View.GONE);
                        binding.persInfoType.setVisibility(View.GONE);
                        binding.persInfoFdType.setVisibility(View.GONE);
                        binding.persInfoHelpType.setVisibility(View.GONE);
                        binding.editPrice.setVisibility(View.GONE);
                        binding.editReward.setVisibility(View.GONE);
                        binding.editDate.setVisibility(View.GONE);
                        binding.editPlace.setVisibility(View.GONE);
                        binding.editScore.setVisibility(View.GONE);
                        binding.editCommobj.setVisibility(View.GONE);

                }
                //Toast.makeText(Publish_Info_promote_Activity.this,"NOW "+(int)id,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(Publish_Info_promote_Activity.this,"NOW NONE",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.persInfoFdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos_fd=(int)position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        binding.persInfoHelpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos_help=(int)position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        binding.editScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos_score=(int)position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        binding.send.setOnClickListener(v->{
            if(id1!=0&&id2!=0){
                switch(form){
                    case 1:{
                        String detail=binding.detail.getText().toString();
                        reward=binding.editReward.getText().toString().equals("")?-2:
                            binding.editReward.getText().toString().charAt(0)=='.'?-1:
                            binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                            binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                        if(pos_fd==0||detail.equals("")||reward==-2)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(reward==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text=String.valueOf(reward);
                            int length=text.length();
                            int res=0;
                            boolean count=false;
                            for(int i=0;i<length;i++){
                                if(count)
                                    res++;
                                if(text.charAt(i)=='.'){
                                    count=true;
                                }
                            }
                            if(res>2||count&&res==0)
                                Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                            else{
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(1);
                                info.setFd_form(pos_fd);
                                info.setHelp_form(-1);
                                info.setPrice(-1);
                                info.setDate("");
                                info.setPlace("");
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(reward);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                Toast.makeText(Publish_Info_promote_Activity.this,"发布成功!\n可前往[我发布的]查看详情",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this,Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 2:{
                        String detail=binding.detail.getText().toString();
                        reward=binding.editReward.getText().toString().equals("")?-2:
                                binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                        if(pos_help==0||detail.equals("")||reward==-2)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(reward==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text = String.valueOf(reward);
                            int length = text.length();
                            int res = 0;
                            boolean count = false;
                            for (int i = 0; i < length; i++) {
                                if (count)
                                    res++;
                                if (text.charAt(i) == '.') {
                                    count = true;
                                }
                            }
                            if (res > 2 || count && res == 0)
                                Toast.makeText(Publish_Info_promote_Activity.this, "金额格式错误!", Toast.LENGTH_SHORT).show();
                            else {
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(2);
                                info.setFd_form(-1);
                                info.setHelp_form(pos_help);
                                info.setPrice(-1);
                                info.setDate("");
                                info.setPlace("");
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(reward);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 3:{
                        String detail=binding.detail.getText().toString();
                        price=binding.editPrice.getText().toString().equals("")?-2:
                                binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                        if(price==-2||detail.equals(""))
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(price==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text = String.valueOf(price);
                            int length = text.length();
                            int res = 0;
                            boolean count = false;
                            for (int i = 0; i < length; i++) {
                                if (count)
                                    res++;
                                if (text.charAt(i) == '.') {
                                    count = true;
                                }
                            }
                            if (res > 2 || count && res == 0)
                                Toast.makeText(Publish_Info_promote_Activity.this, "金额格式错误!", Toast.LENGTH_SHORT).show();
                            else {
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(3);
                                info.setFd_form(-1);
                                info.setHelp_form(-1);
                                info.setPrice(price);
                                info.setDate("");
                                info.setPlace("");
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(-1);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 4:{
                        String detail=binding.detail.getText().toString();
                        price=binding.editPrice.getText().toString().equals("")?-2:
                                binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                        if(price==-2||detail.equals(""))
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(price==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text = String.valueOf(price);
                            int length = text.length();
                            int res = 0;
                            boolean count = false;
                            for (int i = 0; i < length; i++) {
                                if (count)
                                    res++;
                                if (text.charAt(i) == '.') {
                                    count = true;
                                }
                            }
                            if (res > 2 || count && res == 0)
                                Toast.makeText(Publish_Info_promote_Activity.this, "金额格式错误!", Toast.LENGTH_SHORT).show();
                            else {
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(4);
                                info.setFd_form(-1);
                                info.setHelp_form(-1);
                                info.setPrice(price);
                                info.setDate("");
                                info.setPlace("");
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(-1);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 5:{
                        String detail=binding.detail.getText().toString();
                        String place=binding.editPlace.getText().toString();
                        String date=binding.editDate.getText().toString();
                        reward=binding.editReward.getText().toString().equals("")?-2:
                                binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                        if(detail.equals("")||place.equals("")||date.equals("")||reward==-2)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(reward==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text = String.valueOf(reward);
                            int length = text.length();
                            int res = 0;
                            boolean count = false;
                            for (int i = 0; i < length; i++) {
                                if (count)
                                    res++;
                                if (text.charAt(i) == '.') {
                                    count = true;
                                }
                            }
                            if (res > 2 || count && res == 0)
                                Toast.makeText(Publish_Info_promote_Activity.this, "金额格式错误!", Toast.LENGTH_SHORT).show();
                            else {
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(5);
                                info.setFd_form(-1);
                                info.setHelp_form(-1);
                                info.setPrice(-1);
                                info.setDate(date);
                                info.setPlace(place);
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(reward);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 6:{
                        String detail=binding.detail.getText().toString();
                        String lesson=binding.editCommobj.getText().toString();
                        if(detail.equals("")||lesson.equals("")||pos_score==0)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else {
                            Connector.getDatabase();
                            Info info = new Info();
                            info.setOwner_id(id);
                            info.setAnswered(0);
                            info.setForm(6);
                            info.setFd_form(-1);
                            info.setHelp_form(-1);
                            info.setPrice(-1);
                            info.setDate("");
                            info.setPlace("");
                            info.setLesson(lesson);
                            info.setScore(pos_score);
                            info.setDetail(detail);
                            info.setReward(-1);
                            info.setPicture1(ret_list_1());
                            info.setPicture2(ret_list_2());
                            info.setPicture3(ret_list_3());
                            info.setPicture4(ret_list_4());
                            Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.save();
                            startActivity(intent);
                        }
                        break;
                    }
                    default:
                }
            }
            else
                Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息！",Toast.LENGTH_SHORT).show();
        });
        binding.editDate.setOnClickListener(v -> {
            binding.calendar.setVisibility(View.VISIBLE);
            long timecurrentTimeMillis = System.currentTimeMillis();
            binding.calendar.setMinDate(timecurrentTimeMillis);
            if(!binding.editDate.getText().toString().equals("")){
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date current=simpleDateFormat.parse(binding.editDate.getText().toString());
                    long time=current.getTime();
                    binding.calendar.setDate(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(month<9){
                    if(dayOfMonth<10){
                        binding.editDate.setText(String.valueOf(year)+"-0"+String.valueOf(month+1)+"-0"+String.valueOf(dayOfMonth));
                    }
                    else{
                        binding.editDate.setText(String.valueOf(year)+"-0"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth));
                    }
                }
                else{
                    if(dayOfMonth<10){
                        binding.editDate.setText(String.valueOf(year)+"-"+String.valueOf(month+1)+"-0"+String.valueOf(dayOfMonth));
                    }
                    else{
                        binding.editDate.setText(String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth));
                    }
                }
                binding.calendar.setVisibility(View.GONE);
            }
        });
        //开始定义日历隐藏事件
        binding.editPlace.setOnClickListener(v -> {
            binding.calendar.setVisibility(View.GONE);
        });
        binding.detail.setOnClickListener(v -> {
            binding.calendar.setVisibility(View.GONE);
        });
        binding.editReward.setOnClickListener(v -> {
            binding.calendar.setVisibility(View.GONE);
        });
        binding.editPlace.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.calendar.setVisibility(View.GONE);
                } else {

                }
            }
        });
        binding.detail.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.calendar.setVisibility(View.GONE);
                } else {

                }
            }
        });
        binding.editReward.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.calendar.setVisibility(View.GONE);
                } else {

                }
            }
        });
        //结束日历隐藏事件
        binding.upload.setOnClickListener(v -> {
            if(picture_count>=4){//最多上传四张图片
                Toast.makeText(Publish_Info_promote_Activity.this,"最多只能上传四张图片!",Toast.LENGTH_SHORT).show();
            }
            else{
                upload=true;
                setDialog();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("信息大厅-编辑新信息");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void setDialog() {
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(v -> {
            Intent intent = new Intent(Publish_Info_promote_Activity.this,Upload_Picture_promote_Activity.class);
            intent.putExtra("id",id);
            intent.putExtra("type",2);
            startActivity(intent);
        });
        root.findViewById(R.id.btn_open_camera).setOnClickListener(v -> {
            Intent intent = new Intent(Publish_Info_promote_Activity.this,Upload_Picture_promote_Activity.class);
            intent.putExtra("id",id);
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

    @Override
    protected void onResume(){
        super.onResume();
        initPictureList();
    }

    private void initPictureList(){
        Intent intent=getIntent();
        int picture_id=intent.getIntExtra("picture_id",0);
        if(picture_id!=0){
            picture_count++;
            pictureList.add(picture_id);
        }
        Connector.getDatabase();
        switch(pictureList.size()){
            case 4:
                int picture4=pictureList.get(3);
                List<Picture> picture_4 = DataSupport.where("id=?",String.valueOf(picture4)).find(Picture.class);
                byte[] in_4=picture_4.get(0).getPicture();
                binding.picture4.setVisibility(View.VISIBLE);
                Bitmap bit_4 = BitmapFactory.decodeByteArray(in_4, 0, in_4.length);
                binding.picture4.setImageBitmap(bit_4);
            case 3:
                int picture3=pictureList.get(2);
                List<Picture> picture_3 = DataSupport.where("id=?",String.valueOf(picture3)).find(Picture.class);
                byte[] in_3=picture_3.get(0).getPicture();
                binding.picture3.setVisibility(View.VISIBLE);
                Bitmap bit_3 = BitmapFactory.decodeByteArray(in_3, 0, in_3.length);
                binding.picture3.setImageBitmap(bit_3);
            case 2:
                int picture2=pictureList.get(1);
                List<Picture> picture_2 = DataSupport.where("id=?",String.valueOf(picture2)).find(Picture.class);
                byte[] in_2=picture_2.get(0).getPicture();
                binding.picture2.setVisibility(View.VISIBLE);
                Bitmap bit_2 = BitmapFactory.decodeByteArray(in_2, 0, in_2.length);
                binding.picture2.setImageBitmap(bit_2);
            case 1:
                int picture1=pictureList.get(0);
                List<Picture> picture_1 = DataSupport.where("id=?",String.valueOf(picture1)).find(Picture.class);
                byte[] in_1=picture_1.get(0).getPicture();
                binding.picture1.setVisibility(View.VISIBLE);
                Bitmap bit_1 = BitmapFactory.decodeByteArray(in_1, 0, in_1.length);
                binding.picture1.setImageBitmap(bit_1);
                binding.upload.setText("继续上传图片");
                break;
            default:
                break;

        }
    }

    private int ret_list_1(){
        if(pictureList.size()>=1){
            return pictureList.get(0);
        }
        else{
            return 0;
        }
    }

    private int ret_list_2(){
        if(pictureList.size()>=2){
            return pictureList.get(1);
        }
        else{
            return 0;
        }
    }

    private int ret_list_3(){
        if(pictureList.size()>=3){
            return pictureList.get(2);
        }
        else{
            return 0;
        }
    }

    private int ret_list_4(){
        if(pictureList.size()>=4){
            return pictureList.get(3);
        }
        else{
            return 0;
        }
    }
}