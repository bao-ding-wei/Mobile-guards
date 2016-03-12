package com.boyzhang.projectmobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
 * 创建时间 : 2016-03-10 11:31
 * <p/>
 * 描述 : 程序锁的Dao
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class AppLockDao {

    private final DaoOpenHelper helper;

    public AppLockDao(Context context) {

        //创建DaoOpenHelper对象
        helper = new DaoOpenHelper(context);

    }

    /**
     * 添加程序到程序锁数据库中
     *
     * @param packageName 程序包名
     * @return
     */
    public boolean addAppToLock(String packageName) {

        boolean flag = false;

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("packageName", packageName);

        long res = db.insert("appLock", null, values);

        if (res != -1) {
            flag = true;
        }

        //关闭资源
        db.close();

        return flag;
    }

    /**
     * 获取到所有的加锁的APP信息
     *
     * @return
     */
    public List<String> getAllLockedAppPackageName() {

        List<String> appInfosList = null;

        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.query("appLock", new String[]{"packagename"}, null, null, null, null, null);

        if (cursor != null) {

            appInfosList = new ArrayList<>();

            while (cursor.moveToNext()) {

                String packageName = cursor.getString(cursor.getColumnIndex("packagename"));

                appInfosList.add(packageName);
            }
        }

        //关闭资源
        db.close();
        cursor.close();

        return appInfosList;
    }

    /**
     * 从已经加锁中删除一个
     *
     * @param packageName 应用包名
     * @return
     */
    public boolean deleteFromLocked(String packageName) {

        boolean flag = false;

        SQLiteDatabase db = helper.getWritableDatabase();

        int res = db.delete("appLock", "packagename = ?", new String[]{packageName});

        if (res > 0) {
            flag = true;
        }

        //关闭资源
        db.close();

        return flag;
    }

    /**
     * 根据包名判断一个APP是否是受保护状态
     *
     * @param packageName 包名
     * @return
     */
    public boolean isLocked(String packageName) {

        boolean flag = false;

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query("appLock", new String[]{"packagename"}, "packageName = ?", new String[]{packageName}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                if (cursor.getString(cursor.getColumnIndex("packagename")) != null) {
                    flag = true;
                }
            }
        }

        //关闭资源
        db.close();
        cursor.close();

        return flag;
    }
}
