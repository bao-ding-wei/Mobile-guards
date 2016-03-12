package com.boyzhang.projectmobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.service.LocationService;

public class SmsReceiver extends BroadcastReceiver {

	private Context globalContext;
	private SharedPreferences prefConfig;

	@Override
	public void onReceive(Context context, Intent intent) {

		// 将context提升为成员属性
		globalContext = context;

		prefConfig = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);

		Bundle bundle = intent.getExtras();
		Object[] objects = (Object[]) bundle.get("pdus");
		for (Object object : objects) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
			String sender = sms.getOriginatingAddress();
			String smsBody = sms.getMessageBody();
			// 判断发送人是否是安全号码,指令是什么
			if (sender.equals(prefConfig.getString("safePhoneNum", ""))
					&& smsBody.equals("#*alarm*#")) {
				// 播放报警音乐
				startAlarm();
			} else if (sender.equals(prefConfig.getString("safePhoneNum", ""))
					&& smsBody.equals("#*location*#")) {
				// 获取位置
				getPosition();
			} else if (sender.equals(prefConfig.getString("safePhoneNum", ""))
					&& smsBody.equals("#*wipedata*#")) {
				// 清除数据
				wipedata();

			} else if (sender.equals(prefConfig.getString("safePhoneNum", ""))
					&& smsBody.equals("#*lockscreen*#")) {
				// 锁屏
				lockscreen();
			}
		}

		// 终止当前的广播，不让其他广播接收者再接收消息
		abortBroadcast();
	}

	/**
	 * 远程锁屏
	 */
	private void lockscreen() {
		// 拿到设备管理器对象
		DevicePolicyManager mDPM = (DevicePolicyManager) globalContext
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName mDeviceAdminSample = new ComponentName(globalContext,
				AdminReceiver.class);
		if (mDPM.isAdminActive(mDeviceAdminSample)) {
			// 锁屏
			mDPM.lockNow();
		} else {
			sendSMS(prefConfig.getString("safePhoneNum", ""),
					"Sorry! Device manager not turned on!");
		}
	}

	/**
	 * 远程清除数据
	 */
	private void wipedata() {
		// 拿到设备管理器对象
		DevicePolicyManager mDPM = (DevicePolicyManager) globalContext
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName mDeviceAdminSample = new ComponentName(globalContext,
				AdminReceiver.class);
		if (mDPM.isAdminActive(mDeviceAdminSample)) {
			// 清除数据
			mDPM.wipeData(0);
		} else {
			sendSMS(prefConfig.getString("safePhoneNum", ""),
					"Sorry! Device manager not turned on!");
		}
	}

	/**
	 * 远程获取位置信息
	 */
	private void getPosition() {
		// 发送一条准备状态信息
		sendSMS(prefConfig.getString("safePhoneNum", ""),
				"Getting location information, if not reply after 3 minutes, may be a failure!");
		// 启动位置服务
		globalContext.startService(new Intent(globalContext,
				LocationService.class));
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int time = 0;
				while (time <= 1000 * 180) {
					// 如果获取到了位置信息就发送
					if (!TextUtils.isEmpty(prefConfig.getString("position", ""))) {
						sendSMS(prefConfig.getString("safePhoneNum", ""),
								"Position:  "
										+ prefConfig.getString("position", ""));
						break;
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					time += 2000;
				}
			}
		});
		thread.start();
	}

	/**
	 * 远程播放报警音乐
	 */
	private void startAlarm() {
		// 开始播放报警音乐
		MediaPlayer player = MediaPlayer
				.create(globalContext, R.raw.ring_alarm);
		player.setLooping(true);
		player.setVolume(1f, 1f);
		player.start();
	}

	/**
	 * 发送短信
	 * 
	 * @param address
	 * @param content
	 */
	private void sendSMS(String address, String content) {
		// Android5.0发不出去短信
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(address, null, content, null, null);
	}

}
