package com.example.whuinfoplatform.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.AboutTime;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Entity.Picture;
import com.example.whuinfoplatform.R;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;
    private DB_USER dbHelper;
    SQLiteDatabase db;
    ViewGroup adapter_parent;
    int picture_id;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView timestamp;
        TextView leftMsg;
        TextView rightMsg;
        ImageView picture_left;
        ImageView picture_right;
        ImageView left_upload;
        ImageView right_upload;

        public ViewHolder(View view){
            super(view);
            leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            timestamp = (TextView)view.findViewById(R.id.timestamp);
            leftMsg = (TextView)view.findViewById(R.id.leftMsg);
            rightMsg = (TextView)view.findViewById(R.id.rightMsg);
            picture_left=(ImageView)view.findViewById(R.id.picture_left);
            picture_right=(ImageView)view.findViewById(R.id.picture_right);
            left_upload=(ImageView)view.findViewById(R.id.left_upload);
            right_upload=(ImageView)view.findViewById(R.id.right_upload);
        }
    }

    public MsgAdapter(List<Msg> msgList){
        mMsgList=msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        adapter_parent=parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        dbHelper = new DB_USER(parent.getContext(),"User.db",null,7);
        db = dbHelper.getWritableDatabase();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Msg msg = mMsgList.get(position);
        //当前项时间获取
        String time_ex=msg.getTime();
        int itemYear=Integer.decode(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3)));
        int itemMonth=Integer.decode(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6)));
        int itemDay=Integer.decode(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9)));
        int itemHour=Integer.decode(String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13)));
        int itemMinute;
        if(!String.valueOf(time_ex.charAt(15)).equals("0"))
            itemMinute=Integer.decode(String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16)));
        else
            itemMinute=Integer.decode(String.valueOf(time_ex.charAt(16)));
        //现在时间获取
        AboutTime aboutTime=new AboutTime();
        /*int currentYear=aboutTime.getYear();
        int currentMonth=aboutTime.getMonth();
        int currentDay=aboutTime.getDay();
        int currentHour=aboutTime.getHour();
        int currentMinute=aboutTime.getMinute();*/

        String timestamp;
        timestamp=aboutTime.judgeTimeOnScreen(time_ex);

        if(position==0){
            holder.timestamp.setVisibility(View.VISIBLE);
            holder.timestamp.setText(timestamp);
        }
        else{
            Msg last_msg=mMsgList.get(position-1);
            String last_time_ex=last_msg.getTime();
            //上一项时间获取
            int last_itemYear=Integer.decode(String.valueOf(last_time_ex.charAt(0))+String.valueOf(last_time_ex.charAt(1))+String.valueOf(last_time_ex.charAt(2))+String.valueOf(last_time_ex.charAt(3)));
            int last_itemMonth=Integer.decode(String.valueOf(last_time_ex.charAt(5))+String.valueOf(last_time_ex.charAt(6)));
            int last_itemDay=Integer.decode(String.valueOf(last_time_ex.charAt(8))+String.valueOf(last_time_ex.charAt(9)));
            int last_itemHour=Integer.decode(String.valueOf(last_time_ex.charAt(12))+String.valueOf(last_time_ex.charAt(13)));
            int last_itemMinute;
            if(!String.valueOf(last_time_ex.charAt(15)).equals("0"))
                last_itemMinute=Integer.decode(String.valueOf(last_time_ex.charAt(15))+String.valueOf(last_time_ex.charAt(16)));
            else
                last_itemMinute=Integer.decode(String.valueOf(last_time_ex.charAt(16)));
            if(last_itemYear!=itemYear){
                holder.timestamp.setVisibility(View.VISIBLE);
                holder.timestamp.setText(timestamp);
            }
            else if(last_itemMonth!=itemMonth){
                holder.timestamp.setVisibility(View.VISIBLE);
                holder.timestamp.setText(timestamp);
            }
            else if(last_itemDay!=itemDay){
                holder.timestamp.setVisibility(View.VISIBLE);
                holder.timestamp.setText(timestamp);
            }
            else if(last_itemHour!=itemHour){
                holder.timestamp.setVisibility(View.VISIBLE);
                holder.timestamp.setText(timestamp);
            }
            else if(itemMinute-last_itemMinute>=5){
                holder.timestamp.setVisibility(View.VISIBLE);
                holder.timestamp.setText(timestamp);
            }
            else
                holder.timestamp.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Connector.getDatabase();
                picture_id=msg.getPicture();
                if(picture_id!=0){
                    List<Picture> picture=DataSupport.where("id=?",String.valueOf(picture_id)).find(Picture.class);
                    byte[] in=picture.get(0).getPicture();
                    Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(adapter_parent.getContext(),bit,true);
                }
            }
        });

        if(msg.getType()==Msg.TYPE_RECEIVED){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            //holder.time_left.setText(msg.getTime());
            int id=msg.getSub_id();
            Cursor cursor = db.rawQuery("select picture from User where id=?", new String[]{Integer.toString(id)}, null);
            if(cursor.moveToFirst()){
                if (cursor.getCount() != 0) {
                    byte[] in = cursor.getBlob(cursor.getColumnIndex("picture"));
                    Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                    holder.picture_left.setImageBitmap(bit);
                }
            }
            cursor.close();
            int picture_id=msg.getPicture();
            if(picture_id!=0){
                Connector.getDatabase();
                List<Picture> picture = DataSupport.where("id=?",String.valueOf(picture_id)).find(Picture.class);
                for(int i=0;i<picture.size();i++) {
                    byte[] in = picture.get(i).getPicture();
                    Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                    holder.left_upload.setImageBitmap(bit);

                    Bitmap bitmap_p;
                    double p_width=bit.getWidth();
                    double p_height=bit.getHeight();
                    double width=800;//标准宽
                    double height=1200;//标准高
                    LinearLayout.LayoutParams params;
                    double ratio=p_width/p_height,st_ratio=width/height;
                    if(ratio>st_ratio){
                        height=width/ratio;
                        params = new LinearLayout.LayoutParams((int)width,(int)(height)-1);
                        bitmap_p = Bitmap.createScaledBitmap(bit,(int)width,(int)(height)-1,true);
                        holder.left_upload.setImageBitmap(bitmap_p);
                    }
                    else{
                        width=ratio*height;
                        params = new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                        bitmap_p = Bitmap.createScaledBitmap(bit,(int)width-1,(int)(height),true);
                        holder.left_upload.setImageBitmap(bitmap_p);
                    }
                    holder.left_upload.setLayoutParams(params);
                }
                holder.leftMsg.setVisibility(View.GONE);
                holder.left_upload.setVisibility(View.VISIBLE);
            }
            else{
                holder.leftMsg.setVisibility(View.VISIBLE);
                holder.left_upload.setVisibility(View.GONE);
            }
        }
        else if (msg.getType()==Msg.TYPE_SENT){
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
            //holder.time_right.setText(msg.getTime());
            int id=msg.getSub_id();
            Cursor cursor = db.rawQuery("select picture from User where id=?", new String[]{Integer.toString(id)}, null);
            if(cursor.moveToFirst()){
                if (cursor.getCount() != 0) {
                    byte[] in = cursor.getBlob(cursor.getColumnIndex("picture"));
                    Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                    holder.picture_right.setImageBitmap(bit);
                }
            }
            cursor.close();
            int picture_id=msg.getPicture();
            if(picture_id!=0){
                Connector.getDatabase();
                List<Picture> picture = DataSupport.where("id=?",String.valueOf(picture_id)).find(Picture.class);
                for(int i=0;i<picture.size();i++) {
                    byte[] in = picture.get(i).getPicture();
                    Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                    holder.right_upload.setImageBitmap(bit);

                    Bitmap bitmap_p;
                    double p_width=bit.getWidth();
                    double p_height=bit.getHeight();
                    double width=800;//标准宽
                    double height=1200;//标准高
                    LinearLayout.LayoutParams params;
                    double ratio=p_width/p_height,st_ratio=width/height;
                    if(ratio>st_ratio){
                        height=width/ratio;
                        params = new LinearLayout.LayoutParams((int)width,(int)(height)-1);
                        bitmap_p = Bitmap.createScaledBitmap(bit,(int)width,(int)(height)-1,true);
                        holder.right_upload.setImageBitmap(bitmap_p);
                    }
                    else{
                        width=ratio*height;
                        params = new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                        bitmap_p = Bitmap.createScaledBitmap(bit,(int)width-1,(int)(height),true);
                        holder.right_upload.setImageBitmap(bitmap_p);
                    }
                    holder.right_upload.setLayoutParams(params);
                }
                holder.rightMsg.setVisibility(View.GONE);
                holder.right_upload.setVisibility(View.VISIBLE);
            }
            else{
                holder.rightMsg.setVisibility(View.VISIBLE);
                holder.right_upload.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public int getItemCount(){
        return mMsgList.size();
    }
}
