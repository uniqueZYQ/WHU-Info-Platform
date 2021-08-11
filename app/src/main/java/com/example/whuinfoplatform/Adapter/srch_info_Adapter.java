package com.example.whuinfoplatform.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.AboutTime;
import com.example.whuinfoplatform.Entity.BToast;
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
        String new_detail=srchinfo.getDetail().replace("\n"," ");
        if(new_detail.length()>55){
            detail.setText(new_detail.substring(0,55)+"...");
        }
        else
            detail.setText(new_detail);
        String nickname=srchinfo.getOwner();
        if(srchinfo.getSelf()==1){
            owner.setTextColor(0xFF777777);
            owner.setText(nickname+"(我)");
        }
        else{
            owner.setTextColor(0xFF000000);
            owner.setText(nickname);
        }

        List<LocalPicture> localPictures= DataSupport.where("user_code=?",String.valueOf(srchinfo.getOwner_id())).find(LocalPicture.class);
        if(localPictures.size()!=0){
            byte[] in = Base64.getDecoder().decode(localPictures.get(0).getPicture());
            Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
            picture.setImageBitmap(bit);
        }
        else{
            UserConnection userConnection=new UserConnection();
            userConnection.queryUserInfo(String.valueOf(srchinfo.getOwner_id()), new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    ((Activity)parent.getContext()).runOnUiThread(() -> {
                        picture.setImageResource(R.drawable.download_failed);
                        Looper.prepare();
                        BToast.showText(parent.getContext(),"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    try {
                        JSONObject jsonObject=new JSONObject(result);
                        if(jsonObject.getInt("code")!=101){
                            Looper.prepare();
                            BToast.showText(parent.getContext(),jsonObject.getString("response"),false);
                            Looper.loop();
                        }
                        else{
                            ((Activity)parent.getContext()).runOnUiThread(() -> {
                                try {
                                    byte[] in = Base64.getDecoder().decode(jsonObject.getString("picture"));
                                    Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                                    picture.setImageBitmap(bit);
                                    LocalPicture localPicture=new LocalPicture();
                                    localPicture.userPictureAddToLocal(srchinfo.getOwner_id(),jsonObject.getString("picture"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Looper.prepare();
                                    BToast.showText(parent.getContext(),"数据解析失败！",false);
                                    Looper.loop();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(parent.getContext(),"数据解析失败！",false);
                        Looper.loop();
                    }
                }
            });
        }
        return view;
    }
}
