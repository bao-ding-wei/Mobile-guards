package com.boyzhang.projectmobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class BootCompleteReceiver extends BroadcastReceiver {

	Context context;
	private SharedPreferences prefConfig;

	@Override
	public void onReceive(Context context, Intent intent) {
		// 将context提升为成员属性
		this.context = context;

		// 检测SIM
		checkSIM();
	}

	/**
	 * 检查SIM状态
	 */
	public void checkSIM() {
		prefConfig = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		// 如果没有开启手机防盗就不再执行
		if (!prefConfig.getBoolean("isConfigAntiSteal", false)) {
			return;
		}
		String storeSimSerialNumber = prefConfig.getString("simSerialNumber",
				"");
		// 如果没有开启SIM绑定就直接返回
		if (TextUtils.isEmpty(storeSimSerialNumber)) {
			return;
		}
		// 获取当前的SIM卡序列号
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String currentSimSerialNumber = manager.getSimSerialNumber();

		if (!storeSimSerialNumber.equals(currentSimSerialNumber)) {
			// 拿到安全号码,准备发送报警短信
			String safePhoneNum = prefConfig.getString("safePhoneNum", "");
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(safePhoneNum, null, "sim card changed",
					null, null);
		}
	}

}
