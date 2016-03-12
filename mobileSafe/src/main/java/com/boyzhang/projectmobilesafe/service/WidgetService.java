package com.boyzhang.projectmobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.receiver.MobileSafeAppWidget;
import com.boyzhang.projectmobilesafe.utils.ApplicationUtils;
import com.boyzhang.projectmobilesafe.utils.StorageUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 桌面小控件的服务
 */
public class WidgetService extends Service {

    private AppWidgetManager widgetManager;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //拿到Widget管理器对象
        widgetManager = AppWidgetManager.getInstance(WidgetService.this);

        /**
         * 每个5秒钟更新一次Widget
         */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //参数一: 上下文
                //参数二: 表示由哪一个广播处理当前的桌面小控件
                ComponentName componentName = new ComponentName(getApplicationContext(), MobileSafeAppWidget.class);

                //把Widget的View拿到,远程的View
                //参数:
                // 参数一:应用包名
                // 参数二:widget的布局应用
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.widget_mobilesafe);

                int count = ApplicationUtils.getProcessCount(WidgetService.this);
                String availMem = StorageUtils.getRamInfo(WidgetService.this)[2];

                Intent intent = new Intent();
                intent.setAction("com.boyzhang.projectmobilesafe.killAllProcess");//指定点击事件启动的广播
                intent.putExtra("clickType", "click1");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                //-------------------更新数据--------------------
                remoteViews.setTextViewText(R.id.tv_widget_process_count, "进程数:" + String.valueOf(count));//设置正在运行的进程数量
                remoteViews.setOnClickPendingIntent(R.id.b_widget_clear, pendingIntent);//设置点击事件
                remoteViews.setTextViewText(R.id.tv_widget_process_memory, "可用内存:" + availMem);
                //---------------------------------------------

                //更新一下Widget
                widgetManager.updateAppWidget(componentName, remoteViews);
            }
        }, 0, 5000);
    }
}
