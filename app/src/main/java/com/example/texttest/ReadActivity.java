package com.example.texttest;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.texttest.View.ChapterList;
import com.example.texttest.View.IChapter;
import com.example.texttest.View.ILoadListener;
import com.example.texttest.View.IPageChangeListener;
import com.example.texttest.View.ITextSelectListener;
import com.example.texttest.View.TxtConfig;
import com.example.texttest.View.TxtMsg;
import com.example.texttest.View.TxtReaderBaseView;
import com.example.texttest.View.TxtReaderView;

import java.io.File;


public class ReadActivity extends AppCompatActivity {
    private static final int UI_ANIMATION_DELAY = 100;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    //隐藏
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    //展示
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private final Runnable loadTxt = new Runnable() {
        @Override
        public void run() {
            getIntentData();
            mTxtReaderView = findViewById(R.id.fullscreen_content);
            loadFile();
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private String FilePath = null;
    private String FileName = null;
    private int id = 0;
    private TxtReaderView mTxtReaderView;
    private MenuHolder mMenuHolder = new MenuHolder();
    private Handler mHandler;
    private ChapterList mChapterListPop;
    private TextView content,night,bold,hori,setting,pre,next,back,title;
    private SeekBar seekBar;
    private Boolean isBold;
    private DbAction dbAction;
    private LoadingDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        newLoad();
    }

    private void newLoad() {
        setContentView(R.layout.activity_read);
        loadTxt.run();
        init();
        registerListener();
    }

    private void init() {
        dialog = new LoadingDialog(this);
        dialog.show();
        dbAction = new DbAction(this);
        mHandler = new Handler();
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        content = findViewById(R.id.content);
        night = findViewById(R.id.night);
        setting = findViewById(R.id.setting);
        bold = findViewById(R.id.bold);
        hori = findViewById(R.id.hori);
        pre = findViewById(R.id.pre_chapter);
        next = findViewById(R.id.next_chapter);
        seekBar = findViewById(R.id.seekBar);
        back = findViewById(R.id.back);
        title = findViewById(R.id.read_title);
        mMenuHolder.preChapter  = findViewById(R.id.pre_chapter);
        mMenuHolder.nextChapter = findViewById(R.id.next_chapter);
    }

    private void registerListener() {
        mTxtReaderView.setOnTextSelectListener(new ITextSelectListener() {
            @Override
            public void onTextChanging(String selectText) {

            }

            @Override
            public void onTextSelected(String selectText) {
                ClipboardManager cm = (ClipboardManager) ReadActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("text",mTxtReaderView.getCurrentSelectedText()));
                Toast.makeText(ReadActivity.this,"已复制",Toast.LENGTH_SHORT).show();
            }
        });
        isBold = mTxtReaderView.getTxtReaderContext().getTxtConfig().isBold(this);
        onTextSettingUi(isBold);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mChapterListPop.isShowing()) {
//                    mChapterListPop.showAsDropDown(back);
                    mChapterListPop.showAtLocation(mTxtReaderView, Gravity.CENTER,0,0);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            IChapter currentChapter = mTxtReaderView.getCurrentChapter();
                            if (currentChapter != null) {
                                mChapterListPop.setCurrentIndex(currentChapter.getIndex());
                                mChapterListPop.notifyDataSetChanged();
                            }
                        }
                    }, 300);
                } else {
                    mChapterListPop.dismiss();
                }
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mTxtReaderView.loadFromProgress(seekBar.getProgress());
                }
                return false;
            }
        });

        mTxtReaderView.setPageChangeListener(new IPageChangeListener() {
            @Override
            public void onCurrentPage(float progress) {

                float b = progress * 100f;
                    if (mTxtReaderView.getChapters().size() > 0) {
                        if (mTxtReaderView.getCurrentChapter().getIndex() != 0 && mTxtReaderView.getTxtReaderContext().getPageData().MidPage().HasData()) {
                            dbAction.updateProgress(id, b);
                        }
                    }
                seekBar.setProgress((int) (progress * 100));
                IChapter currentChapter = mTxtReaderView.getCurrentChapter();
                if (currentChapter != null) {
                    title.setText((currentChapter.getTitle() + "").trim());
                } else {
                    title.setText("无章节");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bold.setOnClickListener(new TextSettingClickListener());
        pre.setOnClickListener(new ChapterChangeClickListener(true));
        next.setOnClickListener(new ChapterChangeClickListener(false));
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View bottom = getLayoutInflater().inflate(R.layout.bottom_dialog, null);
                BottomSheetDialog dialog = new BottomSheetDialog(ReadActivity.this);
                mMenuHolder.font_small = bottom.findViewById(R.id.font_small);
                mMenuHolder.font_big = bottom.findViewById(R.id.font_bigger);
                mMenuHolder.fangzhen = bottom.findViewById(R.id.fangzhen);
                mMenuHolder.huadong = bottom.findViewById(R.id.huadong);
                mMenuHolder.stylea = bottom.findViewById(R.id.style_1);
                mMenuHolder.styleb = bottom.findViewById(R.id.style_2);
                mMenuHolder.stylec = bottom.findViewById(R.id.style_3);
                mMenuHolder.styled = bottom.findViewById(R.id.style_4);
                mMenuHolder.stylee = bottom.findViewById(R.id.style_5);
                if (mTxtReaderView.getTxtReaderContext().getTxtConfig().SwitchByTranslate) {
                    mTxtReaderView.setPageSwitchByTranslate();
                } else {
                    mTxtReaderView.setPageSwitchByCover();
                }
                onPageSwitchSettingUi(mTxtReaderView.getTxtReaderContext().getTxtConfig().SwitchByTranslate);
                onDialogShow();
                mMenuHolder.font_big.setOnClickListener(new TextChangeClickListener(true));
                mMenuHolder.font_small.setOnClickListener(new TextChangeClickListener(false));
                mMenuHolder.huadong.setOnClickListener(new SwitchSettingClickListener(true));
                mMenuHolder.fangzhen.setOnClickListener(new SwitchSettingClickListener(false));
                mMenuHolder.stylea.setOnClickListener(new ReadActivity.StyleChangeClickListener(ContextCompat.getColor(ReadActivity.this, R.color.hwtxtreader_styleclor1), StyleTextColors[0],1));
                mMenuHolder.styleb.setOnClickListener(new ReadActivity.StyleChangeClickListener(ContextCompat.getColor(ReadActivity.this, R.color.hwtxtreader_styleclor2), StyleTextColors[1],2));
                mMenuHolder.stylec.setOnClickListener(new ReadActivity.StyleChangeClickListener(ContextCompat.getColor(ReadActivity.this, R.color.hwtxtreader_styleclor3), StyleTextColors[2],3));
                mMenuHolder.styled.setOnClickListener(new ReadActivity.StyleChangeClickListener(ContextCompat.getColor(ReadActivity.this, R.color.hwtxtreader_styleclor4), StyleTextColors[3],4));
                mMenuHolder.stylee.setOnClickListener(new ReadActivity.StyleChangeClickListener(ContextCompat.getColor(ReadActivity.this, R.color.hwtxtreader_styleclor5), StyleTextColors[4],5));
                dialog.setContentView(bottom);
                dialog.show();
//                onPageSwitchSettingUi(mTxtReaderView.getTxtReaderContext().getTxtConfig().SwitchByTranslate);
            }
        });
        night.setOnClickListener(new StyleChangeClickListener(ContextCompat.getColor(ReadActivity.this, R.color.hwtxtreader_styleclor5), StyleTextColors[4],6));
        hori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ReadActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    Log.i("info", "landscape"); // 横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    newLoad();
                    hori.setText("横屏");
                } else if (ReadActivity.this.getResources().getConfiguration().orientation ==Configuration.ORIENTATION_PORTRAIT) {
                    Log.i("info", "portrait"); // 竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    newLoad();
                    hori.setText("纵屏");
                }
            }
        });
    }




    private void onDialogShow() {
        if (mTxtReaderView.getBackgroundColor() == getColor(R.color.hwtxtreader_styleclor5)){
            night.setText("白天");
            Drawable top = getDrawable(R.drawable.light_24dp);
            top.setBounds(0,0,top.getMinimumWidth(),top.getMinimumHeight());
            night.setCompoundDrawables(null,top,null,null);
            mMenuHolder.stylea.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.styleb.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.stylec.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.styled.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.stylee.setBackgroundColor(getColor(R.color.colorAccent));
        } else if(mTxtReaderView.getBackgroundColor() == getColor(R.color.hwtxtreader_styleclor1)){
                mMenuHolder.stylea.setBackgroundColor(getColor(R.color.colorAccent));
                mMenuHolder.styleb.setBackgroundColor(getColor(R.color.dialog_txt));
                mMenuHolder.stylec.setBackgroundColor(getColor(R.color.dialog_txt));
                mMenuHolder.styled.setBackgroundColor(getColor(R.color.dialog_txt));
                mMenuHolder.stylee.setBackgroundColor(getColor(R.color.dialog_txt));
        } else if(mTxtReaderView.getBackgroundColor() == getColor(R.color.hwtxtreader_styleclor2)){
            mMenuHolder.stylea.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.styleb.setBackgroundColor(getColor(R.color.colorAccent));
            mMenuHolder.stylec.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.styled.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.stylee.setBackgroundColor(getColor(R.color.dialog_txt));
        } else if(mTxtReaderView.getBackgroundColor() == getColor(R.color.hwtxtreader_styleclor3)){
            mMenuHolder.stylea.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.styleb.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.stylec.setBackgroundColor(getColor(R.color.colorAccent));
            mMenuHolder.styled.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.stylee.setBackgroundColor(getColor(R.color.dialog_txt));
        } else if(mTxtReaderView.getBackgroundColor() == getColor(R.color.hwtxtreader_styleclor4)){
            mMenuHolder.stylea.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.styleb.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.stylec.setBackgroundColor(getColor(R.color.dialog_txt));
            mMenuHolder.styled.setBackgroundColor(getColor(R.color.colorAccent));
            mMenuHolder.stylee.setBackgroundColor(getColor(R.color.dialog_txt));
        } else {

        }
    }

    private final int[] StyleTextColors = new int[]{
            Color.parseColor("#212421"),
            Color.parseColor("#2e3725"),
            Color.parseColor("#58501e"),
            Color.parseColor("#3a3529"),
            Color.parseColor("#5a5a5a")
    };
    //字体加粗的监听
    private class TextSettingClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mTxtReaderView.setTextBold(!isBold);
            onTextSettingUi(!isBold);
        }
    }

    private void onTextSettingUi(Boolean bold) {
        if (bold){
            this.bold.setBackgroundColor(getColor(R.color.colorAccent));
            isBold = bold;
        }else {
            this.bold.setBackgroundColor(getColor(R.color.statusbar));
            isBold = false;
        }
    }

    //滑页的监听

    private class SwitchSettingClickListener implements View.OnClickListener {
        private Boolean isSwitchTranslate = true;

        public SwitchSettingClickListener(Boolean pre) {
            isSwitchTranslate = pre;
        }

        @Override
        public void onClick(View view) {
            if (!isSwitchTranslate) {
                mTxtReaderView.setPageSwitchByCover();
            } else {
                mTxtReaderView.setPageSwitchByTranslate();
            }
            onPageSwitchSettingUi(isSwitchTranslate);
        }
    }

    private class ChapterChangeClickListener implements View.OnClickListener {
        private Boolean Pre = false;

        public ChapterChangeClickListener(Boolean pre) {
            Pre = pre;
        }

        @Override
        public void onClick(View view) {
            if (Pre) {
                mTxtReaderView.jumpToPreChapter();

            } else {
                mTxtReaderView.jumpToNextChapter();
            }
        }
    }

    //字体增加的监听
    private class TextChangeClickListener implements View.OnClickListener {
        private Boolean Add = false;

        public TextChangeClickListener(Boolean pre) {
            Add = pre;
        }

        @Override
        public void onClick(View view) {
            int textSize = mTxtReaderView.getTextSize();
            if (Add) {
                if (textSize + 2 <= TxtConfig.MAX_TEXT_SIZE) {
                    mTxtReaderView.setTextSize(textSize + 2);
                }
            } else {
                if (textSize - 2 >= TxtConfig.MIN_TEXT_SIZE) {
                    mTxtReaderView.setTextSize(textSize - 2);
                }
            }

        }
    }

    //主题变更的监听
    private class StyleChangeClickListener implements View.OnClickListener {
        private int BgColor;
        private int TextColor;
        private int ii;
        public StyleChangeClickListener(int bgColor, int textColor , int  i) {
            BgColor = bgColor;
            TextColor = textColor;
            ii = i;

        }

        @Override
        public void onClick(View view) {

            switch (ii){
                case 1:
                        night.setText("夜间");
                        Drawable top = getDrawable(R.drawable.night_24dp);
                        top.setBounds(0, 0, top.getMinimumWidth(), top.getMinimumHeight());
                        night.setCompoundDrawables(null, top, null, null);
                    mMenuHolder.stylea.setBackgroundColor(getColor(R.color.colorAccent));
                    mMenuHolder.styleb.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.stylec.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.styled.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.stylee.setBackgroundColor(getColor(R.color.dialog_txt));
                    mTxtReaderView.setStyle(BgColor, TextColor);
                    break;
                case 2:
                    night.setText("夜间");
                    Drawable top1 = getDrawable(R.drawable.night_24dp);
                    top1.setBounds(0, 0, top1.getMinimumWidth(), top1.getMinimumHeight());
                    night.setCompoundDrawables(null, top1, null, null);
                    mMenuHolder.stylea.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.styleb.setBackgroundColor(getColor(R.color.colorAccent));
                    mMenuHolder.stylec.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.styled.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.stylee.setBackgroundColor(getColor(R.color.dialog_txt));
                    mTxtReaderView.setStyle(BgColor, TextColor);
                    break;
                case 3:
                    night.setText("夜间");
                    Drawable top11 = getDrawable(R.drawable.night_24dp);
                    top11.setBounds(0, 0, top11.getMinimumWidth(), top11.getMinimumHeight());
                    night.setCompoundDrawables(null, top11, null, null);
                    mMenuHolder.stylea.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.styleb.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.stylec.setBackgroundColor(getColor(R.color.colorAccent));
                    mMenuHolder.styled.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.stylee.setBackgroundColor(getColor(R.color.dialog_txt));
                    mTxtReaderView.setStyle(BgColor, TextColor);
                    break;
                case 4:
                    night.setText("夜间");
                    Drawable top21 = getDrawable(R.drawable.night_24dp);
                    top21.setBounds(0, 0, top21.getMinimumWidth(), top21.getMinimumHeight());
                    night.setCompoundDrawables(null, top21, null, null);
                    mMenuHolder.stylea.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.styleb.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.stylec.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.styled.setBackgroundColor(getColor(R.color.colorAccent));
                    mMenuHolder.stylee.setBackgroundColor(getColor(R.color.dialog_txt));
                    mTxtReaderView.setStyle(BgColor, TextColor);
                    break;
                case 5:
                    night.setText("白天");
                    Drawable top31 = getDrawable(R.drawable.light_24dp);
                    top31.setBounds(0,0,top31.getMinimumWidth(),top31.getMinimumHeight());
                    night.setCompoundDrawables(null,top31,null,null);
                    mMenuHolder.stylea.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.styleb.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.stylec.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.styled.setBackgroundColor(getColor(R.color.dialog_txt));
                    mMenuHolder.stylee.setBackgroundColor(getColor(R.color.colorAccent));
                    mTxtReaderView.setStyle(BgColor, TextColor);
                    break;
                case 6:
                    if (night.getText().equals("夜间")){
                        night.setText("白天");
                        Drawable top4 = getDrawable(R.drawable.light_24dp);
                        top4.setBounds(0,0,top4.getMinimumWidth(),top4.getMinimumHeight());
                        night.setCompoundDrawables(null,top4,null,null);
                        mTxtReaderView.setStyle(BgColor,TextColor);
                    } else {
                        night.setText("夜间");
                        Drawable top4 = getDrawable(R.drawable.night_24dp);
                        top4.setBounds(0,0,top4.getMinimumWidth(),top4.getMinimumHeight());
                        night.setCompoundDrawables(null,top4,null,null);
                        mTxtReaderView.setStyle(getColor(R.color.hwtxtreader_styleclor1),StyleTextColors[0]);
                    }
                    break;
                    default:
                        break;
            }

        }
    }

    private class MenuHolder {
        TextView preChapter;
        TextView nextChapter;
        SeekBar seekBar;
        TextView font_small,font_big,fangzhen,huadong;
        View stylea,styleb,stylec,styled,stylee;
    }

    private void loadFile() {
        if (TextUtils.isEmpty(FilePath) || !(new File(FilePath).exists())) {
            Toast.makeText(this,"文件不存在",Toast.LENGTH_SHORT).show();
            return;
        }

        mTxtReaderView.loadTxtFile(FilePath, new ILoadListener() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
//                findViewById(R.id.loadView).setVisibility(View.GONE);
                float a = dbAction.getProgress(id) - 0.008f;
                mTxtReaderView.loadFromProgress(a);
                initWhenLoadDone();
                mTxtReaderView.setTouchListener(new TxtReaderBaseView.TouchListener() {
                    @Override
                    public void center() {
                        toggle();
                    }
                });
            }

            @Override
            public void onFail(TxtMsg txtMsg) {
                //加载失败信息
//                TextView t = findViewById(R.id.loding);
//                t.setText("加载失败");
//                Toast.makeText(ReadActivity.this,String.valueOf("加载失败"),Toast.LENGTH_SHORT).show();;
            }

            @Override
            public void onMessage(String message) {
                //加载过程信息
            }
        });
    }

    private void initWhenLoadDone() {
        FileName = mTxtReaderView.getTxtReaderContext().getFileMsg().FileName;
        if (mTxtReaderView.getTxtReaderContext().getTxtConfig().isSwitchByTranslate(this)) {
            mTxtReaderView.setPageSwitchByTranslate();
        } else {
            mTxtReaderView.setPageSwitchByCover();
        }
        if (mTxtReaderView.getBackgroundColor() == getColor(R.color.hwtxtreader_styleclor5)){
            night.setText("白天");
            Drawable top = getDrawable(R.drawable.light_24dp);
            top.setBounds(0,0,top.getMinimumWidth(),top.getMinimumHeight());
            night.setCompoundDrawables(null,top,null,null);
        } else {
            night.setText("夜间");
            Drawable top = getDrawable(R.drawable.night_24dp);
            top.setBounds(0,0,top.getMinimumWidth(),top.getMinimumHeight());
            night.setCompoundDrawables(null,top,null,null);
        }

        //章节初始化
        if (mTxtReaderView.getChapters() != null) {
//            WindowManager m = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//            DisplayMetrics metrics = new DisplayMetrics();
//            m.getDefaultDisplay().getMetrics(metrics);
            int ViewHeight = getResources().getDisplayMetrics().heightPixels * 3 / 5;
            mChapterListPop = new ChapterList(this, ViewHeight, mTxtReaderView.getChapters(), mTxtReaderView.getTxtReaderContext().getParagraphData().getCharNum());
            mChapterListPop.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    IChapter chapter = (IChapter) mChapterListPop.getAdapter().getItem(i);
                    mChapterListPop.dismiss();
                    mTxtReaderView.loadFromProgress(chapter.getStartParagraphIndex(), 0);
                }
            });
        }
    }

    private void onPageSwitchSettingUi(Boolean isFangzhen) {
        if (isFangzhen) {
            mMenuHolder.huadong.setTextColor(getColor(R.color.colorAccent));
            mMenuHolder.fangzhen.setTextColor(getColor(R.color.dialog_txt));
        } else {
            mMenuHolder.huadong.setTextColor(getColor(R.color.dialog_txt));
            mMenuHolder.fangzhen.setTextColor(getColor(R.color.colorAccent));
        }
    }



    private void getIntentData() {
        id = getIntent().getIntExtra("id",0);
        FilePath = getIntent().getStringExtra("FilePath");
        FileName = getIntent().getStringExtra("FileName");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    @Override
    protected void onDestroy() {
        mTxtReaderView.getTxtReaderContext().Clear();
        mHandler.removeCallbacksAndMessages(null);
        if (mChapterListPop != null) {
            if (mChapterListPop.isShowing()) {
                mChapterListPop.dismiss();
            }
            mChapterListPop.onDestroy();
        }
        mMenuHolder = null;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }


    private void hide() {
        mControlsView.setVisibility(View.GONE);
        mVisible = false;
        //在计划时间内隐藏任务栏和导航栏
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // 在计划时间内展示任务栏和导航栏
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


}
