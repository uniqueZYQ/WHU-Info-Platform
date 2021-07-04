package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*import com.example.whuinfoplatform.DB.DB_INFO;*/
import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPublishInfoPromoteBinding;

import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Publish_Info_promote_Activity extends rootActivity {
    private ActivityPublishInfoPromoteBinding binding;
    int id=0,id1=0,id2=0,pos_fd=0,pos_help=0,pos_score=0,form=-1;
    double reward=0,price=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding=ActivityPublishInfoPromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
                        reward=binding.editReward.getText().toString().equals("")?0:Double.parseDouble(binding.editReward.getText().toString());
                        if(pos_fd==0||detail.equals("")||reward==0)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Publish_Info_promote_Activity.this,"发布成功!\n可前往[我发布的]查看详情",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Publish_Info_promote_Activity.this,Info_Hall_Activity.class);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.save();
                            startActivity(intent);
                        }
                        break;
                    }
                    case 2:{
                        String detail=binding.detail.getText().toString();
                        reward=binding.editReward.getText().toString().equals("")?0:Double.parseDouble(binding.editReward.getText().toString());
                        if(pos_help==0||detail.equals("")||reward==0)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else{
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
                            Toast.makeText(Publish_Info_promote_Activity.this,"发布成功!\n可前往[我发布的]查看详情",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Publish_Info_promote_Activity.this,Info_Hall_Activity.class);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.save();
                            startActivity(intent);
                        }
                        break;
                    }
                    case 3:{
                        String detail=binding.detail.getText().toString();
                        price=binding.editPrice.getText().toString().equals("")?0:Double.parseDouble(binding.editPrice.getText().toString());
                        if(price==0||detail.equals(""))
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
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
                    case 4:{
                        String detail=binding.detail.getText().toString();
                        price=binding.editPrice.getText().toString().equals("")?0:Double.parseDouble(binding.editPrice.getText().toString());
                        if(price==0||detail.equals(""))
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
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
                    case 5:{
                        String detail=binding.detail.getText().toString();
                        String place=binding.editPlace.getText().toString();
                        String date=binding.editDate.getText().toString();
                        /*if(binding.editReward.getText().toString().equals(""))
                            reward=0;*/
                        reward=binding.editReward.getText().toString().equals("")?0:Double.parseDouble(binding.editReward.getText().toString());
                        if(detail.equals("")||place.equals("")||date.equals("")||reward==0)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("信息大厅-编辑新信息");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}