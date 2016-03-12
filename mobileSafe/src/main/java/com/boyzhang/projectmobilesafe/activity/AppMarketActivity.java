package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;

import com.boyzhang.projectmobilesafe.R;

import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.OffersWallDialogListener;

public class AppMarketActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_market);

        initUI();
    }

    /**
     * 初始化UI
     */
    public void initUI() {
        OffersManager.getInstance(this)
                .showOffersWallDialog(this, new OffersWallDialogListener() {

                    @Override
                    public void onDialogClose() {
                        //Toast.makeText(AppMarketActivity.this, "积分墙对话框关闭了", Toast.LENGTH_SHORT).show();
                        finish();//当积分墙关闭时就销毁当前Activity
                    }
                });
    }


}
