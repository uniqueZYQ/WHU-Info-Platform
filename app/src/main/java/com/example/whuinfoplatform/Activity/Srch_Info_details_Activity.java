package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Entity.Picture;
import com.example.whuinfoplatform.R;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Srch_Info_details_Activity extends rootActivity {
    private com.example.whuinfoplatform.databinding.ActivitySrchInfoDetailsBinding binding;
    int form=0;
    private DB_USER dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding= com.example.whuinfoplatform.databinding.ActivitySrchInfoDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        String owner=intent.getStringExtra("owner");
        int id= intent.getIntExtra("id",0);
        int self=intent.getIntExtra("self",0);
        if(self==0){
            binding.answer.setVisibility(View.VISIBLE);
        }
        dbHelper = new DB_USER(this, "User.db", null, 7);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Info> info = DataSupport.where("id=?",String.valueOf(id)).find(Info.class);
        for(int i=0;i<info.size();i++){
            form=info.get(i).getForm();
            binding.sendDate.setText("发布于"+(info.get(i).getSend_date()));
            binding.form.setText("信息类别："+(info.get(i).getForm()==1?"私人性-学术咨询信息":info.get(i).getForm()==2?"私人性-日常求助信息":info.get(i).getForm()==3?"私人性-物品出售信息":info.get(i).getForm()==4?"私人性-物品求购信息":info.get(i).getForm()==5?"组织性活动信息":"课程点评信息"));
            binding.detail.setText("具体内容：\n    "+(info.get(i).getDetail()));
            binding.nickname.setText(owner);
            if(info.get(i).getPicture4()!=0){
                List<Picture> picture4=DataSupport.where("id=?",String.valueOf(info.get(i).getPicture4())).find(Picture.class);
                byte[] in_4 = picture4.get(0).getPicture();
                Bitmap bit_4 = BitmapFactory.decodeByteArray(in_4, 0, in_4.length);
                binding.picture4.setImageBitmap(bit_4);
                binding.picture4.setVisibility(View.VISIBLE);
                binding.picture4.setOnClickListener(v->{
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_4,true);
                });
            }
            if(info.get(i).getPicture3()!=0){
                List<Picture> picture3=DataSupport.where("id=?",String.valueOf(info.get(i).getPicture3())).find(Picture.class);
                byte[] in_3 = picture3.get(0).getPicture();
                Bitmap bit_3 = BitmapFactory.decodeByteArray(in_3, 0, in_3.length);
                binding.picture3.setImageBitmap(bit_3);
                binding.picture3.setVisibility(View.VISIBLE);
                binding.picture3.setOnClickListener(v->{
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_3,true);
                });
            }
            if(info.get(i).getPicture2()!=0){
                List<Picture> picture2=DataSupport.where("id=?",String.valueOf(info.get(i).getPicture2())).find(Picture.class);
                byte[] in_2 = picture2.get(0).getPicture();
                Bitmap bit_2 = BitmapFactory.decodeByteArray(in_2, 0, in_2.length);
                binding.picture2.setImageBitmap(bit_2);
                binding.picture2.setVisibility(View.VISIBLE);
                binding.picture2.setOnClickListener(v->{
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_2,true);
                });
            }
            if(info.get(i).getPicture1()!=0){
                List<Picture> picture1=DataSupport.where("id=?",String.valueOf(info.get(i).getPicture1())).find(Picture.class);
                byte[] in_1 = picture1.get(0).getPicture();
                Bitmap bit_1 = BitmapFactory.decodeByteArray(in_1, 0, in_1.length);
                binding.picture1.setImageBitmap(bit_1);
                binding.picture1.setVisibility(View.VISIBLE);
                binding.picture1.setOnClickListener(v->{
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_1,true);
                });
            }
            int owner_id=info.get(i).getOwner_id();
            Cursor cursor = db.rawQuery("select picture from User where id=?", new String[]{String.valueOf(owner_id)}, null);
            if(cursor.moveToFirst()){
                if (cursor.getCount() != 0) {
                    byte[] in = cursor.getBlob(cursor.getColumnIndex("picture"));
                    Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                    binding.picture.setImageBitmap(bit);
                }
            }
            cursor.close();
            if(info.get(i).getAnswered()==0) binding.answer.setText("响应信息\n(你是第一个!)");
            else binding.answer.setText("继续响应");
            switch(info.get(i).getForm()){
                case 1:{
                    binding.fdForm.setText("咨询领域："+(
                            info.get(i).getFd_form()==1?"哲学":
                            info.get(i).getFd_form()==2?"经济学":
                            info.get(i).getFd_form()==3?"法学":
                            info.get(i).getFd_form()==4?"文学":
                            info.get(i).getFd_form()==5?"历史学":
                            info.get(i).getFd_form()==6?"理学":
                            info.get(i).getFd_form()==7?"工学":
                            info.get(i).getFd_form()==8?"农学":
                            info.get(i).getFd_form()==9?"医学":
                            info.get(i).getFd_form()==10?"管理学":
                            info.get(i).getFd_form()==11?"教育学":"艺术学")
                    );
                    binding.fdForm.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(info.get(i).getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(info.get(i).getAnswered()==0?"暂未被响应":"已被响应"));
                    break;
                }
                case 2:{
                    binding.helpForm.setText("求助问题："+(
                            info.get(i).getHelp_form()==1?"代取物品":
                            info.get(i).getHelp_form()==2?"信息求问":
                            info.get(i).getHelp_form()==3?"寻物启事":
                            info.get(i).getHelp_form()==4?"失物招领":"其他")
                    );
                    binding.helpForm.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(info.get(i).getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(info.get(i).getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 3:
                case 4:{
                    binding.price.setText("预期价格："+(String.valueOf(info.get(i).getPrice()))+"元");
                    binding.price.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(info.get(i).getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 5:{
                    binding.date.setText("活动日期："+info.get(i).getDate());
                    binding.date.setVisibility(View.VISIBLE);
                    binding.place.setText("活动地点："+info.get(i).getPlace());
                    binding.place.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(info.get(i).getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(info.get(i).getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 6:{
                    binding.lesson.setText("课程名称："+info.get(i).getLesson());
                    binding.lesson.setVisibility(View.VISIBLE);
                    binding.score.setText("评分："+String.valueOf(info.get(i).getScore())+"分");
                    binding.score.setVisibility(View.VISIBLE);
                    binding.answer.setVisibility(View.GONE);
                    break;
                }
                default:
            }
        }
    }

    @Override
    protected void initClick() {
        super.initClick();
        Intent intent=getIntent();
        int id=intent.getIntExtra("id",0);
        int sub_id=intent.getIntExtra("locid",0);
        int obj_id=intent.getIntExtra("ownerid",0);
        binding.answer.setOnClickListener(v->{
            Info info=new Info();
            info.setAnswered(1);
            info.updateAll("id=?",String.valueOf(id));
            Intent intent1 = new Intent(Srch_Info_details_Activity.this,Chat_Window_Activity.class);
            intent1.putExtra("sub_id",sub_id);
            intent1.putExtra("obj_id",obj_id);
            startActivity(intent1);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("搜索-信息详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}