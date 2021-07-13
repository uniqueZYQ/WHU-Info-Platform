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
    Dialog mCameraDialog;
    int picture_id;

    private ImageView mImageView;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    // 第一个按下的手指的点
    private PointF startPoint = new PointF();
    // 两个按下的手指的触摸点的中点
    private PointF midPoint = new PointF();
    // 初始的两个手指按下的触摸点的距离
    private float oriDis = 1f;

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
        adapter_parent=parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        dbHelper = new DB_USER(parent.getContext(),"User.db",null,7);
        db = dbHelper.getWritableDatabase();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Msg msg = mMsgList.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Connector.getDatabase();
                picture_id=msg.getPicture();
                if(picture_id!=0){
                    setDialog();
                }
            }
        });

        if(msg.getType()==Msg.TYPE_RECEIVED){
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            //holder.time_left.setText(msg.getTime());
            String time_ex=msg.getTime();
            int currentYear=Integer.decode(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3)));
            int currentMonth=Integer.decode(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6)));
            int currentDay=Integer.decode(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9)));
            int year=getYear();
            int month=getMonth();
            int day=getDay();
            if(currentYear!=year){
                holder.time_left.setText(time_ex);
            }
            else if(currentMonth!=month){
                String new_time=new String();
                new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                        +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                holder.time_left.setText(new_time);
            }
            else if(currentDay!=day){
                String new_time=new String();
                new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                        +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                holder.time_left.setText(new_time);
            }
            else {
                String new_time=new String();
                new_time=String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                holder.time_left.setText(new_time);
            }
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
            String time_ex=msg.getTime();
            int currentYear=Integer.decode(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3)));
            int currentMonth=Integer.decode(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6)));
            int currentDay=Integer.decode(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9)));
            int year=getYear();
            int month=getMonth();
            int day=getDay();
            if(currentYear!=year){
                holder.time_right.setText(time_ex);
            }
            else if(currentMonth!=month){
                String new_time=new String();
                new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                        +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                holder.time_right.setText(new_time);
            }
            else if(currentDay!=day){
                String new_time=new String();
                new_time=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                        +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                holder.time_right.setText(new_time);
            }
            else {
                String new_time=new String();
                new_time=String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                        String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
                holder.time_right.setText(new_time);
            }
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

    private void setDialog() {
        LinearLayout root = (LinearLayout) LayoutInflater.from(adapter_parent.getContext()).inflate(R.layout.enlarge_picture, null);
        //初始化视图
        mCameraDialog = new Dialog(adapter_parent.getContext(), R.style.BottomDialog);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);

        Connector.getDatabase();
        List<Picture> picture=DataSupport.where("id=?",String.valueOf(picture_id)).find(Picture.class);
        byte[] in=picture.get(0).getPicture();
        Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
        ImageView image=(ImageView)root.findViewById(R.id.picture);
        image.setImageBitmap(bit);
        Bitmap bitmap_p;
        double p_width=bit.getWidth();
        double p_height=bit.getHeight();
        double width=1000;//标准宽
        double height=2100;//标准高
        LinearLayout.LayoutParams params;
        double ratio=p_width/p_height,st_ratio=width/height;
        if(ratio>st_ratio){
            height=width/ratio;
            params = new LinearLayout.LayoutParams((int)width,(int)(height)-1);
            bitmap_p = Bitmap.createScaledBitmap(bit,(int)width,(int)(height)-1,true);
            image.setImageBitmap(bitmap_p);
        }
        else{
            width=ratio*height;
            params = new LinearLayout.LayoutParams((int)(width)-1,(int)height);
            bitmap_p = Bitmap.createScaledBitmap(bit,(int)width-1,(int)(height),true);
           image.setImageBitmap(bitmap_p);
        }
        image.setLayoutParams(params);
        initTouch(root);
        mCameraDialog.show();
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return /*FloatMath.sqrt(x * x + y * y);*/(float) Math.sqrt(x*x+y*y);
    }

    // 计算两个触摸点的中点
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }

    private void initTouch(LinearLayout root){
        mImageView = (ImageView) root.findViewById(R.id.picture);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;

                // 进行与操作是为了判断多点触摸
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        // 第一个手指按下事件
                        matrix.set(view.getImageMatrix());
                        savedMatrix.set(matrix);
                        startPoint.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // 第二个手指按下事件
                        oriDis = distance(event);
                        if (oriDis > 10f) {
                            savedMatrix.set(matrix);
                            midPoint = middle(event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        // 手指放开事件
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 手指滑动事件
                        if (mode == DRAG) {
                            // 是一个手指拖动
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - startPoint.x, event.getY()
                                    - startPoint.y);
                        } else if (mode == ZOOM) {
                            // 两个手指滑动
                            float newDist = distance(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oriDis;
                                matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                            }
                        }
                        break;
                }

                // 设置ImageView的Matrix
                view.setImageMatrix(matrix);
                return true;
            }
        });
    }

    @Override
    public int getItemCount(){
        return mMsgList.size();
    }
}
