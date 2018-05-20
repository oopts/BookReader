package com.example.texttest;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchFile {
    public static ArrayList<BookInfo> searchLocaltion(Context context){
        ArrayList<BookInfo> bookList = null;
        Uri uri = MediaStore.Files.getContentUri("external");
        //扫描本地的txt文件
        Cursor cursor = context.getApplicationContext().getContentResolver().query(
                        uri,
                        new String[]{MediaStore.Files.FileColumns.DATA,MediaStore.Files.FileColumns.SIZE},
                        MediaStore.Files.FileColumns.MIME_TYPE + " = 'text/plain' ",
                        null,null
                        );
        if (cursor != null) {
            bookList = new ArrayList<BookInfo>();
            while (cursor.moveToNext()) {
                BookInfo book;
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                String name = path.substring(path.lastIndexOf("/") + 1);
                long progress = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)) / 1024;
                if (name.lastIndexOf(".txt") != -1) {
                    name = name.substring(0, name.lastIndexOf(".txt"));
                }
                if (!name.equals("")) {
                    String fistW = name.substring(0, 1);
                    if (isChinese(fistW) || fistW.equals("《") || size >= 20*1024) {
                        book = new BookInfo(666, name, path, size);
                        bookList.add(book);
                    }
                }
            }
        }
        cursor.close();
        return bookList;
    }

    public static boolean isChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find())
            flg = true;
        return flg;
    }
}
