package com.boyzhang.projectmobilesafe.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-03-08 13:18
 * <p/>
 * 描述 :  病毒数据库Dao
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class AntivirusDao {

    private static String antivirusPath = "data/data/com.boyzhang.projectmobilesafe/files/antivirus.db";

    /**
     * 检测文件的特征码是否在病毒库中
     *
     * @param md5Value 软件的Md5特征码
     * @return 如果不是病毒文件就返回 null , 如果是病毒就返回String[2] str[0]:病毒名称 str[1]:病毒描述
     */
    public static String[] checkFileAntivirus(String md5Value) {

        String[] antivirusInfo = null;

        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                antivirusPath, null, SQLiteDatabase.OPEN_READONLY);//拿到一个只读数据库

        String sql = "select name,desc from datable where md5 = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{md5Value});

        if (cursor != null) {
            if (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String desc = cursor.getString(cursor.getColumnIndex("desc"));

                antivirusInfo = new String[2];
                antivirusInfo[0] = name;
                antivirusInfo[1] = desc;
            }
        }
        db.close();
        cursor.close();
        SystemClock.sleep(200);
        return antivirusInfo;
    }

    /**
     * 更新病毒数据库
     *
     * @param md5   病毒特征码
     * @param type  病毒类型
     * @param name  病毒名称
     * @param desc  病毒描述
     * @return      返回是否更新成功
     */
    public static boolean addAntivirus(String md5, String type, String name, String desc) {

        SQLiteDatabase db = SQLiteDatabase.openDatabase(
                antivirusPath, null, SQLiteDatabase.OPEN_READWRITE);//拿到一个可读可写的数据库

        ContentValues values = new ContentValues();
        values.put("md5", md5);
        values.put("type", type);
        values.put("name", name);
        values.put("desc", desc);

        long resSize = db.insert("datable", null, values);

        if (resSize >= 1) {
            return true;//插入成功
        }

        return false;
    }
}
