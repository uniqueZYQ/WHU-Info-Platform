package com.example.whuinfoplatform.Entity;

public class SenseCheck{

    public boolean SenseCheckAllBlankOrNull(String target){
        for(int i=0;i<target.length();i++){
            if(target.charAt(i)==' '||target.charAt(i)=='\0'){
                continue;
            }
            else{
                return true;
            }
        }
        return false;
    }

    public boolean SenseCheckAllBlankOrNullOrEnter(String target){
        for(int i=0;i<target.length();i++){
            if(target.charAt(i)==' '||target.charAt(i)=='\0'||target.charAt(i)=='\n'){
                continue;
            }
            else{
                return true;
            }
        }
        return false;
    }
}
