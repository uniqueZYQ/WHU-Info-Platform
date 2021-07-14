package com.example.whuinfoplatform.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.AboutTime;
import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.Entity.srch_info;
import com.example.whuinfoplatform.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class srch_info_Adapter extends ArrayAdapter<srch_info> {
    private int resourceId;
    private DB_USER dbHelper;

    public srch_info_Adapter(Context context, int textViewResourceId, List<srch_info> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        srch_info srchinfo = getItem(position);
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
        TextView owner = (TextView) view.findViewById(R.id.owner);
        ImageView picture = (ImageView) view.findViewById(R.id.picture);
        //time.setText(srchinfo.getTime());
        String time_ex=srchinfo.getTime();
        int currentYear=Integer.decode(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3)));
        int currentMonth=Integer.decode(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6)));
        int currentDay=Integer.decode(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9)));
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
        detail.setText(srchinfo.getDetail());
        String nickname=srchinfo.getOwner();
        if(nickname.charAt(nickname.length()-3)=='('&&
                nickname.charAt(nickname.length()-2)=='我'&&
                nickname.charAt(nickname.length()-1)==')'){
            owner.setTextColor(0xFF777777);
        }
        else{
            owner.setTextColor(0xFF000000);
        }
        owner.setText(nickname);
        dbHelper = new DB_USER(parent.getContext(), "User.db", null, 7);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id=srchinfo.getOwner_id();
        Cursor cursor = db.rawQuery("select picture from User where id=?", new String[]{Integer.toString(id)}, null);
        if(cursor.moveToFirst()){
            if (cursor.getCount() != 0) {
                byte[] in = cursor.getBlob(cursor.getColumnIndex("picture"));
                Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                picture.setImageBitmap(bit);
            }
            else
                Toast.makeText(parent.getContext(),"头像信息读取失败",Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        return view;
    }
}
