package com.boyzhang.projectmobilesafe.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.activity.RocketSmokeActivity;

public class RocketService extends Service {

	private WindowManager mWM;
	private WindowManager.LayoutParams params;

	private int screenWidth;
	private int screenHeight;
	private int startRawX;
	private int startRawY;
	private int endRawX;
	private int endRawY;

	private SharedPreferences prefConfig;
	private View view;

	private ImageView ivRocket;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 拿到配置文件
		prefConfig = getSharedPreferences("config", MODE_PRIVATE);

		// 显示小火箭
		showRocket();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 销毁小火箭
		if (mWM != null && view != null) {
			mWM.removeView(view);// 移除view
			view = null;
		}

		// 移除保存位置
		Editor edit = prefConfig.edit();
		edit.remove("RocketX");
		edit.remove("RocketY");
		edit.commit();
	}

	/**
	 * 显示火箭
	 */
	@SuppressLint("RtlHardcoded")
	@SuppressWarnings("deprecation")
	protected void showRocket() {
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
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
		view = View.inflate(this, R.layout.view_rocket, null);

		// 拿到ImageView
		ivRocket = (ImageView) view.findViewById(R.id.iv_rocket);

		// 设置View的触摸事件
		view.setOnTouchListener(new OnTouchListener() {

			private AnimationDrawable animRocket;

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

					// 开启火箭的火苗动画
					ivRocket.setBackgroundResource(R.drawable.anim_rocket);
					animRocket = (AnimationDrawable) ivRocket.getBackground();
					animRocket.start();

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
					if (params.y < 0) {
						params.y = 0;
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

					// 发射火箭
					if (params.x + view.getWidth() / 2 > screenWidth / 2 - 30
							&& params.x + view.getWidth() / 2 < screenWidth / 2 + 30
							&& params.y > screenHeight - view.getHeight() - 30) {
						// 发射
						launchRocket();

						// 发射完成,小火箭归位,停止火箭动画
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								params.x = prefConfig.getInt("RocketX",
										screenWidth - ivRocket.getWidth());
								params.y = prefConfig
										.getInt("RocketY",
												(screenHeight - ivRocket
														.getHeight()) / 2);
								// 停止动画
								animRocket.stop();
								// 更新view
								mWM.updateViewLayout(view, params);
							}
						}, 1200);

						// 启动烟雾Activity
						Intent intent = new Intent(RocketService.this,
								RocketSmokeActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								| Intent.FLAG_ACTIVITY_CLEAR_TOP
								| Intent.FLAG_ACTIVITY_CLEAR_TASK);// 这里在Service中启动Activity需要添加此FLAGS
						startActivity(intent);

					} else {
						// 停止火箭动画
						if (animRocket != null) {
							animRocket.stop();
						}

						// 保存位置
						Editor edit = prefConfig.edit();
						edit.putInt("RocketX", params.x);
						edit.putInt("RocketY", params.y);
						edit.commit();
					}
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

		// 向view控件的Layout队列添加一个runable,她会在控件布局测量好后自动调用
		// 这里不使用post无法等到view对象的宽和高,layout还没有被测量好
		view.post(new Runnable() {

			@Override
			public void run() {
				params.x = prefConfig.getInt("RocketX",
						screenWidth - ivRocket.getWidth());
				params.y = prefConfig.getInt("RocketY",
						(screenHeight - ivRocket.getHeight()) / 2);
				// 更新view
				mWM.updateViewLayout(view, params);
			}
		});

		// 将View添加到window上
		mWM.addView(view, params);
	}

	// 更新UI
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mWM.updateViewLayout(view, params);
		};
	};

	/**
	 * 发射火箭
	 */
	protected void launchRocket() {

		new Thread() {
			@Override
			public void run() {
				super.run();

				// 让火箭水平居中
				params.x = (screenWidth - view.getWidth()) / 2;

				// 移动火箭垂直位置
				for (int i = 0; i < 12; i++) {
					params.y -= screenHeight / 10;

					System.out.println("params.y:" + params.y);

					// 更新view
					Message msg = handler.obtainMessage();
					handler.sendMessage(msg);

					// 等待100毫秒
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

}
