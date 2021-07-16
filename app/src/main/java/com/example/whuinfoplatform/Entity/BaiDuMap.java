package com.example.whuinfoplatform.Entity;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Button;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.whuinfoplatform.Activity.Publish_Info_promote_Activity;
import com.example.whuinfoplatform.R;

import org.litepal.LitePalApplication;

public class BaiDuMap extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        LitePalApplication.initialize(context);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }

    public static Context getContext(){
        return context;
    }

    public void getLocation(BaiduMap mBaiduMap, LocationClient mLocationClient, Publish_Info_promote_Activity.MyLocationListener myLocationListener){
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        //设置locationClientOption
        mLocationClient.setLocOption(option);
        //注册LocationListener监听器
        mLocationClient.registerLocationListener(myLocationListener);
        //开启地图定位图层
        mLocationClient.start();
    }

    //实际使用时会失效，原因未知
    public void getLocationBackground(Context context,LocationClient mLocClient){
        //开启前台定位服务：
        Notification.Builder builder = new Notification.Builder (context.getApplicationContext());
        //获取一个Notification构造器
        Intent nfIntent = new Intent(context.getApplicationContext(), Publish_Info_promote_Activity.class);
        builder.setContentIntent(PendingIntent.getActivity(context, 0, nfIntent, 0)) // 设置PendingIntent
                .setContentTitle("正在进行后台定位") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("后台定位通知") // 设置上下文内容
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = null;
        notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        mLocClient.enableLocInForeground(1001, notification);// 调起前台定位
        //停止前台定位服务：
        mLocClient.disableLocInForeground(true);// 关闭前台定位，同时移除通知栏
    }

    public void configMap(BaiduMap mBaiduMap,
                          MyLocationConfiguration.LocationMode mode,
                          boolean enableDirection,
                          BitmapDescriptor customMarker,
                          int accuracyCircleFillColor,
                          int accuracyCircleStrokeColor){
        MyLocationConfiguration mLocationConfiguration=new MyLocationConfiguration(mode,enableDirection, customMarker,accuracyCircleFillColor,accuracyCircleStrokeColor);
        mBaiduMap.setMyLocationConfiguration(mLocationConfiguration);
    }

    public void setMark(LatLng point,BaiduMap mBaiduMap){
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.place);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .animateType(MarkerOptions.MarkerAnimateType.jump)
                .position(point) //必传参数
                .icon(bitmap) //必传参数
                .draggable(false);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
    }

    public void setInfoWindow(BaiduMap mBaiduMap,Button button,LatLng point,String name,String address){
        //用来构造InfoWindow的Button
        button.setBackgroundResource(R.drawable.info_window);
        button.setTextSize(10);
        if(address.equals(""))
            button.setText(name);
        else
            button.setText(name+"\n"+address);
        //构造InfoWindow
        //point 描述的位置点
        //-100 InfoWindow相对于point在y轴的偏移量
        InfoWindow mInfoWindow = new InfoWindow(button, point, -50);

        //使InfoWindow生效
        mBaiduMap.showInfoWindow(mInfoWindow);
    }
}
