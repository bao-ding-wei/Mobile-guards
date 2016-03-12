package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.db.dao.DbAddress;

/**
 * 归属地查询
 * 
 * @author HaiFeng
 * 
 */
public class AttributivelyActivity extends Activity {

	private EditText eAttributively;
	// private Button bAttributively;
	private TextView tvAttributivelyResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attributively);

		// 获取控件
		eAttributively = (EditText) findViewById(R.id.e_Attributively);
		// bAttributively = (Button) findViewById(R.id.b_Attributively);
		tvAttributivelyResult = (TextView) findViewById(R.id.tv_Attributively_result);

		// 设置文本框内容改变监听
		eAttributively.addTextChangedListener(new TextWatcher() {

			// 文本框内容改变时调用
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			// 文本框内容改变前调用
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			// 文本框内容改变后调用
			@Override
			public void afterTextChanged(Editable s) {

				// 获取手机号码
				// String phone = eAttributively.getText().toString();
				String phone = s.toString();

				String address = DbAddress.getaddress(phone);
				if (address.equals("null")) {
					// 这里的动画使用了插补器
					Animation shake = AnimationUtils.loadAnimation(
							AttributivelyActivity.this, R.anim.shake);
					// 设置插补器
					// 自定义插补器
					/*
					 * shake.setInterpolator(new Interpolator() {
					 * 
					 * @Override public float getInterpolation(float input) { //
					 * input相当于时间 // 返回值相当于位移的距离 return (float) Math.sin(input);
					 * } }); shake.setRepeatCount(10);
					 */
					// 播放动画
					eAttributively.startAnimation(shake);
					// 振动
					vibrator();
					tvAttributivelyResult.setText("尚未匹配到");
				} else {
					tvAttributivelyResult.setText(address);
				}

			}
		});

		// 设置点击监听
		/*
		 * bAttributively.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * // 获取手机号码 String phone = eAttributively.getText().toString(); //
		 * 检测号码是否合格 if (phone.matches("^1[3-8]\\d{9}$")) {
		 * 
		 * DbAddress dbAddress = new DbAddress(); String address =
		 * dbAddress.getaddress(phone); if (address.equals("null")) {
		 * tvAttributivelyResult.setText("当前为未知号码"); } else {
		 * tvAttributivelyResult.setText(address); }
		 * 
		 * } else { tvAttributivelyResult.setText("当前查询号码有误!"); } } });
		 */
	}

	/**
	 * 振动器 需要permission
	 */
	public void vibrator() {
		Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(200);// 振动500毫秒
		// 参数说明
		// long数组是节奏
		// 先等待200毫秒,振动300毫秒,暂停100毫秒,振动400毫秒,暂停50毫秒........依次类推
		// 参二,表示循环位置,-1是不循环,1、2、3、4分别表示从参一中1、2、3、4位置开始循环
		// vibrator.vibrate(new long[] { 200, 300, 100, 400, 50, 500 }, -1);
		// 取消振动
		// vibrator.cancel();
	}
}
