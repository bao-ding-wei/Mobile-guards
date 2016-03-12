package com.boyzhang.projectmobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
 * 创建时间 : 2016-03-04 19:28
 * <p/>
 * 描述 : 短信工具类
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
public class SmsUtils {

    /**
     * 短信备份的回调接口
     */
    public interface backUpSmsCallback {

        /**
         * 短信的总数
         *
         * @param count
         */
        void beforeBackUp(int count);

        /**
         * 短信备份的进度
         *
         * @param progress
         */
        void onBackUp(int progress);

    }

    /**
     * 备份短信
     *
     * @param context
     * @param path     短信保存的路径
     * @param encoding 保存的编码格式
     * @param callBack 回调
     * @return
     */
    public static boolean backUpSms(Context context, String path, String encoding, backUpSmsCallback callBack) {

        //因为要备份到SD卡,所以先判断是否拥有SD
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //获取到内容解析者
            ContentResolver resolver = context.getContentResolver();
            //读取短信
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
            if (cursor != null) {
                FileOutputStream outputStream = null;
                XmlSerializer serializer = null;
                try {
                    //文件输出流
                    outputStream = new FileOutputStream(path);
                    //xml序列化器
                    serializer = Xml.newSerializer();
                    //打开流
                    serializer.setOutput(outputStream, encoding);
                    //文件头------开始XML
                    serializer.startDocument(encoding, true);
                    //开始根节点
                    serializer.startTag(null, "message");
                    //短信总数
                    int count = cursor.getCount();
                    //设置节点属性
                    serializer.attribute(null, "size", String.valueOf(count));

                    //------------------------------------
                    //初始化
                    callBack.beforeBackUp(count);
                    int progress = 0;
                    //------------------------------------

                    while (cursor.moveToNext()) {
                        serializer.startTag(null, "sms");

                        //获取数据
                        String address = cursor.getString(cursor.getColumnIndex("address"));
                        //保存为XML节点
                        serializer.startTag(null, "address");
                        serializer.text(address);
                        serializer.endTag(null, "address");

                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        //保存为XML节点
                        serializer.startTag(null, "date");
                        serializer.text(date);
                        serializer.endTag(null, "date");

                        String type = cursor.getString(cursor.getColumnIndex("type"));
                        //保存为XML节点
                        serializer.startTag(null, "type");
                        serializer.text(type);
                        serializer.endTag(null, "type");

                        String body = cursor.getString(cursor.getColumnIndex("body"));
                        //保存为XML节点
                        serializer.startTag(null, "body");
                        //将短信类容加密
                        String enBody = EncodingUtils.enBase64("mobileSafe", body);//加密
                        serializer.text(enBody);
                        serializer.endTag(null, "body");

                        serializer.endTag(null, "sms");

                        //------------------------------------
                        //成功备份一个就刷新回调中的进度
                        progress++;
                        callBack.onBackUp(progress);
                        //------------------------------------
                        //SystemClock.sleep(50);
                    }

                    //结束根节点
                    serializer.endTag(null, "message");
                    //结尾------结束XML
                    serializer.endDocument();

                    //备份成功
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    //关闭资源
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }

            }

        }

        return false;
    }
}
