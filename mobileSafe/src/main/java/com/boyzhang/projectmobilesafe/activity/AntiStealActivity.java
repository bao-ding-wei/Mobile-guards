package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.receiver.AdminReceiver;
import com.boyzhang.projectmobilesafe.utils.EncodingUtils;
import com.boyzhang.projectmobilesafe.view.SettingItem1;
import com.boyzhang.projectmobilesafe.view.SettingItem2;

public class AntiStealActivity extends Activity {

	private SettingItem2 siAntistealSafePassword;
	private SettingItem1 siAntiStealIsBindSim;
	private SettingItem2 siAntiStealReset;

	private SharedPreferences prefConfig;
	private SettingItem2 siAntiStealSafePhoneNum;
	private SettingItem1 siAntistealIsActiveDevice;

	private ComponentName mDeviceAdminSample;

	private DevicePolicyManager mDPM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antisteal);

		siAntistealSafePassword = (SettingItem2) findViewById(R.id.si_antisteal_password);
		siAntiStealIsBindSim = (SettingItem1) findViewById(R.id.si_antisteal_isBindSim);
		siAntiStealSafePhoneNum = (SettingItem2) findViewById(R.id.si_antisteal_safePhoneNum);
		siAntiStealReset = (SettingItem2) findViewById(R.id.si_antisteal_reset);
		siAntistealIsActiveDevice = (SettingItem1) findViewById(R.id.si_antisteal_isActiveDevice);

		prefConfig = getSharedPreferences("config", Activity.MODE_PRIVATE);

		// 拿到设备管理器对象
		mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		// mDeviceAdminSample是指明由谁激活设备管理器
		mDeviceAdminSample = new ComponentName(AntiStealActivity.this,
				AdminReceiver.class);

		// 初始化数据
		initData();

		// 设置安全密码修改监听
		siAntistealSafePassword.setOnClickListener(new OnClickListener() {

			private Button bSetFinish;
			private EditText eSetConfirm;
			private EditText eSetPassword;
			private AlertDialog alertDialog;

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(
						AntiStealActivity.this);
				alertDialog = builder.create();
				View view = View.inflate(AntiStealActivity.this,
						R.layout.antisteal_setup_wizard_page3, null);
				alertDialog.setView(view, 0, 0, 0, 0);
				eSetPassword = (EditText) view
						.findViewById(R.id.e_set_guide_password);
				eSetConfirm = (EditText) view
						.findViewById(R.id.e_set_guide_confirm);
				bSetFinish = (Button) view
						.findViewById(R.id.b_set_guide_finish);

				bSetFinish.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 获取密码并判断
						String pass_setword = eSetPassword.getText().toString();
						String confirm_password = eSetConfirm.getText()
								.toString();
						// 使用有TextUtils类检测用户输入的密码是否为空
						if (!TextUtils.isEmpty(pass_setword)
								&& !TextUtils.isEmpty(confirm_password)) {
							if (pass_setword.length() < 6) {
								Toast.makeText(AntiStealActivity.this,
										"密码长度不能低于6位", Toast.LENGTH_SHORT)
										.show();
								return;
							} else {
								if (!pass_setword.equals(confirm_password)) {
									Toast.makeText(AntiStealActivity.this,
											"两次输入密码不一致", Toast.LENGTH_SHORT)
											.show();
									return;
								} else {
									// 用户输入无误准备保存密码
									prefConfig
											.edit()
											.putString(
													"antiStealPassword",
													EncodingUtils
															.md5(pass_setword))
											.commit();
									// 将用户是否设置过手机防盗密码设为true
									prefConfig
											.edit()
											.putBoolean("isConfigAntiSteal",
													true).commit();
									// 销毁alertDialog
									alertDialog.dismiss();
								}
							}
						} else {
							Toast.makeText(AntiStealActivity.this, "输入框不能为空",
									Toast.LENGTH_SHORT).show();
							return;
						}
					}
				});
				alertDialog.show();
			}
		});

		// 设置开启设备管理器监听
		siAntistealIsActiveDevice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!mDPM.isAdminActive(mDeviceAdminSample)) {
					Intent intent = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					// mDeviceAdminSample是指明由谁激活设备管理器
					// mDeviceAdminSample = new ComponentName(
					// AntiStealActivity.this, AdminReceiver.class);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							mDeviceAdminSample);
					// 这个是开启设备管理器时的提示语
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							"开启设备管理器轻松锁屏,远程删除数据不再是问题");
					startActivityForResult(intent, 10);
				} else {
					mDPM.removeActiveAdmin(mDeviceAdminSample);
					siAntistealIsActiveDevice.setChecked(false);
				}
			}
		});

		// 设置重新设置的点击监听
		siAntiStealReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 清除所有的手机防盗信息
				prefConfig.edit().remove("safePhoneNum").commit();
				prefConfig.edit().remove("simSerialNumber").commit();
				prefConfig.edit().remove("antiStealPassword").commit();
				prefConfig.edit().remove("safePhoneNum").commit();
				prefConfig.edit().remove("isConfigAntiSteal").commit();
				// 跳转设置向导页面
				Intent intent = new Intent(AntiStealActivity.this,
						AntiStealSetupWizard.class);
				startActivity(intent);
				finish();
			}
		});

	}

	/**
	 * 初始化
	 */
	private void initData() {
		// 初始化安全号码
		String safePhone = prefConfig.getString("safePhoneNum", "安全号码未设置");
		if (!TextUtils.isEmpty(safePhone)) {
			siAntiStealSafePhoneNum.setChecked(true);
			siAntiStealSafePhoneNum.setDesc(safePhone);
		} else {
			siAntiStealSafePhoneNum.setChecked(false);
		}
		// 初始化设备管理器
		if (mDPM.isAdminActive(mDeviceAdminSample)) {
			siAntistealIsActiveDevice.setChecked(true);
		} else {
			siAntistealIsActiveDevice.setChecked(false);
		}
		// 初始化防盗密码状态
		siAntistealSafePassword.setChecked(true);
		// 初始化SIM卡序列化状态
		String simSerialNumber = prefConfig.getString("simSerialNumber", "");
		if (simSerialNumber != null && simSerialNumber.length() > 0) {
			siAntiStealIsBindSim.setChecked(true);
		} else {
			siAntiStealIsBindSim.setChecked(false);
		}
		// 设置重置的选择状态为选择
		siAntiStealReset.setChecked(true);
	}

	// 返回结果时调用
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10) {
			if (mDPM.isAdminActive(mDeviceAdminSample)) {
				siAntistealIsActiveDevice.setChecked(true);
			} else {
				siAntistealIsActiveDevice.setChecked(false);
			}
		}
	}

}
