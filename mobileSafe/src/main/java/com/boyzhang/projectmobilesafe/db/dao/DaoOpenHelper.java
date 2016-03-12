package com.boyzhang.projectmobilesafe.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
 * 描述 : SQLiteOpenHelper继承类,用于数据库的操作
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class DaoOpenHelper extends SQLiteOpenHelper {


    public DaoOpenHelper(Context context) {
        //创建数据库
        super(context, "mobileSafe.db", null, 3);
    }

    /**
     * 创建数据表
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建黑名单拦截数据表
        /**
         * blacklist 表名
         * _id 主键自增长
         * number 电话号码
         * mode 拦截模式 电话拦截/短信拦截
         */
        db.execSQL("create table blacklist (_id integer primary key autoincrement,number varchar(20) unique,mode varchar(2))");

        /**
         * 程序锁表
         */
        db.execSQL("create table appLock (_id integer primary key autoincrement,packagename varchar(50) unique)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
