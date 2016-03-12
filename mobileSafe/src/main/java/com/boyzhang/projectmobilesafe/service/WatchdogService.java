package com.boyzhang.projectmobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.boyzhang.projectmobilesafe.activity.EnterPasswordActivity;
import com.boyzhang.projectmobilesafe.db.dao.AppLockDao;

import java.util.List;

/**
 * 看门狗服务
 */
public class WatchdogService extends Service {

    public volatile boolean isStart = true;

    public String tempStopProtectPackageName = null;
    private ActivityManager activityManager;
    private MB mbReceiver;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    class MB extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (action.equals("com.boyzhang.mobileSafe.receiver.watchDog.stop")) {

                tempStopProtectPackageName = intent.getStringExtra("packageName");//等到需要放行的APP包名

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                tempStopProtectPackageName = null;
                // 让狗休息
                //isStart = false;
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //让狗继续干活
                //isStart = true;
                //startDog();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //注册一个广播
        mbReceiver = new MB();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.boyzhang.mobileSafe.receiver.watchDog.stop");//当发出停止服务的广播时就停止
        /**
         * 当屏幕锁住的时候。狗就休息
         * 屏幕解锁的时候。让狗活过来
         */
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mbReceiver, filter);

        //获取到进程管理器
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        startDog();//开启狗
    }

    public void startDog() {
        //开启前一个子线程实现看门狗
        new Thread() {
            @Override
            public void run() {
                super.run();

                while (isStart) {
                    //获取到所有运行中的进程
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

                    //System.out.println(runningAppProcesses.size()+"========"+runningAppProcesses);

                    //拿到任务栈里面最上面的进程,即刚刚打开的进程
                    ActivityManager.RunningAppProcessInfo runningTopAppProcessInfo = runningAppProcesses.get(0);

                    String processName = runningTopAppProcessInfo.processName;//获取到进程包名

                    //检查是否是加锁的应用
                    AppLockDao appLockDao = new AppLockDao(WatchdogService.this);
                    boolean isLocked = appLockDao.isLocked(processName);
                    if (isLocked) {

                        if (processName.equals(tempStopProtectPackageName)) {
                            continue;//如果是临时放开的程序就跳过他
                        }

                        //如果被锁的应用就启动输入密码Activity
                        //System.out.println("锁住了");
                        //启动输入面膜界面
                        Intent intent = new Intent(WatchdogService.this, EnterPasswordActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("packageName", processName);
                        startActivity(intent);
                    }
                }


            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //停止广播
        unregisterReceiver(mbReceiver);

        isStart = false;//停止看门狗
    }
}
