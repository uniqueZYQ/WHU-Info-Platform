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

import java.util.List;


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
            last_time.setText(mymsg.getLastTime());
            last_detail.setText(mymsg.getLastDetail());
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
    }
