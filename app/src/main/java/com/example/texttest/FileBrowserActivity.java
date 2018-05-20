package com.example.texttest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserActivity extends AppCompatActivity {
    private ListView fileList = null ;//显示文件的列表
    private ArrayAdapter adapter = null ;//适配器
    String strPath = null;
    private DbAction dbAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("titleInfo",Bookshelf_Activity.MODE_PRIVATE);
        int  theme = sharedPreferences.getInt("theme",R.style.AppTheme_NoActionBar);
        if (theme == R.style.biubiu){
        setTheme(R.style.biubiuf);
        }
        setContentView(R.layout.activity_file_browser);
        if (theme == R.style.biubiu){
            findViewById(R.id.dongji).setBackgroundColor(getColor(R.color.colorAccent));
            findViewById(R.id.bc).setBackgroundTintList(getResources().getColorStateList(R.color.colorAccent));
        }
        fileList = findViewById(R.id.file_list);//绑定组件
        openFile(adapter, fileList);//打开文件和配置适配器，显示在列表上
        fileList.setOnItemClickListener(new OnItemClick());//文件列表的子项单击监听处理

    }
    public void binggouu(View view){
        finish();
    }

    /**
     * 此方法用于装载数据
     * @param adapter ArrayAdapter适配器
     * @param list 要显示的ListView
     */
    public void openFile(ArrayAdapter adapter, ListView list){
        List<SubFile> ndata = new ArrayList<SubFile>();//用于存放文件名，加载到适配器中
        strPath = (getIntent().getStringExtra("filename"));//获取上个界面传来的值
        if(strPath == null){//没有有数据
            strPath = Environment.getExternalStorageDirectory().getPath();//根目录
        }
        File pathFile = new File(strPath);//要显示的目录
        if(pathFile != null){//有这个目录
            File[] files = pathFile.listFiles();//获取目录下的所有文件夹与文件
            for(File file : files){//全部遍历
                if(new SubFile(file).toString()!=null){//如果不是筛选掉的文件
                    ndata.add(new SubFile(file));//添加到mdata对象中
                }
            }
            //配置适配器
            adapter = new ArrayAdapter(FileBrowserActivity.this, android.R.layout.simple_list_item_1, ndata);
            list.setAdapter(adapter);//设置列表的适配去
        }else{//空文件处理
            Toast.makeText(FileBrowserActivity.this , "查找文件为空！", Toast.LENGTH_SHORT).show();
        }
    }

    public void over(View view){
        Log.i("0000000000000000000001",strPath);
        if (strPath.equals(null) || strPath.equals(Environment.getExternalStorageDirectory().getPath()) || strPath.equals("/storage/emulated/0") ){
            Toast.makeText(FileBrowserActivity.this,"你已无路可退",Toast.LENGTH_SHORT).show();
        } else {
            Intent intent;
            strPath = strPath.substring(0,strPath.lastIndexOf("/"));
            intent = new Intent(FileBrowserActivity.this, FileBrowserActivity.class);//还是跳转到改Activity
            intent.putExtra("filename", strPath);//传入路径
            startActivity(intent);
            overridePendingTransition(R.anim.abc_popup_enter,R.anim.abc_popup_exit);
            finish();
        }
    }

    //文件列表子项的单击监听处理
    private class OnItemClick implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SubFile subFile = (SubFile) parent.getAdapter().getItem(position);//获取子项存储的SubFile类，该类可以获取到子项所在路径
            String filename = subFile.getFile().getPath();//filename赋值为subFile对象中的路径
            Intent intent = null;//声明Intent类，用于跳转界面
            if(subFile.getFile().isDirectory()){//如果路径为文件夹
                intent = new Intent(FileBrowserActivity.this, FileBrowserActivity.class);//还是跳转到改Activity
                intent.putExtra("filename", filename);//传入路径
                startActivity(intent);//跳转界面
                finish();
            }else{//文件的话
//                intent = new Intent(FileBrowserActivity.this, Bookshelf_Activity.class);//跳转到主界面
                File file = new File(filename);
                if (file.exists()){
                    String name = file.getName();
                    name = name.substring(0,name.lastIndexOf(".txt"));
                    writedata(name,filename,0);
                    Toast.makeText(FileBrowserActivity.this,"已加入书架",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FileBrowserActivity.this,"文件不存在",Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void writedata(String filename, String filePath , float profress){
        dbAction = new DbAction(this);
        dbAction.insert(filename, filePath, profress);
    }


}
