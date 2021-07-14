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

import com.example.whuinfoplatform.Activity.Personal_Message_Activity;
import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.my_info;
import com.example.whuinfoplatform.Entity.my_msg;
import com.example.whuinfoplatform.R;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class my_msg_Adapter extends ArrayAdapter<my_msg> {
        private int resourceId;
        private DB_USER dbHelper;
        public my_msg_Adapter(Context context, int textViewResourceId, List<my_msg> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            my_msg mymsg = getItem(position);
            //View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            View view;
            if(convertView==null){
                view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            }
            else
                view=convertView;
            TextView last_time = (TextView) view.findViewById(R.id.last_time);
            TextView last_detail = (TextView) view.findViewById(R.id.last_detail);
            TextView oppo_name = (TextView) view.findViewById(R.id.oppo_name);
            TextView last = (TextView) view.findViewById(R.id.last);
            ImageView picture = (ImageView) view.findViewById(R.id.picture);
            String time_ex=mymsg.getLastTime();
            int currentYear=Integer.decode(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3)));
            int currentMonth=Integer.decode(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6)));
            int currentDay=Integer.decode(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9)));
            int year=getYear();
            int month=getMonth();
            int day=getDay();
            if(currentYear!=year){
                last_time.setText(time_ex);
            }
            else if(currentMonth!=month){
                String new_time=new String();
                new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                        +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                last_time.setText(new_time);
            }
            else if(currentDay!=day){
                String new_time=new String();
                new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                        +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                last_time.setText(new_time);
            }
            else {
                String new_time=new String();
                new_time="今天 "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                last_time.setText(new_time);
            }
            String detail=mymsg.getLastDetail();
            if(detail.equals(""))
                last_detail.setText("[图片]");
            else
                last_detail.setText(detail);
            oppo_name.setText(mymsg.getOppoName());
            dbHelper = new DB_USER(parent.getContext(), "User.db", null, 7);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int id=mymsg.getOppo_id();
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
            if(!mymsg.getLast().equals("")){
                last.setText(mymsg.getLast());
                last.setVisibility(View.VISIBLE);
            }
            else last.setVisibility(View.GONE);
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

