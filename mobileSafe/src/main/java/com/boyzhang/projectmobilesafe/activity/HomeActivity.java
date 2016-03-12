package com.boyzhang.projectmobilesafe.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.utils.EncodingUtils;

/**
 * home页面,功能列表页面
 *
 * @author HaiFeng
 */
public class HomeActivity extends Activity {

    private GridView gridView;
    String[] funcionText = {"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心", "应用中心"};
    int[] functionIcon = {R.drawable.home_safe, R.drawable.home_callmsgsafe,
            R.drawable.home_apps, R.drawable.home_taskmanager,
            R.drawable.home_netmanager, R.drawable.home_trojan,
            R.drawable.home_sysoptimize, R.drawable.home_tools,
            R.drawable.home_settings, R.drawable.sprite_ui};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gridView = (GridView) findViewById(R.id.gv_home_functionList);

        // 为gridView设置适配器
        gridView.setAdapter(new HomeGridViewAdapter());
        // 设置节点的点击监听
        gridView.setOnItemClickListener(new OnItemClickListener() {

            private SharedPreferences prefConfig;
            private EditText pass_login;
            private Button ok_login;
            private AlertDialog dialog_login;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        // 第一个0个节点被点击,弹密码框
                        alertdialog();
                        // Intent intent0 = new Intent(HomeActivity.this,
                        // AntiStealActivity.class);
                        // startActivity(intent0);
                        break;
                    case 1:
                        // 第一个1个节点被点击,跳转黑名单Activity
                        enterBlacklist();
                        break;
                    case 2:
                        // 第一个2个节点被点击,跳转软件管理Activity
                        enterAppManage();
                        break;
                    case 3:
                        // 第一个2个节点被点击,跳转软件管理Activity
                        enterTaskManage();
                        break;
                    case 4:
                        //第3个节点点击,转流量管理
                        entryTrafficManager();
                        break;
                    case 5:
                        // 第一个5个节点被点击,跳转病毒查杀Activity
                        enterAntivirus();
                        break;
                    case 6:
                        //第7个节点点击跳转到缓存清理Activity
                        enterCacheClear();
                        break;
                    case 7:
                        // 第八个节点点击
                        enterAdvanceTools();
                        break;
                    case 8:
                        // 第九个节点点击时跳转设置中心
                        enterSetting();
                        break;
                    case 9:
                        //第八个点击进入应用市场
                        enterAppMarket();
                        break;
                }
            }

            // 用户点击了第一个节点手机防盗功能会调用此方法
            private void alertdialog() {
                prefConfig = getSharedPreferences("config", MODE_PRIVATE);
                if (prefConfig.getBoolean("isConfigAntiSteal", false)) {
                    // 如果已经设置过节弹出登录对话框
                    alertLoginAntiStealdialog();

                } else {
                    // 如果没有设置就进入设置向导页面
                    enterAntiStealSetipWizard();
                }
            }

            // 弹出登录手机防盗功能对话框
            private void alertLoginAntiStealdialog() {
                AlertDialog.Builder builder = new Builder(HomeActivity.this);
                dialog_login = builder.create();
                // 将布局问填充问dialog_set
                View view = View.inflate(HomeActivity.this,
                        R.layout.antisteal_login_dialog, null);
                // 获取view中控件
                pass_login = (EditText) view
                        .findViewById(R.id.e_login_password);
                ok_login = (Button) view.findViewById(R.id.b_login_login);

                // 为登录按钮设置监听
                ok_login.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password_login = pass_login.getText().toString();
                        // 判断密码是否正确
                        if (EncodingUtils.md5(password_login)
                                .equals(prefConfig.getString(
                                        "antiStealPassword", null))) {
                            // 密码正确准备跳转
                            enterAntiSteal();
                        } else {
                            // 不正确
                            Toast.makeText(HomeActivity.this, "密码错误",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog_login.dismiss();
                    }
                });
                // 数字参数是4个边的内边距
                dialog_login.setView(view, 0, 0, 0, 0);
                dialog_login.show();
            }

            // 进入手机防盗的设置向导页面
            private void enterAntiStealSetipWizard() {
                Intent intent = new Intent(HomeActivity.this,
                        AntiStealSetupWizard.class);
                startActivity(intent);
            }

        });
    }


    /**
     * 创建GridView 的适配器
     *
     * @author HaiFeng
     */
    class HomeGridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return funcionText.length;
        }

        @Override
        public Object getItem(int position) {
            return funcionText[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(HomeActivity.this,
                    R.layout.view_home_grid_item, null);
            ImageView ivFunctionIcon = (ImageView) view
                    .findViewById(R.id.iv_functionIcon);
            TextView tvFunctionText = (TextView) view
                    .findViewById(R.id.tv_functionText);
            ivFunctionIcon.setImageResource(functionIcon[position]);
            tvFunctionText.setText(funcionText[position]);
            return view;
        }

    }

    /**
     * 以下为Activitie跳转函数
     */
    //进入手机防盗Activity
    private void enterAntiSteal() {
        Intent intent0 = new Intent(HomeActivity.this, AntiStealActivity.class);
        startActivity(intent0);
    }

    //进入黑名单管理Activity
    private void enterBlacklist() {

        //这里黑名单的Activity有三个,一个是一次性全部将黑名单加载出来,一个是分页加载,一个是分批加载
        Intent intent1 = new Intent(HomeActivity.this, BlacklistByBatchesActivity.class);
        startActivity(intent1);
    }

    //进入应用管理Activity
    private void enterAppManage() {
        Intent intent2 = new Intent(HomeActivity.this, AppManageActivity.class);
        startActivity(intent2);
    }

    //进入进程管理Activity
    private void enterTaskManage() {
        Intent intent3 = new Intent(HomeActivity.this, TaskManageActivity.class);
        startActivity(intent3);
    }

    //进入流量管理Activity
    private void entryTrafficManager() {
        Intent intent4 = new Intent(HomeActivity.this, TrafficManagerActivity.class);
        startActivity(intent4);
    }

    //进入病毒查杀Activity
    private void enterAntivirus() {
        Intent intent5 = new Intent(HomeActivity.this, AntivirusActivity.class);
        startActivity(intent5);
    }

    /**
     * 进入缓存清理Activity
     */
    private void enterCacheClear() {
        Intent intent6 = new Intent(HomeActivity.this, CacheClearActivity.class);
        startActivity(intent6);
    }

    //进入高级工具Activity
    private void enterAdvanceTools() {
        Intent intent7 = new Intent(HomeActivity.this,
                AdvanceToolsActivity.class);
        startActivity(intent7);
    }

    //进入设置Activity
    private void enterSetting() {
        Intent intent8 = new Intent(HomeActivity.this, SettingActivity.class);
        startActivity(intent8);
    }

    //进入应用市场
    private void enterAppMarket() {
        Intent intent9 = new Intent(HomeActivity.this, AppMarketActivity.class);
        startActivity(intent9);
    }
}
