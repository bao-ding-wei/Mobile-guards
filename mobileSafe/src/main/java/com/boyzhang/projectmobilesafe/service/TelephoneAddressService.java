package com.boyzhang.projectmobilesafe.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.db.dao.DbAddress;

public class TelephoneAddressService extends Service {

	private TelephonyManager tm;
	private MyPhoneStateListener listener;
	private OutgoingCallReceiver receiver;
	private WindowManager mWM;
	private View view;

	private int startRawX;
	private int startRawY;
	private int endRawX;
	private int endRawY;
	private WindowManager.LayoutParams params;
	private int screenWidth;
	private int screenHeight;
	private SharedPreferences prefConfig;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 开启来电提示
		startTelePhoneListen();
		// 开启去电提示
		registerOutgoingCallReceiver();
	}

	protected void startTelePhoneListen() {
		// 拿到电话服务
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneStateListener();
		// 监听来电状态
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	protected void registerOutgoingCallReceiver() {
		receiver = new OutgoingCallReceiver();
		IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
		registerReceiver(receiver, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 关闭来电监听服务
		// PhoneStateListener.LISTEN_NONE标记停止
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);

		// 关闭去电广播接受者
		unregisterReceiver(receiver);
	}

	/**
	 * 监听打电话 -----需要权限:android.permission.PROCESS_OUTGOING_CALLS
	 * 
	 */
	public class OutgoingCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 拿到打出电话的电话号码
			String number = getResultData();
			// System.out.println(number);
			// 查询归属地
			String address = DbAddress.getaddress(number);
			// Toast.makeText(context, address, Toast.LENGTH_LONG).show();
			showCustomTelephoneAddressBox(address == "null" ? "未知号码" : address);
		}

	}

	/**
	 * 电话状态监听
	 * 
	 * @author HaiFeng
	 * 
	 */
	class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// 电话铃响了
				// System.out.println("电话响了");
				// 查询电话归属地
				String address = DbAddress.getaddress(incomingNumber);
				// Toast.makeText(TelephoneAddressService.this, address,
				// Toast.LENGTH_LONG).show();
				showCustomTelephoneAddressBox(address == "null" ? "未知号码"
						: address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// 电话处于闲置状态
				// 销毁地址框
				if (mWM != null && view != null) {
					mWM.removeView(view);// 移除view
					view = null;
				}
				break;
			default:
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("RtlHardcoded")
	protected void showCustomTelephoneAddressBox(String text) {
		mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		screenWidth = mWM.getDefaultDisplay().getWidth();
		screenHeight = mWM.getDefaultDisplay().getHeight();

		// 设置参数
		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.format = PixelFormat.TRANSLUCENT;
		// params.type级别太低,这里提高,不提高可能无法为view设置触摸事件
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;
		// TYPE_PHONE需要权限android.permission.SYSTEM_ALERT_WINDOW
		params.type = WindowManager.LayoutParams.TYPE_PHONE;
		params.setTitle("CustomToast");
		// 因为要响应触摸事件
		// 所以| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE这个flags就去除了
		params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		view = View.inflate(this, R.layout.view_toast_telephoneaddress, null);

		prefConfig = getSharedPreferences("config", MODE_PRIVATE);

		// 设置View的触摸事件
		view.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// System.out.println("触摸响应");
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 获取按下时的点击位置
					startRawX = (int) event.getRawX();
					startRawY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// 获取移动过程中的位置
					endRawX = (int) event.getRawX();
					endRawY = (int) event.getRawY();

					// 计算偏移量
					int dX = endRawX - startRawX;
					int dY = endRawY - startRawY;

					// 防止位置设置越界
					if (params.x < 0) {
						params.x = 0;
					}
					if (params.x > screenWidth - view.getWidth()) {
						params.x = screenWidth - view.getWidth();
					}
					if (params.y < 20) {
						params.y = 20;
					}
					if (params.y > screenHeight - view.getHeight()) {
						params.y = screenHeight - view.getHeight();
					}

					// 更新view的偏移位置
					params.x += dX;
					params.y += dY;
					mWM.updateViewLayout(view, params);

					// 重置起始坐标的位置
					startRawX = (int) event.getRawX();
					startRawY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					// 保存位置
					Editor edit = prefConfig.edit();
					edit.putInt("AttrRawX", params.x);
					edit.putInt("AttrRawY", params.y);
					edit.commit();
					break;

				default:
					break;
				}
				return false;
			}
		});

		// 设置位置
		// 设置原点是左上方(0,0)而不是默认屏幕的中心
		params.gravity = Gravity.LEFT + Gravity.TOP;
		// 设置坐标点
		params.x = prefConfig.getInt("AttrRawX", 50);
		params.y = prefConfig.getInt("AttrRawY", 50);

		int index = prefConfig.getInt("telephoneAddressBoxStyle", 0);
		int[] bgID = { R.drawable.call_locate_white,
				R.drawable.call_locate_orange, R.drawable.call_locate_blue,
				R.drawable.call_locate_gray, R.drawable.call_locate_green };

		TextView tvToastTelephoneaddress = (TextView) view
				.findViewById(R.id.tv_toast_telephoneaddress);

		// 设置背景
		tvToastTelephoneaddress.setBackgroundResource(bgID[index]);

		tvToastTelephoneaddress.setText(text);
		// 将View添加到window上
		mWM.addView(view, params);
	}
}
