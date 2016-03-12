package com.boyzhang.projectmobilesafe.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-03-02 17:10
 * <p/>
 * 描述 : 存储空间工具
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class StorageUtils {

    /**
     * 获取SD是否处于正常挂载的状态
     *
     * @return
     */
    public static boolean isExternalStorageMounted() {

        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    /**
     * 获取手机内存卡的总大小和剩余空间
     *
     * @param context
     * @return res[0]:总大小  res[1]:已用空间 res[1]:剩余空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String[] getExternalStorageSizeInfo(Context context) {

        String[] res = new String[3];

        // 如果SD卡不可以使用就返回NULL
        if (!isExternalStorageMounted()) {
            return null;
        }

        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());

        long blockSize;
        long totalBlocks;
        long variableBlocks;
        // 兼容4.3以下系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // SD卡区块大小
            blockSize = stat.getBlockSizeLong();
            // 总区块数量
            totalBlocks = stat.getBlockCountLong();
            // 可用区块数
            variableBlocks = stat.getAvailableBlocksLong();
        } else {
            // Android4.3以下版本
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            variableBlocks = stat.getAvailableBlocks();
        }

        // 格式化大小
        res[0] = formatSize(context, blockSize * totalBlocks);
        res[1] = formatSize(context, blockSize * (totalBlocks - variableBlocks));
        res[2] = formatSize(context, blockSize * variableBlocks);

        return res;
    }


    /**
     * 获取内部存储空间信息
     *
     * @param context
     * @return res[0]:总大小  res[1]:已用空间 res[2]:剩余空间
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String[] getRomSizeInfo(Context context) {

        String[] res = new String[3];

        // 如果SD卡不可以使用就返回NULL
        if (!isExternalStorageMounted()) {
            return null;
        }

        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());

        long blockSize;
        long totalBlocks;
        long variableBlocks;
        // 兼容4.3以下系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // SD卡区块大小
            blockSize = stat.getBlockSizeLong();
            // 总区块数量
            totalBlocks = stat.getBlockCountLong();
            // 可用区块数
            variableBlocks = stat.getAvailableBlocksLong();
        } else {
            // Android4.3以下版本
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            variableBlocks = stat.getAvailableBlocks();
        }

        // 格式化大小
        res[0] = formatSize(context, blockSize * totalBlocks);
        res[1] = formatSize(context, blockSize * (totalBlocks - variableBlocks));
        res[2] = formatSize(context, blockSize * variableBlocks);

        return res;
    }

    /**
     * 获取android内存信息
     *
     * @param context
     * @return res[0]:总内存  res[1]:已用内存  res[2]:剩余内存
     */
    public static String[] getRamInfo(Context context) {
        long availMem;
        long totalMem;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();//内存信息对象
        am.getMemoryInfo(memoryInfo);//获取内存信息
        availMem = memoryInfo.availMem;//获取到可用内存

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            totalMem = memoryInfo.totalMem;
        } else {
            String str1 = "/proc/meminfo";// 系统内存信息文件
            String str2;
            String[] arrayOfString;
            totalMem = 0;
            BufferedReader localBufferedReader = null;
            try {
                localBufferedReader = new BufferedReader(new FileReader(str1), 8192);
                str2 = localBufferedReader.readLine();// 读取meminfo第一行,系统总内存大小,MemTotal:   1021168 kB

                arrayOfString = str2.split("\\s+");
                if (arrayOfString[1] != null) {
                    totalMem = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (localBufferedReader != null) {
                    try {
                        localBufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 将获取的内存大小规格化
        String[] res = {formatSize(context, totalMem), formatSize(context, totalMem - availMem), formatSize(context, availMem)};
        return res;
    }


    /**
     * 格式化大小
     *
     * @param size
     * @return
     */
    public static String formatSize(Context context, long size) {
        return Formatter.formatFileSize(context, size);
    }

}
