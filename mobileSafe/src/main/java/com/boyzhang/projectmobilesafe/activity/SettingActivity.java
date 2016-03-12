package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.service.BlacklistService;
import com.boyzhang.projectmobilesafe.service.RocketService;
import com.boyzhang.projectmobilesafe.service.TelephoneAddressService;
import com.boyzhang.projectmobilesafe.service.WatchdogService;
import com.boyzhang.projectmobilesafe.utils.ServiceStatusUtils;
import com.boyzhang.projectmobilesafe.utils.ShortcutUtils;
import com.boyzhang.projectmobilesafe.view.SettingItem1;
import com.boyzhang.projectmobilesafe.view.SettingItem2;

/**
 * 设置中心页面
 *
 * @author HaiFeng
 */
public class SettingActivity extends Activity {

    private SettingItem1 siItemAutoupdate;
    private SharedPreferences prefConfig;
    private SettingItem1 siItemTelephoneAddress;
    private SettingItem2 siItemsTelephoneAddressStyle;
    private String[] styleName = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    private int currentStyle;
    private SettingItem2 siItemsTelephoneAddressBoxPosition;
    private SettingItem1 siItemsWindowRocket;
    private SettingItem1 siItemsBlacklist;
    private SettingItem1 siItemsDesktopIcon;
    private SettingItem1 si_items_watchdog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        prefConfig = getSharedPreferences("config", MODE_PRIVATE);

        // 获取控件
        siItemAutoupdate = (SettingItem1) findViewById(R.id.si_items_autoupdate);
        siItemTelephoneAddress = (SettingItem1) findViewById(R.id.si_items_telephoneAddress);
        siItemsTelephoneAddressStyle = (SettingItem2) findViewById(R.id.si_items_TelephoneAddressStyle);
        siItemsTelephoneAddressBoxPosition = (SettingItem2) findViewById(R.id.si_items_TelephoneAddressBoxPosition);
        siItemsWindowRocket = (SettingItem1) findViewById(R.id.si_items_windowRocket);
        siItemsBlacklist = (SettingItem1) findViewById(R.id.si_items_blacklist);
        siItemsDesktopIcon = (SettingItem1) findViewById(R.id.si_items_desktop_icon);
        si_items_watchdog = (SettingItem1) findViewById(R.id.si_items_watchdog);

        initAutoApdate();
        initTelephoneAddress();
        initTelephoneAddressStyle();
        initTelephoneAddressBoxPosition();
        intiWindowRocket();
        initBlacklist();
        initShortcut();
        initWatchdog();

    }

    /**
     * 初始化自动更新Item
     */
    protected void initAutoApdate() {
        // siItemAutoupdate.setTitle("自动更新设置");
        // 根据SharedPreferences中值的状态动态设置值
        if (prefConfig.getBoolean("auto_update", true)) {
            // siItems.setDesc("自动更新已开启");
            siItemAutoupdate.setChecked(true);
        } else {
            // siItems.setDesc("自动更新已关闭");
            siItemAutoupdate.setChecked(false);
        }

        // 设置点击监听
        siItemAutoupdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // 判断是否已经选择
                if (siItemAutoupdate.getChecked()) {
                    siItemAutoupdate.setChecked(false);
                    // siItems.setDesc("自动更新已关闭");
                    // 保存设置
                    prefConfig.edit().putBoolean("auto_update", false).commit();
                } else {
                    siItemAutoupdate.setChecked(true);
                    // siItems.setDesc("自动更新已开启");
                    prefConfig.edit().putBoolean("auto_update", true).commit();
                }
            }
        });
    }

    /**
     * 初始化归属地显示Item
     */
    public void initTelephoneAddress() {
        // 判断电话归属地服务是否是开启状态
        boolean serviceStatus = ServiceStatusUtils
                .isServiceRunning(this,
                        "com.boyzhang.projectmobilesafe.service.TelephoneAddressService");
        if (serviceStatus) {
            siItemTelephoneAddress.setChecked(true);
        } else {
            siItemTelephoneAddress.setChecked(false);
        }
        siItemTelephoneAddress.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (siItemTelephoneAddress.getChecked()) {
                    siItemTelephoneAddress.setChecked(false);
                    // 停止归属地服务
                    stopService(new Intent(SettingActivity.this,
                            TelephoneAddressService.class));
                } else {
                    siItemTelephoneAddress.setChecked(true);
                    // 开启归属地服务
                    startService(new Intent(SettingActivity.this,
                            TelephoneAddressService.class));
                }
            }
        });
    }

    /**
     * 初始化归属地样式
     */
    protected void initTelephoneAddressStyle() {
        currentStyle = prefConfig.getInt("telephoneAddressBoxStyle", 0);
        // System.out.println(currentStyle);
        switch (currentStyle) {
            case 0:
                siItemsTelephoneAddressStyle.setDesc(styleName[0]);
                break;
            case 1:
                siItemsTelephoneAddressStyle.setDesc(styleName[1]);
                break;
            case 2:
                siItemsTelephoneAddressStyle.setDesc(styleName[2]);
                break;
            case 3:
                siItemsTelephoneAddressStyle.setDesc(styleName[3]);
                break;
            case 4:
                siItemsTelephoneAddressStyle.setDesc(styleName[4]);
                break;
            default:
                siItemsTelephoneAddressStyle.setDesc("默认");
                break;
        }

        // 设置点击侦听
        siItemsTelephoneAddressStyle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SettingActivity.this);
                builder.setTitle("样式选择");
                builder.setSingleChoiceItems(styleName, currentStyle,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                prefConfig
                                        .edit()
                                        .putInt("telephoneAddressBoxStyle",
                                                which).commit();
                                currentStyle = which;
                                siItemsTelephoneAddressStyle
                                        .setDesc(styleName[which]);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }

    /**
     * 初始化归属地显示框位置节点
     */
    protected void initTelephoneAddressBoxPosition() {
        siItemsTelephoneAddressBoxPosition.setChecked(true);

        // 设置点击事件
        siItemsTelephoneAddressBoxPosition
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 点击跳转到归属地显示位置设置Activity
                        Intent intent = new Intent(SettingActivity.this,
                                AttributivelyBoxPositionActivity.class);
                        startActivity(intent);
                    }
                });
    }

    /**
     * 初始化桌面小火箭节点
     */
    protected void intiWindowRocket() {
        // 判断小火箭服务是否是开启状态
        boolean serviceStatus = ServiceStatusUtils.isServiceRunning(this,
                "com.boyzhang.projectmobilesafe.service.RocketService");
        if (serviceStatus) {
            siItemsWindowRocket.setChecked(true);
        } else {
            siItemsWindowRocket.setChecked(false);
        }

        // 设置点击侦听
        siItemsWindowRocket.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (siItemsWindowRocket.getChecked()) {
                    // 取消选择
                    siItemsWindowRocket.setChecked(false);
                    // 关闭服务
                    stopService(new Intent(SettingActivity.this,
                            RocketService.class));
                } else {
                    // 选择
                    siItemsWindowRocket.setChecked(true);
                    // 开启服务
                    startService(new Intent(SettingActivity.this,
                            RocketService.class));
                }
            }
        });
    }

    /**
     * 初始化黑名单拦截节点
     */
    public void initBlacklist() {
        // 判断很黑名单服务是否是开启状态
        boolean serviceStatus = ServiceStatusUtils.isServiceRunning(this,
                "com.boyzhang.projectmobilesafe.service.BlacklistService");
        if (serviceStatus) {
            siItemsBlacklist.setChecked(true);
        } else {
            siItemsBlacklist.setChecked(false);
        }

        // 设置点击侦听
        siItemsBlacklist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (siItemsBlacklist.getChecked()) {
                    // 取消选择
                    siItemsBlacklist.setChecked(false);
                    // 关闭服务
                    stopService(new Intent(SettingActivity.this,
                            BlacklistService.class));
                } else {
                    // 选择
                    siItemsBlacklist.setChecked(true);
                    // 开启服务
                    startService(new Intent(SettingActivity.this,
                            BlacklistService.class));
                }
            }
        });
    }

    /**
     * 初始化桌面快捷方式节点
     */
    private void initShortcut() {

        //初始化
        if (ShortcutUtils.isExist(SettingActivity.this, "F卫士")) {
            siItemsDesktopIcon.setChecked(true);
        } else {
            siItemsDesktopIcon.setChecked(false);
        }

        //点击侦听
        siItemsDesktopIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断是否已经创建了快捷图标
                if (ShortcutUtils.isExist(SettingActivity.this, "F卫士")) {
                    //删除快捷方式意图----需要使隐式意图--与创建意图一样
                    Intent doWhatIntent = new Intent();
                    doWhatIntent.setAction("com.boyzhang.projectmobilesafe.home");
                    doWhatIntent.addCategory("android.intent.category.DEFAULT");

                    //删除快捷图标
                    ShortcutUtils.deleteShortcut(SettingActivity.this, "F卫士", doWhatIntent, true);

                    siItemsDesktopIcon.setChecked(false);

                } else {
                    //创建快捷图标
                    //创建快捷方式意图----需要使隐式意图--与删除意图一样
                    Intent doWhatIntent = new Intent();
                    doWhatIntent.setAction("com.boyzhang.projectmobilesafe.home");
                    doWhatIntent.addCategory("android.intent.category.DEFAULT");

                    //图标
                    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
                    //创建快捷图标
                    ShortcutUtils.createShortcut(SettingActivity.this, "F卫士", doWhatIntent, icon, false);

                    siItemsDesktopIcon.setChecked(true);
                }


            }
        });

    }

    /**
     * 初始化看门狗节点
     */
    private void initWatchdog() {

        // 判断看门狗服务是否是开启状态
        boolean serviceStatus = ServiceStatusUtils.isServiceRunning(this,
                "com.boyzhang.projectmobilesafe.service.WatchdogService");
        if (serviceStatus) {
            si_items_watchdog.setChecked(true);
        } else {
            si_items_watchdog.setChecked(false);
        }

        //点击侦听
        si_items_watchdog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (si_items_watchdog.getChecked()) {
                    si_items_watchdog.setChecked(false);
                    stopService(new Intent(SettingActivity.this, WatchdogService.class));
                } else {
                    si_items_watchdog.setChecked(true);
                    startService(new Intent(SettingActivity.this, WatchdogService.class));
                }
            }
        });
    }
}
