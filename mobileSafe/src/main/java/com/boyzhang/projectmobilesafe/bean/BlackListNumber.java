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
 * 创建时间 : 2016-2-28 下午4:36:13
 * <p/>
 * 描述 : 黑名单对象
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class BlackListNumber {

    public final static String MODE_PHONE = "1";
    public final static String MODE_SMS = "2";
    public final static String MODE_ALL = "3";

    //电话号码
    private String number;

    //拦截模式
    private String mode;

    public BlackListNumber(String number, String mode) {
        this.number = number;
        this.mode = mode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackListNumber{" +
                "number='" + number + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
