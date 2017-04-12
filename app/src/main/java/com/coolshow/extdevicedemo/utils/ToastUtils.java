package com.coolshow.extdevicedemo.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * 吐司工具类
 */
public class ToastUtils {
    public static void showToast(final Context context, final String text) {
        if("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }else{
            // 子线程
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
