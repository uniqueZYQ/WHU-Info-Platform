package com.example.whuinfoplatform.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
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
import com.example.whuinfoplatform.Dao.InfoConnection;
import com.example.whuinfoplatform.Entity.BaiDuMap;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.MyInformation;
import com.example.whuinfoplatform.Entity.WebResponse;
import com.example.whuinfoplatform.R;

import org.json.JSONException;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

public class Renew_Info_promote_Activity extends rootActivity {
    private com.example.whuinfoplatform.databinding.ActivityRenewInfoPromoteBinding binding;
    int id=0,form=0,fd_form=0,help_form=0,score=0,init=1,i=1;
    private ArrayList<Integer> pictureList=new ArrayList<Integer>();
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    BaiDuMap baidumap=new BaiDuMap();
    PoiSearch mPoiSearch = PoiSearch.newInstance();
    private double latitude,longitude;
    private String name=new String();
    private String address=new String();
    private String placeId=new String();
    private int first=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView() {
        binding= com.example.whuinfoplatform.databinding.ActivityRenewInfoPromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.calendar.setVisibility(View.GONE);
    }

    private void initMap(){
        mMapView = (MapView) findViewById(R.id.mapView);
        binding.frame.setVisibility(View.VISIBLE);
        mMapView.setVisibility(View.VISIBLE);
        mBaiduMap=mMapView.getMap();
        if(first==0){
            binding.card.setVisibility(View.VISIBLE);
            mPoiSearch.setOnGetPoiSearchResultListener(listener1);
        }
        //定位初始化为武汉大学行政楼
        LatLng ll = new LatLng(30.543803317144, 114.37292090919);
        float zoom=16;
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
        mBaiduMap.setMapStatus(u);
        mBaiduMap.animateMapStatus(u);
        Renew_Info_promote_Activity.MyLocationListener myLocationListener = new Renew_Info_promote_Activity.MyLocationListener();
        //定位监听初始化
        mLocationClient = new LocationClient(this);
        //获取实时定位
        baidumap.getLocation2(mBaiduMap,mLocationClient,myLocationListener);
        //配置地图
        baidumap.configMap(mBaiduMap, MyLocationConfiguration.LocationMode.NORMAL,true, BitmapDescriptorFactory.fromResource(R.drawable.location),0x55FFFFFF,0x55FFFFFF);
        //禁止旋转手势
        UiSettings mUiSettings=mBaiduMap.getUiSettings();
        mUiSettings.setRotateGesturesEnabled(false);
        if(first==1)
            Toast.makeText(Renew_Info_promote_Activity.this,"正在获取实时位置，请稍候...",Toast.LENGTH_LONG).show();
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不再处理新接收的位置
            if (location == null || mMapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            latitude = location.getLatitude();    //获取纬度信息
            longitude = location.getLongitude();    //获取经度信息
            if((latitude>=1||longitude>=1)&&first==1){
                LatLng ll = new LatLng(latitude, longitude);
                //初始化中心点为实时位置,设初始缩放程度为17
                float zoom=17;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
                mBaiduMap.setMapStatus(u);
                mBaiduMap.animateMapStatus(u);
                Toast.makeText(Renew_Info_promote_Activity.this,"实时位置获取成功!",Toast.LENGTH_SHORT).show();
                first=0;
                //设置地图单击事件监听
                mBaiduMap.setOnMapClickListener(listener);
                binding.card.setVisibility(View.VISIBLE);
                mPoiSearch.setOnGetPoiSearchResultListener(listener1);
            }
        }
    }

    OnGetPoiSearchResultListener listener1 = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if(poiResult.getTotalPoiNum()>=5){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                PoiInfo poi2=poiResult.getAllPoi().get(2);
                PoiInfo poi3=poiResult.getAllPoi().get(3);
                PoiInfo poi4=poiResult.getAllPoi().get(4);
                //通过前5条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid+","+poi3.uid+","+poi4.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else if(poiResult.getTotalPoiNum()==4){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                PoiInfo poi2=poiResult.getAllPoi().get(2);
                PoiInfo poi3=poiResult.getAllPoi().get(3);
                //通过前4条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid+","+poi3.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else if(poiResult.getTotalPoiNum()==3){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                PoiInfo poi2=poiResult.getAllPoi().get(2);
                //通过前3条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else if(poiResult.getTotalPoiNum()==2){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                //通过前2条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else if(poiResult.getTotalPoiNum()==1){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                //通过前1条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else{
                //搜索结果为空
                Toast.makeText(Renew_Info_promote_Activity.this,"暂无该地点\n试试其他搜索关键词!",Toast.LENGTH_SHORT).show();
                mBaiduMap.clear();
            }
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            //搜索结果信息获取
            int size=poiDetailSearchResult.getPoiDetailInfoList().size();
            if(size>0){
                Button button1 = new Button(getApplicationContext());
                Button button2 = new Button(getApplicationContext());
                name=poiDetailSearchResult.getPoiDetailInfoList().get(0).getName();
                address=poiDetailSearchResult.getPoiDetailInfoList().get(0).getAddress();
                placeId=poiDetailSearchResult.getPoiDetailInfoList().get(0).getUid();
                LatLng ll=new LatLng(poiDetailSearchResult.getPoiDetailInfoList().get(0).getLocation().latitude,
                        poiDetailSearchResult.getPoiDetailInfoList().get(0).getLocation().longitude);
                float zoom=18;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
                mBaiduMap.setMapStatus(u);
                mBaiduMap.animateMapStatus(u);
                baidumap.setMark(ll,mBaiduMap);
                baidumap.setInfoWindow(mBaiduMap,button1,button2,ll,name,address,true);
                button1.setOnClickListener(v -> {
                    mMapView.setVisibility(View.GONE);
                    binding.frame.setVisibility(View.GONE);
                    binding.card.setVisibility(View.GONE);
                    binding.editPlace.setText(name+" ["+address+"]");
                });
                button2.setOnClickListener(v -> {
                    if(i<size){
                        name=poiDetailSearchResult.getPoiDetailInfoList().get(i).getName();
                        address=poiDetailSearchResult.getPoiDetailInfoList().get(i).getAddress();
                        placeId=poiDetailSearchResult.getPoiDetailInfoList().get(i).getUid();
                        LatLng ll_ex=new LatLng(poiDetailSearchResult.getPoiDetailInfoList().get(i).getLocation().latitude,
                                poiDetailSearchResult.getPoiDetailInfoList().get(i).getLocation().longitude);
                        float zoom_ex=18;
                        MapStatusUpdate u_ex = MapStatusUpdateFactory.newLatLngZoom(ll_ex,zoom_ex);
                        mBaiduMap.setMapStatus(u_ex);
                        mBaiduMap.animateMapStatus(u_ex);
                        baidumap.setMark(ll_ex,mBaiduMap);
                        baidumap.setInfoWindow(mBaiduMap,button1,button2,ll_ex,name,address,true);
                        i++;
                    }
                    else
                        Toast.makeText(Renew_Info_promote_Activity.this,"没有更多结果了\n试试搜索其他关键词？",Toast.LENGTH_SHORT).show();
                });
            }
        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };

    BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng point) {

        }

        @Override
        public void onMapPoiClick(MapPoi mapPoi) {
            Button button1 = new Button(getApplicationContext());
            Button button2 = new Button(getApplicationContext());
            LatLng ll = new LatLng(mapPoi.getPosition().latitude, mapPoi.getPosition().longitude);
            //初始化中心点为点击处,设初始缩放程度为17
            float zoom=18;
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
            mBaiduMap.setMapStatus(u);
            mBaiduMap.animateMapStatus(u);
            name=mapPoi.getName();
            placeId=mapPoi.getUid();
            baidumap.setMark(ll,mBaiduMap);
            baidumap.setInfoWindow(mBaiduMap,button1,button2,ll,name,"",false);
            button1.setOnClickListener(v -> {
                mMapView.setVisibility(View.GONE);
                binding.frame.setVisibility(View.GONE);
                binding.card.setVisibility(View.GONE);
                binding.editPlace.setText(name);
            });
        }
    };

    private void showInfoDetail(MyInformation myInformation){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.detail.setText(myInformation.getDetail());
                binding.editCommobj.setText(myInformation.getLesson());
                binding.editPlace.setText(myInformation.getPlace());
                binding.editDate.setText(myInformation.getDate());
                binding.editReward.setText(String.valueOf(myInformation.getReward()));
                binding.editPrice.setText(String.valueOf(myInformation.getPrice()));
                switch(form) {
                    case 1:{
                        binding.form.setText("学术咨询");
                        binding.persInfoFdType.setVisibility(View.VISIBLE);
                        binding.editReward.setVisibility(View.VISIBLE);
                        binding.persInfoFdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(init==1)parent.setSelection(myInformation.getFd_form());
                                fd_form=(int)position;
                                init=0;
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                parent.setSelection(myInformation.getFd_form());
                            }
                        });
                        binding.finish.setOnClickListener(v->{
                            double reward=binding.editReward.getText().toString().equals("")?-2:
                                    binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                            binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                                    binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                            String detail=binding.detail.getText().toString();
                            if(reward==-1||reward==0)
                                Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                            else if(!(fd_form==0||reward==-2||detail.equals(""))) {
                                String text=String.valueOf(reward);
                                int length=text.length();
                                int res=0;
                                boolean count=false;
                                for(int i=0;i<length;i++){
                                    if(count)
                                        res++;
                                    if(text.charAt(i)=='.'){
                                        count=true;
                                    }
                                }
                                if(res>2||count&&res==0)
                                    Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                                else{
                                    InfoConnection infoConnection=new InfoConnection();
                                    long timecurrentTimeMillis = System.currentTimeMillis();
                                    SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                    String time = sdfTwo.format(timecurrentTimeMillis);
                                    infoConnection.updateMyInfoConnection1(String.valueOf(id),String.valueOf(fd_form),String.valueOf(reward),detail,time, new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,"服务器连接失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String result=response.body().string();
                                            WebResponse webResponse=new WebResponse();
                                            infoConnection.parseJSONForInfoResponse(webResponse,result);
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,webResponse.getResponse(),Toast.LENGTH_SHORT).show();
                                            if(webResponse.getCode()==101){
                                                Intent intent1 = new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                                startActivity(intent1);
                                            }
                                            Looper.loop();
                                        }
                                    });
                                }
                            }
                            else
                                Toast.makeText(Renew_Info_promote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    case 2:{
                        binding.form.setText("日常求助");
                        binding.persInfoHelpType.setVisibility(View.VISIBLE);
                        binding.editReward.setVisibility(View.VISIBLE);
                        binding.persInfoHelpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(init==1)parent.setSelection(myInformation.getHelp_form());
                                help_form=(int)position;
                                init=0;
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                parent.setSelection(myInformation.getHelp_form());
                            }
                        });
                        binding.finish.setOnClickListener(v->{
                            double reward=binding.editReward.getText().toString().equals("")?-2:
                                    binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                            binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                                    binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                            String detail=binding.detail.getText().toString();
                            if(reward==-1||reward==0)
                                Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                            else if(!(help_form==0||reward==-2||detail.equals(""))) {
                                String text=String.valueOf(reward);
                                int length=text.length();
                                int res=0;
                                boolean count=false;
                                for(int i=0;i<length;i++){
                                    if(count)
                                        res++;
                                    if(text.charAt(i)=='.'){
                                        count=true;
                                    }
                                }
                                if(res>2||count&&res==0)
                                    Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                                else{
                                    InfoConnection infoConnection=new InfoConnection();
                                    long timecurrentTimeMillis = System.currentTimeMillis();
                                    SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                    String time = sdfTwo.format(timecurrentTimeMillis);
                                    infoConnection.updateMyInfoConnection2(String.valueOf(id),String.valueOf(help_form),String.valueOf(reward),detail,time, new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,"服务器连接失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String result=response.body().string();
                                            WebResponse webResponse=new WebResponse();
                                            infoConnection.parseJSONForInfoResponse(webResponse,result);
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,webResponse.getResponse(),Toast.LENGTH_SHORT).show();
                                            if(webResponse.getCode()==101){
                                                Intent intent1 = new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                                startActivity(intent1);
                                            }
                                            Looper.loop();
                                        }
                                    });
                                }
                            }
                            else
                                Toast.makeText(Renew_Info_promote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    case 3:{
                        binding.form.setText("物品出售");
                        binding.editPrice.setVisibility(View.VISIBLE);
                        binding.finish.setOnClickListener(v->{
                            double price=binding.editPrice.getText().toString().equals("")?-2:
                                    binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                            binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                                    binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                            String detail=binding.detail.getText().toString();
                            if(price==-1||price==0)
                                Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                            else if(!(price==-2||detail.equals(""))) {
                                String text=String.valueOf(price);
                                int length=text.length();
                                int res=0;
                                boolean count=false;
                                for(int i=0;i<length;i++){
                                    if(count)
                                        res++;
                                    if(text.charAt(i)=='.'){
                                        count=true;
                                    }
                                }
                                if(res>2||count&&res==0)
                                    Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                                else{
                                    InfoConnection infoConnection=new InfoConnection();
                                    long timecurrentTimeMillis = System.currentTimeMillis();
                                    SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                    String time = sdfTwo.format(timecurrentTimeMillis);
                                    infoConnection.updateMyInfoConnection3(String.valueOf(id),String.valueOf(price),detail,time, new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,"服务器连接失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String result=response.body().string();
                                            WebResponse webResponse=new WebResponse();
                                            infoConnection.parseJSONForInfoResponse(webResponse,result);
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,webResponse.getResponse(),Toast.LENGTH_SHORT).show();
                                            if(webResponse.getCode()==101){
                                                Intent intent1 = new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                                startActivity(intent1);
                                            }
                                            Looper.loop();
                                        }
                                    });
                                }
                            }
                            else
                                Toast.makeText(Renew_Info_promote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    case 4:{
                        binding.form.setText("物品求购");
                        binding.editPrice.setVisibility(View.VISIBLE);
                        binding.finish.setOnClickListener(v->{
                            double price=binding.editPrice.getText().toString().equals("")?-2:
                                    binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                            binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                                    binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                            String detail=binding.detail.getText().toString();
                            if(price==-1||price==0)
                                Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                            else if(!(price==-2||detail.equals(""))) {
                                String text=String.valueOf(price);
                                int length=text.length();
                                int res=0;
                                boolean count=false;
                                for(int i=0;i<length;i++){
                                    if(count)
                                        res++;
                                    if(text.charAt(i)=='.'){
                                        count=true;
                                    }
                                }
                                if(res>2||count&&res==0)
                                    Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                                else{
                                    InfoConnection infoConnection=new InfoConnection();
                                    long timecurrentTimeMillis = System.currentTimeMillis();
                                    SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                    String time = sdfTwo.format(timecurrentTimeMillis);
                                    infoConnection.updateMyInfoConnection3(String.valueOf(id),String.valueOf(price),detail,time, new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,"服务器连接失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String result=response.body().string();
                                            WebResponse webResponse=new WebResponse();
                                            infoConnection.parseJSONForInfoResponse(webResponse,result);
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,webResponse.getResponse(),Toast.LENGTH_SHORT).show();
                                            if(webResponse.getCode()==101){
                                                Intent intent1 = new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                                startActivity(intent1);
                                            }
                                            Looper.loop();
                                        }
                                    });
                                }
                            }
                            else
                                Toast.makeText(Renew_Info_promote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    case 5:{
                        binding.form.setText("组织性活动信息");
                        binding.editReward.setVisibility(View.VISIBLE);
                        binding.editDate.setVisibility(View.VISIBLE);
                        binding.editPlace.setVisibility(View.VISIBLE);
                        binding.finish.setOnClickListener(v->{
                            double reward=binding.editReward.getText().toString().equals("")?-2:
                                    binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                            binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                                    binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                            String date=binding.editDate.getText().toString();
                            String place=binding.editPlace.getText().toString();
                            String detail=binding.detail.getText().toString();
                            if(reward==-1||reward==0)
                                Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                            else if(!(reward==-2||detail.equals("")||date.equals("")||place.equals(""))) {
                                String text=String.valueOf(reward);
                                int length=text.length();
                                int res=0;
                                boolean count=false;
                                for(int i=0;i<length;i++){
                                    if(count)
                                        res++;
                                    if(text.charAt(i)=='.'){
                                        count=true;
                                    }
                                }
                                if(res>2||count&&res==0)
                                    Toast.makeText(Renew_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                                else{
                                    InfoConnection infoConnection=new InfoConnection();
                                    long timecurrentTimeMillis = System.currentTimeMillis();
                                    SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                    String time = sdfTwo.format(timecurrentTimeMillis);
                                    infoConnection.updateMyInfoConnection4(String.valueOf(id),String.valueOf(reward),place,date,detail,placeId,time, new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,"服务器连接失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }

                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String result=response.body().string();
                                            WebResponse webResponse=new WebResponse();
                                            infoConnection.parseJSONForInfoResponse(webResponse,result);
                                            Looper.prepare();
                                            Toast.makeText(Renew_Info_promote_Activity.this,webResponse.getResponse(),Toast.LENGTH_SHORT).show();
                                            if(webResponse.getCode()==101){
                                                Intent intent1 = new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                                startActivity(intent1);
                                            }
                                            Looper.loop();
                                        }
                                    });
                                }
                            }
                            else
                                Toast.makeText(Renew_Info_promote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    case 6:{
                        binding.form.setText("课程点评信息");
                        binding.editCommobj.setVisibility(View.VISIBLE);
                        binding.editScore.setVisibility(View.VISIBLE);
                        binding.editScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if(init==1)parent.setSelection(myInformation.getScore());
                                score=(int)position;
                                init=0;
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                parent.setSelection(myInformation.getScore());
                            }
                        });
                        binding.finish.setOnClickListener(v->{
                            String lesson=binding.editCommobj.getText().toString();
                            String detail=binding.detail.getText().toString();
                            if(!(lesson.equals("")||detail.equals("")||score==0)) {
                                InfoConnection infoConnection=new InfoConnection();
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                infoConnection.updateMyInfoConnection5(String.valueOf(id),String.valueOf(score),lesson,detail,time, new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Looper.prepare();
                                        Toast.makeText(Renew_Info_promote_Activity.this,"服务器连接失败，请稍后再试",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String result=response.body().string();
                                        WebResponse webResponse=new WebResponse();
                                        infoConnection.parseJSONForInfoResponse(webResponse,result);
                                        Looper.prepare();
                                        Toast.makeText(Renew_Info_promote_Activity.this,webResponse.getResponse(),Toast.LENGTH_SHORT).show();
                                        if(webResponse.getCode()==101){
                                            Intent intent1 = new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                            startActivity(intent1);
                                        }
                                        Looper.loop();
                                    }
                                });
                            }
                            else
                                Toast.makeText(Renew_Info_promote_Activity.this, "请完善信息", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        Intent intent=getIntent();
        form=intent.getIntExtra("form",0);
        id=intent.getIntExtra("id",0);
        InfoConnection infoConnection=new InfoConnection();
        infoConnection.queryMyInfoDetailConnection(String.valueOf(id), new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(Renew_Info_promote_Activity.this,"服务器连接失败，请稍后再试",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result=response.body().string();
                MyInformation myInformation=new MyInformation();
                try {
                    infoConnection.parseJSONForMyInfoDetailResponse(myInformation,result);
                    showInfoDetail(myInformation);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.editDate.setOnClickListener(v -> {
            binding.calendar.setVisibility(View.VISIBLE);
            long timecurrentTimeMillis = System.currentTimeMillis();
            binding.calendar.setMinDate(timecurrentTimeMillis);
            if(!binding.editDate.getText().toString().equals("")){
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date current=simpleDateFormat.parse(binding.editDate.getText().toString());
                    long time=current.getTime();
                    binding.calendar.setDate(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        binding.calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                if(month<9){
                    if(dayOfMonth<10){
                        binding.editDate.setText(String.valueOf(year)+"-0"+String.valueOf(month+1)+"-0"+String.valueOf(dayOfMonth));
                    }
                    else{
                        binding.editDate.setText(String.valueOf(year)+"-0"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth));
                    }
                }
                else{
                    if(dayOfMonth<10){
                        binding.editDate.setText(String.valueOf(year)+"-"+String.valueOf(month+1)+"-0"+String.valueOf(dayOfMonth));
                    }
                    else{
                        binding.editDate.setText(String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth));
                    }
                }
                binding.calendar.setVisibility(View.GONE);
            }
        });
        //开始定义日历隐藏事件
        binding.editPlace.setOnClickListener(v -> {
            binding.calendar.setVisibility(View.GONE);
            initMap();
        });
        binding.detail.setOnClickListener(v -> {
            binding.calendar.setVisibility(View.GONE);
        });
        binding.editReward.setOnClickListener(v -> {
            binding.calendar.setVisibility(View.GONE);
        });
        binding.editPlace.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.calendar.setVisibility(View.GONE);
                } else {

                }
            }
        });
        binding.detail.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.calendar.setVisibility(View.GONE);
                } else {

                }
            }
        });
        binding.editReward.setOnFocusChangeListener(new android.view.View.
                OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    binding.calendar.setVisibility(View.GONE);
                } else {

                }
            }
        });
        //结束日历隐藏事件
        binding.input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                if(actionId== EditorInfo.IME_ACTION_SEARCH){
                    i=1;//重置索引值
                    String kwd = binding.input.getText().toString();
                    boolean valid = false;
                    for (int i = 0; i < kwd.length(); i++) {
                        if (kwd.charAt(i) == '\0' || kwd.charAt(i) == '\n' || kwd.charAt(i) == ' ')
                            continue;
                        else
                            valid = true;
                    }
                    if(valid){
                        /**
                         *  PoiCiySearchOption 设置检索属性
                         *  city 检索城市
                         *  keyword 检索内容关键字
                         *  pageNum 分页页码
                         */
                        mPoiSearch.searchInCity(new PoiCitySearchOption()
                                .city("武汉") //必填
                                .keyword(kwd) //必填
                                .pageNum(0));
                    }
                    else{
                        binding.input.setText("");
                        Toast.makeText(Renew_Info_promote_Activity.this,"无法搜索无意义的内容！",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                else
                    return false;
            }
        });
        binding.search.setOnClickListener(v->{
            i=1;//重置索引值
            String kwd = binding.input.getText().toString();
            boolean valid = false;
            for (int i = 0; i < kwd.length(); i++) {
                if (kwd.charAt(i) == '\0' || kwd.charAt(i) == '\n' || kwd.charAt(i) == ' ')
                    continue;
                else
                    valid = true;
            }
            if(valid){
                /**
                 *  PoiCiySearchOption 设置检索属性
                 *  city 检索城市
                 *  keyword 检索内容关键字
                 *  pageNum 分页页码
                 */
                mPoiSearch.searchInCity(new PoiCitySearchOption()
                        .city("武汉") //必填
                        .keyword(kwd) //必填
                        .pageNum(0));
            }
            else{
                binding.input.setText("");
                Toast.makeText(Renew_Info_promote_Activity.this,"无法搜索无意义的内容！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("我发布的-信息详情-修改信息");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(form==5&&first==0){
            mMapView.onPause();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(form==5&&first==0){
            mMapView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(form==5&&first==0){
            mLocationClient.stop();
            mBaiduMap.setMyLocationEnabled(false);
            mMapView.onDestroy();
            mPoiSearch.destroy();
            mMapView = null;
        }
    }
}