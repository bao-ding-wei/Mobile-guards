package com.boyzhang.projectmobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.bean.AppInfos;
import com.boyzhang.projectmobilesafe.bean.RunningAppInfos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-03-02 17:05
 * <p/>
 * 描述 : 获取到所有应用程序的---应用名,包名,大小,图标
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class ApplicationUtils {

    /**
     * 获取到所有的应用信息
     *
     * @param context
     * @return
     */
    public static List<AppInfos> getApplicationInfos(Context context) {

        List<AppInfos> appInfos = new ArrayList<>();

        //获取包管理器
        PackageManager packageManager = context.getPackageManager();

        //获取到所有的安装的包
        //参数:Additional option flags. Use any combination of  表示可以使用任意参数,用0
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

        for (PackageInfo installedPackage : installedPackages) {

            AppInfos appInfo = new AppInfos();

            //拿到LOGO
            Drawable icon = installedPackage.applicationInfo.loadIcon(packageManager);

            //拿到应用名称
            String appName = installedPackage.applicationInfo.loadLabel(packageManager).toString();

            //获取到应用程序的包名
            String packageName = installedPackage.packageName;

            //获取到apk资源路径
            String sourceDir = installedPackage.applicationInfo.sourceDir;
            //获取到资源大小-即App占用空间
            File file = new File(sourceDir);
            long appSize = file.length();

            /*
            System.out.println("---------------------------------");
            System.out.println("程序名称:" + appName);
            System.out.println("程序包名:" + packageName);
            System.out.println("程序大小:" + appSize);
            */

            int flags = installedPackage.applicationInfo.flags;

            //判断是系统应用还是第三方应用
            if (isSystemApp(flags)) {
                //表示系统App
                appInfo.setAppType(AppInfos.APP_SYSTEM);
            } else {
                appInfo.setAppType(AppInfos.APP_USER);
            }

            //判断安装路径是ROM中还是SD中
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //表示在SD卡中
                appInfo.setAppLocation(AppInfos.LOCATION_SD);
            } else {
                appInfo.setAppLocation(AppInfos.LOCATION_ROM);
            }

            appInfo.setAppLogo(icon);
            appInfo.setAppName(appName);
            appInfo.setAppSize(appSize);
            appInfo.setAppPackageName(packageName);

            //增加到集合
            appInfos.add(appInfo);

        }

        return appInfos;
    }

    /**
     * 根据包名获取应用信息
     *
     * @param packageNames
     * @return
     */
    public static List<AppInfos> getAppInfoByPackageName(Context context, List<String> packageNames) {

        List<AppInfos> appInfos = new ArrayList<>();
        Drawable icon = null;//应用图标
        String appName = null;//应用名称
        int appType = -1;
        long appSize;

        //获取包管理器
        PackageManager packageManager = context.getPackageManager();

        for (String packageName : packageNames) {

            AppInfos appInfo = new AppInfos();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);

                icon = packageInfo.applicationInfo.loadIcon(packageManager);//拿到图标
                appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();//拿到APP名称

                int flags = packageInfo.applicationInfo.flags;//应用标记
                //判断是系统应用还是第三方应用
                if (isSystemApp(flags)) {
                    //表示系统App
                    appType = RunningAppInfos.APP_TYPE_SYS;
                } else {
                    appType = RunningAppInfos.APP_TYPE_USER;
                }

                //判断安装路径是ROM中还是SD中
                if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                    //表示在SD卡中
                    appInfo.setAppLocation(AppInfos.LOCATION_SD);
                } else {
                    appInfo.setAppLocation(AppInfos.LOCATION_ROM);
                }

                //获取到apk资源路径
                String sourceDir = packageInfo.applicationInfo.sourceDir;
                //获取到资源大小-即App占用空间
                File file = new File(sourceDir);
                appSize = file.length();

                appInfo.setAppName(appName);
                appInfo.setAppLogo(icon);
                appInfo.setAppType(appType);
                appInfo.setAppSize(appSize);
                appInfo.setAppPackageName(packageName);

                //增加到集合
                appInfos.add(appInfo);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        return appInfos;
    }

    /**
     * 拿到所有正在运行的应用信息
     *
     * @param context
     * @return
     */
    public static List<RunningAppInfos> getRunningAppInfos(Context context) {

        Drawable icon = null;//应用图标

        String appName = null;//应用名称

        String packageName;//应用包名

        int pid = -1;

        int memoSize = 0;//占用内存大小

        int appType = -1;

        int positionID = 0;

        List<RunningAppInfos> infosArrayList = new ArrayList<>();

        //拿到进程管理器
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //拿到运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        //拿到包管理器
        PackageManager packageManager = context.getPackageManager();

        for (ActivityManager.RunningAppProcessInfo runningAppProcesse : runningAppProcesses) {

            packageName = runningAppProcesse.processName;//拿到进程名(包名)

            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);//拿到包信息

                icon = packageInfo.applicationInfo.loadIcon(packageManager);//拿到图标
                appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();//拿到APP名称

                int flags = packageInfo.applicationInfo.flags;//应用标记
                //判断是系统应用还是第三方应用
                if (isSystemApp(flags)) {
                    //表示系统App
                    appType = RunningAppInfos.APP_TYPE_SYS;
                } else {
                    appType = RunningAppInfos.APP_TYPE_USER;
                }

                pid = runningAppProcesse.pid;//拿到进程ID
                Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(new int[]{pid});
                memoSize = memoryInfo[0].getTotalPrivateDirty() * 1024;//获取到进程占用内存大小

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                //如果捕获到异常就帮其设置一个默认的图标和名称----------------!!!!!!这一点很重要,否则是不对的
                icon = context.getResources().getDrawable(R.drawable.default_app_logo);
                appName = "UNKNOW APP";
                appType = RunningAppInfos.APP_TYPE_SYS;

            } finally {
                //创建一个RunningAppInfos
                RunningAppInfos runningAppInfos = new RunningAppInfos(icon, appName, packageName, pid, memoSize, appType, false, positionID);
                //添加到集合
                infosArrayList.add(runningAppInfos);
            }
            positionID++;
        }

        //SystemClock.sleep(5000);

        return infosArrayList;
    }

    /**
     * 返回进程的总个数
     *
     * @param context
     * @return
     */
    public static int getProcessCount(Context context) {
        // 得到进程管理者
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        // 获取到当前手机上面所有运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager
                .getRunningAppProcesses();

        // 获取手机上面一共有多少个进程
        return runningAppProcesses.size();
    }

    /**
     * 判断是否系统APP
     *
     * @param flags
     * @return
     */
    private static boolean isSystemApp(int flags) {
        /**
         * 方法一:
         * 第三方app放在data/data下,系统应用放在system/data下
         * 可以判断路径是否包含关键路径来区别应用类型
         */
        /**
         * 方法二:
         * 采用App的flags进行与运算
         */

        //判断是系统应用还是第三方应用
        if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
            //表示系统App
            return true;
        } else {
            return false;
        }
    }
}
