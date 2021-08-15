package com.example.whuinfoplatform.Entity;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LocalLogin extends DataSupport{
    private int id;
    private int user_id;
    private long time;
    private static final long LOGIN_TIME=1000*60*60*8;//8h

    public void setId(int id) {
        this.id=id;
    }
    public int getId() {
        return id;
    }
    public int getUser_id() {
        return user_id;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time=time;
    }
    public void setUser_id(int user_id) {
        this.user_id=user_id;
    }

    public int judgeLogin(){
        List<LocalLogin> localLoginList=DataSupport.order("time desc").find(LocalLogin.class);
        if(localLoginList.size()==0){//无历史登录数据
            return -1;
        }
        else{
            if(System.currentTimeMillis()-localLoginList.get(0).getTime()<LOGIN_TIME){
                return localLoginList.get(0).getUser_id();
            }
            else{
                return 0;
            }
        }
    }

    public void updateOrInsert(int user_id){
        boolean insert=true;
        LocalLogin localLogin=new LocalLogin();
        List<LocalLogin> localLogins=DataSupport.select("user_id").find(LocalLogin.class);
        if(localLogins!=null){
            for(int i=0;i<localLogins.size();i++){
                if(user_id==localLogins.get(i).getUser_id()){
                    insert=false;
                    localLogin.setTime(System.currentTimeMillis());
                    localLogin.updateAll("user_id=?",String.valueOf(user_id));
                    break;
                }
            }
            if(insert){
                localLogin.setUser_id(user_id);
                localLogin.setTime(System.currentTimeMillis());
                localLogin.save();
            }
        }
        else{
            localLogin.setUser_id(user_id);
            localLogin.setTime(System.currentTimeMillis());
            localLogin.save();
        }
    }
}
