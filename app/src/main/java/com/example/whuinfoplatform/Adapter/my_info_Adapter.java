package com.example.whuinfoplatform.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class my_info_Adapter extends ArrayAdapter<my_info> {
    private int resourceId;

    public my_info_Adapter(Context context, int textViewResourceId, List<my_info> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        my_info myinfo = getItem(position);
        //View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        View view;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        }
        else
            view=convertView;
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView form = (TextView) view.findViewById(R.id.form);
        TextView detail = (TextView) view.findViewById(R.id.detail);
        TextView answered = (TextView) view.findViewById(R.id.answered);
        //time.setText(myinfo.getTime());
        String time_ex=myinfo.getTime();
        int currentYear=Integer.decode(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3)));
        int currentMonth=Integer.decode(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6)));
        int currentDay=Integer.decode(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9)));
        int year=getYear();
        int month=getMonth();
        int day=getDay();
        if(currentYear!=year){
            time.setText(time_ex);
        }
        else if(currentMonth!=month){
            String new_time=new String();
            new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                    +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            time.setText(new_time);
        }
        else if(day-currentDay==1){
            String new_time=new String();
            new_time="昨天 "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            time.setText(new_time);
        }
        else if(currentDay!=day){
            String new_time=new String();
            new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                    +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            time.setText(new_time);
        }
        else {
            String new_time=new String();
            new_time="今天 "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            time.setText(new_time);
        }
        form.setText(myinfo.getForm());
        detail.setText(myinfo.getDetail());
        answered.setText(myinfo.getAnswered());
        return view;
    }
    private int getYear(){
        long timecurrentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        String time = sdfTwo.format(timecurrentTimeMillis);
        int year=Integer.decode(String.valueOf(time.charAt(0))+String.valueOf(time.charAt(1))+String.valueOf(time.charAt(2))+String.valueOf(time.charAt(3)));
        return year;
    }

    private int getMonth(){
        long timecurrentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        String time = sdfTwo.format(timecurrentTimeMillis);
        int month=Integer.decode(String.valueOf(time.charAt(5))+String.valueOf(time.charAt(6)));
        return month;
    }

    private int getDay(){
        long timecurrentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        String time = sdfTwo.format(timecurrentTimeMillis);
        int day=Integer.decode(String.valueOf(time.charAt(8))+String.valueOf(time.charAt(9)));
        return day;
    }
}

