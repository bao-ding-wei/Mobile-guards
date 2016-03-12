package com.boyzhang.projectmobilesafe.bean;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.boyzhang.projectmobilesafe.utils.StorageUtils;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-03-02 18:06
 * <p/>
 * 描述 : 应用信息基类
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class AppInfos {

    public static int APP_SYSTEM = 0;//表示系统APP
    public static int APP_USER = 1;//表示用户APP

    public static int LOCATION_ROM = 2;//表示安装在ROM中
    public static int LOCATION_SD = 3;//表示安装在SD卡中

    //应用LOGO
    private Drawable appLogo;

    //应用名称
    private String appName;

    //应用大小
    private long appSize;

    //应用包名
    private String appPackageName;

    //应用类型,是系统应用还是用户app
    private int appType;

    //APP安装的位置,是ROM中还是Sdcard中
    private int appLocation;

    public AppInfos(Drawable appLogo, String appName, long appSize, String appPackageName, int appType, int appLocation) {
        this.appLogo = appLogo;
        this.appName = appName;
        this.appSize = appSize;
        this.appPackageName = appPackageName;
        this.appType = appType;
        this.appLocation = appLocation;
    }

    public AppInfos() {
    }

    public Drawable getAppLogo() {
        return appLogo;
    }

    public void setAppLogo(Drawable appLogo) {
        this.appLogo = appLogo;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 返回的大小是经过Format的
     *
     * @param context
     * @return
     */
    public String getAppSize(Context context) {
        return StorageUtils.formatSize(context, appSize);
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public int getAppLocation() {
        return appLocation;
    }

    public void setAppLocation(int appLocation) {
        this.appLocation = appLocation;
    }

    @Override
    public String toString() {
        return "AppInfos{" +
                "appLogo=" + appLogo +
                ", appName='" + appName + '\'' +
                ", appSize=" + appSize +
                ", appPackageName='" + appPackageName + '\'' +
                ", appType=" + appType +
                ", appLocation=" + appLocation +
                '}';
    }
}
