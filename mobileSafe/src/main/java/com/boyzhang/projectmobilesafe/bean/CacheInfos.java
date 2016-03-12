package com.boyzhang.projectmobilesafe.bean;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-03-11 23:00
 * <p/>
 * 描述 :  应用缓存的信息
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class CacheInfos {

    private long cacheSize;
    private String appName;
    private String packageName;
    private Drawable icon;

    public CacheInfos(long cacheSize, String appName, String packageName, Drawable icon) {
        this.cacheSize = cacheSize;
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
    }

    public String getCacheSize(Context context) {
        return Formatter.formatFileSize(context, cacheSize);
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "CacheInfos{" +
                "cacheSize=" + cacheSize +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", icon=" + icon +
                '}';
    }
}
