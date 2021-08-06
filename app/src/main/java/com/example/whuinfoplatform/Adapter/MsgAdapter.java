package com.example.whuinfoplatform.Adapter;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.AboutTime;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Entity.Picture;
import com.example.whuinfoplatform.R;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;
    private DB_USER dbHelper;
    SQLiteDatabase db;
    ViewGroup adapter_parent;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView timestamp;
        TextView leftMsg;
        TextView rightMsg;
        TextView recalled;
        ImageView picture_left;
        ImageView picture_right;
        ImageView left_upload;
        ImageView right_upload;
        LinearLayout right_layout_inner;
        LinearLayout left_layout_inner;
        View msgView;

        public ViewHolder(View view){
            super(view);
            msgView=view;
            right_layout_inner = (LinearLayout)view.findViewById(R.id.right_layout_inner);
            left_layout_inner = (LinearLayout)view.findViewById(R.id.left_layout_inner);
            leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            timestamp = (TextView)view.findViewById(R.id.timestamp);
            leftMsg = (TextView)view.findViewById(R.id.leftMsg);
            rightMsg = (TextView)view.findViewById(R.id.rightMsg);
            recalled = (TextView)view.findViewById(R.id.recalled);
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

        final ViewHolder holder=new ViewHolder(view);
        holder.right_layout_inner.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position=holder.getAdapterPosition();
                Msg msg=mMsgList.get(position);
                if(msg.getType()==1){
                    Connector.getDatabase();
                    int sss=msg.getSub_id();
                    int ooo=msg.getObj_id();
                    List<Msg> msg1=DataSupport.where("sub_id=? and obj_id=? or sub_id=? and obj_id=?",String.valueOf(ooo),String.valueOf(sss),String.valueOf(sss),String.valueOf(ooo)).order("id asc").find(Msg.class);
                    int id=msg1.get(position).getId();
                    int recalled=msg.getRecalled();
                    if(recalled==0){
                        String send_time=msg.getTime();
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                        try {
                            Date current=simpleDateFormat.parse(send_time);
                            long time=current.getTime();
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            if(timecurrentTimeMillis-time<=1000*2*60){
                                dialog(holder,id);
                            }
                            else
                                Toast.makeText(adapter_parent.getContext(),"超过两分钟的消息无法撤回!",Toast.LENGTH_SHORT).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });

        return holder;
    }

    protected void dialog(ViewHolder holder,int id){
        AlertDialog.Builder dialog = new AlertDialog.Builder(adapter_parent.getContext());
        dialog.setTitle("撤回消息");
        dialog.setMessage("确定撤回？该操作不可恢复！");
        dialog.setCancelable(true);
        dialog.setPositiveButton("是",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Msg cumsg=new Msg();
                cumsg.setRecalled(1);
                cumsg.updateAll("id=?",String.valueOf(id));
                holder.rightLayout.setVisibility(View.GONE);
                holder.recalled.setVisibility(View.VISIBLE);
                holder.recalled.setText("你撤回了一条消息");
            }
        });
        dialog.setNegativeButton("不，我再想想",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    };

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Msg msg = mMsgList.get(position);
        //重新进行撤回判断，否则刷新时撤回失效
        Connector.getDatabase();
        int sss=msg.getSub_id();
        int ooo=msg.getObj_id();
        List<Msg> msg1=DataSupport.where("sub_id=? and obj_id=? or sub_id=? and obj_id=?",String.valueOf(ooo),String.valueOf(sss),String.valueOf(sss),String.valueOf(ooo)).order("id asc").find(Msg.class);
        int recalled=msg1.get(position).getRecalled();

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

        if(msg.getType()==Msg.TYPE_RECEIVED){
            if(recalled==1){
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.GONE);
                holder.recalled.setVisibility(View.VISIBLE);
                holder.recalled.setText("对方撤回了一条消息");
            }
            else{
                holder.recalled.setVisibility(View.GONE);
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
                            holder.left_upload.setOnClickListener(v -> {
                                initEnlargePicture(bitmap_p);
                            });
                        }
                        else{
                            width=ratio*height;
                            params = new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                            bitmap_p = Bitmap.createScaledBitmap(bit,(int)width-1,(int)(height),true);
                            holder.left_upload.setImageBitmap(bitmap_p);
                            holder.left_upload.setOnClickListener(v -> {
                                initEnlargePicture(bitmap_p);
                            });
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
        }

        else if (msg.getType()==Msg.TYPE_SENT){
            if(recalled==1){
                holder.leftLayout.setVisibility(View.GONE);
                holder.rightLayout.setVisibility(View.GONE);
                holder.recalled.setVisibility(View.VISIBLE);
                holder.recalled.setText("你撤回了一条消息");
            }
            else{
                holder.recalled.setVisibility(View.GONE);
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
                            holder.right_upload.setOnClickListener(v -> {
                                initEnlargePicture(bitmap_p);
                            });
                        }
                        else{
                            width=ratio*height;
                            params = new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                            bitmap_p = Bitmap.createScaledBitmap(bit,(int)width-1,(int)(height),true);
                            holder.right_upload.setImageBitmap(bitmap_p);
                            holder.right_upload.setOnClickListener(v -> {
                                initEnlargePicture(bitmap_p);
                            });
                        }
                        holder.right_upload.setLayoutParams(params);
                    }
                    holder.right_upload.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            int position=holder.getAdapterPosition();
                            Msg msg=mMsgList.get(position);
                            if(msg.getType()==1){
                                Connector.getDatabase();
                                int sss=msg.getSub_id();
                                int ooo=msg.getObj_id();
                                List<Msg> msg1=DataSupport.where("sub_id=? and obj_id=? or sub_id=? and obj_id=?",String.valueOf(ooo),String.valueOf(sss),String.valueOf(sss),String.valueOf(ooo)).order("id asc").find(Msg.class);
                                int id=msg1.get(position).getId();
                                int recalled=msg.getRecalled();
                                if(recalled==0){
                                    String send_time=msg.getTime();
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                                    try {
                                        Date current=simpleDateFormat.parse(send_time);
                                        long time=current.getTime();
                                        long timecurrentTimeMillis = System.currentTimeMillis();
                                        if(timecurrentTimeMillis-time<=1000*2*60){
                                            dialog(holder,id);
                                        }
                                        else
                                            Toast.makeText(adapter_parent.getContext(),"超过两分钟的消息无法撤回!",Toast.LENGTH_SHORT).show();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            return true;
                        }
                    });
                    holder.rightMsg.setVisibility(View.GONE);
                    holder.right_upload.setVisibility(View.VISIBLE);
                }
                else{
                    holder.rightMsg.setVisibility(View.VISIBLE);
                    holder.right_upload.setVisibility(View.GONE);
                }
            }
        }

    }

    private void initEnlargePicture(Bitmap bit){
        EnlargePicture enlargePicture=new EnlargePicture();
        enlargePicture.EnlargePicture(adapter_parent.getContext(),bit,true);
    }

    @Override
    public int getItemCount(){
        return mMsgList.size();
    }
}
