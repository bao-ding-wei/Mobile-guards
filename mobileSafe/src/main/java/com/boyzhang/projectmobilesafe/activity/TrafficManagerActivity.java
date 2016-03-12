package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.adapter.ListViewBaseAdapter;
import com.boyzhang.projectmobilesafe.bean.TraffficInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量统计
 * <p/>
 * static long  getMobileRxBytes()  //获取通过Mobile连接收到的字节总数，不包含WiFi
 * static long  getMobileRxPackets()  //获取Mobile连接收到的数据包总数
 * static long  getMobileTxBytes()  //Mobile发送的总字节数
 * static long  getMobileTxPackets()  //Mobile发送的总数据包数
 * static long  getTotalRxBytes()  //获取总的接受字节数，包含Mobile和WiFi等
 * static long  getTotalRxPackets()  //总的接受数据包数，包含Mobile和WiFi等
 * static long  getTotalTxBytes()  //总的发送字节数，包含Mobile和WiFi等
 * static long  getTotalTxPackets()  //发送的总数据包数，包含Mobile和WiFi等
 * static long  getUidRxBytes(int uid)  //获取某个网络UID的接受字节数
 * static long  getUidTxBytes(int uid) //获取某个网络UID的发送字节数
 * 总接受流量TrafficStats.getTotalRxBytes()，
 * 总发送流量TrafficStats.getTotalTxBytes());
 * 不包含WIFI的手机GPRS接收量TrafficStats.getMobileRxBytes());
 * 不包含Wifi的手机GPRS发送量TrafficStats.getMobileTxBytes());
 * 某一个进程的总接收量TrafficStats.getUidRxBytes(Uid));
 * 某一个进程的总发送量TrafficStats.getUidTxBytes(Uid));
 * 这些都是从第一次启动程序到最后一次启动的统计量。并不是这篇文章里所说的“从本次开机到本次关机的统计量”！
 */
public class TrafficManagerActivity extends Activity {

    private LinearLayout ll_trafficManager_loadProgress;
    private ListView lv_trafficManager;

    PackageManager packageManager;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //隐藏进度圈
            ll_trafficManager_loadProgress.setVisibility(View.INVISIBLE);

            //更新UI
            initUI();
        }
    };
    private List<TraffficInfos> traffficInfosList;
    private MyAdapter trafficMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);

        //获取控件
        ll_trafficManager_loadProgress = (LinearLayout) findViewById(R.id.ll_trafficManager_loadProgress);
        lv_trafficManager = (ListView) findViewById(R.id.lv_trafficManager);


        initData();
    }

    /**
     * 初始化数据
     */
    public void initData() {

        traffficInfosList = new ArrayList<>();

        packageManager = getPackageManager();


        new Thread() {

            @Override
            public void run() {
                super.run();

                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_PERMISSIONS);//得到所有安装的包

                for (PackageInfo ininstalledPackage : installedPackages) {


                    String[] permissions = ininstalledPackage.requestedPermissions;//得到应用的所有权限

                    if (permissions != null && permissions.length > 0) {//如果权限不为空

                        System.out.println(permissions.length);

                        for (String permission : permissions) {//迭代权限
                            if (permission.equals("android.permission.INTERNET")) {//如果有网络权限,说明是个网络应用

                                String appname = ininstalledPackage.applicationInfo.loadLabel(packageManager).toString();
                                String packageName = ininstalledPackage.packageName;
                                Drawable icon = ininstalledPackage.applicationInfo.loadIcon(packageManager);

                                int uid = ininstalledPackage.applicationInfo.uid;

                                long uploadTraffic = TrafficStats.getUidTxBytes(uid);//获取到上传的流量
                                long downloadTraffic = TrafficStats.getUidRxBytes(uid);//获取到下载的流量
                                long totalTraffic = uploadTraffic + downloadTraffic;//上传下载的总流量

                                TraffficInfos traffficInfos = new TraffficInfos(appname, packageName, icon, uploadTraffic, downloadTraffic, totalTraffic);
                                traffficInfosList.add(traffficInfos);//增加到集合
                            }
                        }
                    }
                }

                //通知更新UI
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 初始化UI
     */
    private void initUI() {

        //刷新ListView
        trafficMyAdapter = new MyAdapter(TrafficManagerActivity.this, traffficInfosList);
        lv_trafficManager.setAdapter(trafficMyAdapter);

    }

    /**
     * Adapter
     */
    class MyAdapter extends ListViewBaseAdapter<TraffficInfos> {

        public MyAdapter(Context mContext, List<TraffficInfos> list) {
            super(mContext, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                convertView = View.inflate(TrafficManagerActivity.this, R.layout.view_list_item_traffic, null);

                viewHolder.iv_trafficManager_item_icon = (ImageView) convertView.findViewById(R.id.iv_trafficManager_item_icon);
                viewHolder.tv_trafficManager_item_appName = (TextView) convertView.findViewById(R.id.tv_trafficManager_item_appName);
                viewHolder.tv_trafficManager_item_uploadSize = (TextView) convertView.findViewById(R.id.tv_trafficManager_item_uploadSize);
                viewHolder.tv_trafficManager_item_downloadSize = (TextView) convertView.findViewById(R.id.tv_trafficManager_item_downloadSize);
                viewHolder.iv_trafficManager_item_totalSize = (TextView) convertView.findViewById(R.id.iv_trafficManager_item_totalSize);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.iv_trafficManager_item_icon.setImageDrawable(traffficInfosList.get(position).getIcon());
            viewHolder.tv_trafficManager_item_appName.setText(traffficInfosList.get(position).getAppName());
            viewHolder.tv_trafficManager_item_uploadSize.setText("上传 : " + traffficInfosList.get(position).getUploadTraffic(TrafficManagerActivity.this));
            viewHolder.tv_trafficManager_item_downloadSize.setText("下载 : " + traffficInfosList.get(position).getDownloadTraffic(TrafficManagerActivity.this));
            viewHolder.iv_trafficManager_item_totalSize.setText(traffficInfosList.get(position).getTotalTraffic(TrafficManagerActivity.this));

            return convertView;
        }
    }

    /**
     * ViewHolder
     */
    class ViewHolder {
        ImageView iv_trafficManager_item_icon;
        TextView tv_trafficManager_item_appName;
        TextView tv_trafficManager_item_uploadSize;
        TextView tv_trafficManager_item_downloadSize;
        TextView iv_trafficManager_item_totalSize;
    }

}
