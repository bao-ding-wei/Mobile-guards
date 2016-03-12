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
 * 创建时间 : 2016-03-05 22:41
 * <p/>
 * 描述 :
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class RunningAppInfos {

    public static final int APP_TYPE_USER = 0;
    public static final int APP_TYPE_SYS = 1;

    private Drawable icon;//应用图标

    private String appName;//应用名称

    private String packageName;//应用包名

    private int PID;//应用的进程ID

    private int memoSize;//占用内存大小

    private int appType;//是系统App还是用户App

    private int positionID;//节点的id

    private boolean isChecked;

    public RunningAppInfos(Drawable icon, String appName, String packageName, int PID, int memoSize, int appType, boolean isChecked, int positionID) {
        this.icon = icon;
        this.appName = appName;
        this.packageName = packageName;
        this.PID = PID;
        this.memoSize = memoSize;
        this.appType = appType;
        this.positionID = positionID;
        this.isChecked = isChecked;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
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

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public String getMemoSize(Context context) {
        return Formatter.formatFileSize(context, this.memoSize);
    }

    public void setMemoSize(int memoSize) {
        this.memoSize = memoSize;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getPositionID() {
        return positionID;
    }

    public void setPositionID(int positionID) {
        this.positionID = positionID;
    }

    @Override
    public String toString() {
        return "RunningAppInfos{" +
                "icon=" + icon +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", PID=" + PID +
                ", memoSize=" + memoSize +
                ", appType=" + appType +
                ", positionID=" + positionID +
                ", isChecked=" + isChecked +
                '}';
    }
}
