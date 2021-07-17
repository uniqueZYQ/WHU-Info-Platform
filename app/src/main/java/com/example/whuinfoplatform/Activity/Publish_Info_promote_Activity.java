package com.example.whuinfoplatform.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*import com.example.whuinfoplatform.DB.DB_INFO;*/
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
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
import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.BaiDuMap;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.Picture;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPublishInfoPromoteBinding;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Publish_Info_promote_Activity extends rootActivity {
    private ActivityPublishInfoPromoteBinding binding;
    private int id=0,id1=0,id2=0,pos_fd=0,pos_help=0,pos_score=0,form=-1,picture_count=0,i=1;
    private double reward=0,price=0;
    private Dialog mCameraDialog;
    private boolean upload=false;
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
        binding=ActivityPublishInfoPromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.calendar.setVisibility(View.GONE);
    }

    private void initMap(){
        mMapView = (MapView) findViewById(R.id.mapView);
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
        Publish_Info_promote_Activity.MyLocationListener myLocationListener = new Publish_Info_promote_Activity.MyLocationListener();
        //定位监听初始化
        mLocationClient = new LocationClient(this);
        //获取实时定位
        baidumap.getLocation(mBaiduMap,mLocationClient,myLocationListener);
        //配置地图
        baidumap.configMap(mBaiduMap,MyLocationConfiguration.LocationMode.NORMAL,true,BitmapDescriptorFactory.fromResource(R.drawable.location),0x55FFFFFF,0x55FFFFFF);
        //禁止旋转手势
        UiSettings mUiSettings=mBaiduMap.getUiSettings();
        mUiSettings.setRotateGesturesEnabled(false);
        if(first==1)
            Toast.makeText(Publish_Info_promote_Activity.this,"正在获取实时位置，请稍候...",Toast.LENGTH_LONG).show();
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
                Toast.makeText(Publish_Info_promote_Activity.this,"实时位置获取成功!",Toast.LENGTH_SHORT).show();
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
            //PoiInfo 检索到的第一条信息
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
                //通过前5条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid+","+poi3.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else if(poiResult.getTotalPoiNum()==3){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                PoiInfo poi2=poiResult.getAllPoi().get(2);
                //通过前5条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else if(poiResult.getTotalPoiNum()==2){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                //通过前5条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else if(poiResult.getTotalPoiNum()==1){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                //通过前5条检索信息对应的uid发起详细信息检索
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid)); // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔。
            }
            else{
                Toast.makeText(Publish_Info_promote_Activity.this,"暂无该地点\n试试其他搜索关键词!",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Publish_Info_promote_Activity.this,"没有更多结果了\n试试搜索其他关键词？",Toast.LENGTH_SHORT).show();
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
                binding.card.setVisibility(View.GONE);
                binding.editPlace.setText(name);
            });
        }
    };

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        id=intent.getIntExtra("id",0);
        binding.infoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((int)id){
                    case 1:{//私人性信息
                        id1=1;
                        binding.calendar.setVisibility(View.GONE);
                        binding.persInfoType.setVisibility(View.VISIBLE);
                        binding.editPlace.setVisibility(View.GONE);
                        binding.editDate.setVisibility(View.GONE);
                        binding.editScore.setVisibility(View.GONE);
                        binding.editCommobj.setVisibility(View.GONE);
                        binding.persInfoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch ((int)id){
                                    case 1:{//学术咨询
                                        id2=1;
                                        binding.persInfoFdType.setVisibility(View.VISIBLE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editReward.setVisibility(View.VISIBLE);
                                        binding.editPrice.setVisibility(View.GONE);
                                        form=1;
                                        break;
                                    }
                                    case 2:{//日常求助
                                        id2=2;
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.VISIBLE);
                                        binding.editReward.setVisibility(View.VISIBLE);
                                        binding.editPrice.setVisibility(View.GONE);
                                        form=2;
                                        break;
                                    }
                                    case 3:{/*物品求购*/
                                        id2=3;
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editPrice.setVisibility(View.VISIBLE);
                                        binding.editReward.setVisibility(View.GONE);
                                        form=3;
                                        break;
                                    }
                                    case 4:{//物品出售
                                        id2=4;
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editPrice.setVisibility(View.VISIBLE);
                                        binding.editReward.setVisibility(View.GONE);
                                        form=4;
                                        break;
                                    }
                                    default:{
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editPrice.setVisibility(View.GONE);
                                        binding.editReward.setVisibility(View.GONE);
                                    }

                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) { }
                        });
                        break;
                    }
                    case 2:{//组织性信息
                        id1=2;
                        id2=1;
                        binding.persInfoType.setVisibility(View.GONE);
                        binding.persInfoFdType.setVisibility(View.GONE);
                        binding.persInfoHelpType.setVisibility(View.GONE);
                        binding.editPrice.setVisibility(View.GONE);
                        binding.editReward.setVisibility(View.VISIBLE);
                        binding.editDate.setVisibility(View.VISIBLE);
                        binding.editPlace.setVisibility(View.VISIBLE);
                        binding.editScore.setVisibility(View.GONE);
                        binding.editCommobj.setVisibility(View.GONE);
                        form=5;
                        break;
                    }
                    case 3:{//点评信息
                        id1=3;
                        id2=1;
                        binding.calendar.setVisibility(View.GONE);
                        binding.persInfoType.setVisibility(View.GONE);
                        binding.persInfoFdType.setVisibility(View.GONE);
                        binding.persInfoHelpType.setVisibility(View.GONE);
                        binding.editPrice.setVisibility(View.GONE);
                        binding.editReward.setVisibility(View.GONE);
                        binding.editDate.setVisibility(View.GONE);
                        binding.editPlace.setVisibility(View.GONE);
                        binding.editScore.setVisibility(View.VISIBLE);
                        binding.editCommobj.setVisibility(View.VISIBLE);
                        form=6;
                        break;
                    }
                    default:
                        binding.calendar.setVisibility(View.GONE);
                        binding.persInfoType.setVisibility(View.GONE);
                        binding.persInfoFdType.setVisibility(View.GONE);
                        binding.persInfoHelpType.setVisibility(View.GONE);
                        binding.editPrice.setVisibility(View.GONE);
                        binding.editReward.setVisibility(View.GONE);
                        binding.editDate.setVisibility(View.GONE);
                        binding.editPlace.setVisibility(View.GONE);
                        binding.editScore.setVisibility(View.GONE);
                        binding.editCommobj.setVisibility(View.GONE);

                }
                //Toast.makeText(Publish_Info_promote_Activity.this,"NOW "+(int)id,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Toast.makeText(Publish_Info_promote_Activity.this,"NOW NONE",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void initClick() {
        super.initClick();
        binding.persInfoFdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos_fd=(int)position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        binding.persInfoHelpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos_help=(int)position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        binding.editScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos_score=(int)position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        binding.send.setOnClickListener(v->{
            if(id1!=0&&id2!=0){
                switch(form){
                    case 1:{
                        String detail=binding.detail.getText().toString();
                        reward=binding.editReward.getText().toString().equals("")?-2:
                            binding.editReward.getText().toString().charAt(0)=='.'?-1:
                            binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                            binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                        if(pos_fd==0||detail.equals("")||reward==-2)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(reward==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
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
                                Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                            else{
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(1);
                                info.setFd_form(pos_fd);
                                info.setHelp_form(-1);
                                info.setPrice(-1);
                                info.setDate("");
                                info.setPlace("");
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(reward);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                info.setPlaceId("0");
                                Toast.makeText(Publish_Info_promote_Activity.this,"发布成功!\n可前往[我发布的]查看详情",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this,Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 2:{
                        String detail=binding.detail.getText().toString();
                        reward=binding.editReward.getText().toString().equals("")?-2:
                                binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                        if(pos_help==0||detail.equals("")||reward==-2)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(reward==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text = String.valueOf(reward);
                            int length = text.length();
                            int res = 0;
                            boolean count = false;
                            for (int i = 0; i < length; i++) {
                                if (count)
                                    res++;
                                if (text.charAt(i) == '.') {
                                    count = true;
                                }
                            }
                            if (res > 2 || count && res == 0)
                                Toast.makeText(Publish_Info_promote_Activity.this, "金额格式错误!", Toast.LENGTH_SHORT).show();
                            else {
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(2);
                                info.setFd_form(-1);
                                info.setHelp_form(pos_help);
                                info.setPrice(-1);
                                info.setDate("");
                                info.setPlace("");
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(reward);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                info.setPlaceId("0");
                                Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 3:{
                        String detail=binding.detail.getText().toString();
                        price=binding.editPrice.getText().toString().equals("")?-2:
                                binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                        if(price==-2||detail.equals(""))
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(price==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text = String.valueOf(price);
                            int length = text.length();
                            int res = 0;
                            boolean count = false;
                            for (int i = 0; i < length; i++) {
                                if (count)
                                    res++;
                                if (text.charAt(i) == '.') {
                                    count = true;
                                }
                            }
                            if (res > 2 || count && res == 0)
                                Toast.makeText(Publish_Info_promote_Activity.this, "金额格式错误!", Toast.LENGTH_SHORT).show();
                            else {
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(3);
                                info.setFd_form(-1);
                                info.setHelp_form(-1);
                                info.setPrice(price);
                                info.setDate("");
                                info.setPlace("");
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(-1);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                info.setPlaceId("0");
                                Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 4:{
                        String detail=binding.detail.getText().toString();
                        price=binding.editPrice.getText().toString().equals("")?-2:
                                binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                        if(price==-2||detail.equals(""))
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(price==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text = String.valueOf(price);
                            int length = text.length();
                            int res = 0;
                            boolean count = false;
                            for (int i = 0; i < length; i++) {
                                if (count)
                                    res++;
                                if (text.charAt(i) == '.') {
                                    count = true;
                                }
                            }
                            if (res > 2 || count && res == 0)
                                Toast.makeText(Publish_Info_promote_Activity.this, "金额格式错误!", Toast.LENGTH_SHORT).show();
                            else {
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(4);
                                info.setFd_form(-1);
                                info.setHelp_form(-1);
                                info.setPrice(price);
                                info.setDate("");
                                info.setPlace("");
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(-1);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                info.setPlaceId("0");
                                Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 5:{
                        String detail=binding.detail.getText().toString();
                        String place=binding.editPlace.getText().toString();
                        String date=binding.editDate.getText().toString();
                        reward=binding.editReward.getText().toString().equals("")?-2:
                                binding.editReward.getText().toString().charAt(0)=='.'?-1:
                                binding.editReward.getText().toString().charAt(binding.editReward.getText().toString().length()-1)=='.'?-1:
                                binding.editReward.getText().toString().equals(".")?-1:Double.parseDouble(binding.editReward.getText().toString());
                        if(detail.equals("")||place.equals("")||date.equals("")||reward==-2)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else if(reward==-1)
                            Toast.makeText(Publish_Info_promote_Activity.this,"金额格式错误!",Toast.LENGTH_SHORT).show();
                        else {//格式判断
                            String text = String.valueOf(reward);
                            int length = text.length();
                            int res = 0;
                            boolean count = false;
                            for (int i = 0; i < length; i++) {
                                if (count)
                                    res++;
                                if (text.charAt(i) == '.') {
                                    count = true;
                                }
                            }
                            if (res > 2 || count && res == 0)
                                Toast.makeText(Publish_Info_promote_Activity.this, "金额格式错误!", Toast.LENGTH_SHORT).show();
                            else {
                                Connector.getDatabase();
                                Info info = new Info();
                                info.setOwner_id(id);
                                info.setAnswered(0);
                                info.setForm(5);
                                info.setFd_form(-1);
                                info.setHelp_form(-1);
                                info.setPrice(-1);
                                info.setDate(date);
                                info.setPlace(place);
                                info.setLesson("");
                                info.setScore(-1);
                                info.setDetail(detail);
                                info.setReward(reward);
                                info.setPicture1(ret_list_1());
                                info.setPicture2(ret_list_2());
                                info.setPicture3(ret_list_3());
                                info.setPicture4(ret_list_4());
                                info.setPlaceId(placeId);
                                Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                info.setSend_date(time);
                                info.save();
                                startActivity(intent);
                            }
                        }
                        break;
                    }
                    case 6:{
                        String detail=binding.detail.getText().toString();
                        String lesson=binding.editCommobj.getText().toString();
                        if(detail.equals("")||lesson.equals("")||pos_score==0)
                            Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息!",Toast.LENGTH_SHORT).show();
                        else {
                            Connector.getDatabase();
                            Info info = new Info();
                            info.setOwner_id(id);
                            info.setAnswered(0);
                            info.setForm(6);
                            info.setFd_form(-1);
                            info.setHelp_form(-1);
                            info.setPrice(-1);
                            info.setDate("");
                            info.setPlace("");
                            info.setLesson(lesson);
                            info.setScore(pos_score);
                            info.setDetail(detail);
                            info.setReward(-1);
                            info.setPicture1(ret_list_1());
                            info.setPicture2(ret_list_2());
                            info.setPicture3(ret_list_3());
                            info.setPicture4(ret_list_4());
                            info.setPlaceId("0");
                            Toast.makeText(Publish_Info_promote_Activity.this, "发布成功!\n可前往[我发布的]查看详情", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Publish_Info_promote_Activity.this, Info_Hall_Activity.class);
                            long timecurrentTimeMillis = System.currentTimeMillis();
                            SimpleDateFormat sdfTwo = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
                            String time = sdfTwo.format(timecurrentTimeMillis);
                            info.setSend_date(time);
                            info.save();
                            startActivity(intent);
                        }
                        break;
                    }
                    default:
                }
            }
            else
                Toast.makeText(Publish_Info_promote_Activity.this,"请完善信息！",Toast.LENGTH_SHORT).show();
        });
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
        binding.upload.setOnClickListener(v -> {
            if(picture_count>=4){//最多上传四张图片
                Toast.makeText(Publish_Info_promote_Activity.this,"最多只能上传四张图片!",Toast.LENGTH_SHORT).show();
            }
            else{
                upload=true;
                setDialog();
            }
        });
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
                        Toast.makeText(Publish_Info_promote_Activity.this,"无法搜索无意义的内容！",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Publish_Info_promote_Activity.this,"无法搜索无意义的内容！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("信息大厅-编辑新信息");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void setDialog() {
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.bottom_dialog, null);
        //初始化视图
        root.findViewById(R.id.btn_choose_img).setOnClickListener(v -> {
            Intent intent = new Intent(Publish_Info_promote_Activity.this,Upload_Picture_promote_Activity.class);
            intent.putExtra("id",id);
            intent.putExtra("type",2);
            startActivity(intent);
        });
        root.findViewById(R.id.btn_open_camera).setOnClickListener(v -> {
            Intent intent = new Intent(Publish_Info_promote_Activity.this,Upload_Picture_promote_Activity.class);
            intent.putExtra("id",id);
            intent.putExtra("type",3);
            startActivity(intent);
        });
        Button btn_choose_img=root.findViewById(R.id.btn_choose_img);
        Button btn_open_camera=root.findViewById(R.id.btn_open_camera);
        btn_choose_img.setText("从相册中选择图片");
        btn_open_camera.setText("拍摄图片");
        mCameraDialog = new Dialog(this, R.style.BottomDialog);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        //dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(form==5&&first==0){
            mMapView.onPause();
        }
        if(upload)
            if(mCameraDialog.isShowing()) mCameraDialog.cancel();
    }

    /**
     *
     * 重写此方法，加上setIntent(intent);否则在onResume里面得不到intent
     * @param intent intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(form==5&&first==0){
            mMapView.onResume();
        }
        initPictureList();
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

    private void initPictureList(){
        Intent intent=getIntent();
        int picture_id=intent.getIntExtra("picture_id",0);
        if(picture_id!=0){
            picture_count++;
            pictureList.add(picture_id);
        }
        Connector.getDatabase();
        switch(pictureList.size()){
            case 4:
                int picture4=pictureList.get(3);
                List<Picture> picture_4 = DataSupport.where("id=?",String.valueOf(picture4)).find(Picture.class);
                byte[] in_4=picture_4.get(0).getPicture();
                binding.picture4.setVisibility(View.VISIBLE);
                Bitmap bit_4 = BitmapFactory.decodeByteArray(in_4, 0, in_4.length);
                binding.picture4.setImageBitmap(bit_4);
            case 3:
                int picture3=pictureList.get(2);
                List<Picture> picture_3 = DataSupport.where("id=?",String.valueOf(picture3)).find(Picture.class);
                byte[] in_3=picture_3.get(0).getPicture();
                binding.picture3.setVisibility(View.VISIBLE);
                Bitmap bit_3 = BitmapFactory.decodeByteArray(in_3, 0, in_3.length);
                binding.picture3.setImageBitmap(bit_3);
            case 2:
                int picture2=pictureList.get(1);
                List<Picture> picture_2 = DataSupport.where("id=?",String.valueOf(picture2)).find(Picture.class);
                byte[] in_2=picture_2.get(0).getPicture();
                binding.picture2.setVisibility(View.VISIBLE);
                Bitmap bit_2 = BitmapFactory.decodeByteArray(in_2, 0, in_2.length);
                binding.picture2.setImageBitmap(bit_2);
            case 1:
                int picture1=pictureList.get(0);
                List<Picture> picture_1 = DataSupport.where("id=?",String.valueOf(picture1)).find(Picture.class);
                byte[] in_1=picture_1.get(0).getPicture();
                binding.picture1.setVisibility(View.VISIBLE);
                Bitmap bit_1 = BitmapFactory.decodeByteArray(in_1, 0, in_1.length);
                binding.picture1.setImageBitmap(bit_1);
                binding.upload.setText("继续上传图片");
                break;
            default:
                break;
        }
    }

    private int ret_list_1(){
        if(pictureList.size()>=1){
            return pictureList.get(0);
        }
        else{
            return 0;
        }
    }

    private int ret_list_2(){
        if(pictureList.size()>=2){
            return pictureList.get(1);
        }
        else{
            return 0;
        }
    }

    private int ret_list_3(){
        if(pictureList.size()>=3){
            return pictureList.get(2);
        }
        else{
            return 0;
        }
    }

    private int ret_list_4(){
        if(pictureList.size()>=4){
            return pictureList.get(3);
        }
        else{
            return 0;
        }
    }
}