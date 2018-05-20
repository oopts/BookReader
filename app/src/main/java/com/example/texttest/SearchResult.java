package com.example.texttest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchResult extends AppCompatActivity {
    private ListView book_list;
    private ArrayList<BookInfo> list;
    private sAdapter sAdapter;
    private View view;
    private LoadingDialog dialog;
    private boolean isCompete = false;
//    private final Handler mHideHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//
//            }
//        }
//    };
    HandlerThread handlerThread = new HandlerThread("HandlerThrea"){
        @Override
        public void run() {
            String path = Environment.getExternalStorageDirectory().getPath();
            File file = new File(path);
            ArrayList<BookInfo> bookInfos = searchFile(file);
            Message msg = mHandler.obtainMessage(0, bookInfos);
            mHandler.sendMessage(msg);

        }
    };
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0 :
                    list = (ArrayList<BookInfo>) msg.obj;
                    view.setVisibility(View.GONE);
                    isCompete = true;
                    if (dialog.isShowing()){
                        sAdapter.refresh(list);
                        dialog.dismiss();
                    }
                    break;
                case 2:
                    break;
                    default:
                        break;
            }

        }
    };

//    private Runnable searchFile = new Runnable() {
//        @Override
//        public void run() {
//            list = new ArrayList<BookInfo>();
//            searchFile(new File(Environment.getExternalStorageDirectory().getPath()));
//            view.setVisibility(View.GONE);
//            sAdapter.refresh(list);
//            button.setVisibility(View.VISIBLE);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences sharedPreferences = getSharedPreferences("titleInfo",Bookshelf_Activity.MODE_PRIVATE);
        int  theme = sharedPreferences.getInt("theme",R.style.AppTheme_NoActionBar);
        if (theme == R.style.biubiu){
            setTheme(R.style.biubiuf);
        }
        setContentView(R.layout.activity_search_result);
        handlerThread.start();
        if (theme == R.style.biubiu){
            TextView view = findViewById(R.id.gg);
            view.setTextColor(getColor(R.color.colorAccent));
            findViewById(R.id.zn).setBackgroundColor(getColor(R.color.colorAccent));
            findViewById(R.id.bb).setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
            findViewById(R.id.ref).setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        }
        book_list = findViewById(R.id.SearchResult_list);
        view = findViewById(R.id.gg);
        list = new ArrayList<BookInfo>();
        list = SearchFile.searchLocaltion(this);
        sAdapter = new sAdapter(this,list);
        book_list.setAdapter(sAdapter);
        dialog = new LoadingDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    public void ss(View view){

        if (isCompete){
            dialog.dismiss();
            sAdapter.refresh(list);
        } else {
            dialog.show();
        }
    }

    public void binggou(View view){
        finish();
    }
    //进入文件夹选择文件button
    public void enterFile(View view){
        Intent intent = new Intent(this,FileBrowserActivity.class);
        startActivity(intent);
        finish();
    }
    BookInfo bookInfo;
    ArrayList<BookInfo> bookInfos = new ArrayList<>();
    public ArrayList<BookInfo> searchFile(final File file){
        File[] files = file.listFiles();
        try {
            for (final File f : files) {
                if (!f.isDirectory()) {
                    if (f.getName().endsWith(".txt")) {
                        String name = f.getName().substring(0,f.getName().lastIndexOf(".txt"));
                        String path = f.getPath();
                        long size = f.length();
                        if (!name.equals("")){
                            String firstChar = name.substring(0,1);
                        if (size > 1024 * 20 || isChinese(firstChar) || firstChar.equals("《")) {
                            size = size/1024;
                            bookInfo = new BookInfo(666, name, path, size);
                            bookInfos.add(bookInfo);
                        }
                        }
                    }
                } else if (f.isDirectory()) {
                            searchFile(f);
                        }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bookInfos;
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
