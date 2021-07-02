package com.example.whuinfoplatform.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DB_INFO extends SQLiteOpenHelper {

    public static final String CREATE_INFO =
            "create table Info (" +
            "id integer primary key autoincrement,"+
            "owner_id integer ," +
            "send_date DATE," +
            "answered integer," +
            "form integer," +
            "fd_form integer," +
            "help_form integer," +
            "price real," +
            "date text," +
            "place text," +
            "lesson text," +
            "score integer," +
            "detail text," +
            "reward real" +
            ")";

    private Context mContext;

    public DB_INFO(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_INFO);
        //Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Info");
        onCreate(db);
    }
}
