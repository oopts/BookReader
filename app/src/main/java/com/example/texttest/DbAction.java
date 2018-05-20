package com.example.texttest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DbAction {
    private static MySQL mySQL;

    public DbAction(Context context){
        mySQL = new MySQL(context);
    }

    //获取progress
    public static float getProgress(int id){
        SQLiteDatabase db = mySQL.getWritableDatabase();
        String sql = "select * from Book_Info where id = "+id+"";
        Cursor cur = db.rawQuery(sql,null);
        float progress = 50;
        while (cur.moveToNext()) {
            progress = cur.getFloat(cur.getColumnIndex("progress"));
           }
        cur.close();
        db.close();
        Log.i("-------数据库获取成功---------",progress + "----------------------------");
        return progress;
    }
    public void updateProgress(int id, float progress){
        SQLiteDatabase db = mySQL.getWritableDatabase();
        String sql = "update Book_Info set progress = '" + progress + "' where id = '" + id + "'";
        db.execSQL(sql);
        db.close();
        Log.i("---------------------","数据库更新成功----------------------------");
    }


    //添加数据
    public void insert(String name, String path, float progress){
        SQLiteDatabase db = mySQL.getWritableDatabase();
        String sql = "insert into Book_Info (name, path, progress) values ('" +name+ "', '"+path+"', '"+progress+"')";
        db.execSQL(sql);
        db.close();
    }
    //删除数据
    public void delete(int id){
        SQLiteDatabase db = mySQL.getWritableDatabase();
        String sql = "delete from Book_Info where id = '"+id+"'  ";
        db.execSQL(sql);
        Log.i("___________","删除数据的ID为"+id);
//        String sqln = "update Book_Info set id = id-1 where id > '"+id+"' ";
//        db.execSQL(sqln);
//        Log.i("___________","更改id成功");
        db.close();
    }
    //查询数据
    public static List<BookInfo> select(){
        SQLiteDatabase db = mySQL.getWritableDatabase();
        String sql = "select * from Book_Info";
        Cursor cur = db.rawQuery(sql, null);
        List<BookInfo> list = new ArrayList<BookInfo>();
        while (cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex("id"));
            String name = cur.getString(1);
            String path = cur.getString(2);
            float progress = cur.getFloat(cur.getColumnIndex("progress"));
            list.add(new BookInfo(id,name,path,progress));
        }
        cur.close();
        db.close();
        return list;

    }
}
