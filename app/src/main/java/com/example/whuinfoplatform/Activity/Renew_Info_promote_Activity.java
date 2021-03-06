package com.example.whuinfoplatform.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;

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
import com.example.whuinfoplatform.Entity.Info;
import com.example.whuinfoplatform.Entity.SenseCheck;
import com.example.whuinfoplatform.Entity.WebResponse;
import com.example.whuinfoplatform.R;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Response;

public class Renew_Info_promote_Activity extends rootActivity{
    private com.example.whuinfoplatform.databinding.ActivityRenewInfoPromoteBinding binding;
    private int id=0,form=0,fd_form=0,help_form=0,score=0,init=1,i=1,first=1;
    private MapView mMapView=null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private BaiDuMap baidumap=new BaiDuMap();
    private PoiSearch mPoiSearch=PoiSearch.newInstance();
    private double latitude,longitude;
    private String name,address,placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void bindView(){
        binding=com.example.whuinfoplatform.databinding.ActivityRenewInfoPromoteBinding.inflate(getLayoutInflater());
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
        Renew_Info_promote_Activity.MyLocationListener myLocationListener=new Renew_Info_promote_Activity.MyLocationListener();
        //?????????????????????
        mLocationClient=new LocationClient(this);
        //??????????????????
        baidumap.getLocation2(mBaiduMap,mLocationClient,myLocationListener);
        //????????????
        baidumap.configMap(mBaiduMap,MyLocationConfiguration.LocationMode.NORMAL,true,BitmapDescriptorFactory.fromResource(R.drawable.location),0x55FFFFFF,0x55FFFFFF);
        //??????????????????
        UiSettings mUiSettings=mBaiduMap.getUiSettings();
        mUiSettings.setRotateGesturesEnabled(false);
        if(first==1)
            BToast.showText(Renew_Info_promote_Activity.this,"????????????????????????????????????...");
    }

    public class MyLocationListener extends BDAbstractLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location){
            //mapView ???????????????????????????????????????
            if (location==null||mMapView==null){
                return;
            }
            MyLocationData locData=new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // ?????????????????????????????????????????????????????????0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            latitude=location.getLatitude();    //??????????????????
            longitude=location.getLongitude();    //??????????????????
            if((latitude>=1||longitude>=1)&&first==1){
                LatLng ll=new LatLng(latitude,longitude);
                //?????????????????????????????????,????????????????????????17
                float zoom=17;
                MapStatusUpdate u=MapStatusUpdateFactory.newLatLngZoom(ll,zoom);
                mBaiduMap.setMapStatus(u);
                mBaiduMap.animateMapStatus(u);
                BToast.showText(Renew_Info_promote_Activity.this,"????????????????????????!",true);
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
                //?????????4????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid+","+poi3.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else if(poiResult.getTotalPoiNum()==3){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                PoiInfo poi2=poiResult.getAllPoi().get(2);
                //?????????3????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid+","+poi2.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else if(poiResult.getTotalPoiNum()==2){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                PoiInfo poi1=poiResult.getAllPoi().get(1);
                //?????????2????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid+","+poi1.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else if(poiResult.getTotalPoiNum()==1){
                PoiInfo poi0=poiResult.getAllPoi().get(0);
                //?????????1????????????????????????uid????????????????????????
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                        .poiUids(poi0.uid)); // uid??????????????????????????????10???uid?????????uid??????????????????????????????
            }
            else{
                //??????????????????
                BToast.showText(Renew_Info_promote_Activity.this,"???????????????\n???????????????????????????!",false);
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
                        BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????\n???????????????????????????!",false);
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
            LatLng ll=new LatLng(mapPoi.getPosition().latitude, mapPoi.getPosition().longitude);
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

    private void showInfoDetail(Info myInformation){
        runOnUiThread(()->{
            binding.detail.setText(myInformation.getDetail());
            binding.editCommobj.setText(myInformation.getLesson());
            binding.editPlace.setText(myInformation.getPlace());
            binding.editDate.setText(myInformation.getDate());
            binding.editReward.setText(String.valueOf(myInformation.getReward()));
            binding.editPrice.setText(String.valueOf(myInformation.getPrice()));
            switch(form){
                case 1:{
                    binding.form.setText("????????????");
                    binding.persInfoFdType.setVisibility(View.VISIBLE);
                    binding.editReward.setVisibility(View.VISIBLE);
                    binding.persInfoFdType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                            if(init==1) parent.setSelection(myInformation.getFd_form());
                            fd_form=position;
                            init=0;
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent){
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
                            BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                        else if(!(fd_form==0||reward==-2||detail.equals(""))){
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
                                BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                            else{
                                InfoConnection infoConnection=new InfoConnection();
                                long timecurrentTimeMillis=System.currentTimeMillis();
                                SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss",Locale.getDefault());
                                String time=sdfTwo.format(timecurrentTimeMillis);
                                infoConnection.updateMyInfoConnection1(String.valueOf(id),String.valueOf(fd_form),String.valueOf(reward),detail,time, new okhttp3.Callback() {
                                    @Override
                                    public void onFailure(Call call,IOException e){
                                        Looper.prepare();
                                        BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
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
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),true);
                                            Intent intent1=new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                            startActivity(intent1);
                                        }
                                        else{
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),false);
                                        }
                                        Looper.loop();
                                    }
                                });
                            }
                        }
                        else
                            BToast.showText(Renew_Info_promote_Activity.this,"???????????????",false);
                    });
                    break;
                }
                case 2:{
                    binding.form.setText("????????????");
                    binding.persInfoHelpType.setVisibility(View.VISIBLE);
                    binding.editReward.setVisibility(View.VISIBLE);
                    binding.persInfoHelpType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                            if(init==1)parent.setSelection(myInformation.getHelp_form());
                            help_form=position;
                            init=0;
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent){
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
                            BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                        else if(!(help_form==0||reward==-2||detail.equals(""))){
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
                                BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                            else{
                                InfoConnection infoConnection=new InfoConnection();
                                long timecurrentTimeMillis=System.currentTimeMillis();
                                SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss",Locale.getDefault());
                                String time=sdfTwo.format(timecurrentTimeMillis);
                                infoConnection.updateMyInfoConnection2(String.valueOf(id),String.valueOf(help_form),String.valueOf(reward),detail,time,new okhttp3.Callback(){
                                    @Override
                                    public void onFailure(Call call,IOException e){
                                        Looper.prepare();
                                        BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
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
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),true);
                                            Intent intent1 = new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                            startActivity(intent1);
                                        }
                                        else{
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),false);
                                        }
                                        Looper.loop();
                                    }
                                });
                            }
                        }
                        else
                            BToast.showText(Renew_Info_promote_Activity.this,"???????????????",false);
                    });
                    break;
                }
                case 3:{
                    binding.form.setText("????????????");
                    binding.editPrice.setVisibility(View.VISIBLE);
                    binding.finish.setOnClickListener(v->{
                        double price=binding.editPrice.getText().toString().equals("")?-2:
                                binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                        String detail=binding.detail.getText().toString();
                        if(price==-1||price==0)
                            BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                        else if(!(price==-2||detail.equals(""))){
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
                                BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                            else{
                                InfoConnection infoConnection=new InfoConnection();
                                long timecurrentTimeMillis=System.currentTimeMillis();
                                SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss",Locale.getDefault());
                                String time=sdfTwo.format(timecurrentTimeMillis);
                                infoConnection.updateMyInfoConnection3(String.valueOf(id),String.valueOf(price),detail,time,new okhttp3.Callback(){
                                    @Override
                                    public void onFailure(Call call,IOException e){
                                        Looper.prepare();
                                        BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
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
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),true);
                                            Intent intent1=new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                            startActivity(intent1);
                                        }
                                        else{
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),false);
                                        }
                                        Looper.loop();
                                    }
                                });
                            }
                        }
                        else
                            BToast.showText(Renew_Info_promote_Activity.this,"???????????????",false);
                    });
                    break;
                }
                case 4:{
                    binding.form.setText("????????????");
                    binding.editPrice.setVisibility(View.VISIBLE);
                    binding.finish.setOnClickListener(v->{
                        double price=binding.editPrice.getText().toString().equals("")?-2:
                                binding.editPrice.getText().toString().charAt(0)=='.'?-1:
                                binding.editPrice.getText().toString().charAt(binding.editPrice.getText().toString().length()-1)=='.'?-1:
                                binding.editPrice.getText().toString().equals(".")?-1:Double.parseDouble(binding.editPrice.getText().toString());
                        String detail=binding.detail.getText().toString();
                        if(price==-1||price==0)
                            BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                        else if(!(price==-2||detail.equals(""))){
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
                                BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                            else{
                                InfoConnection infoConnection=new InfoConnection();
                                long timecurrentTimeMillis=System.currentTimeMillis();
                                SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss",Locale.getDefault());
                                String time=sdfTwo.format(timecurrentTimeMillis);
                                infoConnection.updateMyInfoConnection3(String.valueOf(id),String.valueOf(price),detail,time,new okhttp3.Callback(){
                                    @Override
                                    public void onFailure(Call call,IOException e){
                                        Looper.prepare();
                                        BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
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
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),true);
                                            Intent intent1=new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                            startActivity(intent1);
                                        }
                                        else{
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),false);
                                        }
                                        Looper.loop();
                                    }
                                });
                            }
                        }
                        else
                            BToast.showText(Renew_Info_promote_Activity.this,"???????????????",false);
                    });
                    break;
                }
                case 5:{
                    placeId=myInformation.getPlaceId();
                    binding.form.setText("?????????????????????");
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
                            BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                        else if(!(reward==-2||detail.equals("")||date.equals("")||place.equals(""))){
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
                                BToast.showText(Renew_Info_promote_Activity.this,"??????????????????!",false);
                            else{
                                InfoConnection infoConnection=new InfoConnection();
                                long timecurrentTimeMillis = System.currentTimeMillis();
                                SimpleDateFormat sdfTwo =new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss",Locale.getDefault());
                                String time = sdfTwo.format(timecurrentTimeMillis);
                                infoConnection.updateMyInfoConnection4(String.valueOf(id),String.valueOf(reward),place,date,detail,placeId,time,new okhttp3.Callback(){
                                    @Override
                                    public void onFailure(Call call,IOException e){
                                        Looper.prepare();
                                        BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
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
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),true);
                                            Intent intent1=new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                            startActivity(intent1);
                                        }
                                        else{
                                            BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),false);
                                        }
                                        Looper.loop();
                                    }
                                });
                            }
                        }
                        else
                            BToast.showText(Renew_Info_promote_Activity.this,"???????????????",false);
                    });
                    break;
                }
                case 6:{
                    binding.form.setText("??????????????????");
                    binding.editCommobj.setVisibility(View.VISIBLE);
                    binding.editScore.setVisibility(View.VISIBLE);
                    binding.editScore.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> parent,View view,int position,long id){
                            if(init==1)parent.setSelection(myInformation.getScore());
                            score=position;
                            init=0;
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent){
                            parent.setSelection(myInformation.getScore());
                        }
                    });
                    binding.finish.setOnClickListener(v->{
                        String lesson=binding.editCommobj.getText().toString();
                        String detail=binding.detail.getText().toString();
                        if(!(lesson.equals("")||detail.equals("")||score==0)){
                            InfoConnection infoConnection=new InfoConnection();
                            long timecurrentTimeMillis=System.currentTimeMillis();
                            SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss",Locale.getDefault());
                            String time=sdfTwo.format(timecurrentTimeMillis);
                            infoConnection.updateMyInfoConnection5(String.valueOf(id),String.valueOf(score),lesson,detail,time,new okhttp3.Callback(){
                                @Override
                                public void onFailure(Call call,IOException e){
                                    Looper.prepare();
                                    BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
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
                                        BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),true);
                                        Intent intent1=new Intent(Renew_Info_promote_Activity.this,Personal_Center_Activity.class);
                                        startActivity(intent1);
                                    }
                                    else{
                                        BToast.showText(Renew_Info_promote_Activity.this,webResponse.getResponse(),false);
                                    }
                                    Looper.loop();
                                }
                            });
                        }
                        else
                            BToast.showText(Renew_Info_promote_Activity.this,"???????????????",false);
                    });
                    break;
                }
            }
        });
    }

    @Override
    protected void initData(){
        super.initData();
        Intent intent=getIntent();
        form=intent.getIntExtra("form",0);
        id=intent.getIntExtra("id",0);
        InfoConnection infoConnection=new InfoConnection();
        infoConnection.queryMyInfoDetailConnection(String.valueOf(id),new okhttp3.Callback(){
            @Override
            public void onFailure(Call call,IOException e){
                Looper.prepare();
                BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????????????????",false);
                Looper.loop();
            }

            @Override
            public void onResponse(Call call,Response response) throws IOException{
                String result=response.body().string();
                Info myInformation=new Info();
                try {
                    infoConnection.parseJSONForMyInfoDetailResponse(myInformation,result);
                    showInfoDetail(myInformation);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void initClick(){
        super.initClick();
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
                } catch (ParseException e) {
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
        binding.editPlace.setOnFocusChangeListener((v,hasFocus)->{
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
                    BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????",false);
                }
                return true;
            }
            else
                return false;
        });
        binding.search.setOnClickListener(v->{
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
                BToast.showText(Renew_Info_promote_Activity.this,"?????????????????????????????????",false);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void initWidget(){
        super.initWidget();
        ActionBar actionBar =getSupportActionBar();
        actionBar.setTitle("????????????-????????????-????????????");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPause(){
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
            mMapView=null;
        }
    }
}