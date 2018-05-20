package com.example.texttest;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQL extends SQLiteOpenHelper{
    public static final String DB_Name = "Book_Info";
    private Context mContext;

    public MySQL(Context context) {
        super(context, DB_Name, null, 11);
        this.mContext = context;
    }

    public MySQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    /**
     * 用户第一次使用软件时调用的操作，用于获取数据库创建语句（SW）,然后创建数据库
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table if not exists Book_Info(id INTEGER PRIMARY KEY AUTOINCREMENT, name text, path text, progress float)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
