package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.adapter.ListViewBaseAdapter;
import com.boyzhang.projectmobilesafe.bean.CacheInfos;
import com.boyzhang.projectmobilesafe.utils.UiUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 缓存清理
 */
public class CacheClearActivity extends Activity {

    private ListView lv_cacheList;
    private LinearLayout llLoadProgress;

    private PackageManager packageManager;
    private List<CacheInfos> cacheInfos;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //将圆圈隐藏
            llLoadProgress.setVisibility(View.INVISIBLE);

            //更新UI
            initUI();
        }
    };
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_clear);

        //获取控件
        lv_cacheList = (ListView) findViewById(R.id.lv_cacheList);
        llLoadProgress = (LinearLayout) findViewById(R.id.ll_loadProgress);

        initData();//初始化数据
    }

    /**
     * 初始化数据
     */
    private void initData() {

        cacheInfos = new ArrayList<>();//用来存放应用信息

        //拿到包管理器
        packageManager = getPackageManager();

        new Thread() {


            @Override
            public void run() {
                super.run();

                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                //迭代所有包信息
                for (PackageInfo installPackage : installedPackages) {

                    getCacheSize(installPackage);
                }

                //这里数据的初始化完成使用消息队列发送消息更新UI
                handler.sendEmptyMessageDelayed(0, 3000);//这里因为AIDL在子线程中运行的,不得不延迟,否则数据获取没有完成就更新UI了!!!!因此这里是个隐患

            }
        }.start();
    }

    /**
     * 根据包名获取到应用的缓存大小,使用反射实现
     *
     * @param installPackage
     */
    private void getCacheSize(PackageInfo installPackage) {

        /**
         * 在PackageManager类中有getPackageSizeInfo()方法可以获取到应用程序的缓存大小,但是此方法是被隐藏的,因此需要使用反射
         */

        try {

            Class<?> clazz = getClassLoader().loadClass("android.content.pm.PackageManager");//使用类加载器获取到PackageManager的字节码

            /**
             * 使用获取到方法
             * 参数:
             *      String 方法名称
             *      args..  参数类型的字节码
             */
            Method method = clazz.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);

            /**
             * 运行方法
             *
             *  参数:
             *      调用此方法的对象
             *      后两个是函数getPackageSizeInfo()的参数
             */
            method.invoke(packageManager, installPackage.packageName, new MyIPackageStatsObserver(installPackage));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取缓存大小AIDL的继承
     */
    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

        PackageInfo installPackage;

        public MyIPackageStatsObserver(PackageInfo installPackage) {
            this.installPackage = installPackage;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {

            long cacheSize = pStats.cacheSize;//得到缓存大小

            if (cacheSize > 0) {//没有缓存的就不处理了

                String appName = installPackage.applicationInfo.loadLabel(packageManager).toString();//得到应用名称

                Drawable icon = installPackage.applicationInfo.loadIcon(packageManager);//得到应用LOGO

                String packageName = installPackage.applicationInfo.packageName;//应用包名

                CacheInfos cacheInfo = new CacheInfos(cacheSize, appName, packageName, icon);

                cacheInfos.add(cacheInfo);//增加到集合中
            }
        }
    }

    /**
     * 更新UI
     */
    public void initUI() {

        myAdapter = new MyAdapter(CacheClearActivity.this, cacheInfos);
        lv_cacheList.setAdapter(myAdapter);
    }

    /**
     * Adapter
     */
    class MyAdapter extends ListViewBaseAdapter<CacheInfos> {

        public MyAdapter(Context mContext, List<CacheInfos> list) {
            super(mContext, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = new ViewHolder();
            if (convertView == null) {
                convertView = View.inflate(CacheClearActivity.this, R.layout.view_list_item_cache_clear, null);

                viewHolder.iv_clear_icon = (ImageView) convertView.findViewById(R.id.iv_clear_icon);
                viewHolder.tv_clear_appName = (TextView) convertView.findViewById(R.id.tv_clear_appName);
                viewHolder.tv_clear_cacheSize = (TextView) convertView.findViewById(R.id.tv_clear_cacheSize);
                viewHolder.iv_clear = (ImageView) convertView.findViewById(R.id.iv_clear);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.iv_clear_icon.setImageDrawable(cacheInfos.get(position).getIcon());
            viewHolder.tv_clear_appName.setText(cacheInfos.get(position).getAppName());
            viewHolder.tv_clear_cacheSize.setText(cacheInfos.get(position).getCacheSize(CacheClearActivity.this));

            viewHolder.iv_clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UiUtils.showToast(CacheClearActivity.this, "" + position);
                }
            });

            return convertView;
        }
    }

    /**
     * ViewHolder
     */
    class ViewHolder {
        ImageView iv_clear_icon;
        TextView tv_clear_appName;
        TextView tv_clear_cacheSize;
        ImageView iv_clear;
    }

    /**
     * 一键清理垃圾,清理缓存需要调用PackageManager类的freeStorageAndNotify()方法,此方法是系统方法,需要使用反射
     *
     * @param v
     */
    public void clearAllCache(View v) {

        PackageManager packageManager = getPackageManager();

        try {

            Class<?> clazz = getClassLoader().loadClass("android.content.pm.PackageManager");

            Method[] methods = clazz.getMethods();//得到此类的所有方法

            //遍历方法看看有没有freeStorageAndNotify方法,如果有就使用它清理缓存
            for (Method method : methods) {

                String name = method.getName();

                if (name.equals("freeStorageAndNotify")) {
                    method.invoke(packageManager, Integer.MAX_VALUE, new MyIPackageDataObserver());//实际证明不好使
                }

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    /**
     * 清理缓存AIDL的继承
     */
    class MyIPackageDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }

}
