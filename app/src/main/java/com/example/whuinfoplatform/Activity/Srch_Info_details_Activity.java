package com.example.whuinfoplatform.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.bikenavi.BikeNavigateHelper;
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener;
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener;
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError;
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam;
import com.baidu.mapapi.bikenavi.params.BikeRouteNodeInfo;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.walknavi.WalkNavigateHelper;
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener;
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener;
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError;
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam;
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo;
import com.example.whuinfoplatform.DB.DB_USER;
import com.example.whuinfoplatform.Entity.BaiDuMap;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.Msg;
import com.example.whuinfoplatform.Entity.Picture;
import com.example.whuinfoplatform.R;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Srch_Info_details_Activity extends rootActivity {
    private com.example.whuinfoplatform.databinding.ActivitySrchInfoDetailsBinding binding;
    int form=0;
    private DB_USER dbHelper;
    private ArrayList<Integer> pictureList=new ArrayList<Integer>();
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    BaiDuMap baidumap=new BaiDuMap();
    PoiSearch mPoiSearch = PoiSearch.newInstance();
    LatLng startPt;
    LatLng endPt;
    BikeNaviLaunchParam mParam;
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
        binding= com.example.whuinfoplatform.databinding.ActivitySrchInfoDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void initMap(String Uid){
        mMapView = (MapView) findViewById(R.id.mapView);
        binding.frame.setVisibility(View.VISIBLE);
        mMapView.setVisibility(View.VISIBLE);
        mBaiduMap=mMapView.getMap();
        //定位初始化为武汉大学行政楼
        LatLng ll = new LatLng(30.543803317144, 114.37292090919);
        float zoom=16;
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
        mBaiduMap.setMapStatus(u);
        mBaiduMap.animateMapStatus(u);
        mPoiSearch.setOnGetPoiSearchResultListener(listener1);
        mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                .poiUids(Uid));
        Srch_Info_details_Activity.MyLocationListener myLocationListener = new Srch_Info_details_Activity.MyLocationListener();
        //定位监听初始化
        mLocationClient = new LocationClient(this);
        //获取实时定位
        baidumap.getLocation3(mBaiduMap,mLocationClient,myLocationListener);
        //配置地图
        baidumap.configMap(mBaiduMap, MyLocationConfiguration.LocationMode.NORMAL,true, BitmapDescriptorFactory.fromResource(R.drawable.location),0x55FFFFFF,0x55FFFFFF);
        //禁止旋转手势
        UiSettings mUiSettings=mBaiduMap.getUiSettings();
        mUiSettings.setRotateGesturesEnabled(false);
        if(first==1)
            Toast.makeText(Srch_Info_details_Activity.this,"正在获取实时位置，请稍候...",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Srch_Info_details_Activity.this,"实时位置获取成功!",Toast.LENGTH_SHORT).show();
                first=0;
                binding.walkGuide.setVisibility(View.VISIBLE);
                binding.bikeGuide.setVisibility(View.VISIBLE);
                binding.Guide.setVisibility(View.VISIBLE);
                binding.busGuide.setVisibility(View.VISIBLE);
            }
        }
    }

    private void startWalkNavigation(){
        Intent i1 = new Intent();

        // 步行路线规划

        i1.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+String.valueOf(latitude)+","+String.valueOf(longitude)
                +"&destination=name:"+name+"|latlng:"+String.valueOf(endPt.latitude)+","+String.valueOf(endPt.longitude)
                +"&coord_type=bd09ll&mode=walking&src=andr.baidu.openAPIdemo"));
        /*i1.setData(Uri.parse("baidumap://map/direction?region=beijing&origin=39.98871,116.43234&destination=40.057406655722,116.2964407172&coord_type=bd09ll&mode=walking&src=andr.baidu.openAPIdemo"));*/

        startActivity(i1);
       }

    private void startBikeNavigation(){
        Intent i1 = new Intent();

        // 骑行路线规划

        i1.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+String.valueOf(latitude)+","+String.valueOf(longitude)
                +"&destination=name:"+name+"|latlng:"+String.valueOf(endPt.latitude)+","+String.valueOf(endPt.longitude)
                +"&coord_type=bd09ll&mode=riding&src=andr.baidu.openAPIdemo"));

        startActivity(i1);
    }

    private void startBusNavigation(){
        Intent i1 = new Intent();

        // 公交路线规划

        i1.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+String.valueOf(latitude)+","+String.valueOf(longitude)+
                "&destination="+name+"|latlng:"+String.valueOf(endPt.latitude)+","+String.valueOf(endPt.longitude)+
                "&coord_type=bd09ll&mode=transit&target=1&src=andr.baidu.openAPIdemo"));


        startActivity(i1);
    }

    private void startNavigation(){
        Intent i1 = new Intent();

        // 驾车路线规划

        i1.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+String.valueOf(latitude)+","+String.valueOf(longitude)
                +"&destination=name:"+name+"|latlng:"+String.valueOf(endPt.latitude)+","+String.valueOf(endPt.longitude)
                +"&coord_type=bd09ll&mode=driving&src=andr.baidu.openAPIdemo"));

        startActivity(i1);
    }

    OnGetPoiSearchResultListener listener1 = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            //搜索结果信息获取
            int size=poiDetailSearchResult.getPoiDetailInfoList().size();
            if(size>0){
                Button button1 = new Button(getApplicationContext());
                Button button2 = new Button(getApplicationContext());
                //定位初始化为活动地点
                LatLng ll = poiDetailSearchResult.getPoiDetailInfoList().get(0).getLocation();
                float zoom=16;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
                mBaiduMap.setMapStatus(u);
                mBaiduMap.animateMapStatus(u);
                baidumap.setMark(ll,mBaiduMap);
                name=poiDetailSearchResult.getPoiDetailInfoList().get(0).getName();
                address=poiDetailSearchResult.getPoiDetailInfoList().get(0).getAddress();
                endPt=poiDetailSearchResult.getPoiDetailInfoList().get(0).getLocation();
                baidumap.setInfoWindow(mBaiduMap,button1,button2,ll,name,address,false);
            }
            else
                Toast.makeText(Srch_Info_details_Activity.this,"地址解析失败!",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };

    @Override
    protected void initData() {
        super.initData();
        Intent intent = getIntent();
        String owner=intent.getStringExtra("owner");
        int id= intent.getIntExtra("id",0);
        int self=intent.getIntExtra("self",0);
        if(self==0){
            binding.answer.setVisibility(View.VISIBLE);
        }
        dbHelper = new DB_USER(this, "User.db", null, 7);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Info> info = DataSupport.where("id=?",String.valueOf(id)).find(Info.class);
        for(int i=0;i<info.size();i++){
            form=info.get(i).getForm();
            binding.sendDate.setText("发布于"+(info.get(i).getSend_date()));
            binding.form.setText("信息类别："+(info.get(i).getForm()==1?"私人性-学术咨询信息":info.get(i).getForm()==2?"私人性-日常求助信息":info.get(i).getForm()==3?"私人性-物品出售信息":info.get(i).getForm()==4?"私人性-物品求购信息":info.get(i).getForm()==5?"组织性活动信息":"课程点评信息"));
            binding.detail.setText("具体内容：\n    "+(info.get(i).getDetail()));
            binding.nickname.setText(owner);
            if(info.get(i).getPicture4()!=0){
                List<Picture> picture4=DataSupport.where("id=?",String.valueOf(info.get(i).getPicture4())).find(Picture.class);
                byte[] in_4 = picture4.get(0).getPicture();
                Bitmap bit_4 = BitmapFactory.decodeByteArray(in_4, 0, in_4.length);
                binding.picture4.setImageBitmap(bit_4);
                binding.picture4.setVisibility(View.VISIBLE);
                binding.picture4.setOnClickListener(v->{
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_4,true);
                });
            }
            if(info.get(i).getPicture3()!=0){
                List<Picture> picture3=DataSupport.where("id=?",String.valueOf(info.get(i).getPicture3())).find(Picture.class);
                byte[] in_3 = picture3.get(0).getPicture();
                Bitmap bit_3 = BitmapFactory.decodeByteArray(in_3, 0, in_3.length);
                binding.picture3.setImageBitmap(bit_3);
                binding.picture3.setVisibility(View.VISIBLE);
                binding.picture3.setOnClickListener(v->{
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_3,true);
                });
            }
            if(info.get(i).getPicture2()!=0){
                List<Picture> picture2=DataSupport.where("id=?",String.valueOf(info.get(i).getPicture2())).find(Picture.class);
                byte[] in_2 = picture2.get(0).getPicture();
                Bitmap bit_2 = BitmapFactory.decodeByteArray(in_2, 0, in_2.length);
                binding.picture2.setImageBitmap(bit_2);
                binding.picture2.setVisibility(View.VISIBLE);
                binding.picture2.setOnClickListener(v->{
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_2,true);
                });
            }
            if(info.get(i).getPicture1()!=0){
                List<Picture> picture1=DataSupport.where("id=?",String.valueOf(info.get(i).getPicture1())).find(Picture.class);
                byte[] in_1 = picture1.get(0).getPicture();
                Bitmap bit_1 = BitmapFactory.decodeByteArray(in_1, 0, in_1.length);
                binding.picture1.setImageBitmap(bit_1);
                binding.picture1.setVisibility(View.VISIBLE);
                binding.picture1.setOnClickListener(v->{
                    EnlargePicture enlargePicture=new EnlargePicture();
                    enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_1,true);
                });
            }
            int owner_id=info.get(i).getOwner_id();
            Cursor cursor = db.rawQuery("select picture from User where id=?", new String[]{String.valueOf(owner_id)}, null);
            if(cursor.moveToFirst()){
                if (cursor.getCount() != 0) {
                    byte[] in = cursor.getBlob(cursor.getColumnIndex("picture"));
                    Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
                    binding.picture.setImageBitmap(bit);
                }
            }
            cursor.close();
            if(info.get(i).getAnswered()==0) binding.answer.setText("响应信息\n(你是第一个!)");
            else binding.answer.setText("继续响应");
            form=info.get(i).getForm();
            switch(info.get(i).getForm()){
                case 1:{
                    binding.fdForm.setText("咨询领域："+(
                            info.get(i).getFd_form()==1?"哲学":
                            info.get(i).getFd_form()==2?"经济学":
                            info.get(i).getFd_form()==3?"法学":
                            info.get(i).getFd_form()==4?"文学":
                            info.get(i).getFd_form()==5?"历史学":
                            info.get(i).getFd_form()==6?"理学":
                            info.get(i).getFd_form()==7?"工学":
                            info.get(i).getFd_form()==8?"农学":
                            info.get(i).getFd_form()==9?"医学":
                            info.get(i).getFd_form()==10?"管理学":
                            info.get(i).getFd_form()==11?"教育学":"艺术学")
                    );
                    binding.fdForm.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(info.get(i).getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(info.get(i).getAnswered()==0?"暂未被响应":"已被响应"));
                    break;
                }
                case 2:{
                    binding.helpForm.setText("求助问题："+(
                            info.get(i).getHelp_form()==1?"代取物品":
                            info.get(i).getHelp_form()==2?"信息求问":
                            info.get(i).getHelp_form()==3?"寻物启事":
                            info.get(i).getHelp_form()==4?"失物招领":"其他")
                    );
                    binding.helpForm.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(info.get(i).getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(info.get(i).getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 3:
                case 4:{
                    binding.price.setText("预期价格："+(String.valueOf(info.get(i).getPrice()))+"元");
                    binding.price.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(info.get(i).getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 5:{
                    binding.date.setText("活动日期："+info.get(i).getDate());
                    binding.date.setVisibility(View.VISIBLE);
                    binding.place.setText("活动地点："+info.get(i).getPlace());
                    binding.place.setVisibility(View.VISIBLE);
                    binding.placeDetail.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(info.get(i).getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(info.get(i).getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 6:{
                    binding.lesson.setText("课程名称："+info.get(i).getLesson());
                    binding.lesson.setVisibility(View.VISIBLE);
                    binding.score.setText("评分："+String.valueOf(info.get(i).getScore())+"分");
                    binding.score.setVisibility(View.VISIBLE);
                    binding.answer.setVisibility(View.GONE);
                    break;
                }
                default:
            }
        }
    }

    @Override
    protected void initClick() {
        super.initClick();
        Intent intent=getIntent();
        int id=intent.getIntExtra("id",0);
        int sub_id=intent.getIntExtra("locid",0);
        int obj_id=intent.getIntExtra("ownerid",0);
        binding.answer.setOnClickListener(v->{
            Info info=new Info();
            info.setAnswered(1);
            info.updateAll("id=?",String.valueOf(id));
            Intent intent1 = new Intent(Srch_Info_details_Activity.this,Chat_Window_Activity.class);
            intent1.putExtra("sub_id",sub_id);
            intent1.putExtra("obj_id",obj_id);
            startActivity(intent1);
        });
        binding.placeDetail.setOnClickListener(v -> {
            List<Info> info = DataSupport.where("id=?",String.valueOf(id)).find(Info.class);
            String Uid=info.get(0).getPlaceId();
            if(Uid.equals("0"))
                Toast.makeText(Srch_Info_details_Activity.this,"地址信息获取失败!",Toast.LENGTH_SHORT).show();
            else
                initMap(Uid);
        });
        binding.walkGuide.setOnClickListener(v -> {
            startWalkNavigation();
        });
        binding.bikeGuide.setOnClickListener(v -> {
            startBikeNavigation();
        });
        binding.Guide.setOnClickListener(v -> {
            startNavigation();
        });
        binding.busGuide.setOnClickListener(v -> {
            startBusNavigation();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        if(form==5&&first==0){
            mMapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(form==5&&first==0){
            mMapView.onPause();
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

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget() {
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("搜索-信息详情");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}