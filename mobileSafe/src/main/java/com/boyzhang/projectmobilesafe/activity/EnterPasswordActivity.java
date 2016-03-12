package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.utils.UiUtils;

/**
 * 输入密码的Activity
 */
public class EnterPasswordActivity extends Activity implements View.OnClickListener {

    private EditText et_enterPWD_pwd;
    private Button bt_enterPWD_ok;

    private Button bt_enterPWD_keyBoard_01;
    private Button bt_enterPWD_keyBoard_02;
    private Button bt_enterPWD_keyBoard_03;
    private Button bt_enterPWD_keyBoard_04;
    private Button bt_enterPWD_keyBoard_05;
    private Button bt_enterPWD_keyBoard_06;
    private Button bt_enterPWD_keyBoard_07;
    private Button bt_enterPWD_keyBoard_08;
    private Button bt_enterPWD_keyBoard_09;
    private Button bt_enterPWD_keyBoard_00;

    private Button bt_enterPWD_keyBoard_clear;
    private Button bt_enterPWD_keyBoard_backSpace;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        //获取控件
        et_enterPWD_pwd = (EditText) findViewById(R.id.et_enterPWD_pwd);
        bt_enterPWD_ok = (Button) findViewById(R.id.bt_enterPWD_ok);

        bt_enterPWD_keyBoard_01 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_01);
        bt_enterPWD_keyBoard_02 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_02);
        bt_enterPWD_keyBoard_03 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_03);
        bt_enterPWD_keyBoard_04 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_04);
        bt_enterPWD_keyBoard_05 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_05);
        bt_enterPWD_keyBoard_06 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_06);
        bt_enterPWD_keyBoard_07 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_07);
        bt_enterPWD_keyBoard_08 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_08);
        bt_enterPWD_keyBoard_09 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_09);
        bt_enterPWD_keyBoard_00 = (Button) findViewById(R.id.bt_enterPWD_keyBoard_00);
        bt_enterPWD_keyBoard_clear = (Button) findViewById(R.id.bt_enterPWD_keyBoard_clear);
        bt_enterPWD_keyBoard_backSpace = (Button) findViewById(R.id.bt_enterPWD_keyBoard_backSpace);


        initUI();
    }


    /**
     * 初始化UI
     */
    private void initUI() {

        Intent intent = getIntent();
        packageName = intent.getStringExtra("packageName");

        et_enterPWD_pwd.setInputType(InputType.TYPE_NULL);//这样可以让EditText的键盘不弹出!!!!!!!!____^^^^^^^&*(&**(&*(&(*&%$%^&$

        //----------------------------------设置点击侦听---------------------------------------------
        //-----------------------------------------------------------------------------------------
        bt_enterPWD_ok.setOnClickListener(this);

        bt_enterPWD_keyBoard_01.setOnClickListener(this);
        bt_enterPWD_keyBoard_02.setOnClickListener(this);
        bt_enterPWD_keyBoard_03.setOnClickListener(this);
        bt_enterPWD_keyBoard_04.setOnClickListener(this);
        bt_enterPWD_keyBoard_05.setOnClickListener(this);
        bt_enterPWD_keyBoard_06.setOnClickListener(this);
        bt_enterPWD_keyBoard_07.setOnClickListener(this);
        bt_enterPWD_keyBoard_08.setOnClickListener(this);
        bt_enterPWD_keyBoard_09.setOnClickListener(this);
        bt_enterPWD_keyBoard_00.setOnClickListener(this);

        bt_enterPWD_keyBoard_clear.setOnClickListener(this);
        bt_enterPWD_keyBoard_backSpace.setOnClickListener(this);
        //-----------------------------------------------------------------------------------------

    }


    /**
     * 点击事件处理
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        int id = v.getId();//获取到点View的Id
        String inputContent = et_enterPWD_pwd.getText().toString();//输入的内容

        switch (id) {
            case R.id.bt_enterPWD_ok:
                if (inputContent.equals("123")) {//密码正确
                    Intent intent = new Intent();
                    // 发送广播。停止保护
                    intent.setAction("com.boyzhang.mobileSafe.receiver.watchDog.stop");
                    // 跟狗说。现在停止保护短信
                    intent.putExtra("packageName", packageName);

                    sendBroadcast(intent);
                    finish();
                } else {
                    UiUtils.showToast(EnterPasswordActivity.this, "抱歉,密码有误!");
                }
                break;
            case R.id.bt_enterPWD_keyBoard_01:
                inputContent += "1";
                break;
            case R.id.bt_enterPWD_keyBoard_02:
                inputContent += "2";
                break;
            case R.id.bt_enterPWD_keyBoard_03:
                inputContent += "3";
                break;
            case R.id.bt_enterPWD_keyBoard_04:
                inputContent += "4";
                break;
            case R.id.bt_enterPWD_keyBoard_05:
                inputContent += "5";
                break;
            case R.id.bt_enterPWD_keyBoard_06:
                inputContent += "6";
                break;
            case R.id.bt_enterPWD_keyBoard_07:
                inputContent += "7";
                break;
            case R.id.bt_enterPWD_keyBoard_08:
                inputContent += "8";
                break;
            case R.id.bt_enterPWD_keyBoard_09:
                inputContent += "9";
                break;
            case R.id.bt_enterPWD_keyBoard_00:
                inputContent += "0";
                break;
            case R.id.bt_enterPWD_keyBoard_clear:
                inputContent = "";
                break;
            case R.id.bt_enterPWD_keyBoard_backSpace:
                if (inputContent.length() <= 0) {
                    return;
                }
                inputContent = inputContent.substring(0, inputContent.length() - 1);
                break;
        }
        et_enterPWD_pwd.setText(inputContent);
    }

    // 监听当前页面的后退健
    // <intent-filter>
    // <action android:name="android.intent.action.MAIN" />
    // <category android:name="android.intent.category.HOME" />
    // <category android:name="android.intent.category.DEFAULT" />
    // <category android:name="android.intent.category.MONKEY"/>
    // </intent-filter>
    @Override
    public void onBackPressed() {
        // 当用户输入后退健 的时候。我们进入到桌面,否则会停留于在密码输入界面退不出去
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }
}
