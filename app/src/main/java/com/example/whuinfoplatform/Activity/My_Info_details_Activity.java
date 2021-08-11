package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;

import com.example.whuinfoplatform.Dao.InfoConnection;
import com.example.whuinfoplatform.Dao.PictureConnection;
import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.LocalPicture;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.Entity.WebResponse;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityMyInfoDetailsBinding;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class My_Info_details_Activity extends rootActivity {
    private ActivityMyInfoDetailsBinding binding;
    int form=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding= com.example.whuinfoplatform.databinding.ActivityMyInfoDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        int id=intent.getIntExtra("id",0);
        int owner_id=intent.getIntExtra("owner_id",0);

        List<LocalPicture> localPictures=DataSupport.where("user_code=?",String.valueOf(owner_id)).find(LocalPicture.class);
        if(localPictures.size()!=0){
            byte[] in=Base64.getDecoder().decode(localPictures.get(0).getPicture());
            UserConnection userConnection=new UserConnection();
            userConnection.queryUserInfoWithoutPicture(String.valueOf(owner_id), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        String nkn=jsonObject.getString("nickname");
                        setPictureAndNicknameWithoutDownloadingPicture(nkn,in);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(My_Info_details_Activity.this,"数据解析失败！",false);
                        Looper.loop();
                    }
                }
            });
        }
        else{
            UserConnection userConnection=new UserConnection();
            userConnection.queryUserInfo(String.valueOf(owner_id), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    User user=new User();
                    userConnection.parseJSON(user,result);
                    setPictureAndNickname(user);
                    LocalPicture localPicture=new LocalPicture();
                    localPicture.userPictureAddToLocal(owner_id,Base64.getEncoder().encodeToString(user.getPicture()));
                }
            });
        }
        InfoConnection infoConnection=new InfoConnection();
        infoConnection.queryMyInfoDetailConnection(String.valueOf(id), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                Info myInformation=new Info();
                try {
                    infoConnection.parseJSONForMyInfoDetailResponse(myInformation,result);
                    if (myInformation.getCode()!=101){
                        Looper.prepare();
                        BToast.showText(My_Info_details_Activity.this,myInformation.getResponse(),false);
                        Looper.loop();
                    }
                    else{
                        showMyInfoDetail(myInformation);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initClick() {
        super.initClick();
        Intent intent = getIntent();
        int id=intent.getIntExtra("id",0);
        binding.renewInfo.setOnClickListener(v->{
            Intent intent1=new Intent(My_Info_details_Activity.this,Renew_Info_promote_Activity.class);
            intent1.putExtra("id",id);
            intent1.putExtra("form",form);
            startActivity(intent1);
        });
        binding.delInfo.setOnClickListener(v->{
            AlertDialog.Builder dialog = new AlertDialog.Builder(My_Info_details_Activity.this);
            dialog.setTitle("删除信息");
            dialog.setMessage("该数据无法恢复！\n确定删除？");
            dialog.setCancelable(false);
            dialog.setPositiveButton("是", (dialog12, which) -> {
                InfoConnection infoConnection=new InfoConnection();
                infoConnection.deleteMyInfoConnection(String.valueOf(id), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Looper.prepare();
                        String result=response.body().string();
                        WebResponse webResponse=new WebResponse();
                        infoConnection.parseJSONForInfoResponse(webResponse,result);
                        if(webResponse.getCode()==101){
                            BToast.showText(My_Info_details_Activity.this,webResponse.getResponse(),true);
                            Intent intent2=new Intent(My_Info_details_Activity.this,Personal_Center_Activity.class);
                            startActivity(intent2);
                        }
                        else{
                            BToast.showText(My_Info_details_Activity.this,webResponse.getResponse(),false);
                        }
                        Looper.loop();
                    }
                });
            });
            dialog.setNegativeButton("不，我再想想", (dialog1, which) -> {});
            dialog.show();

        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("我发布的-信息详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void setPictureAndNickname(User user){
        runOnUiThread(() -> {
            binding.nickname.setText(user.getNickname());
            byte[] in=user.getPicture();
            Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
            binding.picture.setImageBitmap(bit);
        });
    }

    private void setPictureAndNicknameWithoutDownloadingPicture(String nickname,byte[] in){
        runOnUiThread(() -> {
            binding.nickname.setText(nickname);
            Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
            binding.picture.setImageBitmap(bit);
        });
    }

    private void setNullImage(ImageView imageView){
        runOnUiThread(() -> {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.download_failed);
            imageView.setClickable(false);
        });
    }

    private void setImage(ImageView imageView,byte[] in){
        runOnUiThread(() -> {
            Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
            imageView.setImageBitmap(bit);
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(v->{
                EnlargePicture enlargePicture=new EnlargePicture();
                enlargePicture.EnlargePicture(My_Info_details_Activity.this,bit,true);
            });
        });
    }

    private void showMyInfoDetail(Info myInformation){
        runOnUiThread(() -> {
            form=myInformation.getForm();
            binding.sendDate.setText("发布于"+(myInformation.getSend_date()));
            binding.form.setText("信息类别："+(myInformation.getForm()==1?"私人性-学术咨询信息":myInformation.getForm()==2?"私人性-日常求助信息":myInformation.getForm()==3?"私人性-物品出售信息":myInformation.getForm()==4?"私人性-物品求购信息":myInformation.getForm()==5?"组织性活动信息":"课程点评信息"));
            binding.detail.setText("具体内容：\n    "+(myInformation.getDetail()));
            if(myInformation.getPicture4()!=0){
                binding.picture4.setVisibility(View.VISIBLE);
                binding.picture4.setImageResource(R.drawable.downloading);
                List<LocalPicture> picture4=DataSupport.where("code=?",String.valueOf(myInformation.getPicture4())).find(LocalPicture.class);
                if(picture4.size()!=0){
                    byte[] in_4 = Base64.getDecoder().decode(picture4.get(0).getPicture());
                    Bitmap bit_4 = BitmapFactory.decodeByteArray(in_4, 0, in_4.length);
                    binding.picture4.setImageBitmap(bit_4);
                    binding.picture4.setOnClickListener(v->{
                        EnlargePicture enlargePicture=new EnlargePicture();
                        enlargePicture.EnlargePicture(My_Info_details_Activity.this,bit_4,true);
                    });
                }
                else{
                    PictureConnection pictureConnection=new PictureConnection();
                    pictureConnection.initDownloadConnection(String.valueOf(myInformation.getPicture4()), new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            setNullImage(binding.picture4);
                            BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            LocalPicture picture=new LocalPicture();
                            pictureConnection.parseJSONForDownloadPictureResponse(picture,result);
                            if(picture.getCode()==101){
                                byte[] in_4=Base64.getDecoder().decode(picture.getPicture());
                                setImage(binding.picture4,in_4);
                                picture.infoPictureAddToLocal(myInformation.getPicture4(),picture.getPicture());
                            }
                            else{
                                Looper.prepare();
                                setNullImage(binding.picture4);
                                BToast.showText(My_Info_details_Activity.this,picture.getResponse(),false);
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            if(myInformation.getPicture3()!=0){
                binding.picture3.setVisibility(View.VISIBLE);
                binding.picture3.setImageResource(R.drawable.downloading);
                List<LocalPicture> picture3=DataSupport.where("code=?",String.valueOf(myInformation.getPicture3())).find(LocalPicture.class);
                if(picture3.size()!=0){
                    byte[] in_3 = Base64.getDecoder().decode(picture3.get(0).getPicture());
                    Bitmap bit_3 = BitmapFactory.decodeByteArray(in_3, 0, in_3.length);
                    binding.picture3.setImageBitmap(bit_3);
                    binding.picture3.setOnClickListener(v->{
                        EnlargePicture enlargePicture=new EnlargePicture();
                        enlargePicture.EnlargePicture(My_Info_details_Activity.this,bit_3,true);
                    });
                }
                else{
                    PictureConnection pictureConnection=new PictureConnection();
                    pictureConnection.initDownloadConnection(String.valueOf(myInformation.getPicture3()), new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            setNullImage(binding.picture3);
                            BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            LocalPicture picture=new LocalPicture();
                            pictureConnection.parseJSONForDownloadPictureResponse(picture,result);
                            if(picture.getCode()==101){
                                byte[] in_3=Base64.getDecoder().decode(picture.getPicture());
                                setImage(binding.picture3,in_3);
                                picture.infoPictureAddToLocal(myInformation.getPicture3(),picture.getPicture());
                            }
                            else{
                                Looper.prepare();
                                setNullImage(binding.picture3);
                                BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            if(myInformation.getPicture2()!=0){
                binding.picture2.setVisibility(View.VISIBLE);
                binding.picture2.setImageResource(R.drawable.downloading);
                List<LocalPicture> picture2=DataSupport.where("code=?",String.valueOf(myInformation.getPicture2())).find(LocalPicture.class);
                if(picture2.size()!=0){
                    byte[] in_2 = Base64.getDecoder().decode(picture2.get(0).getPicture());
                    Bitmap bit_2 = BitmapFactory.decodeByteArray(in_2, 0, in_2.length);
                    binding.picture2.setImageBitmap(bit_2);
                    binding.picture2.setOnClickListener(v->{
                        EnlargePicture enlargePicture=new EnlargePicture();
                        enlargePicture.EnlargePicture(My_Info_details_Activity.this,bit_2,true);
                    });
                }
                else{
                    PictureConnection pictureConnection=new PictureConnection();
                    pictureConnection.initDownloadConnection(String.valueOf(myInformation.getPicture2()), new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            setNullImage(binding.picture2);
                            BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            LocalPicture picture=new LocalPicture();
                            pictureConnection.parseJSONForDownloadPictureResponse(picture,result);
                            if(picture.getCode()==101){
                                byte[] in_2=Base64.getDecoder().decode(picture.getPicture());
                                setImage(binding.picture2,in_2);
                                picture.infoPictureAddToLocal(myInformation.getPicture2(),picture.getPicture());
                            }
                            else{
                                Looper.prepare();
                                setNullImage(binding.picture2);
                                BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            if(myInformation.getPicture1()!=0){
                binding.picture1.setVisibility(View.VISIBLE);
                binding.picture1.setImageResource(R.drawable.downloading);
                List<LocalPicture> picture1=DataSupport.where("code=?",String.valueOf(myInformation.getPicture1())).find(LocalPicture.class);
                if(picture1.size()!=0){
                    byte[] in_1 = Base64.getDecoder().decode(picture1.get(0).getPicture());
                    Bitmap bit_1 = BitmapFactory.decodeByteArray(in_1, 0, in_1.length);
                    binding.picture1.setImageBitmap(bit_1);
                    binding.picture1.setOnClickListener(v->{
                        EnlargePicture enlargePicture=new EnlargePicture();
                        enlargePicture.EnlargePicture(My_Info_details_Activity.this,bit_1,true);
                    });
                }
                else{
                    PictureConnection pictureConnection=new PictureConnection();
                    pictureConnection.initDownloadConnection(String.valueOf(myInformation.getPicture1()), new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            setNullImage(binding.picture1);
                            BToast.showText(My_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            LocalPicture picture=new LocalPicture();
                            pictureConnection.parseJSONForDownloadPictureResponse(picture,result);
                            if(picture.getCode()==101){
                                byte[] in_1=Base64.getDecoder().decode(picture.getPicture());
                                setImage(binding.picture1,in_1);
                                picture.infoPictureAddToLocal(myInformation.getPicture1(),picture.getPicture());
                            }
                            else{
                                Looper.prepare();
                                setNullImage(binding.picture1);
                                BToast.showText(My_Info_details_Activity.this,picture.getResponse(),false);
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            switch(myInformation.getForm()){
                case 1:{
                    binding.fdForm.setText("咨询领域："+(
                            myInformation.getFd_form()==1?"哲学":
                                    myInformation.getFd_form()==2?"经济学":
                                            myInformation.getFd_form()==3?"法学":
                                                    myInformation.getFd_form()==4?"文学":
                                                            myInformation.getFd_form()==5?"历史学":
                                                                    myInformation.getFd_form()==6?"理学":
                                                                            myInformation.getFd_form()==7?"工学":
                                                                                    myInformation.getFd_form()==8?"农学":
                                                                                            myInformation.getFd_form()==9?"医学":
                                                                                                    myInformation.getFd_form()==10?"管理学":
                                                                                                            myInformation.getFd_form()==11?"教育学":"艺术学")
                    );
                    binding.fdForm.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(myInformation.getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(myInformation.getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 2:{
                    binding.helpForm.setText("求助问题："+(
                            myInformation.getHelp_form()==1?"代取物品":
                                    myInformation.getHelp_form()==2?"信息求问":
                                            myInformation.getHelp_form()==3?"寻物启事":
                                                    myInformation.getHelp_form()==4?"失物招领":"其他")
                    );
                    binding.helpForm.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(myInformation.getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(myInformation.getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 3:
                case 4:{
                    binding.price.setText("预期价格："+(String.valueOf(myInformation.getPrice()))+"元");
                    binding.price.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(myInformation.getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 5:{
                    binding.date.setText("活动日期："+myInformation.getDate());
                    binding.date.setVisibility(View.VISIBLE);
                    binding.place.setText("活动地点："+myInformation.getPlace());
                    binding.place.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(myInformation.getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(myInformation.getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 6:{
                    binding.lesson.setText("课程名称："+myInformation.getLesson());
                    binding.lesson.setVisibility(View.VISIBLE);
                    binding.score.setText("评分："+String.valueOf(myInformation.getScore())+"分");
                    binding.score.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.GONE);
                    break;
                }
                default:
            }
        });
    }
}