package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.whuinfoplatform.Dao.InfoConnection;
import com.example.whuinfoplatform.Dao.PictureConnection;
import com.example.whuinfoplatform.Dao.UserConnection;
import com.example.whuinfoplatform.Entity.ActivityCollector;
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.BaiDuMap;
import com.example.whuinfoplatform.Entity.EnlargePicture;
import com.example.whuinfoplatform.Entity.LocalPicture;
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class Srch_Info_details_Activity extends rootActivity{
    private com.example.whuinfoplatform.databinding.ActivitySrchInfoDetailsBinding binding;
    private int form=0,first=1;
    private String owner,name,address;
    private MapView mMapView=null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private BaiDuMap baidumap=new BaiDuMap();
    private PoiSearch mPoiSearch=PoiSearch.newInstance();
    private LatLng endPt;
    private double latitude,longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView(){
        binding=com.example.whuinfoplatform.databinding.ActivitySrchInfoDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void BaiDuAPPNotExist(){
        //BToast.showText(Srch_Info_details_Activity.this,"若要使用导航功能，请先下载百度地图客户端",false);
        AlertDialog.Builder dialog=new AlertDialog.Builder(Srch_Info_details_Activity.this);
        dialog.setTitle("未安装应用");
        dialog.setMessage("若要使用导航功能，请先下载百度地图客户端");
        dialog.setCancelable(true);
        dialog.setPositiveButton("现在安装",(dialog1,which)->{
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://na.mbd.baidu.com/r/qLuvZrymZy"));
            startActivity(intent);
        });
        dialog.setNegativeButton("不，我再想想",(dialog12,which)->{});
        dialog.show();
    }

    private void initMap(String Uid){
        runOnUiThread(()->{
            mMapView=findViewById(R.id.mapView);
            binding.frame.setVisibility(View.VISIBLE);
            mMapView.setVisibility(View.VISIBLE);
            mBaiduMap=mMapView.getMap();
            //定位初始化为武汉大学行政楼
            LatLng ll=new LatLng(30.543803317144,114.37292090919);
            float zoom=16;
            MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
            mBaiduMap.setMapStatus(u);
            mBaiduMap.animateMapStatus(u);
            mPoiSearch.setOnGetPoiSearchResultListener(listener1);
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUids(Uid));
            MyLocationListener myLocationListener=new MyLocationListener();
            //定位监听初始化
            mLocationClient=new LocationClient(Srch_Info_details_Activity.this);
            //获取实时定位
            baidumap.getLocation3(mBaiduMap,mLocationClient,myLocationListener);
            //配置地图
            baidumap.configMap(mBaiduMap, MyLocationConfiguration.LocationMode.NORMAL,true,BitmapDescriptorFactory.fromResource(R.drawable.location),0x55FFFFFF,0x55FFFFFF);
            //禁止旋转手势
            UiSettings mUiSettings=mBaiduMap.getUiSettings();
            mUiSettings.setRotateGesturesEnabled(false);
            if(first==1)
                BToast.showText(Srch_Info_details_Activity.this,"正在获取实时位置，请稍候...");
        });
    }


    public class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location){
            //mapView 销毁后不再处理新接收的位置
            if(location==null||mMapView==null){
                return;
            }
            MyLocationData locData=new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            latitude=location.getLatitude();    //获取纬度信息
            longitude=location.getLongitude();    //获取经度信息
            if((latitude>=1||longitude>=1)&&first==1){
                LatLng ll=new LatLng(latitude, longitude);
                //初始化中心点为实时位置,设初始缩放程度为17
                float zoom=17;
                MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
                mBaiduMap.setMapStatus(u);
                mBaiduMap.animateMapStatus(u);
                BToast.showText(Srch_Info_details_Activity.this,"实时位置获取成功!",true);
                first=0;
                binding.walkGuide.setVisibility(View.VISIBLE);
                binding.bikeGuide.setVisibility(View.VISIBLE);
                binding.Guide.setVisibility(View.VISIBLE);
                binding.busGuide.setVisibility(View.VISIBLE);
            }
        }
    }

    private void startWalkNavigation() throws ActivityNotFoundException{
        try{
            Intent i1=new Intent();
            // 步行路线规划
            i1.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+String.valueOf(latitude)+","+String.valueOf(longitude)
                    +"&destination=name:"+name+"|latlng:"+String.valueOf(endPt.latitude)+","+String.valueOf(endPt.longitude)
                    +"&coord_type=bd09ll&mode=walking&src=andr.baidu.openAPIdemo"));
            startActivity(i1);
        }catch(Exception e){
            e.printStackTrace();
            BaiDuAPPNotExist();
        }
    }

    private void startBikeNavigation() throws ActivityNotFoundException{
        try{
            Intent i1=new Intent();
            // 骑行路线规划
            i1.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+String.valueOf(latitude)+","+String.valueOf(longitude)
                +"&destination=name:"+name+"|latlng:"+String.valueOf(endPt.latitude)+","+String.valueOf(endPt.longitude)
                +"&coord_type=bd09ll&mode=riding&src=andr.baidu.openAPIdemo"));
                startActivity(i1);
        }catch(Exception e){
            e.printStackTrace();
            BaiDuAPPNotExist();
        }
    }

    private void startBusNavigation() throws ActivityNotFoundException{
        try{
            Intent i1=new Intent();
            // 公交路线规划
            i1.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+String.valueOf(latitude)+","+String.valueOf(longitude)+
                    "&destination="+name+"|latlng:"+String.valueOf(endPt.latitude)+","+String.valueOf(endPt.longitude)+
                    "&coord_type=bd09ll&mode=transit&target=1&src=andr.baidu.openAPIdemo"));
            startActivity(i1);
        }catch(Exception e){
            e.printStackTrace();
            BaiDuAPPNotExist();
        }
    }

    private void startNavigation() throws ActivityNotFoundException{
        try{
            Intent i1=new Intent();
            // 驾车路线规划
            i1.setData(Uri.parse("baidumap://map/direction?origin=name:我的位置|latlng:"+String.valueOf(latitude)+","+String.valueOf(longitude)
                    +"&destination=name:"+name+"|latlng:"+String.valueOf(endPt.latitude)+","+String.valueOf(endPt.longitude)
                    +"&coord_type=bd09ll&mode=driving&src=andr.baidu.openAPIdemo"));
            startActivity(i1);
        }catch(Exception e){
            e.printStackTrace();
            BaiDuAPPNotExist();
        }
    }

    OnGetPoiSearchResultListener listener1=new OnGetPoiSearchResultListener(){
        @Override
        public void onGetPoiResult(PoiResult poiResult){}

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult){
            //搜索结果信息获取
            if(!(poiDetailSearchResult.getPoiDetailInfoList()==null)){
                Button button1=new Button(getApplicationContext());
                Button button2=new Button(getApplicationContext());
                //定位初始化为活动地点
                LatLng ll=poiDetailSearchResult.getPoiDetailInfoList().get(0).getLocation();
                float zoom=16;
                MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
                mBaiduMap.setMapStatus(u);
                mBaiduMap.animateMapStatus(u);
                baidumap.setMark(ll,mBaiduMap);
                name=poiDetailSearchResult.getPoiDetailInfoList().get(0).getName();
                address=poiDetailSearchResult.getPoiDetailInfoList().get(0).getAddress();
                endPt=poiDetailSearchResult.getPoiDetailInfoList().get(0).getLocation();
                baidumap.setInfoWindow(mBaiduMap,button1,button2,ll,name,address,false);
            }
            else
                BToast.showText(Srch_Info_details_Activity.this,"地址解析失败!",false);
        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult){}
        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult){}
    };

    @Override
    protected void initData(){
        super.initData();
        Intent intent=getIntent();
        owner=intent.getStringExtra("owner");
        int id=intent.getIntExtra("id",0);
        int self=intent.getIntExtra("self",0);
        if(self==0){
            binding.answer.setVisibility(View.VISIBLE);
        }
        else{
            owner+="(我)";
        }

        InfoConnection infoConnection=new InfoConnection();
        infoConnection.queryInfoByIdConnection(String.valueOf(id),new okhttp3.Callback(){
            @Override
            public void onFailure(Call call,IOException e){
                Looper.prepare();
                BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                Looper.loop();
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException{
                String result=response.body().string();
                Info myInformation=new Info();
                try {
                    infoConnection.parseJSONForMyInfoDetailResponse(myInformation,result);
                    showInfoDetails(myInformation,owner);
                }catch(JSONException e){
                    e.printStackTrace();
                    Looper.prepare();
                    BToast.showText(Srch_Info_details_Activity.this,"数据解析失败！",false);
                    Looper.loop();
                }
            }
        });
    }

    @Override
    protected void initClick(){
        super.initClick();
        Intent intent=getIntent();
        int id=intent.getIntExtra("id",0);
        int sub_id=intent.getIntExtra("locid",0);
        int obj_id=intent.getIntExtra("ownerid",0);
        binding.answer.setOnClickListener(v->{

            AlertDialog.Builder dialog=new AlertDialog.Builder(Srch_Info_details_Activity.this);
            dialog.setTitle("响应本信息");
            dialog.setMessage("信息发布者将会收到您的信息。\n确定响应？");
            dialog.setCancelable(false);
            dialog.setNegativeButton("不，我再想想",(dialog12,which)->{});
            dialog.setPositiveButton("是",(dialog1,which)->{
                InfoConnection infoConnection=new InfoConnection();
                infoConnection.answerInfoConnection(String.valueOf(id),new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call,IOException e){
                        Looper.prepare();
                        BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call,Response response) throws IOException{
                        String result=response.body().string();
                        try{
                            JSONObject jsonObject=new JSONObject(result);
                            if(jsonObject.getInt("code")==101){
                                Intent intent1=new Intent(Srch_Info_details_Activity.this,Chat_Window_Activity.class);
                                intent1.putExtra("sub_id",sub_id);
                                intent1.putExtra("obj_id",obj_id);
                                intent1.putExtra("nickname",owner);
                                startActivity(intent1);
                            }
                            else{
                                Looper.prepare();
                                BToast.showText(Srch_Info_details_Activity.this,jsonObject.getString("response"),false);
                                Looper.loop();
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                            Looper.prepare();
                            BToast.showText(Srch_Info_details_Activity.this,"数据解析失败！",false);
                            Looper.loop();
                        }
                    }
                });
            });
            dialog.show();
        });
        binding.placeDetail.setOnClickListener(v->{
            //List<Info> info = DataSupport.where("id=?",String.valueOf(id)).find(Info.class);
            InfoConnection infoConnection=new InfoConnection();
            infoConnection.queryInfoByIdConnection(String.valueOf(id),new okhttp3.Callback(){
                @Override
                public void onFailure(Call call,IOException e){
                    Looper.prepare();
                    BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                    Looper.loop();
                }

                @Override
                public void onResponse(Call call,Response response) throws IOException{
                    String result=response.body().string();
                    try{
                        JSONObject jsonObject=new JSONObject(result);
                        String Uid=jsonObject.getString("placeId");
                        if(jsonObject.getInt("code")!=101){
                            Looper.prepare();
                            BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }
                        else
                            initMap(Uid);
                    }catch(JSONException e){
                        e.printStackTrace();
                        Looper.prepare();
                        BToast.showText(Srch_Info_details_Activity.this,"数据解析失败！",false);
                        Looper.loop();
                    }
                }
            });
        });
        binding.walkGuide.setOnClickListener(v->startWalkNavigation());
        binding.bikeGuide.setOnClickListener(v->startBikeNavigation());
        binding.Guide.setOnClickListener(v->startNavigation());
        binding.busGuide.setOnClickListener(v->startBusNavigation());
    }

    @Override
    protected void onResume(){
        super.onResume();
        initData();
        if(form==5&&first==0){
            mMapView.onResume();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(form==5&&first==0){
            mMapView.onPause();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(form==5&&first==0){
            mLocationClient.stop();
            mBaiduMap.setMyLocationEnabled(false);
            mMapView.onDestroy();
            mPoiSearch.destroy();
            mMapView=null;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showInfoDetails(Info myInformation, String owner){
        runOnUiThread(() -> {
            form=myInformation.getForm();
            binding.sendDate.setText("发布于"+(myInformation.getSend_date()));
            binding.form.setText("信息类别："+(myInformation.getForm()==1?"私人性-学术咨询信息":myInformation.getForm()==2?"私人性-日常求助信息":myInformation.getForm()==3?"私人性-物品出售信息":myInformation.getForm()==4?"私人性-物品求购信息":myInformation.getForm()==5?"组织性活动信息":"课程点评信息"));
            binding.detail.setText("具体内容：\n    "+(myInformation.getDetail()));
            binding.nickname.setText(owner);
            if(myInformation.getPicture4()!=0){
                binding.picture4.setVisibility(View.VISIBLE);
                binding.picture4.setImageResource(R.drawable.downloading);
                List<LocalPicture> picture4=DataSupport.where("code=?",String.valueOf(myInformation.getPicture4())).find(LocalPicture.class);
                if(picture4.size()!=0){
                    byte[] in_4 = Base64.getDecoder().decode(picture4.get(0).getPicture());
                    Bitmap bit_4 = BitmapFactory.decodeByteArray(in_4, 0, in_4.length);
                    binding.picture4.setImageBitmap(bit_4);
                    binding.picture4.setOnClickListener(v->{
                        EnlargePicture enlargePicture=new EnlargePicture();
                        enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_4,true);
                    });
                }
                else{
                    PictureConnection pictureConnection=new PictureConnection();
                    pictureConnection.initDownloadConnection(String.valueOf(myInformation.getPicture4()), new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            setNullImage(binding.picture4);
                            BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            LocalPicture picture=new LocalPicture();
                            pictureConnection.parseJSONForDownloadPictureResponse(picture,result);
                            if(picture.getCode()==101){
                                byte[] in_4=Base64.getDecoder().decode(picture.getPicture());
                                setImage(binding.picture4,in_4);
                                picture.infoPictureAddToLocal(myInformation.getPicture4(),picture.getPicture());
                            }
                            else{
                                Looper.prepare();
                                setNullImage(binding.picture4);
                                BToast.showText(Srch_Info_details_Activity.this,picture.getResponse(),false);
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            if(myInformation.getPicture3()!=0){
                binding.picture3.setVisibility(View.VISIBLE);
                binding.picture3.setImageResource(R.drawable.downloading);
                List<LocalPicture> picture3=DataSupport.where("code=?",String.valueOf(myInformation.getPicture3())).find(LocalPicture.class);
                if(picture3.size()!=0){
                    byte[] in_3 = Base64.getDecoder().decode(picture3.get(0).getPicture());
                    Bitmap bit_3 = BitmapFactory.decodeByteArray(in_3, 0, in_3.length);
                    binding.picture3.setImageBitmap(bit_3);
                    binding.picture3.setOnClickListener(v->{
                        EnlargePicture enlargePicture=new EnlargePicture();
                        enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_3,true);
                    });
                }
                else{
                    PictureConnection pictureConnection=new PictureConnection();
                    pictureConnection.initDownloadConnection(String.valueOf(myInformation.getPicture3()), new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            setNullImage(binding.picture3);
                            BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            LocalPicture picture=new LocalPicture();
                            pictureConnection.parseJSONForDownloadPictureResponse(picture,result);
                            if(picture.getCode()==101){
                                byte[] in_3=Base64.getDecoder().decode(picture.getPicture());
                                setImage(binding.picture3,in_3);
                                picture.infoPictureAddToLocal(myInformation.getPicture3(),picture.getPicture());
                            }
                            else{
                                Looper.prepare();
                                setNullImage(binding.picture3);
                                BToast.showText(Srch_Info_details_Activity.this,picture.getResponse(),false);
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            if(myInformation.getPicture2()!=0){
                binding.picture2.setVisibility(View.VISIBLE);
                binding.picture2.setImageResource(R.drawable.downloading);
                List<LocalPicture> picture2=DataSupport.where("code=?",String.valueOf(myInformation.getPicture2())).find(LocalPicture.class);
                if(picture2.size()!=0){
                    byte[] in_2 = Base64.getDecoder().decode(picture2.get(0).getPicture());
                    Bitmap bit_2 = BitmapFactory.decodeByteArray(in_2, 0, in_2.length);
                    binding.picture2.setImageBitmap(bit_2);
                    binding.picture2.setOnClickListener(v->{
                        EnlargePicture enlargePicture=new EnlargePicture();
                        enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_2,true);
                    });
                }
                else{
                    PictureConnection pictureConnection=new PictureConnection();
                    pictureConnection.initDownloadConnection(String.valueOf(myInformation.getPicture2()), new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            setNullImage(binding.picture2);
                            BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            LocalPicture picture=new LocalPicture();
                            pictureConnection.parseJSONForDownloadPictureResponse(picture,result);
                            if(picture.getCode()==101){
                                byte[] in_2=Base64.getDecoder().decode(picture.getPicture());
                                setImage(binding.picture2,in_2);
                                picture.infoPictureAddToLocal(myInformation.getPicture2(),picture.getPicture());
                            }
                            else{
                                Looper.prepare();
                                setNullImage(binding.picture2);
                                BToast.showText(Srch_Info_details_Activity.this,picture.getResponse(),false);
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            if(myInformation.getPicture1()!=0){
                binding.picture1.setVisibility(View.VISIBLE);
                binding.picture1.setImageResource(R.drawable.downloading);
                List<LocalPicture> picture1=DataSupport.where("code=?",String.valueOf(myInformation.getPicture1())).find(LocalPicture.class);
                if(picture1.size()!=0){
                    byte[] in_1 = Base64.getDecoder().decode(picture1.get(0).getPicture());
                    Bitmap bit_1 = BitmapFactory.decodeByteArray(in_1, 0, in_1.length);
                    binding.picture1.setImageBitmap(bit_1);
                    binding.picture1.setOnClickListener(v->{
                        EnlargePicture enlargePicture=new EnlargePicture();
                        enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit_1,true);
                    });
                }
                else{
                    PictureConnection pictureConnection=new PictureConnection();
                    pictureConnection.initDownloadConnection(String.valueOf(myInformation.getPicture1()), new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Looper.prepare();
                            setNullImage(binding.picture1);
                            BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                            Looper.loop();
                        }

                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            LocalPicture picture=new LocalPicture();
                            pictureConnection.parseJSONForDownloadPictureResponse(picture,result);
                            if(picture.getCode()==101){
                                byte[] in_1=Base64.getDecoder().decode(picture.getPicture());
                                setImage(binding.picture1,in_1);
                                picture.infoPictureAddToLocal(myInformation.getPicture1(),picture.getPicture());
                            }
                            else{
                                Looper.prepare();
                                setNullImage(binding.picture1);
                                BToast.showText(Srch_Info_details_Activity.this,picture.getResponse(),false);
                                Looper.loop();
                            }
                        }
                    });
                }
            }
            int owner_id=myInformation.getOwner_id();
            List<LocalPicture> localPictures=DataSupport.where("user_code=?",String.valueOf(owner_id)).find(LocalPicture.class);
            if(localPictures.size()!=0){
                String picture=localPictures.get(0).getPicture();
                showUserPicture(picture);
            }
            else{
                UserConnection userConnection=new UserConnection();
                userConnection.queryUserInfo(String.valueOf(owner_id), new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        BToast.showText(Srch_Info_details_Activity.this,"服务器连接失败，请检查网络设置",false);
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result=response.body().string();
                        try {
                            JSONObject jsonObject=new JSONObject(result);
                            String picture=jsonObject.getString("picture");
                            LocalPicture localPicture=new LocalPicture();
                            localPicture.userPictureAddToLocal(owner_id,picture,jsonObject.getInt("picture_version"));
                            showUserPicture(picture);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Looper.prepare();
                            BToast.showText(Srch_Info_details_Activity.this,"数据解析失败！",false);
                            Looper.loop();
                        }
                    }
                });
            }
            if(myInformation.getAnswered()==0) binding.answer.setText("响应信息\n(你是第一个!)");
            else binding.answer.setText("继续响应");
            form=myInformation.getForm();
            switch(myInformation.getForm()){
                case 1:{
                    binding.fdForm.setText("咨询领域："+(
                            myInformation.getFd_form()==1?"哲学":
                            myInformation.getFd_form()==2?"经济学":
                            myInformation.getFd_form()==3?"法学":
                            myInformation.getFd_form()==4?"文学":
                            myInformation.getFd_form()==5?"历史学":
                            myInformation.getFd_form()==6?"理学":
                            myInformation.getFd_form()==7?"工学":
                            myInformation.getFd_form()==8?"农学":
                            myInformation.getFd_form()==9?"医学":
                            myInformation.getFd_form()==10?"管理学":
                            myInformation.getFd_form()==11?"教育学":"艺术学")
                    );
                    binding.fdForm.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(myInformation.getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(myInformation.getAnswered()==0?"暂未被响应":"已被响应"));
                    break;
                }
                case 2:{
                    binding.helpForm.setText("求助问题："+(
                            myInformation.getHelp_form()==1?"代取物品":
                            myInformation.getHelp_form()==2?"信息求问":
                            myInformation.getHelp_form()==3?"寻物启事":
                            myInformation.getHelp_form()==4?"失物招领":"其他")
                    );
                    binding.helpForm.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(myInformation.getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(myInformation.getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 3:
                case 4:{
                    binding.price.setText("预期价格："+(String.valueOf(myInformation.getPrice()))+"元");
                    binding.price.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(myInformation.getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 5:{
                    binding.date.setText("活动日期："+myInformation.getDate());
                    binding.date.setVisibility(View.VISIBLE);
                    binding.place.setText("活动地点："+myInformation.getPlace());
                    binding.place.setVisibility(View.VISIBLE);
                    binding.placeDetail.setVisibility(View.VISIBLE);
                    binding.reward.setText("报酬："+(String.valueOf(myInformation.getReward()))+"元");
                    binding.reward.setVisibility(View.VISIBLE);
                    binding.answered.setVisibility(View.VISIBLE);
                    binding.answered.setText("响应情况："+(myInformation.getAnswered()==0?"暂无响应":"已被响应"));
                    break;
                }
                case 6:{
                    binding.lesson.setText("课程名称："+myInformation.getLesson());
                    binding.lesson.setVisibility(View.VISIBLE);
                    binding.score.setText("评分："+String.valueOf(myInformation.getScore())+"分");
                    binding.score.setVisibility(View.VISIBLE);
                    binding.answer.setVisibility(View.GONE);
                    break;
                }
                default:
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showUserPicture(String picture){
        runOnUiThread(() -> {
            byte[] in = Base64.getDecoder().decode(picture);
            Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
            binding.picture.setImageBitmap(bit);
        });
    }

    private void setNullImage(ImageView imageView){
        runOnUiThread(() -> {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.download_failed);
            imageView.setClickable(false);
        });
    }

    private void setImage(ImageView imageView,byte[] in){
        runOnUiThread(() -> {
            Bitmap bit = BitmapFactory.decodeByteArray(in, 0, in.length);
            imageView.setImageBitmap(bit);
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(v->{
                EnlargePicture enlargePicture=new EnlargePicture();
                enlargePicture.EnlargePicture(Srch_Info_details_Activity.this,bit,true);
            });
        });
    }
}