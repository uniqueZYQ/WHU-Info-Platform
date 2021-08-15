package com.example.whuinfoplatform.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whuinfoplatform.Dao.MsgConnection;
import com.example.whuinfoplatform.Dao.PictureConnection;
import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.AboutTime;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.LocalPicture;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import org.litepal.exceptions.DataSupportException;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;


public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{
    private List<Msg> mMsgList;
    private ViewGroup adapter_parent;

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
            right_layout_inner=view.findViewById(R.id.right_layout_inner);
            left_layout_inner=view.findViewById(R.id.left_layout_inner);
            leftLayout=view.findViewById(R.id.left_layout);
            rightLayout=view.findViewById(R.id.right_layout);
            timestamp=view.findViewById(R.id.timestamp);
            leftMsg=view.findViewById(R.id.leftMsg);
            rightMsg=view.findViewById(R.id.rightMsg);
            recalled=view.findViewById(R.id.recalled);
            picture_left=view.findViewById(R.id.picture_left);
            picture_right=view.findViewById(R.id.picture_right);
            left_upload=view.findViewById(R.id.left_upload);
            right_upload=view.findViewById(R.id.right_upload);
        }
    }

    public MsgAdapter(List<Msg> msgList){
        mMsgList=msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        adapter_parent=parent;
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);

        final ViewHolder holder=new ViewHolder(view);
        holder.right_layout_inner.setOnLongClickListener(v->{
            int position=holder.getAdapterPosition();
            Msg msg=mMsgList.get(position);
            if(msg.getType()==1){
                int id=msg.getId();
                int recalled=msg.getRecalled();
                if(recalled==0){
                    String send_time=msg.getTime();
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                    try{
                        Date current=simpleDateFormat.parse(send_time);
                        long time=current.getTime();
                        long timecurrentTimeMillis=System.currentTimeMillis();
                        if(timecurrentTimeMillis-time<=1000*2*60){
                            dialog(holder,id,position);
                        }
                        else
                            BToast.showText(adapter_parent.getContext(),"超过两分钟的消息无法撤回！",false);
                    }catch(ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        });
        return holder;
    }

    protected void dialog(ViewHolder holder,int id,int position){
        AlertDialog.Builder dialog=new AlertDialog.Builder(adapter_parent.getContext());
        dialog.setTitle("撤回消息");
        dialog.setMessage("确定撤回？该操作不可恢复！");
        dialog.setCancelable(true);
        dialog.setPositiveButton("是",(dialog1, which)->{
            MsgConnection msgConnection=new MsgConnection();
            msgConnection.recallMsgById(String.valueOf(id),new okhttp3.Callback(){
                @Override
                public void onFailure(Call call,IOException e){
                    Looper.prepare();
                    BToast.showText(adapter_parent.getContext(),"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call,Response response) throws IOException{
                    String result=response.body().string();
                    try{
                        JSONObject jsonObject=new JSONObject(result);
                        if(jsonObject.getInt("code")!=101){
                            Looper.prepare();
                            BToast.showText(adapter_parent.getContext(),jsonObject.getString("response"),false);
                            Looper.loop();
                        }
                        else{
                            mMsgList.get(position).setRecalled(1);
                            ((Activity)adapter_parent.getContext()).runOnUiThread(()->{
                                holder.rightLayout.setVisibility(View.GONE);
                                holder.recalled.setVisibility(View.VISIBLE);
                                holder.recalled.setText("你撤回了一条消息");
                            });
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(adapter_parent.getContext(),"数据解析失败！",false);
                        Looper.loop();
                    }
                }
            });
        });
        dialog.setNegativeButton("不，我再想想",(dialog12, which)->{});
        dialog.show();
    };

    @RequiresApi(api=Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder,int position) throws DataSupportException{
        Msg msg=mMsgList.get(position);
        int recalled=msg.getRecalled();
        //当前项时间获取
        String time_ex=msg.getTime();
        int itemYear=Integer.valueOf(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3))).intValue();
        int itemMonth=Integer.valueOf(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))).intValue();
        int itemDay=Integer.valueOf(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))).intValue();
        int itemHour=Integer.valueOf(String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))).intValue();
        int itemMinute;
        if(!String.valueOf(time_ex.charAt(15)).equals("0"))
            itemMinute=Integer.valueOf(String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16))).intValue();
        else
            itemMinute=Integer.valueOf(String.valueOf(time_ex.charAt(16))).intValue();
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
            int last_itemYear=Integer.valueOf(String.valueOf(last_time_ex.charAt(0))+String.valueOf(last_time_ex.charAt(1))+String.valueOf(last_time_ex.charAt(2))+String.valueOf(last_time_ex.charAt(3))).intValue();
            int last_itemMonth=Integer.valueOf(String.valueOf(last_time_ex.charAt(5))+String.valueOf(last_time_ex.charAt(6))).intValue();
            int last_itemDay=Integer.valueOf(String.valueOf(last_time_ex.charAt(8))+String.valueOf(last_time_ex.charAt(9))).intValue();
            int last_itemHour=Integer.valueOf(String.valueOf(last_time_ex.charAt(12))+String.valueOf(last_time_ex.charAt(13))).intValue();
            int last_itemMinute;
            if(!String.valueOf(last_time_ex.charAt(15)).equals("0"))
                last_itemMinute=Integer.valueOf(String.valueOf(last_time_ex.charAt(15))+String.valueOf(last_time_ex.charAt(16))).intValue();
            else
                last_itemMinute=Integer.valueOf(String.valueOf(last_time_ex.charAt(16))).intValue();
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
                int id=msg.getSub_id();
                getUserPicture(id,"left",holder);
                int picture_id=msg.getPicture();
                if(picture_id!=0){
                    Connector.getDatabase();
                    holder.left_upload.setImageResource(R.drawable.downloading);
                    try{
                        List<LocalPicture> picture=DataSupport.where("chat_code=?", String.valueOf(picture_id)).find(LocalPicture.class);
                        if(picture.size()!=0){
                            byte[] in=Base64.getDecoder().decode(picture.get(0).getPicture());
                            Bitmap bit=BitmapFactory.decodeByteArray(in,0,in.length);
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
                                params=new LinearLayout.LayoutParams((int)width,(int)(height)-1);
                                bitmap_p=Bitmap.createScaledBitmap(bit,(int)width,(int)(height)-1,true);
                            }
                            else{
                                width=ratio*height;
                                params=new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                                bitmap_p=Bitmap.createScaledBitmap(bit,(int)width-1,(int)(height),true);
                            }
                            holder.left_upload.setImageBitmap(bitmap_p);
                            holder.left_upload.setOnClickListener(v->initEnlargePicture(bitmap_p));
                            holder.left_upload.setLayoutParams(params);
                        }
                        else{
                            downloadAndInsertLocal(holder.left_upload,picture_id);
                        }
                        holder.leftMsg.setVisibility(View.GONE);
                        holder.left_upload.setVisibility(View.VISIBLE);
                    }catch(Exception e){
                        e.printStackTrace();
                        holder.leftMsg.setVisibility(View.GONE);
                        holder.left_upload.setVisibility(View.VISIBLE);
                        BToast.showText(adapter_parent.getContext(),"图片过大，加载失败",false);
                    }
                }
                else{
                    holder.leftMsg.setVisibility(View.VISIBLE);
                    holder.left_upload.setVisibility(View.GONE);
                }
            }
        }
        else if(msg.getType()==Msg.TYPE_SENT){
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
                int id=msg.getSub_id();
                getUserPicture(id,"right",holder);
                int picture_id=msg.getPicture();
                if(picture_id!=0){
                    Connector.getDatabase();
                    holder.right_upload.setImageResource(R.drawable.downloading);
                    try{
                        List<LocalPicture> picture=DataSupport.where("chat_code=?",String.valueOf(picture_id)).find(LocalPicture.class);
                        if(picture.size()!=0){
                            byte[] in=Base64.getDecoder().decode(picture.get(0).getPicture());
                            Bitmap bit=BitmapFactory.decodeByteArray(in,0,in.length);
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
                                params=new LinearLayout.LayoutParams((int)width,(int)(height)-1);
                                bitmap_p=Bitmap.createScaledBitmap(bit,(int)width,(int)(height)-1,true);
                            }
                            else{
                                width=ratio*height;
                                params=new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                                bitmap_p=Bitmap.createScaledBitmap(bit,(int)width-1,(int)(height),true);
                            }
                            holder.right_upload.setImageBitmap(bitmap_p);
                            holder.right_upload.setOnClickListener(v->initEnlargePicture(bitmap_p));
                            holder.right_upload.setLayoutParams(params);
                        }
                        else{
                            downloadAndInsertLocal(holder.right_upload,picture_id);
                        }
                        holder.right_upload.setOnLongClickListener(v->{
                            int position1=holder.getAdapterPosition();
                            Msg msg1=mMsgList.get(position1);
                            if(msg1.getType()==1){
                                int id1=msg1.getId();
                                int recalled1=msg1.getRecalled();
                                if(recalled1== 0){
                                    String send_time=msg1.getTime();
                                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                                    try{
                                        Date current=simpleDateFormat.parse(send_time);
                                        long time=current.getTime();
                                        long timecurrentTimeMillis=System.currentTimeMillis();
                                        if (timecurrentTimeMillis-time<=1000*2*60){
                                            dialog(holder, id1, position1);
                                        }
                                        else
                                            BToast.showText(adapter_parent.getContext(),"超过两分钟的消息无法撤回！",false);
                                    }catch(ParseException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                            return true;
                        });
                        holder.rightMsg.setVisibility(View.GONE);
                        holder.right_upload.setVisibility(View.VISIBLE);
                    }catch(Exception e){
                        e.printStackTrace();
                        holder.rightMsg.setVisibility(View.GONE);
                        holder.right_upload.setVisibility(View.VISIBLE);
                        BToast.showText(adapter_parent.getContext(),"图片过大，加载失败",false);
                    }
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

    private void downloadAndInsertLocal(ImageView imageView,int picture_id){
        PictureConnection pictureConnection=new PictureConnection();
        pictureConnection.initDownloadConnection(String.valueOf(picture_id),new okhttp3.Callback(){
            @Override
            public void onFailure(Call call,IOException e){
                ((Activity)adapter_parent.getContext()).runOnUiThread(()->{
                    imageView.setImageResource(R.drawable.download_failed);
                    Looper.prepare();
                    BToast.showText(adapter_parent.getContext(),"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                });
            }

            @RequiresApi(api=Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call call,Response response) throws IOException{
                String result=response.body().string();
                try{
                    JSONObject jsonObject=new JSONObject(result);
                    if(jsonObject.getInt("code")!=101){
                        Looper.prepare();
                        BToast.showText(adapter_parent.getContext(),jsonObject.getString("response"),false);
                        Looper.loop();
                    }
                    else{
                        ((Activity)adapter_parent.getContext()).runOnUiThread(()->{
                            try{
                                byte[] in=Base64.getDecoder().decode(jsonObject.getString("picture"));
                                Bitmap bit=BitmapFactory.decodeByteArray(in,0,in.length);
                                imageView.setImageBitmap(bit);

                                Bitmap bitmap_p;
                                double p_width=bit.getWidth();
                                double p_height=bit.getHeight();
                                double width=800;//标准宽
                                double height=1200;//标准高
                                LinearLayout.LayoutParams params;
                                double ratio=p_width/p_height,st_ratio=width/height;
                                if(ratio>st_ratio){
                                    height=width/ratio;
                                    params=new LinearLayout.LayoutParams((int)width,(int)(height)-1);
                                    bitmap_p=Bitmap.createScaledBitmap(bit,(int)width,(int)(height)-1,true);
                                }
                                else{
                                    width=ratio*height;
                                    params=new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                                    bitmap_p=Bitmap.createScaledBitmap(bit,(int)width-1,(int)(height),true);
                                }
                                imageView.setImageBitmap(bitmap_p);
                                imageView.setOnClickListener(v->initEnlargePicture(bitmap_p));
                                imageView.setLayoutParams(params);
                                LocalPicture localPicture=new LocalPicture();
                                localPicture.chatPictureAddToLocal(picture_id,jsonObject.getString("picture"));
                            }catch(JSONException e){
                                e.printStackTrace();
                            }
                        });
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                    Looper.prepare();
                    BToast.showText(adapter_parent.getContext(),"数据解析失败！",false);
                    Looper.loop();
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return mMsgList.size();
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    private void getUserPicture(int id,String type,ViewHolder holder){
        List<LocalPicture> localPictures=DataSupport.where("user_code=?",String.valueOf(id)).find(LocalPicture.class);
        if(localPictures.size()!=0){
            byte[] in=Base64.getDecoder().decode(localPictures.get(0).getPicture());
            Bitmap bit=BitmapFactory.decodeByteArray(in,0,in.length);
            if(type.equals("left")){
                holder.picture_left.setImageBitmap(bit);
            }
            else{
                holder.picture_right.setImageBitmap(bit);
            }
        }
        else{
            UserConnection userConnection=new UserConnection();
            userConnection.queryUserInfo(String.valueOf(id),new okhttp3.Callback(){
                @Override
                public void onFailure(Call call,IOException e){
                    ((Activity)adapter_parent.getContext()).runOnUiThread(()->{
                        if(type.equals("left")){
                            holder.picture_left.setImageResource(R.drawable.download_failed);
                        }
                        else{
                            holder.picture_right.setImageResource(R.drawable.download_failed);
                        }
                        Looper.prepare();
                        BToast.showText(adapter_parent.getContext(),"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    });
                }

                @Override
                public void onResponse(Call call,Response response) throws IOException{
                    String result=response.body().string();
                    try{
                        JSONObject jsonObject=new JSONObject(result);
                        if(jsonObject.getInt("code")!=101){
                            Looper.prepare();
                            BToast.showText(adapter_parent.getContext(),jsonObject.getString("response"),false);
                            Looper.loop();
                        }
                        else{
                            ((Activity)adapter_parent.getContext()).runOnUiThread(()->{
                                try{
                                    byte[] in=Base64.getDecoder().decode(jsonObject.getString("picture"));
                                    Bitmap bit=BitmapFactory.decodeByteArray(in,0,in.length);
                                    if(type.equals("left")){
                                        holder.picture_left.setImageBitmap(bit);
                                    }
                                    else{
                                        holder.picture_right.setImageBitmap(bit);
                                    }
                                    LocalPicture localPicture=new LocalPicture();
                                    localPicture.userPictureAddToLocal(id,jsonObject.getString("picture"),jsonObject.getInt("picture_version"));
                                }catch(JSONException e){
                                    e.printStackTrace();
                                    Looper.prepare();
                                    BToast.showText(adapter_parent.getContext(),"数据解析失败！",false);
                                    Looper.loop();
                                }
                            });
                        }
                    }catch(JSONException e){
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(adapter_parent.getContext(),"数据解析失败！",false);
                        Looper.loop();
                    }
                }
            });
        }
    }
}
