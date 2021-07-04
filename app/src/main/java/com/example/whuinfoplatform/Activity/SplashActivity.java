package com.example.whuinfoplatform.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.whuinfoplatform.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class SplashActivity extends AppCompatActivity {
    private ImageView splash;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_splash);
        splash = findViewById(R.id.img_splash);

        //开启一个子线程执行跳转任务
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(1500);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animateImage();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //放大图片
    public void animateImage() {


        AnimatorSet set = new AnimatorSet();
        //设置缩放动画
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(splash, "scaleX", 1f,
                1.007f);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(splash, "scaleY", 1f,
                1.007f);
        set.setDuration(20000).play(animatorX).with(animatorY);
        set.start();
        AnimatorSet set1 = new AnimatorSet();
        //设置缩放动画
        ObjectAnimator animatorX1 = ObjectAnimator.ofFloat(splash, "scaleX", 1.010f,
                1.017f);
        ObjectAnimator animatorY1 = ObjectAnimator.ofFloat(splash, "scaleY", 1.010f,
                1.017f);
        set1.setDuration(20000).play(animatorX1).with(animatorY1).after(animatorY);
        set1.start();

        //动画完成监听
        set1.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                //动画完成后跳转首页
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }
}