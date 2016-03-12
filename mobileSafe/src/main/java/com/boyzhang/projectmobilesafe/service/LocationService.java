package com.boyzhang.projectmobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

	private SharedPreferences prefConfig;
	private LocationManager locationManager;
	private MyLocationListener locationListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 拿到SharedPreferences
		prefConfig = getSharedPreferences("config", MODE_PRIVATE);
		// 获取位置
		getPosition();
	}

	// 获取位置
	protected void getPosition() {

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		// 参数说明
		// criteria: 获取位置信息的标准,如是否允许使用3G网、获取位置的精确度
		// enabledOnly: ture表示位置可以得到的provider才返回值
		Criteria criteria = new Criteria();
		criteria.setCostAllowed(true);// 允许付费得到位置,即可以使用网络获取位置
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 获取位置的精确度
		String bestProvider = locationManager.getBestProvider(criteria, true);
		locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(bestProvider, 0, 0,
				locationListener);
	}

	// 位置监听
	class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// 将位置保存到SharedPreferences
			double j = location.getLongitude();
			double w = location.getLatitude();
			double h = location.getAltitude();
			double ac = location.getAccuracy();
			prefConfig
					.edit()
					.putString("position",
							"j:" + j + ";w:" + w + ";h:" + h + ";ac:" + ac)
					.commit();

			// 这里获取位置成功了,将服务关闭了************
			stopSelf();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}

	}

	// 销毁服务
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 关闭位置服务
		locationManager.removeUpdates(locationListener);
	}

}
