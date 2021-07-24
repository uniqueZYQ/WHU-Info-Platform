package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.User;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityRenewPermsgPromteBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;

import okhttp3.Call;
import okhttp3.Response;

public class Renew_Permsg_Promte_Activity extends rootActivity {
    private ActivityRenewPermsgPromteBinding binding;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageView = (ImageView) this.findViewById(R.id.picture);
        mImageView.setOnTouchListener(new View.OnTouchListener() {
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
        binding= ActivityRenewPermsgPromteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent1 = getIntent();
        String id = Integer.toString(intent1.getIntExtra("id", 0));
        if(intent1.getIntExtra("type",0)==1) {
            binding.editNickname.setVisibility(View.VISIBLE);
            binding.editPwd.setVisibility(View.VISIBLE);
            binding.editPwd.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        for (int i = 0; i < s.length(); i++) {
                            char c = s.charAt(i);
                            if (c >= 0x4e00 && c <= 0X9fff) { // 根据字节码判断
                                // 如果是中文，则清除输入的字符，否则保留
                                s.delete(i, i + 1);
                            }
                        }
                    }
                }
            });
            UserConnection userConnection=new UserConnection();
            userConnection.queryUserInfo(id, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Looper.prepare();
                    Toast.makeText(Renew_Permsg_Promte_Activity.this,"服务器连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    User user=new User();
                    userConnection.parseJSON(user,result);
                    Looper.prepare();
                    if(user.getCode()==101){
                        String nkn=user.getNickname();
                        String rnm=user.getRealname();
                        String stdid=user.getStdid();
                        showResult(nkn,rnm,stdid);
                    }
                    Looper.loop();
                }
            });
        }
        if(intent1.getIntExtra("type",0)>1){
            binding.picture.setVisibility(View.VISIBLE);
            binding.button1.setText("确定上传");
            binding.textStdidRnm.setText("缩放或拖动图片以适配相框");
            if(intent1.getIntExtra("type",0)==2){
                picture=binding.picture;
                if(ContextCompat.checkSelfPermission(Renew_Permsg_Promte_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Renew_Permsg_Promte_Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
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
                    imageUri= FileProvider.getUriForFile(Renew_Permsg_Promte_Activity.this,"com.example.whuinfoplatform.fileprovider",outputImage);
                }
                else{
                    imageUri=Uri.fromFile(outputImage);
                }
                Intent intent=new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void initClick() {
        super.initClick();
        binding.button1.setOnClickListener(v->{
            Intent intent1 = getIntent();
            if(intent1.getIntExtra("type",0)==1) {
                String id = Integer.toString(intent1.getIntExtra("id", 0));
                String newnkn = binding.editNickname.getText().toString();
                String newpwd = binding.editPwd.getText().toString();

                if ((newnkn.equals("")) || (newpwd.equals(""))) {
                    Toast.makeText(Renew_Permsg_Promte_Activity.this, "请填入完整信息！", Toast.LENGTH_SHORT).show();
                } else {
                    UserConnection userConnection=new UserConnection();
                    userConnection.renewUserInfo(id,newpwd,newnkn, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            Toast.makeText(Renew_Permsg_Promte_Activity.this,"服务器连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            User user=new User();
                            userConnection.parseJSON(user,result);
                            Looper.prepare();
                            Toast.makeText(Renew_Permsg_Promte_Activity.this, user.getResponse(), Toast.LENGTH_SHORT).show();
                            if(user.getCode()==101){
                                Intent intent2 = new Intent(Renew_Permsg_Promte_Activity.this, MainActivity.class);
                                startActivity(intent2);
                            }
                            Looper.loop();
                        }
                    });
                }
            }
            if(intent1.getIntExtra("type",0)>1) {
                String id = Integer.toString(intent1.getIntExtra("id", 0));
                binding.picture.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(binding.picture.getDrawingCache());
                binding.picture.setDrawingCacheEnabled(false);
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                byte[] in=os.toByteArray();
                String FileBuf = Base64.getEncoder().encodeToString(in);
                UserConnection userConnection=new UserConnection();
                userConnection.renewUserPicture(id,FileBuf, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(Renew_Permsg_Promte_Activity.this,"服务器连接失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result=response.body().string();
                        User user=new User();
                        userConnection.parseJSON(user,result);
                        Looper.prepare();
                        Toast.makeText(Renew_Permsg_Promte_Activity.this,user.getResponse(),Toast.LENGTH_SHORT).show();
                        if(user.getCode()==101){
                            Intent intent = new Intent(Renew_Permsg_Promte_Activity.this, Personal_Message_Activity.class);
                            startActivity(intent);
                        }
                        Looper.loop();
                    }
                });
            }
        });
        binding.button2.setOnClickListener(v->{
            Intent intent=new Intent(Renew_Permsg_Promte_Activity.this,Personal_Message_Activity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        Intent intent1 = getIntent();
        if(intent1.getIntExtra("type",0)==1) {
            actionBar.setTitle("个人资料-修改");
        }else
            actionBar.setTitle("个人资料-选择头像");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void showResult(String nkn,String rnm,String stdid){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.editNickname.setText(nkn);
                binding.textStdidRnm.setText(stdid + "-" + rnm);
            }
        });
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

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        switch(requestCode){
            case TAKE_PHOTO:
                if (resultCode==RESULT_OK){
                    try{
                        //显示照片
                        Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);
                    }catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                else{
                    Intent intent=new Intent(Renew_Permsg_Promte_Activity.this,Personal_Message_Activity.class);
                    startActivity(intent);
                }
                break;
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK){
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }
                    else{
                        handleImageBeforeKitKat(data);
                    }
                }
                else{
                    Intent intent=new Intent(Renew_Permsg_Promte_Activity.this,Personal_Message_Activity.class);
                    startActivity(intent);
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

    @TargetApi(19)
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

    private void displayImage(String imagePath){
        if(imagePath!=null){
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            //picture.setImageBitmap(bitmap);
            Bitmap bitmap_p;
            double p_width=bitmap.getWidth();
            double p_height=bitmap.getHeight();
            double width=800;//标准宽
            double height=800;//标准高
            LinearLayout.LayoutParams params;
            double ratio=p_width/p_height;
            if(ratio>1){
                height=width/ratio;
                params = new LinearLayout.LayoutParams(800,800);
                bitmap_p = Bitmap.createScaledBitmap(bitmap,(int)width,(int)(height)-1,true);
                picture.setImageBitmap(bitmap_p);
            }
            else{
                width=ratio*height;
                params = new LinearLayout.LayoutParams(800,800);
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