package com.boyzhang.projectmobilesafe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;

public class AppLockActivity extends FragmentActivity {

    private TextView tv_appLock_unLock;
    private TextView tv_appLock_locked;
    private FrameLayout fl_appLock_content;
    private ListView lv_appLock;

    private boolean isFragment1Showing = true;
    private boolean isFragment2Showing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);


        //获取控件
        tv_appLock_unLock = (TextView) findViewById(R.id.tv_appLock_unLock);
        tv_appLock_locked = (TextView) findViewById(R.id.tv_appLock_Locked);
        fl_appLock_content = (FrameLayout) findViewById(R.id.fl_appLock_content);
        lv_appLock = (ListView) findViewById(R.id.lv_appLock);

        initUI();
        changeToUnLockFragment();//初始化显示为加锁Fragment
    }

    /**
     * 初始化UI
     */
    public void initUI() {

        //为加锁点击侦听
        tv_appLock_unLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFragment1Showing){
                    return;
                }

                //-------------------------------------------------------------------
                tv_appLock_unLock.setBackgroundResource(R.drawable.tab_left_pressed);//点击切换按钮背景
                tv_appLock_locked.setBackgroundResource(R.drawable.tab_right_default);
                //---------------------------------------------------------------------
                //---------------------------------------------------------------------

                changeToUnLockFragment();
                isFragment1Showing = true;
                isFragment2Showing = false;
            }
        });

        //已加锁点击侦听
        tv_appLock_locked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFragment2Showing){
                    return;
                }

                //---------------------------------------------------------------------
                tv_appLock_locked.setBackgroundResource(R.drawable.tab_right_pressed);//点击切换按钮背景
                tv_appLock_unLock.setBackgroundResource(R.drawable.tab_left_default);
                //---------------------------------------------------------------------
                //---------------------------------------------------------------------

                changeToLockedFragment();
                isFragment2Showing = true;
                isFragment1Showing = false;
            }
        });

    }

    /**
     * 切换到未加锁Fragment
     */
    public void changeToUnLockFragment() {

        AppLockFragment1 appLockFragment = new AppLockFragment1();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.push_right_in,
                R.anim.push_right_out);//切换动画

        fragmentTransaction.replace(R.id.fl_appLock_content, appLockFragment);

        fragmentTransaction.commit();
    }

    /**
     * 切换到已加锁Fragment
     */
    public void changeToLockedFragment() {

        AppLockFragment2 appLockFragment = new AppLockFragment2();

        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();

        fragmentTransaction.setCustomAnimations(R.anim.push_left_in,
                R.anim.push_left_out);//切换动画

        fragmentTransaction.replace(R.id.fl_appLock_content, appLockFragment);

        fragmentTransaction.commit();
    }

}
