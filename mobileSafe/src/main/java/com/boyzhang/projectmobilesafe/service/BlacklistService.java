package com.boyzhang.projectmobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.boyzhang.projectmobilesafe.bean.BlackListNumber;
import com.boyzhang.projectmobilesafe.db.dao.BlacklistDao;

import java.lang.reflect.Method;

/***
 * 黑名单拦截服务
 */
public class BlacklistService extends Service {

    private BroadcastReceiver smsReceiver;

    private TelephonyManager tm;
    private MyPhoneStateListener phoneListener;
    private BlacklistDao blacklistDao;

    public BlacklistService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //System.out.println("创建");

        blacklistDao = new BlacklistDao(this);

        //监听拦截短信
        interceptSMS();

        //监听拦截电话
        interceptPhone();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //System.out.println("销毁");

        //取消短信广播接收者
        unregisterReceiver(smsReceiver);

        // 关闭来电监听服务
        // PhoneStateListener.LISTEN_NONE标记停止
        tm.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * 监听拦截电话
     */
    private void interceptPhone() {
        // 拿到电话服务
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        phoneListener = new MyPhoneStateListener();
        // 监听来电状态
        tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 短信拦截
     */
    private void interceptSMS() {
        //注册短信广播接收者
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter);
    }

    /**
     * 短信广播接收者
     */
    class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //System.out.println("收到短信");
            Bundle bundle = intent.getExtras();
            Object[] pdus = (Object[]) bundle.get("pdus");

            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String address = sms.getOriginatingAddress();//拿到短信号码
                String body = sms.getMessageBody();
                //System.out.println(address);

                String mode = blacklistDao.selectMode(address);

                if (mode != null) {
                    //黑名单拦截
                    if (mode.equals(BlackListNumber.MODE_SMS) || mode.equals(BlackListNumber.MODE_ALL)) {
                        //System.out.println("短信被拦截");
                        Toast.makeText(context, "拦截短信", Toast.LENGTH_SHORT).show();
                        abortBroadcast();
                    }
                    //智能短信拦截
                    //从关键词数据库中查取垃圾关键词,与短信内容对比
                    if (body.contains("学生妹")) {
                        //认为是垃圾短信
                    }
                }
            }
        }
    }

    /**
     * 来电监听
     */
    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            //获取来电的号码拦截模式
            String mode = blacklistDao.selectMode(incomingNumber);

            //System.out.println(mode);

            /**
             * CALL_STATE_OFFHOOK ：电话接通
             * CALL_STATE_RINGING : 电话铃响
             * CALL_STATE_IDLE ：电话闲置-
             */
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // 电话铃响了,拦截电话
                    //System.out.println("电话响了");
                    if (mode != null) {
                        if (mode.equals(BlackListNumber.MODE_PHONE) || mode.equals(BlackListNumber.MODE_ALL)) {
                            //System.out.println("挂断黑名单电话号码");

                            Uri uri = Uri.parse("content://call_log/calls");

                            //注册一个内容观察者,观察电话LOG的变化
                            getContentResolver().registerContentObserver(uri, true, new MyContentObserver(new Handler(), incomingNumber));

                            //挂断电话
                            endCall();

                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    // 电话处于闲置状态
                    //System.out.println("电话闲置");
                    break;
                default:
                    break;
            }
        }

        private class MyContentObserver extends ContentObserver {

            String incomingNumber;

            /**
             * Creates a content observer.
             *
             * @param handler The handler to run {@link #onChange} on, or null if none.
             */
            public MyContentObserver(Handler handler, String incomingNumber) {
                super(handler);
                this.incomingNumber = incomingNumber;
            }

            //当数据改变的时候调用的方法
            @Override
            public void onChange(boolean selfChange) {

                //取消注册
                getContentResolver().unregisterContentObserver(this);

                //删除号码
                deleteCallLog(incomingNumber);

                super.onChange(selfChange);
            }
        }
    }

    /**
     * 删掉电话号码
     *
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {

        Uri uri = Uri.parse("content://call_log/calls");

        getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});

    }

    /**
     * 挂断电话,通过反射完成,在低版本中可以直接使用电话服务的endCall()方法完成,高版本的不行了,此方法是隐藏的方法
     * 使用反射和AIDL完成
     */
    private void endCall() {

        try {
            //通过类加载器加载ServiceManager
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射得到当前的方法
            Method method = clazz.getDeclaredMethod("getService", String.class);

            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);

            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
