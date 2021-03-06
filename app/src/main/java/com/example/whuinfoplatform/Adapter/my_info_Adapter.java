package com.example.whuinfoplatform.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.whuinfoplatform.Entity.AboutTime;
import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.R;

import java.util.List;

public class my_info_Adapter extends ArrayAdapter<my_info>{
    private int resourceId;

    public my_info_Adapter(Context context, int textViewResourceId,List<my_info> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        my_info myinfo=getItem(position);
        View view;
        if(convertView==null){
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        }
        else
            view=convertView;
        TextView time=view.findViewById(R.id.time);
        TextView form=view.findViewById(R.id.form);
        TextView detail=view.findViewById(R.id.detail);
        TextView answered=view.findViewById(R.id.answered);
        String time_ex=myinfo.getTime();
        int currentYear=Integer.valueOf(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3))).intValue();
        int currentMonth=Integer.valueOf(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))).intValue();
        int currentDay=Integer.valueOf(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))).intValue();
        AboutTime aboutTime=new AboutTime();
        int year=aboutTime.getYear();
        int month=aboutTime.getMonth();
        int day=aboutTime.getDay();
        if(currentYear!=year){
            time.setText(time_ex);
        }
        else if(currentMonth!=month){
            String new_time;
            new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                    +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            time.setText(new_time);
        }
        else if(day-currentDay==1){
            String new_time;
            new_time="?????? "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            time.setText(new_time);
        }
        else if(currentDay!=day){
            String new_time;
            new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                    +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            time.setText(new_time);
        }
        else {
            String new_time;
            new_time="?????? "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            time.setText(new_time);
        }
        form.setText(myinfo.getForm());
        String new_detail=myinfo.getDetail().replace("\n"," ");
        if(new_detail.length()>57){
            detail.setText(new_detail.substring(0,56)+"...");
        }
        else
            detail.setText(new_detail);
        answered.setText(myinfo.getAnswered());
        return view;
    }
}

