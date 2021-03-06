package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

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
import com.example.whuinfoplatform.Entity.BToast;
import com.example.whuinfoplatform.Entity.BaiDuMap;
import com.example.whuinfoplatform.Entity.LocalPicture;
import com.example.whuinfoplatform.Entity.SenseCheck;
import com.example.whuinfoplatform.Entity.WebResponse;
import com.example.whuinfoplatform.R;
import com.example.whuinfoplatform.databinding.ActivityPublishInfoPromoteBinding;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

public class Publish_Info_promote_Activity extends rootActivity{
    private ActivityPublishInfoPromoteBinding binding;
    private int id=0,id1=0,id2=0,pos_fd=0,pos_help=0,pos_score=0,form=-1,picture_count=0,i=1,first=1;
    private double latitude,longitude,reward=0,price=0;
    private Dialog mCameraDialog;
    private boolean upload=false;
    private ArrayList<Integer> pictureList=new ArrayList<>();
    private MapView mMapView=null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private BaiDuMap baidumap=new BaiDuMap();
    private PoiSearch mPoiSearch=PoiSearch.newInstance();
    private String name,address,placeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView(){
        binding=ActivityPublishInfoPromoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.calendar.setVisibility(View.GONE);
    }

    private void initMap(){
        mMapView=findViewById(R.id.mapView);
        binding.frame.setVisibility(View.VISIBLE);
        mMapView.setVisibility(View.VISIBLE);
        mBaiduMap=mMapView.getMap();
        if(first==0){
            binding.card.setVisibility(View.VISIBLE);
            mPoiSearch.setOnGetPoiSearchResultListener(listener1);
        }
        //???????????????????????????????????????
        LatLng ll=new LatLng(30.543803317144,114.37292090919);
        float zoom=16;
        MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
        mBaiduMap.setMapStatus(u);
        mBaiduMap.animateMapStatus(u);
        Publish_Info_promote_Activity.MyLocationListener myLocationListener=new Publish_Info_promote_Activity.MyLocationListener();
        //?????????????????????
        mLocationClient=new LocationClient(this);
        //??????????????????
        baidumap.getLocation(mBaiduMap,mLocationClient,myLocationListener);
        //????????????
        baidumap.configMap(mBaiduMap,MyLocationConfiguration.LocationMode.NORMAL,true,BitmapDescriptorFactory.fromResource(R.drawable.location),0x55FFFFFF,0x55FFFFFF);
        //??????????????????
        UiSettings mUiSettings=mBaiduMap.getUiSettings();
        mUiSettings.setRotateGesturesEnabled(false);
        if(first==1)
            BToast.showText(Publish_Info_promote_Activity.this,"????????????????????????????????????...");
    }

    public class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location){
            //mapView ???????????????????????????????????????
            if(location==null||mMapView==null){
                return;
            }
            MyLocationData locData=new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // ?????????????????????????????????????????????????????????0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            latitude = location.getLatitude();    //??????????????????
            longitude = location.getLongitude();    //??????????????????
            if((latitude>=1||longitude>=1)&&first==1){
                LatLng ll=new LatLng(latitude,longitude);
                //?????????????????????????????????,????????????????????????17
                float zoom=17;
                MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
                mBaiduMap.setMapStatus(u);
                mBaiduMap.animateMapStatus(u);
                BToast.showText(Publish_Info_promote_Activity.this,"????????????????????????!",true);
                first=0;
                //??????????????????????????????
                mBaiduMap.setOnMapClickListener(listener);
                binding.card.setVisibility(View.VISIBLE);
                mPoiSearch.setOnGetPoiSearchResultListener(listener1);
            }
        }
    }

    OnGetPoiSearchResultListener listener1=new OnGetPoiSearchResultListener(){
        @Override
        public void onGetPoiResult(PoiResult poiResult){
            //PoiInfo ???????????????????????????
            if(poiResult.getTotalPoiNum()>=5){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                PoiInfo poi2=poiResult.getAllPoi().get(2);
                PoiInfo poi3=poiResult.getAllPoi().get(3);
                PoiInfo poi4=poiResult.getAllPoi().get(4);
                //?????????5????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid+","+poi3.uid+","+poi4.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else if(poiResult.getTotalPoiNum()==4){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                PoiInfo poi2=poiResult.getAllPoi().get(2);
                PoiInfo poi3=poiResult.getAllPoi().get(3);
                //?????????5????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid+","+poi3.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else if(poiResult.getTotalPoiNum()==3){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                PoiInfo poi2=poiResult.getAllPoi().get(2);
                //?????????5????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else if(poiResult.getTotalPoiNum()==2){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                //?????????5????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else if(poiResult.getTotalPoiNum()==1){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                //?????????5????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else{
                BToast.showText(Publish_Info_promote_Activity.this,"???????????????\n???????????????????????????!",false);
                mBaiduMap.clear();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult){
            //????????????????????????
            int size=poiDetailSearchResult.getPoiDetailInfoList().size();
            if(size>0){
                Button button1=new Button(getApplicationContext());
                Button button2=new Button(getApplicationContext());
                name=poiDetailSearchResult.getPoiDetailInfoList().get(0).getName();
                address=poiDetailSearchResult.getPoiDetailInfoList().get(0).getAddress();
                placeId=poiDetailSearchResult.getPoiDetailInfoList().get(0).getUid();
                LatLng ll=new LatLng(poiDetailSearchResult.getPoiDetailInfoList().get(0).getLocation().latitude,
                        poiDetailSearchResult.getPoiDetailInfoList().get(0).getLocation().longitude);
                float zoom=18;
                MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
                mBaiduMap.setMapStatus(u);
                mBaiduMap.animateMapStatus(u);
                baidumap.setMark(ll,mBaiduMap);
                baidumap.setInfoWindow(mBaiduMap,button1,button2,ll,name,address,true);
                button1.setOnClickListener(v->{
                    mMapView.setVisibility(View.GONE);
                    binding.frame.setVisibility(View.GONE);
                    binding.card.setVisibility(View.GONE);
                    binding.editPlace.setText(name+" ["+address+"]");
                });
                button2.setOnClickListener(v->{
                    if(i<size){
                        name=poiDetailSearchResult.getPoiDetailInfoList().get(i).getName();
                        address=poiDetailSearchResult.getPoiDetailInfoList().get(i).getAddress();
                        placeId=poiDetailSearchResult.getPoiDetailInfoList().get(i).getUid();
                        LatLng ll_ex=new LatLng(poiDetailSearchResult.getPoiDetailInfoList().get(i).getLocation().latitude,
                                poiDetailSearchResult.getPoiDetailInfoList().get(i).getLocation().longitude);
                        float zoom_ex=18;
                        MapStatusUpdate u_ex=MapStatusUpdateFactory.newLatLngZoom(ll_ex,zoom_ex);
                        mBaiduMap.setMapStatus(u_ex);
                        mBaiduMap.animateMapStatus(u_ex);
                        baidumap.setMark(ll_ex,mBaiduMap);
                        baidumap.setInfoWindow(mBaiduMap,button1,button2,ll_ex,name,address,true);
                        i++;
                    }
                    else
                        BToast.showText(Publish_Info_promote_Activity.this,"?????????????????????\n??????????????????????????????",false);
                });
            }
        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult){}
        //??????
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult){}
    };

    BaiduMap.OnMapClickListener listener=new BaiduMap.OnMapClickListener(){
        @Override
        public void onMapClick(LatLng point){}

        @Override
        public void onMapPoiClick(MapPoi mapPoi){
            Button button1=new Button(getApplicationContext());
            Button button2=new Button(getApplicationContext());
            LatLng ll=new LatLng(mapPoi.getPosition().latitude,mapPoi.getPosition().longitude);
            //??????????????????????????????,????????????????????????17
            float zoom=18;
            MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
            mBaiduMap.setMapStatus(u);
            mBaiduMap.animateMapStatus(u);
            name=mapPoi.getName();
            placeId=mapPoi.getUid();
            baidumap.setMark(ll,mBaiduMap);
            baidumap.setInfoWindow(mBaiduMap,button1,button2,ll,name,"",false);
            button1.setOnClickListener(v->{
                mMapView.setVisibility(View.GONE);
                binding.frame.setVisibility(View.GONE);
                binding.card.setVisibility(View.GONE);
                binding.editPlace.setText(name);
            });
        }
    };

    @Override
    protected void initData(){
        super.initData();
        Intent intent=getIntent();
        id=intent.getIntExtra("id",0);
        binding.infoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                switch((int)id){
                    case 1:{//???????????????
                        id1=1;
                        binding.calendar.setVisibility(View.GONE);
                        binding.persInfoType.setVisibility(View.VISIBLE);
                        binding.editPlace.setVisibility(View.GONE);
                        binding.editDate.setVisibility(View.GONE);
                        binding.editScore.setVisibility(View.GONE);
                        binding.editCommobj.setVisibility(View.GONE);
                        binding.persInfoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                            @Override
                            public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                                switch ((int)id){
                                    case 1:{//????????????
                                        id2=1;
                                        binding.persInfoFdType.setVisibility(View.VISIBLE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editReward.setVisibility(View.VISIBLE);
                                        binding.editPrice.setVisibility(View.GONE);
                                        form=1;
                                        break;
                                    }
                                    case 2:{//????????????
                                        id2=2;
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.VISIBLE);
                                        binding.editReward.setVisibility(View.VISIBLE);
                                        binding.editPrice.setVisibility(View.GONE);
                                        form=2;
                                        break;
                                    }
                                    case 3:{//????????????
                                        id2=3;
                                        binding.persInfoFdType.setVisibility(View.GONE);
                                        binding.persInfoHelpType.setVisibility(View.GONE);
                                        binding.editPrice.setVisibility(View.VISIBLE);
                                        binding.editReward.setVisibility(View.GONE);
                                        form=3;
                                        break;
                                    }
                                    case 4:{//????????????
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
                            public void onNothingSelected(AdapterView<?> parent){}
                        });
                        break;
                    }
                    case 2:{//???????????????
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
                    case 3:{//????????????
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
                    default:{
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
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
    }

    @Override
    protected void initClick(){
        super.initClick();
        binding.persInfoFdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                pos_fd=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
        binding.persInfoHelpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                pos_help=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
        });
        binding.editScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                pos_score=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){}
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
                            BToast.showText(Publish_Info_promote_Activity.this,"???????????????!",false);
                        else if(reward==-1)
                            BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                        else {//????????????
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
                                BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                            else{
                                Publish("1",String.valueOf(pos_fd),"-1","-1","","","","-1",detail,String.valueOf(reward),"0");
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
                            BToast.showText(Publish_Info_promote_Activity.this,"???????????????!",false);
                        else if(reward==-1)
                            BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                        else {//????????????
                            String text=String.valueOf(reward);
                            int length=text.length();
                            int res=0;
                            boolean count=false;
                            for (int i=0;i<length;i++){
                                if (count)
                                    res++;
                                if (text.charAt(i)=='.'){
                                    count=true;
                                }
                            }
                            if (res>2||count&&res==0)
                                BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                            else {
                                Publish("2","-1",String.valueOf(pos_help),"-1","","","","-1",detail,String.valueOf(reward),"0");
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
                            BToast.showText(Publish_Info_promote_Activity.this,"???????????????!",false);
                        else if(price==-1)
                            BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                        else {//????????????
                            String text=String.valueOf(price);
                            int length=text.length();
                            int res=0;
                            boolean count=false;
                            for (int i=0;i<length;i++){
                                if (count)
                                    res++;
                                if (text.charAt(i)=='.'){
                                    count=true;
                                }
                            }
                            if (res>2||count&&res==0)
                                BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                            else {
                                Publish("3","-1","-1",String.valueOf(price),"","","","-1",detail,"-1","0");
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
                            BToast.showText(Publish_Info_promote_Activity.this,"???????????????!",false);
                        else if(price==-1)
                            BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                        else {//????????????
                            String text=String.valueOf(price);
                            int length=text.length();
                            int res=0;
                            boolean count=false;
                            for (int i=0;i<length;i++){
                                if (count)
                                    res++;
                                if (text.charAt(i)=='.'){
                                    count=true;
                                }
                            }
                            if (res>2||count&&res==0)
                                BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                            else {
                                Publish("4","-1","-1",String.valueOf(price),"","","","-1",detail,"-1","0");
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
                            BToast.showText(Publish_Info_promote_Activity.this,"???????????????!",false);
                        else if(reward==-1)
                            BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                        else {//????????????
                            String text=String.valueOf(reward);
                            int length=text.length();
                            int res=0;
                            boolean count=false;
                            for (int i=0;i<length;i++){
                                if (count)
                                    res++;
                                if (text.charAt(i)=='.'){
                                    count = true;
                                }
                            }
                            if (res>2||count&&res==0)
                                BToast.showText(Publish_Info_promote_Activity.this,"??????????????????!",false);
                            else {
                                Publish("5","-1","-1","-1",date,place,"","-1",detail,String.valueOf(reward),String.valueOf(placeId));
                            }
                        }
                        break;
                    }
                    case 6:{
                        String detail=binding.detail.getText().toString();
                        String lesson=binding.editCommobj.getText().toString();
                        if(detail.equals("")||lesson.equals("")||pos_score==0)
                            BToast.showText(Publish_Info_promote_Activity.this,"???????????????!",false);
                        else {
                            Publish("6","-1","-1","-1","","",lesson,String.valueOf(pos_score),detail,"-1","0");
                        }
                        break;
                    }
                    default:
                }
            }
            else
                BToast.showText(Publish_Info_promote_Activity.this,"???????????????!",false);
        });
        binding.editDate.setOnClickListener(v->{
            binding.calendar.setVisibility(View.VISIBLE);
            long timecurrentTimeMillis=System.currentTimeMillis();
            binding.calendar.setMinDate(timecurrentTimeMillis);
            if(!binding.editDate.getText().toString().equals("")){
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date current=simpleDateFormat.parse(binding.editDate.getText().toString());
                    long time=current.getTime();
                    binding.calendar.setDate(time);
                }catch(ParseException e){
                    e.printStackTrace();
                }
            }
        });
        binding.calendar.setOnDateChangeListener((view,year,month,dayOfMonth)->{
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
        });
        //??????????????????????????????
        binding.editPlace.setOnClickListener(v->{
            binding.calendar.setVisibility(View.GONE);
            initMap();
        });
        binding.detail.setOnClickListener(v->binding.calendar.setVisibility(View.GONE));
        binding.editReward.setOnClickListener(v->binding.calendar.setVisibility(View.GONE));
        binding.editPlace.setOnFocusChangeListener((v, hasFocus)->{
            if(hasFocus){
                binding.calendar.setVisibility(View.GONE);
            }
        });
        binding.detail.setOnFocusChangeListener((v,hasFocus)->{
            if(hasFocus){
                binding.calendar.setVisibility(View.GONE);
            }
        });
        binding.editReward.setOnFocusChangeListener((v,hasFocus)->{
            if(hasFocus){
                binding.calendar.setVisibility(View.GONE);
            }
        });
        //????????????????????????
        binding.upload.setOnClickListener(v->{
            if(picture_count>=4){//????????????????????????
                BToast.showText(Publish_Info_promote_Activity.this,"??????????????????????????????!",false);
            }
            else{
                upload=true;
                setDialog();
            }
        });
        binding.input.setOnEditorActionListener((v,actionId,event)->{
            if(actionId==EditorInfo.IME_ACTION_SEARCH){
                i=1;//???????????????
                String kwd=binding.input.getText().toString();
                SenseCheck senseCheck=new SenseCheck();
                if(senseCheck.SenseCheckAllBlankOrNull(kwd)){
                    /**
                     *  PoiCiySearchOption ??????????????????
                     *  city ????????????
                     *  keyword ?????????????????????
                     *  pageNum ????????????
                     */
                    mPoiSearch.searchInCity(new PoiCitySearchOption()
                    .city("??????") //??????
                    .keyword(kwd) //??????
                    .pageNum(0));
                }
                else{
                    binding.input.setText("");
                    BToast.showText(Publish_Info_promote_Activity.this,"?????????????????????????????????",false);
                }
                return true;
            }
            else
                return false;
        });
        binding.search.setOnClickListener(v->{
            i=1;//???????????????
            String kwd = binding.input.getText().toString();
            SenseCheck senseCheck=new SenseCheck();
            if(senseCheck.SenseCheckAllBlankOrNull(kwd)){
                /**
                 *  PoiCiySearchOption ??????????????????
                 *  city ????????????
                 *  keyword ?????????????????????
                 *  pageNum ????????????
                 */
                mPoiSearch.searchInCity(new PoiCitySearchOption()
                        .city("??????") //??????
                        .keyword(kwd) //??????
                        .pageNum(0));
            }
            else{
                binding.input.setText("");
                BToast.showText(Publish_Info_promote_Activity.this,"?????????????????????????????????",false);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget(){
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("????????????-???????????????");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    private void Publish(String form,String fd_form,String help_form,String price,String date,String place,String lesson,String score,String detail,String reward,String placeId){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss", Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        InfoConnection infoConnection=new InfoConnection();
        infoConnection.initRegisterConnection(String.valueOf(id),time,
                "0",form,fd_form,help_form,
                price,date,place,lesson,score,detail,reward,
                String.valueOf(ret_list_1()),String.valueOf(ret_list_2()),String.valueOf(ret_list_3())
                ,String.valueOf(ret_list_4()),placeId, new okhttp3.Callback(){
                    @Override
                    public void onFailure(Call call,IOException e){
                        Looper.prepare();
                        BToast.showText(Publish_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
                        Looper.loop();
                    }

                    @RequiresApi(api=Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call call,Response response) throws IOException{
                        String result=response.body().string();
                        WebResponse webResponse=new WebResponse();
                        infoConnection.parseJSONForInfoResponse(webResponse,result);
                        Looper.prepare();
                        if(webResponse.getCode()==101){
                            BToast.showText(Publish_Info_promote_Activity.this,"????????????!\n?????????[????????????]????????????",true);
                            Intent intent=new Intent(Publish_Info_promote_Activity.this,Info_Hall_Activity.class);
                            startActivity(intent);
                        }
                        else
                            BToast.showText(Publish_Info_promote_Activity.this,webResponse.getResponse(),false);
                        Looper.loop();
                    }
                }
        );
    }

    private void setDialog(){
        LinearLayout root=(LinearLayout)LayoutInflater.from(this).inflate(R.layout.bottom_dialog,null);
        //???????????????
        root.findViewById(R.id.btn_choose_img).setOnClickListener(v->{
            Intent intent=new Intent(Publish_Info_promote_Activity.this,Upload_Picture_promote_Activity.class);
            intent.putExtra("id",id);
            intent.putExtra("type",2);
            startActivity(intent);
        });
        root.findViewById(R.id.btn_open_camera).setOnClickListener(v->{
            Intent intent=new Intent(Publish_Info_promote_Activity.this,Upload_Picture_promote_Activity.class);
            intent.putExtra("id",id);
            intent.putExtra("type",3);
            startActivity(intent);
        });
        Button btn_choose_img=root.findViewById(R.id.btn_choose_img);
        Button btn_open_camera=root.findViewById(R.id.btn_open_camera);
        btn_choose_img.setText("????????????????????????");
        btn_open_camera.setText("????????????");
        mCameraDialog=new Dialog(this, R.style.BottomDialog);
        mCameraDialog.setContentView(root);
        Window dialogWindow=mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        //dialogWindow.setWindowAnimations(R.style.dialogstyle); // ????????????
        WindowManager.LayoutParams lp=dialogWindow.getAttributes(); // ?????????????????????????????????
        lp.x=0; // ?????????X??????
        lp.y=0; // ?????????Y??????
        lp.width=getResources().getDisplayMetrics().widthPixels; // ??????
        root.measure(0,0);
        lp.height=root.getMeasuredHeight();
        lp.alpha=9f; // ?????????
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(form==5&&first==0){
            mMapView.onPause();
        }
        if(upload)
            if(mCameraDialog.isShowing()) mCameraDialog.cancel();
    }

    /**
     *
     * ????????????????????????setIntent(intent);?????????onResume???????????????intent
     * @param intent intent
     */
    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @RequiresApi(api=Build.VERSION_CODES.O)
    @Override
    protected void onResume(){
        super.onResume();
        if(form==5&&first==0){
            mMapView.onResume();
        }
        initPictureList();
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

    @RequiresApi(api=Build.VERSION_CODES.O)
    private void initPictureList(){
        Intent intent=getIntent();
        int picture_id=intent.getIntExtra("picture_id",0);
        if(picture_id!=0){
            picture_count++;
            pictureList.add(picture_id);
        }
        switch(pictureList.size()){
            case 4:
                int picture4=pictureList.get(3);
                List<LocalPicture> picture_4=DataSupport.where("code=?",String.valueOf(picture4)).find(LocalPicture.class);
                String str4=picture_4.get(0).getPicture();
                byte[] in_4=Base64.getDecoder().decode(str4);
                binding.picture4.setVisibility(View.VISIBLE);
                Bitmap bit_4=BitmapFactory.decodeByteArray(in_4,0,in_4.length);
                binding.picture4.setImageBitmap(bit_4);
            case 3:
                int picture3=pictureList.get(2);
                List<LocalPicture> picture_3=DataSupport.where("code=?",String.valueOf(picture3)).find(LocalPicture.class);
                String str3=picture_3.get(0).getPicture();
                byte[] in_3=Base64.getDecoder().decode(str3);
                binding.picture3.setVisibility(View.VISIBLE);
                Bitmap bit_3=BitmapFactory.decodeByteArray(in_3,0,in_3.length);
                binding.picture3.setImageBitmap(bit_3);
            case 2:
                int picture2=pictureList.get(1);
                List<LocalPicture> picture_2=DataSupport.where("code=?",String.valueOf(picture2)).find(LocalPicture.class);
                String str2=picture_2.get(0).getPicture();
                byte[] in_2=Base64.getDecoder().decode(str2);
                binding.picture2.setVisibility(View.VISIBLE);
                Bitmap bit_2=BitmapFactory.decodeByteArray(in_2,0,in_2.length);
                binding.picture2.setImageBitmap(bit_2);
            case 1:
                int picture1=pictureList.get(0);
                List<LocalPicture> picture_1=DataSupport.where("code=?",String.valueOf(picture1)).find(LocalPicture.class);
                String str1=picture_1.get(0).getPicture();
                byte[] in_1=Base64.getDecoder().decode(str1);
                binding.picture1.setVisibility(View.VISIBLE);
                Bitmap bit_1=BitmapFactory.decodeByteArray(in_1,0,in_1.length);
                binding.picture1.setImageBitmap(bit_1);
                binding.upload.setText("??????????????????");
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