package com.example.whuinfoplatform.Entity;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AboutTime{

    public String getTime(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss",Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        return time;
    }

    public int getYear(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss",Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        int year=Integer.valueOf(String.valueOf(time.charAt(0))+String.valueOf(time.charAt(1))+String.valueOf(time.charAt(2))+String.valueOf(time.charAt(3))).intValue();
        return year;
    }

    public int getMonth(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss",Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        int month;
        if(!String.valueOf(time.charAt(5)).equals("0"))
            month=Integer.valueOf(String.valueOf(time.charAt(5))+String.valueOf(time.charAt(6))).intValue();
        else
            month=Integer.valueOf(String.valueOf(time.charAt(6))).intValue();
        return month;
    }

    public int getDay(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss",Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        int day;
        if(!String.valueOf(time.charAt(8)).equals("0"))
            day=Integer.valueOf(String.valueOf(time.charAt(8))+String.valueOf(time.charAt(9))).intValue();
        else
            day=Integer.valueOf(String.valueOf(time.charAt(9))).intValue();
        return day;
    }

    public int getHour(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss",Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        int hour;
        if(!String.valueOf(time.charAt(12)).equals("0"))
            hour=Integer.valueOf(String.valueOf(time.charAt(12))+String.valueOf(time.charAt(13))).intValue();
        else
            hour=Integer.valueOf(String.valueOf(time.charAt(13))).intValue();
        return hour;
    }

    public int getMinute(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss",Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        int minute;
        if(!String.valueOf(time.charAt(15)).equals("0"))
            minute=Integer.valueOf(String.valueOf(time.charAt(15))+String.valueOf(time.charAt(16))).intValue();
        else
            minute=Integer.valueOf(String.valueOf(time.charAt(16))).intValue();
        return minute;
    }

    public int getSecond(){
        long timecurrentTimeMillis=System.currentTimeMillis();
        SimpleDateFormat sdfTwo=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss",Locale.getDefault());
        String time=sdfTwo.format(timecurrentTimeMillis);
        int second=Integer.valueOf(String.valueOf(time.charAt(18))+String.valueOf(time.charAt(19))).intValue();
        return second;
    }

    public String judgeTimeOnScreen(String time_ex){
        String timestamp;
        int currentYear=getYear();
        int currentMonth=getMonth();
        int currentDay=getDay();
        int itemYear=Integer.valueOf(String.valueOf(time_ex.charAt(0))+String.valueOf(time_ex.charAt(1))+String.valueOf(time_ex.charAt(2))+String.valueOf(time_ex.charAt(3))).intValue();
        int itemMonth=Integer.valueOf(String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))).intValue();
        int itemDay=Integer.valueOf(String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))).intValue();
        int itemHour=Integer.valueOf(String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))).intValue();
        int itemMinute;
        if(!String.valueOf(time_ex.charAt(15)).equals("0"))
            itemMinute=Integer.valueOf(String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16)));
        else
            itemMinute=Integer.valueOf(String.valueOf(time_ex.charAt(16))).intValue();
        if(itemYear!=currentYear){
            timestamp=time_ex;
        }
        else if(itemMonth!=currentMonth){
            timestamp=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                    +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
        }
        else if(currentDay-itemDay == 2){
            timestamp="前天 "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
        }
        else if(currentDay-itemDay == 1){
            timestamp="昨天 "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
        }
        else if(currentDay!=itemDay){
            timestamp=String.valueOf(time_ex.charAt(5))+String.valueOf(time_ex.charAt(6))+String.valueOf(time_ex.charAt(7))+String.valueOf(time_ex.charAt(8))+String.valueOf(time_ex.charAt(9))
                    +String.valueOf(time_ex.charAt(10))+String.valueOf(time_ex.charAt(11))+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
        }
        else{
            timestamp="今天 "+String.valueOf(time_ex.charAt(12))+String.valueOf(time_ex.charAt(13))+
                    String.valueOf(time_ex.charAt(14))+String.valueOf(time_ex.charAt(15))+String.valueOf(time_ex.charAt(16));
        }
        return timestamp;
    }
}
