package com.boyzhang.projectmobilesafe.receiver;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.boyzhang.projectmobilesafe.service.WidgetService;

import java.util.List;

/**
 * 桌面小控件
 * 1.步骤:
 * ①在清单文件中添加空间receiver---配置元数据
 * ②创建布局文件和广播接收者
 * <p/>
 * -------------广播的生命周期只有10秒-------------------
 * 需要注意的是:Widget的receiver的生命周期只有10秒所以耗时操作必须放到服务中------------!!!!!!!!@@#####$$$$%%%%%%^%
 * <p/>
 * <p/>
 * MobileSafeAppWidget -> WidgetService -> 点击 -> MobileSafeAppWidget.killAllTask()
 * <p/>
 * MIUI中貌似不能点击Widget弹Toast
 */
public class MobileSafeAppWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String clickType = intent.getStringExtra("clickType");

        if (clickType != null) {
            if (clickType.equals("click1")) {
                killAllTask(context);
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------------
    //---------------------------------------------Widget的生命周期方法-----------------------------------------------
    //--------------------------------------------------------------------------------------------------------------

    /**
     * 第一次创建的时候才会调用当前的生命周期的方法-----相当于Activity中的onCreate()
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        //System.out.println("onEnabled");

        //当Widget开启时就启动Widget服务
        Intent intent = new Intent(context, WidgetService.class);
        context.startService(intent);
    }

    /**
     * 当桌面上面"所有的"桌面小控件都删除的时候才调用当前这个方法-----相当于Activity中的onDestroy()
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        //System.out.println("onDisabled");

        //当Widget移除时候就销毁Widget服务
        Intent intent = new Intent(context, WidgetService.class);
        context.stopService(intent);
    }

    /**
     * 每次有新的桌面小控件生成的时候都会调用-----添加多个桌面小控件时
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //System.out.println("onUpdate");
    }

    /**
     * 每次删除桌面小控件的时候都会调用的方法-----多个小控件删除一个时
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        //System.out.println("onDeleted");
    }
    //--------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------


    /**
     * 杀死所有的进程
     */
    public void killAllTask(Context context) {
        Toast.makeText(context, "收到消息", Toast.LENGTH_SHORT).show();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //得到所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        //迭代杀死进程
        for (ActivityManager.RunningAppProcessInfo runningAppProcesse : runningAppProcesses) {

            activityManager.killBackgroundProcesses(runningAppProcesse.processName);
        }
        Toast.makeText(context, "清理完成", Toast.LENGTH_SHORT).show();
    }
}
