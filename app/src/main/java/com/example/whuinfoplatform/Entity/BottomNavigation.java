package com.example.whuinfoplatform.Entity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.whuinfoplatform.Activity.Info_Hall_Activity;
import com.example.whuinfoplatform.Activity.Message_Center_Activity;
import com.example.whuinfoplatform.Activity.Personal_Center_Activity;
import com.example.whuinfoplatform.Activity.Personal_Message_Activity;
import com.example.whuinfoplatform.Activity.Publish_Info_promote_Activity;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.NavigationbarBinding;

import org.litepal.crud.DataSupport;

import java.util.List;

public class BottomNavigation{

    private int getId(){
        List<LocalLogin> localLoginList=DataSupport.order("time desc").select("user_id").find(LocalLogin.class);
        if(localLoginList.size()!=0){
            return localLoginList.get(0).getUser_id();
        }
        else{
            return 0;
        }
    }

    public void init(NavigationbarBinding binding, Context context, int type){
        binding.navigationMC.setOnClickListener(v->{
            Intent intent=new Intent(context,Message_Center_Activity.class);
            intent.putExtra("id",getId());
            context.startActivity(intent);
            ((Activity)context).finish();
        });
        binding.navigationIH.setOnClickListener(v->{
            Intent intent=new Intent(context,Info_Hall_Activity.class);
            intent.putExtra("id",getId());
            context.startActivity(intent);
            ((Activity)context).finish();
        });
        binding.navigationPublishInfo.setOnClickListener(v->{
            Intent intent=new Intent(context,Publish_Info_promote_Activity.class);
            intent.putExtra("id",getId());
            context.startActivity(intent);
        });
        binding.navigationPC.setOnClickListener(v->{
            Intent intent=new Intent(context,Personal_Center_Activity.class);
            intent.putExtra("id",getId());
            context.startActivity(intent);
            ((Activity)context).finish();
        });
        binding.navigationPM.setOnClickListener(v->{
            Intent intent=new Intent(context, Personal_Message_Activity.class);
            intent.putExtra("id",getId());
            context.startActivity(intent);
            ((Activity)context).finish();
        });
        if(type==1){
            binding.navigationMC.setBackgroundResource(R.drawable.message_center);
            binding.navigationMC.setClickable(false);
            binding.messageCenter.setTextColor(0xff000000);
        }
        else if(type==2){
            binding.navigationIH.setBackgroundResource(R.drawable.info_hall);
            binding.navigationIH.setClickable(false);
            binding.infoHall.setTextColor(0xff000000);
        }
        else if(type==3){
            binding.navigationPC.setBackgroundResource(R.drawable.personal_center);
            binding.navigationPC.setClickable(false);
            binding.personalCenter.setTextColor(0xff000000);
        }
        else if(type==4){
            binding.navigationPM.setBackgroundResource(R.drawable.personal_message);
            binding.navigationPM.setClickable(false);
            binding.personalMessage.setTextColor(0xff000000);
        }
    }
}
