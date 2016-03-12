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
 * 创建时间 : 2016-03-12 19:01
 * <p/>
 * 描述 :
 * <p/>   流量统计的JavaBean
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class TraffficInfos {

    private String appName;

    private String packageName;

    private Drawable icon;

    private long uploadTraffic;

    private long downloadTraffic;

    private long totalTraffic;

    public TraffficInfos(String appName, String packageName, Drawable icon, long uploadTraffic, long downloadTraffic, long totalTraffic) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.uploadTraffic = uploadTraffic;
        this.downloadTraffic = downloadTraffic;
        this.totalTraffic = totalTraffic;
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

    public String getUploadTraffic(Context context) {
        return Formatter.formatFileSize(context, uploadTraffic);
    }

    public void setUploadTraffic(long uploadTraffic) {
        this.uploadTraffic = uploadTraffic;
    }

    public String getDownloadTraffic(Context context) {
        return Formatter.formatFileSize(context, downloadTraffic);
    }

    public void setDownloadTraffic(long downloadTraffic) {
        this.downloadTraffic = downloadTraffic;
    }

    public String getTotalTraffic(Context context) {
        return Formatter.formatFileSize(context, totalTraffic);
    }

    public void setTotalTraffic(long totalTraffic) {
        this.totalTraffic = totalTraffic;
    }

    @Override
    public String toString() {
        return "TraffficInfos{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", icon=" + icon +
                ", uploadTraffic=" + uploadTraffic +
                ", downloadTraffic=" + downloadTraffic +
                ", totalTraffic=" + totalTraffic +
                '}';
    }
}
