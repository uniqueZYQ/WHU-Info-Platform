package com.example.whuinfoplatform.Entity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.whuinfoplatform.R;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class EnlargePicture {
    private Dialog mCameraDialog;
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

    public void EnlargePicture(Context context, Bitmap bit,boolean pan_supported) {
        LinearLayout root = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.enlarge_picture, null);
        //初始化视图
        mCameraDialog = new Dialog(context, R.style.BottomDialog);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);

        Connector.getDatabase();
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
        if(pan_supported){
            initTouch(root);
        }
        mCameraDialog.show();
    }

    private void initTouch(LinearLayout root){

        ImageView mImageView = (ImageView) root.findViewById(R.id.picture);
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
}
