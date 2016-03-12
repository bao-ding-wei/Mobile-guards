package com.boyzhang.projectmobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

/**
 * 判断一个服务是开启还是关闭状态
 * 
 * @author HaiFeng
 * 
 */
public class ServiceStatusUtils {

	/**
	 * @param context
	 *            上下文对象
	 * @param ServiceName
	 *            要判断的服务的名称
	 * @return 返回服务是否开启状态
	 */
	public static boolean isServiceRunning(Context context, String ServiceName) {

		// 拿到ActivityManager
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 拿到所有运行的服务
		// 参数:要获取的最大条数
		List<RunningServiceInfo> runningServices = activityManager
				.getRunningServices(200);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			// System.out.println(runningServiceInfo.service.getClassName());
			// 拿到服务的名称
			String name = runningServiceInfo.service.getClassName();
			if (ServiceName.equals(name)) {
				return true;
			}
		}
		return false;
	}
}
