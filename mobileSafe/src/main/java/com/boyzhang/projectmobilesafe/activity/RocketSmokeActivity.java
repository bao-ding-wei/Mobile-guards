package com.boyzhang.projectmobilesafe.activity;

import com.boyzhang.projectmobilesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

public class RocketSmokeActivity extends Activity {

	private ImageView ivRocketSmokeM;
	private ImageView ivRocketSmokeT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rocket_smoke_background);

		ivRocketSmokeM = (ImageView) findViewById(R.id.iv_rocketSmoke_m);
		ivRocketSmokeT = (ImageView) findViewById(R.id.iv_rocketSmoke_t);

		// 渐变动画
		alphaAnimation(ivRocketSmokeM);
		alphaAnimation(ivRocketSmokeT);

		// 延迟1秒后销毁Activity
		// 这是一个运行在主线程的RunAble
		// 延迟1秒的消息
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				finish();
			}
		}, 1000);

	}

	/**
	 * 渐变动画
	 * 
	 * @param iv
	 */
	protected void alphaAnimation(ImageView iv) {
		AlphaAnimation animation = new AlphaAnimation(0, 1);
		animation.setDuration(500);
		animation.setFillAfter(true);
		iv.startAnimation(animation);
	}

}
