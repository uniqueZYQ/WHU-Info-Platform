package com.example.whuinfoplatform.Adapter;

import android.app.Activity;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.whuinfoplatform.Activity.Srch_Info_details_Activity;
import com.example.whuinfoplatform.Dao.PictureConnection;
import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Dao.InfoConnection;
import com.example.whuinfoplatform.Entity.AboutTime;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.LocalPicture;
import com.example.whuinfoplatform.Entity.srch_info;
import com.example.whuinfoplatform.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class srch_info_Adapter extends RecyclerView.Adapter<srch_info_Adapter.ViewHolder>{
    private List<srch_info> srch_info_list;
    private ViewGroup adapter_parent;
    private int locid;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView time,form,owner,detail;
        ImageView picture;
        View srch_info_view;
        LinearLayout srch_info_item;
        ImageView first_picture;
        TextView views;

        public ViewHolder(View view){
            super(view);
            srch_info_view=view;
            time=view.findViewById(R.id.time);
            form=view.findViewById(R.id.form);
            detail=view.findViewById(R.id.detail);
            owner=view.findViewById(R.id.owner);
            picture=view.findViewById(R.id.picture);
            srch_info_item=view.findViewById(R.id.srch_info_item);
            first_picture=view.findViewById(R.id.first_picture);
            views=view.findViewById(R.id.views);
        }
    }

    public srch_info_Adapter(List<srch_info> srch_infoList,int locid){
        srch_info_list=srch_infoList;
        this.locid=locid;
    }

    public void clear(){
        srch_info_list.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int ViewType){
        adapter_parent=parent;
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.srch_info_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);

        holder.srch_info_view.setOnClickListener(v->{
            int position=holder.getAdapterPosition();
            srch_info srchinfo=srch_info_list.get(position);
            int infoid=srchinfo.getId();
            InfoConnection infoConnection=new InfoConnection();
            infoConnection.queryInfoByIdAndAddViewsConnection(String.valueOf(infoid),new okhttp3.Callback(){
                @Override
                public void onFailure(Call call,IOException e){
                    Looper.prepare();
                    BToast.showText(adapter_parent.getContext(),"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call,Response response)throws IOException{
                    String result=response.body().string();
                    JSONObject jsonObject;
                    try{
                        jsonObject=new JSONObject(result);
                        int ownerid=jsonObject.getInt("owner_id");
                        int cuself=srchinfo.getSelf();
                        String cuowner=srchinfo.getOwner();
                        Intent intent=new Intent(adapter_parent.getContext(),Srch_Info_details_Activity.class);
                        intent.putExtra("id",infoid);
                        intent.putExtra("locid",locid);
                        intent.putExtra("owner",cuowner);
                        intent.putExtra("self",cuself);
                        intent.putExtra("ownerid",ownerid);
                        adapter_parent.getContext().startActivity(intent);
                    }catch(JSONException e){
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(adapter_parent.getContext(), "数据解析失败！", false);
                        Looper.loop();
                    }
                }
            });
        });
        return holder;
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        srch_info srchinfo=srch_info_list.get(position);
        holder.views.setText(String.valueOf(srchinfo.getViews()));
        String time_ex=srchinfo.getTime();
        int currentYear=Integer.valueOf(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3))).intValue();
        int currentMonth=Integer.valueOf(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))).intValue();
        int currentDay=Integer.valueOf(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))).intValue();
        AboutTime aboutTime=new AboutTime();
        int year=aboutTime.getYear();
        int month=aboutTime.getMonth();
        int day=aboutTime.getDay();
        if(currentYear!=year){
            holder.time.setText(time_ex);
        }
        else if(currentMonth!=month){
            String new_time;
            new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                    +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            holder.time.setText(new_time);
        }
        else if(day-currentDay==1){
            String new_time;
            new_time="昨天 "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            holder.time.setText(new_time);
        }
        else if(currentDay!=day){
            String new_time;
            new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                    +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            holder.time.setText(new_time);
        }
        else {
            String new_time;
            new_time="今天 "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
            holder.time.setText(new_time);
        }
        holder.form.setText(srchinfo.getForm());
        String new_detail=srchinfo.getDetail().replace("\n"," ");
        if(new_detail.length()>55){
            holder.detail.setText(new_detail.substring(0,55)+"...");
        }
        else
            holder.detail.setText(new_detail);
        String nickname=srchinfo.getOwner();
        if(srchinfo.getSelf()==1){
            holder.owner.setTextColor(0xFF777777);
            holder.owner.setText(nickname+"(我)");
        }
        else{
            holder.owner.setTextColor(0xFF000000);
            holder.owner.setText(nickname);
        }
        if(srchinfo.getFirst_picture()!=0){
            EnlargePicture enlargePicture=new EnlargePicture();
            holder.first_picture.setImageResource(R.drawable.downloading);
            List<LocalPicture> localPictures=DataSupport.where("code=?",String.valueOf(srchinfo.getFirst_picture())).find(LocalPicture.class);
            if(localPictures.size()!=0){
                byte[] in=Base64.getDecoder().decode(localPictures.get(0).getPicture());
                Bitmap bit=BitmapFactory.decodeByteArray(in, 0, in.length);
                enlargePicture.adjustPictureInSearchInfo(bit,holder.first_picture);
            }
            else{
                PictureConnection pictureConnection=new PictureConnection();
                pictureConnection.initDownloadConnection(String.valueOf(srchinfo.getFirst_picture()),new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call,IOException e){
                        ((Activity)adapter_parent.getContext()).runOnUiThread(()->{
                            holder.first_picture.setImageResource(R.drawable.download_failed);
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
                                        enlargePicture.adjustPictureInSearchInfo(bit,holder.first_picture);
                                        LocalPicture localPicture=new LocalPicture();
                                        localPicture.infoPictureAddToLocal(srchinfo.getFirst_picture(),jsonObject.getString("picture"));
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
        else{
            holder.first_picture.setVisibility(View.GONE);
        }

        List<LocalPicture> localPictures=DataSupport.where("user_code=?",String.valueOf(srchinfo.getOwner_id())).find(LocalPicture.class);
        if(localPictures.size()!=0){
            byte[] in=Base64.getDecoder().decode(localPictures.get(0).getPicture());
            Bitmap bit=BitmapFactory.decodeByteArray(in, 0, in.length);
            holder.picture.setImageBitmap(bit);
        }
        else{
            UserConnection userConnection=new UserConnection();
            userConnection.queryUserInfo(String.valueOf(srchinfo.getOwner_id()), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call,IOException e){
                    ((Activity)adapter_parent.getContext()).runOnUiThread(()->{
                        holder.picture.setImageResource(R.drawable.download_failed);
                        Looper.prepare();
                        BToast.showText(adapter_parent.getContext(),"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
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
                                    holder.picture.setImageBitmap(bit);
                                    LocalPicture localPicture=new LocalPicture();
                                    localPicture.userPictureAddToLocal(srchinfo.getOwner_id(),jsonObject.getString("picture"),jsonObject.getInt("picture_version"));
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

    @Override
    public int getItemCount() {
        return srch_info_list.size();
    }
}
