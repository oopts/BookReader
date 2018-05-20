package com.example.texttest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.texttest.View.DisPlayUtil;

import java.io.File;
import java.util.List;

/*
作者:ooo
 */
public class Bookshelf_Activity extends MPermissionsActivity {
    public static final int SHOW_PICTURE = 2;
    private RecyclerView bookList = null;
    private List<BookInfo> list = null;
    private static final  int REMOVE_BOOK = Menu.FIRST;
    private int longClickPosition = 0;
    private DbAction dbAction;
    private mAdapter mAdapter;
    private View picture;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences sharedPreferences = getSharedPreferences("titleInfo",Bookshelf_Activity.MODE_PRIVATE);
        int  theme = sharedPreferences.getInt("theme",R.style.AppTheme_NoActionBar);
        setTheme(theme);
        setContentView(R.layout.activity_main);

        picture = findViewById(R.id.picture);
        requestPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 0x0001);
        sharedPreferences = getSharedPreferences("titleInfo",Bookshelf_Activity.MODE_PRIVATE);
        String Tittle = sharedPreferences.getString("title","清风不识字");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Tittle);
        setSupportActionBar(toolbar);

        dbAction = new DbAction(this);
        list = dbAction.select();

        mAdapter = new mAdapter(this, list);
        bookList = findViewById(R.id.BookShelf_list);
        bookList.setAdapter(mAdapter);
        registerForContextMenu(bookList);   //为txt列表组件注册上下文菜单
        bookList.setLayoutManager(new GridLayoutManager(this , 3));
        mAdapter.setOnItemClickListener(new mAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {

                if (position + 1 == list.size()){
                    Intent intent = new Intent(Bookshelf_Activity.this,SearchResult.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(Bookshelf_Activity.this,ReadActivity.class);
                    intent.putExtra("FileName", list.get(position).getName());
                    intent.putExtra("FilePath", list.get(position).getPath());
                    intent.putExtra("id",list.get(position).getId());
                    startActivity(intent);
                }
            }
        });

        mAdapter.setOnItemLongClicklistener(new mAdapter.OnItemLongClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if(position + 1 == list.size()){
                    longClickPosition = -1;
                    Intent intent = new Intent(Bookshelf_Activity.this,FileBrowserActivity.class);
                    startActivity(intent);
                }else{
                    longClickPosition = position;   //记录了按了列表哪个子项}
                }
            }
        });
//        mAppBarLayout = findViewById(R.id.app_bar);
//        mAppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
//            @Override
//            public void onStateChanged(AppBarLayout appBarLayout, State state) {
//                Log.d("STATE", state.name());
//                if( state == State.EXPANDED ) {
//                    Toast.makeText(Bookshelf_Activity.this,"展开",Toast.LENGTH_SHORT).show();
//                    //展开状态
//
//                }else if(state == State.COLLAPSED){
//
//                    //折叠状态
//                    Toast.makeText(Bookshelf_Activity.this,"折叠",Toast.LENGTH_SHORT).show();
//
//                }else {
//
//                    //中间状态
//                    Toast.makeText(Bookshelf_Activity.this,"中间",Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/QFBSZ");
//判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            //通过file的mkdirs()方法创建<span style="color:#FF0000;">目录中包含却不存在</span>的文件夹
            file.mkdirs();
        }
        setBack();
        }




    @Override
    protected void onRestart() {
        super.onRestart();
        list = dbAction.select();
        mAdapter.refresh(list);
    }

    //创建上下文菜单

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (longClickPosition == -1){

        } else {
            menu.add(0, REMOVE_BOOK, 0, "移除书籍");
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == REMOVE_BOOK){
            removeBook();
        }
        return super.onContextItemSelected(item);
    }
    //移除图书处理
    private void removeBook() {
        if (longClickPosition == -1){
            return;
        }
        int id = list.get(longClickPosition).getId();
        dbAction.delete(id);
        list = DbAction.select();
        mAdapter.refresh(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //此处调用了图片选择器
                //如果直接写intent.setDataAndType("image/*");调用的是系统图库
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                intent.putExtra("crop", "true");//剪切动作的信号
//                intent.putExtra("aspectX", 1);//x和y是否等比缩放
//                intent.putExtra("aspectY", 1);
//                intent.putExtra("outputX", 320);
//                intent.putExtra("outputY", 320);//剪切后图片的尺寸
//                intent.putExtra("return-data", true);//是否把剪切后的图片通过data返回
//                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());//图片的输出格式
//                intent.putExtra("noFaceDetection", true);  //关闭面部识别
//                //设置剪切的图片保存位置
//                Uri cropUri = Uri.fromFile(new File(
//                        Environment.getExternalStorageDirectory().getPath() + "/QFBSZ/crop.png"));
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,cropUri);
                //开启意图
                startActivityForResult(intent, SHOW_PICTURE);
                break;
            case R.id.action_share:
                AlertDialog.Builder builder = new AlertDialog.Builder(Bookshelf_Activity.this);
                View dview = View.inflate(Bookshelf_Activity.this, R.layout.dialog_book, null);
                final EditText et = dview.findViewById(R.id.et_dialog);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String title = String.valueOf(et.getText());
                        SharedPreferences sharedPreferences = getSharedPreferences("titleInfo", Bookshelf_Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("title", title);
                        editor.commit();
                        Intent intent1 = new Intent(Bookshelf_Activity.this, Bookshelf_Activity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.abc_popup_enter,R.anim.abc_popup_exit);
                        finish();
                    }
                });
                builder.setView(dview);
                builder.show();
                break;
            case R.id.action_settings:
                SharedPreferences sharedPreferences = getSharedPreferences("titleInfo",Bookshelf_Activity.MODE_PRIVATE);
                int  theme = sharedPreferences.getInt("theme",R.style.AppTheme_NoActionBar);
                if (theme == R.style.AppTheme_NoActionBar){
                    setTheme(R.style.biubiu);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("theme", R.style.biubiu);
                    editor.commit();
                } else {
                    setTheme(R.style.AppTheme_NoActionBar);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("theme", R.style.AppTheme_NoActionBar);
                    editor.commit();
                }
                Intent intent1 = new Intent(Bookshelf_Activity.this, Bookshelf_Activity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.abc_popup_enter,R.anim.abc_popup_exit);
                finish();
                Toast.makeText(Bookshelf_Activity.this,"主题切换成功",Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SHOW_PICTURE:

                if (data != null) {//判断数据是否为空
                    Uri url1 = data.getData();        //获得图片的uri

//                        bm1 = MediaStore.Images.Media.getBitmap(resolver, url1);
//                        String[] proj = {MediaStore.Images.Media.DATA};
//                        //好像是Android多媒体数据库的封装接口，具体的看Android文档
//                        Cursor cursor = managedQuery(url1, proj, null, null, null);
//                        //按我个人理解 这个是获得用户选择的图片的索引值
//                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                        //将光标移至开头 ，这个很重要，不小心很容易引起越界
//                        cursor.moveToFirst();
                        //最后根据索引值获取图片路径
//                        String path = cursor.getString(column_index);

                        Intent intent = new Intent("com.android.camera.action.CROP");//发起剪切动作
                        intent.setDataAndType(url1, "image/*");//设置剪切图片的uri和类型
                        intent.putExtra("crop", "true");//剪切动作的信号
                        intent.putExtra("aspectX", getResources().getDisplayMetrics().widthPixels);//x和y是否等比缩放
                        intent.putExtra("aspectY", DisPlayUtil.dip2px(Bookshelf_Activity.this,320));
//                        intent.putExtra("outputX", getResources().getDisplayMetrics().widthPixels);
//                        intent.putExtra("outputY", DisPlayUtil.dip2px(Bookshelf_Activity.this,320));//剪切后图片的尺寸
                        intent.putExtra("return-data", false);//是否把剪切后的图片通过data返回
//                        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());//图片的输出格式
                        intent.putExtra("noFaceDetection", true);  //关闭面部识别
                        //设置剪切的图片保存位置
                        Uri cropUri = Uri.fromFile(new File(
                                Environment.getExternalStorageDirectory().getPath() + "/QFBSZ/crop.png"));
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,cropUri);
                        startActivityForResult(intent,5);

                }
                break;
            case 5:
                setBack();
                break;
            default:
                break;
        }
    }

    private void setBack() {
        String path= Environment.getExternalStorageDirectory().getPath() + "/QFBSZ/crop.png";
        File imageFile=new File(path);
        if (imageFile.exists()) {
            Drawable drawable = Drawable.createFromPath(path);
            picture.setBackground(drawable);
        }
    }


    //    public abstract static class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
//
//        public enum State {
//            EXPANDED,
//            COLLAPSED,
//            IDLE
//        }
//
//        private State mCurrentState = State.IDLE;
//
//        @Override
//        public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//            if (i == 0) {
//                if (mCurrentState != State.EXPANDED) {
//                    onStateChanged(appBarLayout, State.EXPANDED);
//                }
//                mCurrentState = State.EXPANDED;
//            } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
//                if (mCurrentState != State.COLLAPSED) {
//                    onStateChanged(appBarLayout, State.COLLAPSED);
//                }
//                mCurrentState = State.COLLAPSED;
//            } else {
//                if (mCurrentState != State.IDLE) {
//                    onStateChanged(appBarLayout, State.IDLE);
//                }
//                mCurrentState = State.IDLE;
//            }
//        }
//
//        public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
//    }
    /**
     * 权限成功回调函数
     *
     * @param requestCode
     */
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        switch (requestCode) {
            case 0x0001:
//                into();
                break;
        }

    }
}
