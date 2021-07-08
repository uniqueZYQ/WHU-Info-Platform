package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.R;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Renew_Info_prmote_Activity extends rootActivity {
    private com.example.whuinfoplatform.databinding.ActivityRenewInfoPrmoteBinding binding;
    int fd_form=0,help_form=0,score=0,init=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding= com.example.whuinfoplatform.databinding.ActivityRenewInfoPrmoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent=getIntent();
        int form=intent.getIntExtra("form",0);
        int id=intent.getIntExtra("id",0);
        Connector.getDatabase();
        Info info = new Info();
        List<Info> cuinfo=DataSupport.where("id=?",String.valueOf(id)).find(Info.class);
        binding.detail.setText(cuinfo.get(0).getDetail());
        binding.editCommobj.setText(cuinfo.get(0).getLesson());
        binding.editPlace.setText(cuinfo.get(0).getPlace());
        binding.editDate.setText(cuinfo.get(0).getDate());
        binding.editReward.setText(String.valueOf(cuinfo.get(0).getReward()));
        binding.editPrice.setText(String.valueOf(cuinfo.get(0).getPrice()));
        switch(form) {
            case 1:{
                binding.form.setText("学术咨询");
                binding.persInfoFdType.setVisibility(View.VISIBLE);
                binding.editReward.setVisibility(View.VISIBLE);
                binding.persInfoFdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(init==1)parent.setSelection(cuinfo.get(0).getFd_form());
                        fd_form=(int)position;
                        init=0;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        parent.setSelection(cuinfo.get(0).getFd_form());
                    }
                });
                binding.finish.setOnClickListener(v->{
                    double reward=binding.editReward.getText().toString().equals("")?-2:
                            binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                    binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                            binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                    String detail=binding.detail.getText().toString();
                    if(reward==-1||reward==0)
                        Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                    else if(!(fd_form==0||reward==-2||detail.equals(""))) {
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
                            Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else{
                            info.setFd_form(fd_form);
                            info.setReward(reward);
                            info.setDetail(detail);
                            Toast.makeText(Renew_Info_prmote_Activity.this, "修改成功！\n可在[我发布的]查看最新信息", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(Renew_Info_prmote_Activity.this,Personal_Center_Activity.class);
                            startActivity(intent1);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.updateAll("id=?",String.valueOf(id));
                        }
                    }
                    else
                        Toast.makeText(Renew_Info_prmote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                });
                break;
            }
            case 2:{
                binding.form.setText("日常求助");
                binding.persInfoHelpType.setVisibility(View.VISIBLE);
                binding.editReward.setVisibility(View.VISIBLE);
                binding.persInfoHelpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(init==1)parent.setSelection(cuinfo.get(0).getHelp_form());
                        help_form=(int)position;
                        init=0;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        parent.setSelection(cuinfo.get(0).getHelp_form());
                    }
                });
                binding.finish.setOnClickListener(v->{
                    double reward=binding.editReward.getText().toString().equals("")?-2:
                            binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                    binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                            binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                    String detail=binding.detail.getText().toString();
                    if(reward==-1||reward==0)
                        Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                    else if(!(help_form==0||reward==-2||detail.equals(""))) {
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
                            Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else{
                            info.setHelp_form(help_form);
                            info.setReward(reward);
                            info.setDetail(detail);
                            Toast.makeText(Renew_Info_prmote_Activity.this, "修改成功！\n刷新[我发布的]可查看最新信息", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(Renew_Info_prmote_Activity.this,Personal_Center_Activity.class);
                            startActivity(intent1);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.updateAll("id=?",String.valueOf(id));
                        }
                    }
                    else
                        Toast.makeText(Renew_Info_prmote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                });
                break;
            }
            case 3:{
                binding.form.setText("物品出售");
                binding.editPrice.setVisibility(View.VISIBLE);
                binding.finish.setOnClickListener(v->{
                    double price=binding.editPrice.getText().toString().equals("")?-2:
                            binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                    binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                            binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                    String detail=binding.detail.getText().toString();
                    if(price==-1||price==0)
                        Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                    else if(!(price==-2||detail.equals(""))) {
                        String text=String.valueOf(price);
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
                            Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else{
                            info.setPrice(price);
                            info.setDetail(detail);
                            Toast.makeText(Renew_Info_prmote_Activity.this, "修改成功！\n刷新[我发布的]可查看最新信息", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(Renew_Info_prmote_Activity.this,Personal_Center_Activity.class);
                            startActivity(intent1);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.updateAll("id=?",String.valueOf(id));
                        }
                    }
                    else
                        Toast.makeText(Renew_Info_prmote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                });
                break;
            }
            case 4:{
                binding.form.setText("物品求购");
                binding.editPrice.setVisibility(View.VISIBLE);
                binding.finish.setOnClickListener(v->{
                    double price=binding.editPrice.getText().toString().equals("")?-2:
                            binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                    binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                            binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                    String detail=binding.detail.getText().toString();
                    if(price==-1||price==0)
                        Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                    else if(!(price==-2||detail.equals(""))) {
                        String text=String.valueOf(price);
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
                            Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else{
                            info.setPrice(price);
                            info.setDetail(detail);
                            Toast.makeText(Renew_Info_prmote_Activity.this, "修改成功！\n刷新[我发布的]可查看最新信息", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(Renew_Info_prmote_Activity.this,Personal_Center_Activity.class);
                            startActivity(intent1);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.updateAll("id=?",String.valueOf(id));
                        }
                    }
                    else
                        Toast.makeText(Renew_Info_prmote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                });
                break;
            }
            case 5:{
                binding.form.setText("组织性活动信息");
                binding.editReward.setVisibility(View.VISIBLE);
                binding.editDate.setVisibility(View.VISIBLE);
                binding.editPlace.setVisibility(View.VISIBLE);
                binding.finish.setOnClickListener(v->{
                    double reward=binding.editReward.getText().toString().equals("")?-2:
                            binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                    binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                            binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                    String date=binding.editDate.getText().toString();
                    String place=binding.editPlace.getText().toString();
                    String detail=binding.detail.getText().toString();
                    if(reward==-1||reward==0)
                        Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                    else if(!(reward==-2||detail.equals("")||date.equals("")||place.equals(""))) {
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
                            Toast.makeText(Renew_Info_prmote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else{
                            info.setReward(reward);
                            info.setPlace(place);
                            info.setDate(date);
                            info.setDetail(detail);
                            Toast.makeText(Renew_Info_prmote_Activity.this, "修改成功！\n刷新[我发布的]可查看最新信息", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(Renew_Info_prmote_Activity.this,Personal_Center_Activity.class);
                            startActivity(intent1);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.updateAll("id=?",String.valueOf(id));
                        }
                    }
                    else
                        Toast.makeText(Renew_Info_prmote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                });
                break;
            }
            case 6:{
                binding.form.setText("课程点评信息");
                binding.editCommobj.setVisibility(View.VISIBLE);
                binding.editScore.setVisibility(View.VISIBLE);
                binding.editScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(init==1)parent.setSelection(cuinfo.get(0).getScore());
                        score=(int)position;
                        init=0;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        parent.setSelection(cuinfo.get(0).getScore());
                    }
                });
                binding.finish.setOnClickListener(v->{
                    String lesson=binding.editCommobj.getText().toString();
                    String detail=binding.detail.getText().toString();
                    if(!(lesson.equals("")||detail.equals("")||score==0)) {
                        info.setScore(score);
                        info.setLesson(lesson);
                        info.setDetail(detail);
                        Toast.makeText(Renew_Info_prmote_Activity.this, "修改成功！\n刷新[我发布的]可查看最新信息", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(Renew_Info_prmote_Activity.this,Personal_Center_Activity.class);
                        startActivity(intent1);
                        long timecurrentTimeMillis = System.currentTimeMillis();
                        SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                        String time = sdfTwo.format(timecurrentTimeMillis);
                        info.setSend_date(time);
                        info.updateAll("id=?",String.valueOf(id));
                    }
                    else
                        Toast.makeText(Renew_Info_prmote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                });
                break;
            }
        }
    }

    @Override
    protected void initClick() {
        super.initClick();

    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("我发布的-信息详情-修改信息");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}