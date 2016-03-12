package com.boyzhang.projectmobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;

import com.boyzhang.projectmobilesafe.bean.BlackListNumber;

import java.util.ArrayList;

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
 * 描述 : 黑名单数据库
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class BlacklistDao {


    private final DaoOpenHelper helper;

    public BlacklistDao(Context context) {

        //创建DaoOpenHelper对象
        helper = new DaoOpenHelper(context);

    }

    /**
     * 获取黑名单数据库的条目数
     *
     * @return
     */
    public int getCount() {

        int count = 0;

        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select count(*) from blacklist", new String[]{});
        if (cursor != null) {
            cursor.moveToNext();
            count = cursor.getInt(0);
        }

        return count;
    }

    /**
     * 新建黑名单拦截
     *
     * @param number
     * @param mode
     * @return 返回是否插入成功
     */
    public boolean insertItem(String number, String mode) {

        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long blacklist = database.insert("blacklist", null, values);

        //释放资源
        database.close();

        if (blacklist == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据电话号码删除拦截
     *
     * @param number
     * @return 返回是否删除成功
     */
    public boolean deleteItem(String number) {

        SQLiteDatabase database = helper.getWritableDatabase();
        int blacklist = database.delete("blacklist", "number = ?", new String[]{number});

        //释放资源
        database.close();

        if (blacklist == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据电话号码更新拦截模式
     *
     * @param number
     * @return 返回修改是否成功
     */
    public boolean updateMode(String number, String mode) {

        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        int blacklist1 = database.update("blacklist", values, "number = ?", new String[]{number});

        //释放资源
        database.close();

        if (blacklist1 == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 根据电话号码进行查找模式
     *
     * @param number
     * @return
     */
    public String selectMode(String number) {

        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query("blacklist", new String[]{"mode"}, "number = ?", new String[]{number}, null, null, null);
        if (cursor != null) {
            String mode = null;
            while (cursor.moveToNext()) {
                mode = cursor.getString(cursor.getColumnIndex("mode"));
                break;
            }
            //关闭资源
            cursor.close();
            database.close();

            return mode;
        }
        return null;
    }

    /**
     * 查询所有的黑名单
     *
     * @return
     */
    public ArrayList<BlackListNumber> selectAll() {
        ArrayList<BlackListNumber> al = null;
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.query("blacklist", new String[]{"number", "mode"}, null, null, null, null, null);
        if (cursor != null) {
            al = new ArrayList<>();
            String number;
            String mode;
            while (cursor.moveToNext()) {
                number = cursor.getString(cursor.getColumnIndex("number"));
                mode = cursor.getString(cursor.getColumnIndex("mode"));
                if (number != null && mode != null) {
                    BlackListNumber blackListNumber = new BlackListNumber(number, mode);
                    al.add(blackListNumber);
                }
            }

        }

        //释放资源
        cursor.close();
        database.close();

        //模拟网络获取数据,sleep2秒
        //SystemClock.sleep是Android封装的Thread.sleep()好处是不要try...catch
        //SystemClock.sleep(2000);

        return al;
    }

    /**
     * 分页获取数据
     *
     * @param currentPage 当前页码
     * @param pageSize    每页显示的数据量
     * @return
     */
    public ArrayList<BlackListNumber> selectByPage(int currentPage, int pageSize) {

        ArrayList<BlackListNumber> al = null;

        SQLiteDatabase database = helper.getWritableDatabase();

        /**
         * ? 最多显示的数目,及每页显示的数目
         * ? 表示跳过的数量,第0页跳过0条从(0*pageSize,pageSize),第二页跳过currentPage*1条从(1*pageSize+1,pageSize)
         */
        Cursor cursor = database.rawQuery("select number,mode from blacklist limit ? offset ?"
                , new String[]{String.valueOf(pageSize), String.valueOf(currentPage * pageSize)});

        if (cursor != null) {

            al = new ArrayList<>();

            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex("number"));
                String mode = cursor.getString(cursor.getColumnIndex("mode"));

                if (number != null && mode != null) {
                    BlackListNumber blackListNumber = new BlackListNumber(number, mode);
                    al.add(blackListNumber);
                }
            }
        }

        //释放资源
        database.close();
        cursor.close();

        return al;
    }

    /**
     * 分批次获取数据
     *
     * @param startIndex 开始的条目索引
     * @param maxCount   显示的最大条目数
     * @return
     */
    public ArrayList<BlackListNumber> selectByBatches(int startIndex, int maxCount) {

        ArrayList<BlackListNumber> al = null;

        SQLiteDatabase database = helper.getWritableDatabase();

        /**
         * ? 最多显示的数目,及每页显示的数目
         * ? 表示跳过的数量,第0页跳过0条从(0*pageSize,pageSize),第二页跳过currentPage*1条从(1*pageSize+1,pageSize)
         */
        Cursor cursor = database.rawQuery("select number,mode from blacklist limit ? offset ?"
                , new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});

        if (cursor != null) {

            al = new ArrayList<>();

            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex("number"));
                String mode = cursor.getString(cursor.getColumnIndex("mode"));

                if (number != null && mode != null) {
                    BlackListNumber blackListNumber = new BlackListNumber(number, mode);
                    al.add(blackListNumber);
                }
            }
        }

        //释放资源
        database.close();
        cursor.close();

        //SystemClock.sleep(5000);

        return al;
    }

}
