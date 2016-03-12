package com.boyzhang.projectmobilesafe.activity;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.utils.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AntiStealSetupWizardFragment3 extends BaseFragment {

    private EditText eSetGuidePassword;
    private EditText eSetGuideConfirm;
    private Button bSetGuideFinish;

    private SharedPreferences prefConfig;

    @SuppressLint("InflateParams")
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(
                R.layout.antisteal_setup_wizard_page3, null);

        // 获取控件
        eSetGuidePassword = (EditText) v
                .findViewById(R.id.e_set_guide_password);
        eSetGuideConfirm = (EditText) v.findViewById(R.id.e_set_guide_confirm);
        bSetGuideFinish = (Button) v.findViewById(R.id.b_set_guide_finish);

        // 为完成按钮设置点击侦听
        setClickEvent();

        return v;
    }

    /**
     * 完成点击事件
     */
    private void setClickEvent() {

        bSetGuideFinish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prefConfig = getActivity().getSharedPreferences("config",
                        Activity.MODE_PRIVATE);
                // 获取密码并判断
                String pass_setword = eSetGuidePassword.getText().toString();
                String confirm_setpass_set = eSetGuideConfirm.getText()
                        .toString();
                // 使用有TextUtils类检测用户输入的密码是否为空
                if (!TextUtils.isEmpty(pass_setword)
                        && !TextUtils.isEmpty(confirm_setpass_set)) {
                    if (pass_setword.length() < 6) {
                        Toast.makeText(getActivity(), "密码长度不能低于6位",
                                Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (!pass_setword.equals(confirm_setpass_set)) {
                            Toast.makeText(getActivity(), "两次输入密码不一致",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            // System.out.println(pass_setword +
                            // "-------"
                            // + confirm_setpass_set);
                            // 用户输入无误准备保存密码
                            prefConfig
                                    .edit()
                                    .putString("antiStealPassword",
                                            EncodingUtils.md5(pass_setword))
                                    .commit();
                            // 将用户是否设置过手机防盗密码设为true
                            prefConfig.edit()
                                    .putBoolean("isConfigAntiSteal", true)
                                    .commit();
                            // 绑定SIM卡
                            bindSIM();
                            // 跳转AntiStealActivity
                            enterAntiSteal();
                            // 销毁当前Activity
                            ((AntiStealSetupWizard) getActivity()).finish();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "输入框不能为空", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
            }
        });
    }

    /**
     * 绑定SIM卡
     */
    public void bindSIM() {
        // 拿到系统服务,强转为TelephonyManager
        TelephonyManager manager = (TelephonyManager) getActivity()
                .getSystemService(Context.TELEPHONY_SERVICE);
        // 获取到SIM卡的序列号
        String simSerialNumber = manager.getSimSerialNumber();
        if (TextUtils.isEmpty(simSerialNumber)) {
            Toast.makeText(getActivity(), "未读取到SIM卡序列号,绑定SIM卡失败",
                    Toast.LENGTH_SHORT).show();
        }
        prefConfig.edit().putString("simSerialNumber", simSerialNumber)
                .commit();
    }

    /**
     * 以下为Activitie跳转函数
     */
    private void enterAntiSteal() {
        Intent intent0 = new Intent(getActivity(), AntiStealActivity.class);
        startActivity(intent0);
        // 设置Activity跳转动画
        ((AntiStealSetupWizard) getActivity()).overridePendingTransition(
                R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public boolean checkInput() {
        // TODO Auto-generated method stub
        return true;
    }
}
