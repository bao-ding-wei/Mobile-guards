package com.boyzhang.projectmobilesafe.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.adapter.ListViewBaseAdapter;
import com.boyzhang.projectmobilesafe.bean.AppInfos;
import com.boyzhang.projectmobilesafe.utils.ApplicationUtils;
import com.boyzhang.projectmobilesafe.utils.StorageUtils;


import java.util.ArrayList;
import java.util.List;

public class AppManageActivity extends Activity implements View.OnClickListener {

    public static final int FLAG_INSTALL = 0;

    public static final int TYPE_TITLE = 0;
    public static final int TYPE_NORMAL_ITEM = 1;
    public static final int TYPE_COUNT = 2;

    private TextView tvAppManageRomInfo;
    private TextView tvAppManageSDcardInfo;
    private String[] romInfo;
    private String[] sdCardInfo;
    private List<AppInfos> appInfos;


    private ListView lvAppManage;
    private MyAdapter<AppInfos> appInfosMyAdapter;
    private LinearLayout llAppManageLoadProgress;
    private List<AppInfos> sysApp;
    private List<AppInfos> userApp;
    private TextView tvListAppManageAppType;
    private PopupWindow popupWindow;
    private AppInfos clickItemInfos;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //隐藏进度圈
            llAppManageLoadProgress.setVisibility(View.INVISIBLE);

            //更新UI
            initUI();
        }
    };
    private AppUninstallReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manage);

        //获取控件
        lvAppManage = (ListView) findViewById(R.id.lv_appManage);
        tvAppManageRomInfo = (TextView) findViewById(R.id.tv_appManage_romInfo);
        tvAppManageSDcardInfo = (TextView) findViewById(R.id.tv_appManage_SDcardInfo);
        llAppManageLoadProgress = (LinearLayout) findViewById(R.id.ll_appManage_loadProgress);
        tvListAppManageAppType = (TextView) findViewById(R.id.tv_list_appManage_appType);

        //当App被卸载时注册APP卸载的广播,用来实时刷新UI
        receiver = new AppUninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);//注册包移除广播
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);

        //初始化数据
        initData();
    }

    /**
     * 重写点击事件,这样可以根据ID判断点-------------******________--------------********!!!!!!!!^^^^^^^^&&&&&&&&&&&
     * setOnClickListener(Activity.this);中参数要传入当前Activity的对象
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        /**
         * 点击事件分别对应
         *      查看APP详情
         *      启动APP
         *      短信分享APP
         */
        switch (v.getId()) {
            case R.id.ll_appManage_popupwindow_detail:
                //查看APP详情
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + clickItemInfos.getAppPackageName()));
                startActivity(detail_intent);
                //销毁PopupWindow
                dismissPopupWindow();
                break;
            case R.id.ll_appManage_popupwindow_start:
                //启动APP
                Intent start_localIntent = this.getPackageManager().getLaunchIntentForPackage(clickItemInfos.getAppPackageName());
                this.startActivity(start_localIntent);
                //销毁PopupWindow
                dismissPopupWindow();
                break;
            case R.id.ll_appManage_popupwindow_share:
                //短信分享APP----使用google的地址,Android中会自动弹出各类APP商店APP
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                shareIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickItemInfos.getAppName() + "下载地址:" + "https://play.google.com/store/apps/details?id=" + clickItemInfos.getAppPackageName());
                this.startActivity(Intent.createChooser(shareIntent, "分享"));
                //销毁PopupWindow
                dismissPopupWindow();
                break;
            default:
                break;
        }

    }

    /**
     * 结果回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppManageActivity.FLAG_INSTALL) {
            //System.out.println("卸载");

            //刷新显示
            appInfosMyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Activity销毁时销毁PopupWindow否则会在LogCat报错
        dismissPopupWindow();

        //销毁APP卸载广播
        unregisterReceiver(receiver);
    }

    /**
     * 初始化UI
     */
    private void initUI() {

        //刷新存储信息UI
        tvAppManageRomInfo.setText("ROM: " + romInfo[1] + "/" + romInfo[0]);
        tvAppManageSDcardInfo.setText("SDcard: " + sdCardInfo[1] + "/" + sdCardInfo[0]);

        //刷新ListView
        appInfosMyAdapter = new MyAdapter<>(AppManageActivity.this, appInfos);
        lvAppManage.setAdapter(appInfosMyAdapter);

        //ListView滚动时监听,控制tvListAppManageAppType不同显示
        lvAppManage.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //滚动时调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //ListView滚动时销毁PopupWindow
                dismissPopupWindow();

                if (firstVisibleItem < userApp.size() + 2) {
                    tvListAppManageAppType.setText("用户程序(" + userApp.size() + ")");
                } else {
                    tvListAppManageAppType.setText("系统程序(" + sysApp.size() + ")");
                }
            }
        });

        //设置ListView的节点点击侦听,弹出PopupWindow
        lvAppManage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                clickItemInfos = appInfos.get(position);

                //如果不是普通节点点击了就返回去
                if (position == 0 || position == userApp.size() + 1) {
                    return;
                }

                //点击时如果PopupWindow已经显示就销毁
                dismissPopupWindow();

                //填充布局
                View contentView = View.inflate(AppManageActivity.this, R.layout.view_appmanage_popupwindow, null);

                //获取PopupWindow中控件
                LinearLayout llAppManagePopupwindowDetail = (LinearLayout) contentView.findViewById(R.id.ll_appManage_popupwindow_detail);
                LinearLayout llAppManagePopupwindowStart = (LinearLayout) contentView.findViewById(R.id.ll_appManage_popupwindow_start);
                LinearLayout llAppManagePopupwindowShare = (LinearLayout) contentView.findViewById(R.id.ll_appManage_popupwindow_share);
                //为控件设置点击侦听
                llAppManagePopupwindowDetail.setOnClickListener(AppManageActivity.this);
                llAppManagePopupwindowStart.setOnClickListener(AppManageActivity.this);
                llAppManagePopupwindowShare.setOnClickListener(AppManageActivity.this);

                /*
                int[] location = new int[2];
                view.getLocationInWindow(location);
                location[1] : 就是view在Window上的高度
                */
                /**
                 * 创建PopupWindow
                 * 参数:
                 * contentView : 填充的View对象
                 * width : View显示的宽度
                 * height : VieW显示的高度
                 */
                popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                //需要注意的是:PopupWindow 要想设置动画,PopupWindow就要设置一个背景,这里为PopupWindow设置一个透明背景即可------------!!!!!
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                /**
                 * 显示PopupWindow
                 * 参数:
                 *      view : 传入一个父View
                 *      gravity : 布局的模式,左上中右下的对齐模式
                 *      x: popupWindow 在屏幕上显示的X坐标
                 *      Y: popupWindow 在屏幕上显示的Y坐标
                 */
                popupWindow.showAtLocation(view, Gravity.RIGHT + Gravity.TOP, (int) view.getX(), (int) view.getY() + view.getHeight());


                //为popupWindow设置一个动画
                ScaleAnimation sa = new ScaleAnimation(0.5F, 1.0F, 0.5F, 1.0F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
                sa.setDuration(500);
                AlphaAnimation aa = new AlphaAnimation(0, 1);
                aa.setDuration(500);

                AnimationSet as = new AnimationSet(true);//动画集合
                as.addAnimation(sa);
                as.addAnimation(aa);

                contentView.startAnimation(as);
            }
        });
    }

    /**
     * 初始化数据
     */
    public void initData() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                //获取存储信息
                romInfo = StorageUtils.getRomSizeInfo(AppManageActivity.this);
                sdCardInfo = StorageUtils.getExternalStorageSizeInfo(AppManageActivity.this);

                //获取所有App信息
                appInfos = ApplicationUtils.getApplicationInfos(AppManageActivity.this);

                sysApp = new ArrayList<>();
                userApp = new ArrayList<>();
                //拆分appInfos为系统APP和用户APP
                for (AppInfos appInfo : appInfos) {

                    //系统程序
                    if (appInfo.getAppType() == AppInfos.APP_SYSTEM) {
                        sysApp.add(appInfo);
                    } else {
                        //用户程序
                        userApp.add(appInfo);
                    }
                }

                appInfos.clear();//将appInfos中的数据清空
                appInfos.addAll(userApp);//将userApp添加到appInfos中
                appInfos.addAll(sysApp);//将sysApp添加到appInfos中

                //由于增加了两个节点,所有ArrayList的数据也要增加--------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                appInfos.add(0, null);
                appInfos.add(userApp.size() + 1, null);

                //System.out.println(sysApp.size());
                //System.out.println(userApp.size());

                //发送更新UI信息
                handler.sendEmptyMessage(0);


                /*
                for (AppInfos info : appInfos) {
                    System.out.println(info);
                }
                */

            }
        }.start();
    }

    /**
     * Adapter,此Adapter使用了不同布局的Item,重写了getItemViewType方法
     *
     * @param <AppInfos>
     */
    private class MyAdapter<AppInfos> extends ListViewBaseAdapter {

        public MyAdapter(Context mContext, List list) {
            super(mContext, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //获取当前Item的类型
            int currentType = getItemViewType(position);

            ViewHolder1 holder1 = null;
            ViewHolder2 holder2 = null;

            /**
             * 如果convertView是空,新建所有的convertView,根据currentType区分不同的convertView
             */
            if (convertView == null) {
                switch (currentType) {
                    //普通节点
                    case AppManageActivity.TYPE_NORMAL_ITEM:
                        //---------------------------------------------------------------------------------------------
                        holder1 = new ViewHolder1();
                        convertView = View.inflate(AppManageActivity.this, R.layout.view_list_appinfos_item, null);
                        holder1.ivAppInfosIcon = (ImageView) convertView.findViewById(R.id.iv_appInfos_icon);
                        holder1.tvAppInfosAppName = (TextView) convertView.findViewById(R.id.tv_appInfos_appName);
                        holder1.tvAppInfosAppLocation = (TextView) convertView.findViewById(R.id.tv_appInfos_appLocation);
                        holder1.tvAppInfosAppSize = (TextView) convertView.findViewById(R.id.tv_appInfos_appSize);
                        holder1.ivAppInfosUnInstall = (ImageView) convertView.findViewById(R.id.iv_appInfos_unInstall);

                        //保存convertView
                        convertView.setTag(holder1);
                        //---------------------------------------------------------------------------------------------
                        break;
                    //标题节点
                    case AppManageActivity.TYPE_TITLE:
                        //---------------------------------------------------------------------------------------------
                        holder2 = new ViewHolder2();
                        convertView = View.inflate(AppManageActivity.this, R.layout.view_list_other_item, null);

                        holder2.tvListOtherItem = (TextView) convertView.findViewById(R.id.tv_list_otherItem);

                        //保存convertView
                        convertView.setTag(holder2);
                        //---------------------------------------------------------------------------------------------
                        break;
                    default:
                        break;
                }
            }

            //根据currentType为View设置不同的数据
            switch (currentType) {
                case AppManageActivity.TYPE_NORMAL_ITEM:
                    //---------------------------------------------------------------------------
                    holder1 = (ViewHolder1) convertView.getTag();
                    //判断app是安装在哪里的
                    String location = "未知安装位置";
                    if (appInfos.get(position).getAppLocation() == com.boyzhang.projectmobilesafe.bean.AppInfos.LOCATION_ROM) {
                        location = "手机内存";
                    } else {
                        location = "SD卡存储";
                    }
                    //System.out.println(appInfos.get(position).getAppName());
                    holder1.ivAppInfosIcon.setImageDrawable(appInfos.get(position).getAppLogo());
                    holder1.tvAppInfosAppName.setText(appInfos.get(position).getAppName());
                    holder1.tvAppInfosAppLocation.setText(location);
                    holder1.tvAppInfosAppSize.setText(appInfos.get(position).getAppSize(AppManageActivity.this));
                    /**
                     * 卸载按钮侦听
                     */
                    holder1.ivAppInfosUnInstall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //点击卸载按钮时,销毁PopupWindow
                            dismissPopupWindow();

                            //卸载App
                            unInstallApp(appInfos.get(position).getAppPackageName());
                        }
                    });
                    //---------------------------------------------------------------------------
                    break;
                case AppManageActivity.TYPE_TITLE:
                    //---------------------------------------------------------------------------
                    holder2 = (ViewHolder2) convertView.getTag();
                    String text = "";
                    if (position == 0) {
                        text = "用户程序(" + userApp.size() + ")";
                    } else {
                        text = "系统程序(" + sysApp.size() + ")";
                    }
                    holder2.tvListOtherItem.setText(text);
                    //---------------------------------------------------------------------------
                    break;
                default:
                    break;
            }

            //返回View
            return convertView;
        }

        //返回convertView的类型
        @Override
        public int getItemViewType(int position) {

            if (position == 0 || position == userApp.size() + 1) {
                //如果是特殊节点接返回TYPE_TITLE
                return AppManageActivity.TYPE_TITLE;
            } else {
                //如果是普通节点就返回TYPE_NORMAL_ITEM
                return AppManageActivity.TYPE_NORMAL_ITEM;
            }
        }

        //返回View的种类数目
        @Override
        public int getViewTypeCount() {
            return AppManageActivity.TYPE_COUNT;
        }
    }

    /**
     * ViewHolder1
     */
    public static class ViewHolder1 {

        ImageView ivAppInfosIcon;
        TextView tvAppInfosAppName;
        TextView tvAppInfosAppLocation;
        TextView tvAppInfosAppSize;
        ImageView ivAppInfosUnInstall;

    }

    /**
     * ViewHolder2
     */
    public static class ViewHolder2 {

        TextView tvListOtherItem;

    }

    /**
     * 销毁PopupWindow
     */
    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 根据包名卸载APP
     */
    private void unInstallApp(String packageName) {

        //----------------------------------------------------------------------------
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        // Uri.fromFile(arg0.result)是设置下载后的apk路径
        intent.setData(Uri.parse("package:" + packageName));
        //intent.addFlags(position);//设置一个标记
        // 如果用户取消安装会返回结果
        startActivityForResult(intent, AppManageActivity.FLAG_INSTALL);
    }

    /**
     * 接收APP卸载的广播
     */
    class AppUninstallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //System.out.println("app卸载了");
            //接收到包移除后就刷新ListView的显示
            initData();
        }
    }
}
