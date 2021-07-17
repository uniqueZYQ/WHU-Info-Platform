package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWRouteGuidanceListener;
import com.baidu.mapapi.walknavi.model.RouteGuideKind;
import com.example.whuinfoplatform.R;

public class WNaviGuideActivity extends AppCompatActivity {
    private WalkNavigateHelper mNaviHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi_guide);
        //获取WalkNavigateHelper实例
        mNaviHelper = WalkNavigateHelper.getInstance();

        try {
            View view = mNaviHelper.onCreate(WNaviGuideActivity.this);
            if (view != null) {
                setContentView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mNaviHelper.setRouteGuidanceListener(this, new IWRouteGuidanceListener() {
            //诱导图标更新
            @Override
            public void onRouteGuideIconUpdate(Drawable drawable) {

            }

            //诱导类型枚举
            @Override
            public void onRouteGuideKind(RouteGuideKind routeGuideKind) {

            }
            /**
             * 诱导信息
             * @param charSequence 第一行显示的信息，如“沿当前道路”
             * @param charSequence1  第二行显示的信息，比如“向东出发”，第二行信息也可能为空
             */
            @Override
            public void onRoadGuideTextUpdate(CharSequence charSequence, CharSequence charSequence1) {

            }
            //总的剩余距离
            @Override
            public void onRemainDistanceUpdate(CharSequence charSequence) {

            }
            //总的剩余时间
            @Override
            public void onRemainTimeUpdate(CharSequence charSequence) {

            }
            //GPS状态发生变化，来自诱导引擎的消息
            @Override
            public void onGpsStatusChange(CharSequence charSequence, Drawable drawable) {

            }
            //已经开始偏航
            @Override
            public void onRouteFarAway(CharSequence charSequence, Drawable drawable) {

            }
            //偏航规划中
            @Override
            public void onRoutePlanYawing(CharSequence charSequence, Drawable drawable) {

            }
            //重新算路成功
            @Override
            public void onReRouteComplete() {

            }
            //抵达目的地
            @Override
            public void onArriveDest() {

            }

            @Override
            public void onIndoorEnd(Message message) {

            }


            @Override
            public void onFinalEnd(Message msg) {

            }
            //震动
            @Override
            public void onVibrate() {

            }
        });

        boolean startResult = mNaviHelper.startWalkNavi(WNaviGuideActivity.this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mNaviHelper.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNaviHelper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNaviHelper.quit();
    }
}