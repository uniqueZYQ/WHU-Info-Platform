package com.example.whuinfoplatform.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.whuinfoplatform.Entity.AboutTime;
import com.example.whuinfoplatform.Entity.srch_info;
import com.example.whuinfoplatform.R;

import java.util.Base64;
import java.util.List;

public class srch_info_Adapter extends ArrayAdapter<srch_info> {
    private int resourceId;

    public srch_info_Adapter(Context context, int textViewResourceId, List<srch_info> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        srch_info srchinfo = getItem(position);

        View view;
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        }
        else
            view=convertView;
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView form = (TextView) view.findViewById(R.id.form);
        TextView detail = (TextView) view.findViewById(R.id.detail);
        TextView owner = (TextView) view.findViewById(R.id.owner);
        ImageView picture = (ImageView) view.findViewById(R.id.picture);

        String time_ex=srchinfo.getTime();
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
        form.setText(srchinfo.getForm());
        if(srchinfo.getDetail().length()>55){
            detail.setText(srchinfo.getDetail().substring(0,55)+"...");
        }
        else
            detail.setText(srchinfo.getDetail());
        String nickname=srchinfo.getOwner();
        if(srchinfo.getSelf()==1){
            owner.setTextColor(0xFF777777);
            owner.setText(nickname+"(我)");
        }
        else{
            owner.setTextColor(0xFF000000);
            owner.setText(nickname);
        }

        byte[] in = Base64.getDecoder().decode(srchinfo.getPicture());
        Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
        picture.setImageBitmap(bit);
        return view;
    }
}
