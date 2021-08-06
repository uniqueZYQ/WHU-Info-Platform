package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.whuinfoplatform.Dao.PictureConnection;
import com.example.whuinfoplatform.Entity.LocalPicture;
import com.example.whuinfoplatform.Entity.WebResponse;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityUploadPicturePromoteBinding;

import org.litepal.tablemanager.Connector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

import okhttp3.Call;
import okhttp3.Response;

public class Upload_Picture_promote_Activity extends rootActivity {
    private static final int CENTER = Gravity.CENTER;
    private ActivityUploadPicturePromoteBinding binding;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private Uri imageUri;
    //以下为图片缩放移动使用
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
    int chat=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageView = (ImageView) this.findViewById(R.id.picture);
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
    public void bindView() {
        binding= ActivityUploadPicturePromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initData() {
        super.initData();
        Intent intent1 = getIntent();
        if(intent1.getIntExtra("chat",0)==0)
            binding.upload.setVisibility(View.VISIBLE);
        else{
            binding.chatUpload.setVisibility(View.VISIBLE);
            binding.cancel.setBackgroundResource(R.drawable.cancel);
            binding.cancel.setTextColor(0xFFFFFFFF);
        }
        if(intent1.getIntExtra("type",0)>1){
            if(intent1.getIntExtra("type",0)==2){
                picture=binding.picture;
                if(ContextCompat.checkSelfPermission(Upload_Picture_promote_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Upload_Picture_promote_Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else{
                    openAlbum();
                }
            }
            else if(intent1.getIntExtra("type",0)==3){
                picture=binding.picture;
                File outputImage=new File(getExternalCacheDir(),"output_image.jpg");
                try{
                    if(outputImage.exists()){
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(Build.VERSION.SDK_INT>=24){
                    imageUri= FileProvider.getUriForFile(Upload_Picture_promote_Activity.this,"com.example.whuinfoplatform.fileprovider",outputImage);
                }
                else{
                    imageUri=Uri.fromFile(outputImage);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        }
        binding.chatUpload.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // 设置按钮圆角率为30
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 30);
            }
        });
        binding.chatUpload.setClipToOutline(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void initClick() {
        Connector.getDatabase();
        Intent intent1=getIntent();
        chat=intent1.getIntExtra("chat",0);
        binding.cancel.setOnClickListener(v->{
            Intent intent;
            if(chat==0){
                intent=new Intent(Upload_Picture_promote_Activity.this,Publish_Info_promote_Activity.class);
            }
            else{
                intent=new Intent(Upload_Picture_promote_Activity.this,Chat_Window_Activity.class);
            }
            startActivity(intent);
        });
        binding.upload.setOnClickListener(v->{
            //com.example.whuinfoplatform.Entity.Picture picture=new Picture();
            LocalPicture localPicture=new LocalPicture();
            binding.picture.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(binding.picture.getDrawingCache());
            binding.picture.setDrawingCacheEnabled(false);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, os);
            byte[] in=os.toByteArray();
            String FileBuf = Base64.getEncoder().encodeToString(in);
            localPicture.setPicture(FileBuf);
            /*picture.setPicture(os.toByteArray());
            picture.save();
            int picture_id=picture.getId();
            //Toast.makeText(Upload_Picture_promote_Activity.this,"图片上传成功!",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Upload_Picture_promote_Activity.this,Publish_Info_promote_Activity.class);
            intent.putExtra("picture_id",picture_id);
            startActivity(intent);*/
            PictureConnection pictureConnection=new PictureConnection();
            pictureConnection.initUploadConnection(FileBuf, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(Upload_Picture_promote_Activity.this,"服务器连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    WebResponse webResponse=new WebResponse();
                    pictureConnection.parseJSONForPictureResponse(webResponse,result);
                    Looper.prepare();
                    Toast.makeText(Upload_Picture_promote_Activity.this,webResponse.getResponse(),Toast.LENGTH_SHORT).show();
                    if(webResponse.getCode()==101){
                        Intent intent = new Intent(Upload_Picture_promote_Activity.this, Publish_Info_promote_Activity.class);
                        intent.putExtra("picture_id",webResponse.getId());
                        startActivity(intent);
                    }
                    localPicture.setCode(webResponse.getId());
                    localPicture.save();
                    Looper.loop();
                }
            });
        });
        binding.chatUpload.setOnClickListener(v -> {
            //com.example.whuinfoplatform.Entity.Picture picture=new Picture();
            binding.picture.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(binding.picture.getDrawingCache());
            binding.picture.setDrawingCacheEnabled(false);
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, os);
            byte[] in=os.toByteArray();
            String FileBuf = Base64.getEncoder().encodeToString(in);
            /*picture.setPicture(os.toByteArray());
            picture.save();
            int picture_id=picture.getId();
            //Toast.makeText(Upload_Picture_promote_Activity.this,"图片上传成功!",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Upload_Picture_promote_Activity.this,Publish_Info_promote_Activity.class);
            intent.putExtra("picture_id",picture_id);
            startActivity(intent);*/
            PictureConnection pictureConnection=new PictureConnection();
            pictureConnection.initUploadConnection(FileBuf, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(Upload_Picture_promote_Activity.this,"服务器连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    WebResponse webResponse=new WebResponse();
                    pictureConnection.parseJSONForPictureResponse(webResponse,result);
                    Looper.prepare();
                    Toast.makeText(Upload_Picture_promote_Activity.this,webResponse.getResponse(),Toast.LENGTH_SHORT).show();
                    if(webResponse.getCode()==101){
                        Intent intent = new Intent(Upload_Picture_promote_Activity.this, Chat_Window_Activity.class);
                        intent.putExtra("picture_id",webResponse.getId());
                        startActivity(intent);
                    }
                    Looper.loop();
                }
            });
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
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

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);//打开相册
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch(requestCode){
            case TAKE_PHOTO:
                Intent intent;
                if (resultCode==RESULT_OK){
                    try{
                        //显示照片
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                            Bitmap bitmap_p;
                            double p_width=bitmap.getWidth();
                            double p_height=bitmap.getHeight();
                            double width=1000;//标准宽
                            double height=1600;//标准高
                            LinearLayout.LayoutParams params;
                            double ratio=p_width/p_height,st_ratio=width/height;
                            if(ratio>st_ratio){
                                height=width/ratio;
                                params = new LinearLayout.LayoutParams((int)width,(int)(height)-1);
                                bitmap_p = Bitmap.createScaledBitmap(bitmap,(int)width,(int)(height)-1,true);
                                picture.setImageBitmap(bitmap_p);
                            }
                            else{
                                width=ratio*height;
                                params = new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                                bitmap_p = Bitmap.createScaledBitmap(bitmap,(int)width-1,(int)(height),true);
                                picture.setImageBitmap(bitmap_p);
                            }
                            picture.setLayoutParams(params);
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                else{
                    if(chat==0){
                        intent=new Intent(Upload_Picture_promote_Activity.this,Publish_Info_promote_Activity.class);
                    }
                    else{
                        intent=new Intent(Upload_Picture_promote_Activity.this,Chat_Window_Activity.class);
                    }
                    startActivity(intent);
                }
                break;
            case CHOOSE_PHOTO:
                Intent intent1;
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }
                    else{
                        handleImageBeforeKitKat(data);
                    }
                }
                else{
                    if(chat==0){
                        intent1=new Intent(Upload_Picture_promote_Activity.this,Publish_Info_promote_Activity.class);
                    }
                    else{
                        intent1=new Intent(Upload_Picture_promote_Activity.this,Chat_Window_Activity.class);
                    }
                    startActivity(intent1);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else{
                    Toast.makeText(this,"没有权限访问相册！",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //Document 类型的Uri通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID+"="+id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            //content类型的Uri使用普通方式处理
            imagePath = getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            //file类型的Uri直接获取图片路径
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri,String selection){
        String path = null;
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            Bitmap bitmap_p;
            double p_width=bitmap.getWidth();
            double p_height=bitmap.getHeight();
            double width=1000;//标准宽
            double height=1600;//标准高
            LinearLayout.LayoutParams params;
            double ratio=p_width/p_height,st_ratio=width/height;
            if(ratio>st_ratio){
                height=width/ratio;
                params = new LinearLayout.LayoutParams((int)width,(int)(height)-1);
                bitmap_p = Bitmap.createScaledBitmap(bitmap,(int)width,(int)(height)-1,true);
                picture.setImageBitmap(bitmap_p);
            }
            else{
                width=ratio*height;
                params = new LinearLayout.LayoutParams((int)(width)-1,(int)height);
                bitmap_p = Bitmap.createScaledBitmap(bitmap,(int)width-1,(int)(height),true);
                picture.setImageBitmap(bitmap_p);
            }
            picture.setLayoutParams(params);
        }
        else {
            Toast.makeText(this,"获取图片失败！",Toast.LENGTH_SHORT).show();
        }
    }
}