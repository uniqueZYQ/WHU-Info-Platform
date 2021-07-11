package com.example.whuinfoplatform.Adapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.whuinfoplatform.Activity.Personal_Message_Activity;
import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Entity.Picture;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPublishInfoPromoteBinding;
import com.example.whuinfoplatform.databinding.MsgItemBinding;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;
    private DB_USER dbHelper;
    SQLiteDatabase db;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView time_left;
        TextView time_right;
        ImageView picture_left;
        ImageView picture_right;
        ImageView left_upload;
        ImageView right_upload;
        public ViewHolder(View view){
            super(view);
            leftLayout = (LinearLayout)view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout)view.findViewById(R.id.right_layout);
            leftMsg = (TextView)view.findViewById(R.id.leftMsg);
            rightMsg = (TextView)view.findViewById(R.id.rightMsg);
            time_left=(TextView)view.findViewById(R.id.time_left);
            time_right=(TextView)view.findViewById(R.id.time_right);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        dbHelper = new DB_USER(parent.getContext(),"User.db",null,7);
        db = dbHelper.getWritableDatabase();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Msg msg = mMsgList.get(position);

        if(msg.getType()==Msg.TYPE_RECEIVED){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            holder.time_left.setText(msg.getTime());
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
            String test=msg.getContent();////////////////////////////////////////////////////////////////////////////////////////////
            holder.time_right.setText(msg.getTime());
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
