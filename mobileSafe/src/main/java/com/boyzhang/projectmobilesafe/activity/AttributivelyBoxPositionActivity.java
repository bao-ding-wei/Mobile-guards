package com.boyzhang.projectmobilesafe.activity;

import com.boyzhang.projectmobilesafe.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 归属地显示框位置设置Activity
 * 
 * @author HaiFeng
 * 
 */
public class AttributivelyBoxPositionActivity extends Activity {

	private TextView tvAttributivelyToptips;
	private TextView tvAttributivelyBottomtips;
	private ImageView ivAttributivelyPositionbox;

	private int startRawX;
	private int startRawY;
	private int endRawX;
	private int endRawY;

	private SharedPreferences prefConfig;
	private Display display;
	private int screenWidth;
	private int screenHeight;

	// 设置双击居中
	long[] mHits = new long[2];// 数组长度表示要点击的次数

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attributively_box_position);

		// 拿到配置文件
		prefConfig = getSharedPreferences("config", MODE_PRIVATE);

		// 获取控件
		tvAttributivelyToptips = (TextView) findViewById(R.id.tv_attributively_toptips);
		tvAttributivelyBottomtips = (TextView) findViewById(R.id.tv_attributively_bottomtips);
		ivAttributivelyPositionbox = (ImageView) findViewById(R.id.iv_attributively_positionbox);

		// 进入Activity时读取控件的位置
		int AttrRawX = prefConfig.getInt("AttrRawX",
				ivAttributivelyPositionbox.getLeft());
		int AttrRawY = prefConfig.getInt("AttrRawY",
				ivAttributivelyPositionbox.getTop());

		// System.out.println(AttrRawX + "---" + AttrRawY);

		// 确定控件的左上右下位置
		// int l = AttrRawX;
		// int t = AttrRawY;
		// int r = ivAttributivelyPositionbox.getWidth() + AttrRawX;
		// int b = ivAttributivelyPositionbox.getHeight() + AttrRawY;
		// 设置控件的位置
		// 这种方式无法实现位置的更新
		// 控件在绘制到界面上必须经过onMeasure(测量view), onLayout(安放位置), onDraw(绘制)
		// 目前还没有测量onMeasure方法没有调用layout是不会生效的
		// ivAttributivelyPositionbox.layout(l, t, r, b);

		// 解决方案
		// 获取到控件的参数设置对象
		// 注意:不同的布局下控件拥有不同的LayoutParams对象,因此需要进行转换
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivAttributivelyPositionbox
				.getLayoutParams();
		params.leftMargin = AttrRawX;
		params.topMargin = AttrRawY;
		// 提交
		ivAttributivelyPositionbox.setLayoutParams(params);

		// 获取去屏幕对象
		display = getWindowManager().getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();

		// 判断是上边的tips显示还是下边的tips显示
		if (AttrRawY < screenHeight / 2) {
			tvAttributivelyToptips.setVisibility(View.INVISIBLE);
			tvAttributivelyBottomtips.setVisibility(View.VISIBLE);
		} else {
			tvAttributivelyToptips.setVisibility(View.VISIBLE);
			tvAttributivelyBottomtips.setVisibility(View.INVISIBLE);
		}

		// 设置滑动侦听
		ivAttributivelyPositionbox.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 按下
					// 获取按下时的点击位置
					startRawX = (int) event.getRawX();
					startRawY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					// 移动
					// 获取移动过程中的位置
					endRawX = (int) event.getRawX();
					endRawY = (int) event.getRawY();

					// 计算偏移量
					int dX = endRawX - startRawX;
					int dY = endRawY - startRawY;

					// 确定控件的左上右下位置
					int l = ivAttributivelyPositionbox.getLeft() + dX;
					int t = ivAttributivelyPositionbox.getTop() + dY;
					int r = ivAttributivelyPositionbox.getRight() + dX;
					int b = ivAttributivelyPositionbox.getBottom() + dY;

					// 如果超出移动的范围就break
					// -20是减去状态栏的高度
					if (l < 0 || r > screenWidth || t < 0
							|| b > screenHeight - 20) {
						break;
					}
					// 判断是上边的tips显示还是下边的tips显示
					if (t < screenHeight / 2) {
						tvAttributivelyToptips.setVisibility(View.INVISIBLE);
						tvAttributivelyBottomtips.setVisibility(View.VISIBLE);
					} else {
						tvAttributivelyToptips.setVisibility(View.VISIBLE);
						tvAttributivelyBottomtips.setVisibility(View.INVISIBLE);
					}

					// 重置控件的位置
					ivAttributivelyPositionbox.layout(l, t, r, b);

					// 重置起始坐标的位置
					startRawX = (int) event.getRawX();
					startRawY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
					// 松开
					// 松开后记录下控件的位置
					Editor edit = prefConfig.edit();
					edit.putInt("AttrRawX",
							ivAttributivelyPositionbox.getLeft());
					edit.putInt("AttrRawY", ivAttributivelyPositionbox.getTop());
					edit.commit();
					break;

				default:
					break;
				}

				// 返回false让事件可以往下传递,是的onClick事件可以响应
				return false;
			}
		});

		// 双击居中
		// 这段代码是从google源码中复制的,可以方便设置多击事件
		ivAttributivelyPositionbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 开机后开始计算的时间
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					int l = (screenWidth - ivAttributivelyPositionbox
							.getWidth()) / 2;
					int t = (screenHeight - ivAttributivelyPositionbox
							.getHeight()) / 2;
					int r = (screenWidth + ivAttributivelyPositionbox
							.getWidth()) / 2;
					;
					int b = (screenHeight + ivAttributivelyPositionbox
							.getHeight()) / 2;
					;
					ivAttributivelyPositionbox.layout(l, t, r, b);

					// 保存位置
					Editor edit = prefConfig.edit();
					edit.putInt("AttrRawX",
							ivAttributivelyPositionbox.getLeft());
					edit.putInt("AttrRawY", ivAttributivelyPositionbox.getTop());
					edit.commit();
				}
			}
		});

	}
}
