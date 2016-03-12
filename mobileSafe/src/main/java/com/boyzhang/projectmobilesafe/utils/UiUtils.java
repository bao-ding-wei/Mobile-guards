package com.boyzhang.projectmobilesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-03-05 10:59
 * <p/>
 * 描述 :  和UI相关的工具类
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class UiUtils {

    public static void showToast(final Activity activity, final String text) {

        //主线程中
        if (Thread.currentThread().getName().equals("main")) {
            Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
        } else {
            //子线程中
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
