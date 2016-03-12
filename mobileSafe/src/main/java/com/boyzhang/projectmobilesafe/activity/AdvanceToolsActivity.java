package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.view.SettingItem2;

public class AdvanceToolsActivity extends Activity {

    private SettingItem2 siAdvancetoolsPhoneAddress;
    private SettingItem2 siAdvancetoolsBackupSMS;
    private SettingItem2 siAdvancetoolsAppLock;
    private SettingItem2 siAdvancetoolsUsuallyPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advancetools);

        // 获取控件
        siAdvancetoolsPhoneAddress = (SettingItem2) findViewById(R.id.si_advancetools_phoneAddress);
        siAdvancetoolsBackupSMS = (SettingItem2) findViewById(R.id.si_advancetools_backupSMS);
        siAdvancetoolsUsuallyPhoneNum = (SettingItem2) findViewById(R.id.si_advancetools_usuallyPhoneNum);
        siAdvancetoolsAppLock = (SettingItem2) findViewById(R.id.si_advancetools_appLock);

        // 初始化Activity
        initData();
        initUI();


    }

    /**
     * 初始化UI
     */
    public void initUI() {
        // 设置归属地点击事件
        siAdvancetoolsPhoneAddress.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 跳转到归属地查询Activity
                enterActivity(AttributivelyActivity.class);
            }
        });

        //设置短信备份点击侦听
        siAdvancetoolsBackupSMS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到归属地查询Activity
                enterActivity(BackupSMS.class);
            }
        });

        //AppLockActivity
        //程序锁点击侦听
        siAdvancetoolsAppLock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(AppLockActivity.class);
            }
        });
    }

    /**
     * 初始化数据
     */
    protected void initData() {
        siAdvancetoolsPhoneAddress.setChecked(true);
        siAdvancetoolsBackupSMS.setChecked(true);
        siAdvancetoolsUsuallyPhoneNum.setChecked(true);
        siAdvancetoolsAppLock.setChecked(true);
    }

    /**
     * Activity跳转函数
     */
    public void enterActivity(Class<?> clazz) {
        Intent intent = new Intent(AdvanceToolsActivity.this,
                clazz);
        startActivity(intent);
    }

}
