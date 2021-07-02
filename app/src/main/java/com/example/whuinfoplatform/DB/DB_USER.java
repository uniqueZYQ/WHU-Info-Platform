package com.example.whuinfoplatform.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DB_USER extends SQLiteOpenHelper {

    public static final String CREATE_USER = "create table User (" +
        " id integer primary key autoincrement,"+
        "nickname text," +
        "pwd text," +
        "stdid integer," +
        "realname text)";

    private Context mContext;

    public DB_USER(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER);
        //Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists User");
        onCreate(db);
    }
}
