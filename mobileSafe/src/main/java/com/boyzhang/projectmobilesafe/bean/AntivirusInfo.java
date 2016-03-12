package com.boyzhang.projectmobilesafe.bean;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-03-09 14:21
 * <p/>
 * 描述 : 病毒信息JavaBean
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class AntivirusInfo {

    public AntivirusInfo() {
    }

    public String md5;
    public String type;
    public String name;
    public String desc;

    @Override
    public String toString() {
        return "AntivirusInfo{" +
                "md5='" + md5 + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
