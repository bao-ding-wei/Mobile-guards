package com.boyzhang.projectmobilesafe.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-03-05 19:56
 * <p/>
 * 描述 :  Android的桌面图标工具类
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class ShortcutUtils {

    /**
     * 创建快捷图标
     *
     * @param context      上下文对象
     * @param shortcutName 快捷图标名称
     * @param doWhatIntent 快捷图标的点击意图
     * @param shortcutIcon 快捷图标的Icon
     * @param isDuplicate  是否允许多个快捷方式的副本
     */
    public static void createShortcut(Context context, String shortcutName, Intent doWhatIntent, Bitmap shortcutIcon, boolean isDuplicate) {
        /**
         * 创建桌面快捷方式的步骤:
         *          1.创建桌面图标干什么
         *          2.桌面图标叫什么名字
         *          3.桌面图标长什么样子(图标)
         * 创建桌面图标靠发送广播----见launcher2
         *
         * 需要权限:
         *          com.android.launcher.permission.INSTALL_SHORTCUT
         */

        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        // 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
        shortcutIntent.putExtra("duplicate", isDuplicate);

        /*
        //创建快捷方式意图----需要使隐式意图
        Intent doWhatIntent = new Intent();
        doWhatIntent.setAction("com.boyzhang.projectmobilesafe.home");
        doWhatIntent.addCategory("android.intent.category.DEFAULT");
        */

        //----------------------------------------------------------------------
        //设置快捷方式名称----叫什么
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);

        //设置快捷方式Icon----长什么样
        //shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.app_logo));
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, shortcutIcon);

        //设置快捷方式意图----干什么
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, doWhatIntent);
        //----------------------------------------------------------------------

        //发送广播
        context.sendBroadcast(shortcutIntent);
    }

    /**
     * 删除快捷方式
     *
     * @param context      上下文对象
     * @param shortcutName 快捷方式名称
     *                     <p/>
     *                     需要权限:
     *                     com.android.launcher.permission.UNINSTALL_SHORTCUT
     */
    public static void deleteShortcut(Context context, String shortcutName, Intent doWhatIntent, boolean isDuplicate) {

        Intent shortcutIntent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 是否可以有多个快捷方式的副本，参数如果是true就可以生成多个快捷方式，如果是false就不会重复添加
        shortcutIntent.putExtra("duplicate", isDuplicate);
        //快捷方式的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
        /**删除和创建需要对应才能找到快捷方式并成功删除**/

        //意图要与创建的一样才能删除
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, doWhatIntent);
        context.sendBroadcast(shortcutIntent);
    }

    /**
     * 判断快捷图标是否在数据库中已存在
     * <p/>
     * 需要权限:
     * com.android.launcher.permission.READ_SETTINGS
     */
    public static boolean isExist(Context context, String shortcutName) {
        boolean isExist = false;
        int version = getSdkVersion();
        Uri uri;
        if (version < 2.0) {
            uri = Uri.parse("content://com.android.launcher.settings/favorites");
        } else {
            uri = Uri.parse("content://com.android.launcher2.settings/favorites");
        }
        String selection = " title = ?";
        String[] selectionArgs = new String[]{shortcutName};
        Cursor c = context.getContentResolver().query(uri, null, selection, selectionArgs, null);

        if (c != null && c.getCount() > 0) {
            isExist = true;
        }

        if (c != null) {
            c.close();
        }

        return isExist;
    }

    /**
     * 得到当前系统SDK版本
     */
    private static int getSdkVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }
}
