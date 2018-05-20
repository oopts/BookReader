package com.example.texttest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

/**
 * Author LYJ
 * Created on 2017/2/3.
 * Time 10:43
 */

public class LoadingDialog extends Dialog {

    private AnimationDrawable animation;//动画
    public LoadingDialog(Context context) {
        super(context,R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);//点击外侧不关闭Dialog
        setContentView(R.layout.loading_dialog);//加载布局
        //获取动画
        animation = (AnimationDrawable) ((ImageView)
                findViewById(R.id.loading)).getDrawable();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("LoadingDialog","onStart: ");
        animation.start();//播放动画
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w("LoadingDialog","onStop: ");
        animation.stop();//停止动画
    }

}
