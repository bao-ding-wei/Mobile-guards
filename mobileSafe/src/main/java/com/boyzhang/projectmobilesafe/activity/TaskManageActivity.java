package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.adapter.ListViewBaseAdapter;
import com.boyzhang.projectmobilesafe.bean.RunningAppInfos;
import com.boyzhang.projectmobilesafe.utils.ApplicationUtils;
import com.boyzhang.projectmobilesafe.utils.StorageUtils;
import com.boyzhang.projectmobilesafe.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskManageActivity extends Activity {

    public static final int RUNNING_APP_TYPE_COUNT = 2;
    public static final int RUNNING_APP_ITEM_NORMAL = 0;
    public static final int RUNNING_APP_ITEM_OTHER = 1;

    private TextView tvTaskManageRunningApp;
    private TextView tvTaskManageRamInfo;
    private ListView lvTaskManage;
    private TextView tvListRunningAppManageAppType;
    private LinearLayout llRunningAppManageLoadProgress;

    private List<RunningAppInfos> runningAppInfos;
    private List<RunningAppInfos> runningSysAppInfos;
    private List<RunningAppInfos> runningUserAppInfos;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //隐藏进度
            llRunningAppManageLoadProgress.setVisibility(View.INVISIBLE);

            //初始化UI
            initUI();
        }
    };
    private String[] ramInfo;
    private int taskCount;
    private MyAdapter<RunningAppInfos> runningAppInfosMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manage);

        /**
         * -------------------------------------------------------
         * Bundle savedInstanceState是干嘛的?????
         * 可以用它保存数据,savedInstanceState.putExtra(String , values);
         * 比如一个看书的Activity可以用它保存页码,当用户关闭按了home键后会重新调用
         * -------------------------------------------------------
         */


        //获取控件
        tvTaskManageRunningApp = (TextView) findViewById(R.id.tv_taskManage_runningApp);
        tvTaskManageRamInfo = (TextView) findViewById(R.id.tv_taskManage_ramInfo);
        lvTaskManage = (ListView) findViewById(R.id.lv_taskManage);
        llRunningAppManageLoadProgress = (LinearLayout) findViewById(R.id.ll_runningAppManage_loadProgress);
        tvListRunningAppManageAppType = (TextView) findViewById(R.id.tv_list_runningAppManage_appType);

        initData();//初始化数据
    }

    /**
     * 初始化数据
     * <p/>
     * ActivityManage 与 PackageManage 的区别
     * <p/>
     * ActivityManage : 活动进程(任务管理器)
     * <p/>
     * PackageManage : 包管理器
     */
    private void initData() {

        //新开一个线程获取数据
        new Thread() {
            @Override
            public void run() {
                super.run();

                runningAppInfos = ApplicationUtils.getRunningAppInfos(TaskManageActivity.this);//拿到运行中的App信息

                runningSysAppInfos = new ArrayList<>();
                runningUserAppInfos = new ArrayList<>();
                //拆分runningAppInfos为系统APP和用户APP
                for (RunningAppInfos runningAppInfo : runningAppInfos) {

                    //系统程序
                    if (runningAppInfo.getAppType() == RunningAppInfos.APP_TYPE_SYS) {
                        runningSysAppInfos.add(runningAppInfo);
                    } else {
                        //用户程序
                        runningUserAppInfos.add(runningAppInfo);
                    }
                }
                runningAppInfos.clear();//将runningAppInfos中的数据清空
                runningAppInfos.addAll(runningUserAppInfos);//将runningUserAppInfos添加到runningAppInfos中
                runningAppInfos.addAll(runningSysAppInfos);//将runningSysAppInfos添加到runningAppInfos中

                /**
                 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
                 */
                //由于增加了两个节点,所有ArrayList的数据也要增加--------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                runningAppInfos.add(0, null);
                runningAppInfos.add(runningUserAppInfos.size() + 1, null);

                taskCount = runningAppInfos.size();//拿到运行中的App个数,进程数
                ramInfo = StorageUtils.getRamInfo(TaskManageActivity.this);//内存信息

                //发送消息
                handler.sendEmptyMessage(0);

            }
        }.start();
    }

    /**
     * 初始化UI
     */
    private void initUI() {

        tvTaskManageRunningApp.setText("进程数 : " + (taskCount - TaskManageActivity.RUNNING_APP_TYPE_COUNT));
        tvTaskManageRamInfo.setText("运存 : " + ramInfo[1] + "/" + ramInfo[0]);

        /*
        //如果已经设置了适配器就更新数据
        if (runningAppInfosMyAdapter != null) {
            runningAppInfosMyAdapter.notifyDataSetChanged();
            return;
        }
        */

        //适配器
        runningAppInfosMyAdapter = new MyAdapter<>(TaskManageActivity.this, runningAppInfos);

        // 设置适配器
        lvTaskManage.setAdapter(runningAppInfosMyAdapter);

        //ListView滚动时监听,控制tvListAppManageAppType不同显示
        lvTaskManage.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //滚动时调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem < runningUserAppInfos.size() + 2) {
                    tvListRunningAppManageAppType.setText("用户程序(" + runningUserAppInfos.size() + ")");
                } else {
                    tvListRunningAppManageAppType.setText("系统程序(" + runningSysAppInfos.size() + ")");
                }
            }
        });

        //节点点击侦听
        lvTaskManage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //特殊节点控制
                if (position == 0 || position == runningUserAppInfos.size() + 1) {
                    return;
                }

                //拿到ViewHolder
                ViewHolder1 viewHolder1 = (ViewHolder1) view.getTag();
                RunningAppInfos infos = TaskManageActivity.this.runningAppInfos.get(position);//拿到RunningAppInfos

                if (infos.getPackageName().equals(getPackageName())) {//保护自己
                    return;
                }

                boolean checked = infos.isChecked();//节点是否选择
                if (checked) {
                    viewHolder1.ivRunningAppInfosSelector.setChecked(false);
                    infos.setIsChecked(false);
                } else {
                    viewHolder1.ivRunningAppInfosSelector.setChecked(true);
                    infos.setIsChecked(true);
                }

            }
        });
    }

    /**
     * 更新UI
     */
    public void updateUI() {
        if (runningAppInfosMyAdapter != null) {
            runningAppInfosMyAdapter.notifyDataSetChanged();
            return;
        }
    }

    class MyAdapter<T> extends ListViewBaseAdapter {

        public MyAdapter(Context mContext, List list) {
            super(mContext, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //拿到节点类型
            int itemViewType = getItemViewType(position);

            ViewHolder1 holder1;
            ViewHolder2 holder2;

            if (convertView == null) {

                switch (itemViewType) {
                    case TaskManageActivity.RUNNING_APP_ITEM_NORMAL:
                        convertView = View.inflate(TaskManageActivity.this, R.layout.view_list_runningappinfos_item, null);
                        holder1 = new ViewHolder1();
                        holder1.ivRunningAppInfosIcon = (ImageView) convertView.findViewById(R.id.iv_runningAppInfos_icon);
                        holder1.tvRunningAppInfosAppName = (TextView) convertView.findViewById(R.id.tv_runningAppInfos_appName);
                        holder1.tv_runningAppInfos_PID = (TextView) convertView.findViewById(R.id.tv_runningAppInfos_PID);
                        holder1.ivRunningAppInfosSelector = (CheckBox) convertView.findViewById(R.id.iv_runningAppInfos_selector);
                        holder1.tvRunningAppInfosMemoSize = (TextView) convertView.findViewById(R.id.tv_runningAppInfos_memoSize);

                        //保存convertView
                        convertView.setTag(holder1);
                        break;
                    case TaskManageActivity.RUNNING_APP_ITEM_OTHER:
                        holder2 = new ViewHolder2();
                        convertView = View.inflate(TaskManageActivity.this, R.layout.view_list_other_item, null);
                        holder2.tvListOtherItem = (TextView) convertView.findViewById(R.id.tv_list_otherItem);

                        //保存convertView
                        convertView.setTag(holder2);
                        break;
                    default:
                        break;
                }
            }

            switch (itemViewType) {
                case TaskManageActivity.RUNNING_APP_ITEM_NORMAL:
                    holder1 = (ViewHolder1) convertView.getTag();

                    //System.out.println(runningAppInfos.size());

                    //分类App类型
                    String appType;
                    if (runningAppInfos.get(position).getAppType() == RunningAppInfos.APP_TYPE_SYS) {
                        appType = "系统应用";
                    } else {
                        appType = "用户应用";
                    }

                    //保护自己程序,将F卫士的选择取消
                    if (runningAppInfos.get(position).getPackageName().equals(getPackageName())) {
                        holder1.ivRunningAppInfosSelector.setVisibility(View.INVISIBLE);
                    }

                    holder1.ivRunningAppInfosIcon.setImageDrawable(runningAppInfos.get(position).getIcon());
                    holder1.tvRunningAppInfosAppName.setText(runningAppInfos.get(position).getAppName());
                    holder1.tv_runningAppInfos_PID.setText("pid: " + runningAppInfos.get(position).getPID());
                    holder1.ivRunningAppInfosSelector.setChecked(runningAppInfos.get(position).isChecked());
                    holder1.tvRunningAppInfosMemoSize.setText(runningAppInfos.get(position).getMemoSize(TaskManageActivity.this));
                    break;
                case TaskManageActivity.RUNNING_APP_ITEM_OTHER:
                    holder2 = (ViewHolder2) convertView.getTag();
                    String text;
                    if (position == 0) {
                        text = "用户程序(" + runningUserAppInfos.size() + ")";
                    } else {
                        text = "系统程序(" + runningSysAppInfos.size() + ")";
                    }
                    holder2.tvListOtherItem.setText(text);
                    break;
                default:
                    break;
            }


            return convertView;
        }


        //节点类型总数
        @Override
        public int getViewTypeCount() {
            return TaskManageActivity.RUNNING_APP_TYPE_COUNT;
        }

        //返回节点类型
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == runningUserAppInfos.size() + 1) {
                return TaskManageActivity.RUNNING_APP_ITEM_OTHER;//普通节点
            } else {
                return TaskManageActivity.RUNNING_APP_ITEM_NORMAL;//特殊节点
            }
        }
    }

    /**
     * ViewHolder1
     */
    static class ViewHolder1 {
        ImageView ivRunningAppInfosIcon;
        TextView tvRunningAppInfosAppName;
        TextView tv_runningAppInfos_PID;
        CheckBox ivRunningAppInfosSelector;
        TextView tvRunningAppInfosMemoSize;
    }

    /**
     * ViewHolder2
     */
    static class ViewHolder2 {
        TextView tvListOtherItem;
    }

    /**
     * 反选
     *
     * @param v
     */
    public void selectInvert(View v) {

        for (RunningAppInfos runningAppInfo : runningAppInfos) {

            if (runningAppInfo != null) {
                if (runningAppInfo.isChecked()) {
                    runningAppInfo.setIsChecked(false);
                } else {
                    runningAppInfo.setIsChecked(true);
                }
                if (runningAppInfo.getPackageName().equals(getPackageName())) {//让自己应用不被选择
                    runningAppInfo.setIsChecked(false);
                }
            }
        }
        updateUI();//更新UI

    }

    /**
     * 全选
     *
     * @param v
     */
    public void selectAll(View v) {
        for (RunningAppInfos runningAppInfo : runningAppInfos) {

            if (runningAppInfo != null && !runningAppInfo.getPackageName().equals(getPackageName())) {//让自己应用不被选择
                runningAppInfo.setIsChecked(true);
            }
        }
        updateUI();//更新UI
    }

    /**
     * 清理
     * <p/>
     * 需要权限 : android.permission.KILL_BACKGROUND_PROCESSES
     *
     * @param v
     */
    public void clear(View v) {

        int killCount = 0;//杀死的进程数
        //ArrayList<RunningAppInfos> killedTask = new ArrayList<>();//杀死的进程信息

        //得到进程管理器
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        //循环Kill进程
        for (RunningAppInfos runningAppInfo : runningAppInfos) {

            if (runningAppInfo != null) {

                if (runningAppInfo.isChecked()) {
                    //杀死进程
                    activityManager.killBackgroundProcesses(runningAppInfo.getPackageName());

                    killCount++;
                    //killedTask.add(runningAppInfo);//这里在迭代的时候List不能直接删除,所以使用killedTask将要kill的进程信息保存,等迭代完成后一起删除
                }
            }
        }

        //runningAppInfos.removeAll(killedTask);//移除杀死的进程
        //runningSysAppInfos.removeAll(killedTask);
        //runningUserAppInfos.removeAll(killedTask);
        initData();//更新数据

        //弹吐司
        UiUtils.showToast(TaskManageActivity.this, "一共清理了" + killCount + "个进程");
    }

    /**
     * 进程设置
     */
    public void taskSetting(View v) {

    }

}
