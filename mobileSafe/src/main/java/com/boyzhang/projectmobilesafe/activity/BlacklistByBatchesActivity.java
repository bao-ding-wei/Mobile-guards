package com.boyzhang.projectmobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.adapter.ListViewBaseAdapter;
import com.boyzhang.projectmobilesafe.bean.BlackListNumber;
import com.boyzhang.projectmobilesafe.db.dao.BlacklistDao;
import com.boyzhang.projectmobilesafe.db.dao.DbAddress;

import java.util.ArrayList;
import java.util.List;

public class BlacklistByBatchesActivity extends Activity {

    private ListView mlvBlacklist;
    private ArrayList<BlackListNumber> blackListNumbers;
    private LinearLayout llLoadProgress;

    private BlacklistDao blacklistDao;

    private int countItems = -1;//总条目数
    private int maxCount = 20;//每批最多显示的条目数
    private int startIndex = 0;

    private int alreadyPage = 0;//表示已经打开的页面数目

    private MyAdapter myAdapter = null;

    private boolean isInitDataNow = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //将圆圈隐藏
            llLoadProgress.setVisibility(View.INVISIBLE);

            if (blackListNumbers == null) {
                blackListNumbers = new ArrayList<>();
            }

            ArrayList<BlackListNumber> blackListNumbersAdd = (ArrayList<BlackListNumber>) msg.obj;
            blackListNumbers.addAll(blackListNumbersAdd);
            //更新UI
            initView();
        }
    };
    private View loadingView;
    private View tipsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist_by_batches);

        //获取控件
        mlvBlacklist = (ListView) findViewById(R.id.lv_blacklist);
        llLoadProgress = (LinearLayout) findViewById(R.id.ll_loadProgress);

        //黑名单数据库DAO
        blacklistDao = new BlacklistDao(BlacklistByBatchesActivity.this);

        //显示加载的圆圈
        //llLoadProgress.setVisibility(View.VISIBLE);


        //为ListView设置滚动侦听,实现分批次加载
        mlvBlacklist.setOnScrollListener(new AbsListView.OnScrollListener() {

            //滚动状态改变时调用,即是滚动状态还是停止滚动状态
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //System.out.println("onScrollStateChanged");
                /**
                 * AbsListView.OnScrollListener.SCROLL_STATE_IDLE 闲置状态,停止了滑动
                 * AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 手指触摸滑动状态
                 * AbsListView.OnScrollListener.SCROLL_STATE_FLING  惯性滑动状态                 *
                 */
                //如果ListView的状态是空闲的,即是停止滑动状态,就执行if
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    //获取最后一个可见节点的索引
                    int lastVisiblePosition = mlvBlacklist.getLastVisiblePosition();

                    //如果最后一个可见节点是等于blackListNumbers.size() - 1就请求一次数据
                    if (lastVisiblePosition == blackListNumbers.size() - 1) {
                        //System.out.println("maxCount");
                        System.out.println(lastVisiblePosition);
                        //如果lastVisiblePosition >= countItems,就Toast
                        System.out.println(countItems);
                        if (alreadyPage -1 >= countItems / maxCount) {
                            //增加没有更多数据的提示
                            if (tipsView == null) {
                                //System.out.println("okok");
                                //填充布局
                                tipsView = View.inflate(BlacklistByBatchesActivity.this, R.layout.view_listview_tips, null);
                                //将尾部添加一个正在加载
                                mlvBlacklist.addFooterView(tipsView);
                            }
                            return;
                        }

                        //填充布局
                        loadingView = View.inflate(BlacklistByBatchesActivity.this, R.layout.view_listview_loading, null);
                        //将尾部添加一个正在加载
                        mlvBlacklist.addFooterView(loadingView);

                        //更新数据
                        initData();
                    }
                }
            }

            //滚动时调用
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //System.out.println("onScroll");
            }
        });


        //初始化数据
        initData();

    }


    /**
     * 初始化数据,防止数据过多导致主线程阻塞,因此另外开启一个线程取数据,在用消息队列刷新UI
     */
    private synchronized void initData() {

        //如果正在请求数据就不再请求
        if (isInitDataNow) {
            return;
        }

        //将正在请求数据设为true
        isInitDataNow = true;

        new Thread() {
            @Override
            public void run() {
                super.run();

                //获取总条目数
                if (countItems == -1) {
                    countItems = blacklistDao.getCount();
                }

                //如果数据已经全部取完就不在取数据
                /*
                if ((startIndex + maxCount) > countItems + 2) {
                    return;
                }
                */

                //通知刷新UI
                Message msg = handler.obtainMessage();

                //按批取数据,实现分批添加
                msg.obj = blacklistDao.selectByBatches(startIndex, maxCount);

                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 初始化View
     */
    private void initView() {

        //设置适配器
        //如果还没有创建适配器就创建并设置适配器,如果已经创建就用适配器通知数据改变
        if (myAdapter == null) {
            mlvBlacklist.setLayoutAnimation(getAnimationController());
            myAdapter = new MyAdapter(BlacklistByBatchesActivity.this, blackListNumbers);
            mlvBlacklist.setAdapter(myAdapter);
        } else {
            //适配器数据改变时通知刷新UI
            myAdapter.notifyDataSetChanged();
        }

        //每刷新一次UI就更新起始索引
        startIndex += maxCount;
        //标记已经打开一页
        alreadyPage++;

        //移除footView
        mlvBlacklist.removeFooterView(loadingView);
        mlvBlacklist.removeFooterView(tipsView);

        //将正在请求数据设为false
        isInitDataNow = false;

    }

    /**
     * ListView的适配器
     */
    class MyAdapter extends ListViewBaseAdapter {


        //创建构造将list和mContext传递给父类
        public MyAdapter(Context mContext, List list) {
            super(mContext, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;//一个holder相当于一个ListView的条目
            if (convertView == null) {
                convertView = View.inflate(BlacklistByBatchesActivity.this, R.layout.view_blacklist_item, null);
                holder = new ViewHolder();
                holder.tvBlacklistItemNumber = (TextView) convertView.findViewById(R.id.tv_blacklist_item_number);
                holder.tvBlacklistItemMode = (TextView) convertView.findViewById(R.id.tv_blacklist_item_mode);
                holder.ivBlacklistDelete = (ImageView) convertView.findViewById(R.id.iv_blacklist_delete);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();//取出已经创建好的条目
            }

            String number = blackListNumbers.get(position).getNumber();
            holder.tvBlacklistItemNumber.setText(number);
            String mode = blackListNumbers.get(position).getMode();
            String address = DbAddress.getaddress(number);
            address = address.equals("null") ? "未知号码" : address;

            if (mode.equals(BlackListNumber.MODE_PHONE)) {
                holder.tvBlacklistItemMode.setText("拦截电话(" + address + ")");
            } else if (mode.equals(BlackListNumber.MODE_SMS)) {
                holder.tvBlacklistItemMode.setText("拦截短信(" + address + ")");
            } else {
                holder.tvBlacklistItemMode.setText("拦截电话+短信(" + address + ")");
            }

            //为按钮设置tag
            holder.ivBlacklistDelete.setTag(number);

            //设置删除按钮监听
            holder.ivBlacklistDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String numberClick = (String) v.getTag();
                    boolean res = blacklistDao.deleteItem(numberClick);
                    if (res) {
                        blackListNumbers.remove(position);
                        countItems--;
                        myAdapter.notifyDataSetChanged();
                        Toast.makeText(BlacklistByBatchesActivity.this, "删除成功!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }

    }

    /**
     * ViewHolder
     */
    static class ViewHolder {
        TextView tvBlacklistItemNumber;

        TextView tvBlacklistItemMode;

        ImageView ivBlacklistDelete;
    }

    /**
     * 增加黑名单
     *
     * @param v
     */
    public void addBlacklist(View v) {
        //System.out.println("增加");
        AlertDialog.Builder builder = new AlertDialog.Builder(BlacklistByBatchesActivity.this);
        final AlertDialog alertDialog = builder.create();

        View view = View.inflate(BlacklistByBatchesActivity.this, R.layout.view_dialog_add_blacklist, null);

        final EditText eAddBlacklistNumber = (EditText) view.findViewById(R.id.e_addBlacklist_number);
        final RadioGroup rgBlacklistRG = (RadioGroup) view.findViewById(R.id.rg_blacklist_rg);
        final Button bAddBlacklist = (Button) view.findViewById(R.id.b_addBlacklist);

        //设置黑名单添加按按钮的点击侦听
        bAddBlacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取电话号码
                String phone = eAddBlacklistNumber.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(BlacklistByBatchesActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取CheckBox中的选项
                String mode = null;
                int checkedRadioButtonId = rgBlacklistRG.getCheckedRadioButtonId();
                System.out.println(checkedRadioButtonId);
                switch (checkedRadioButtonId) {
                    //这里使用id区分
                    case R.id.cb_blacklist_phone:
                        mode = BlackListNumber.MODE_PHONE;
                        break;
                    case R.id.cb_blacklist_sms:
                        mode = BlackListNumber.MODE_SMS;
                        break;
                    case R.id.cb_blacklist_all:
                        mode = BlackListNumber.MODE_ALL;
                        break;
                    default:
                        Toast.makeText(BlacklistByBatchesActivity.this, "请先选择一个拦截模式!", Toast.LENGTH_SHORT).show();
                        return;

                }
                boolean res = blacklistDao.insertItem(phone, mode);
                if (res) {
                    //刷新显示
                    //直接向集合中添加一个
                    blackListNumbers.add(0, new BlackListNumber(phone, mode));
                    myAdapter.notifyDataSetChanged();
                    Toast.makeText(BlacklistByBatchesActivity.this, "增加成功!", Toast.LENGTH_SHORT).show();
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(view);

        //显示
        alertDialog.show();
    }

    /**
     * ListView节点出现动画
     *
     * @return
     */
    protected LayoutAnimationController getAnimationController() {
        int duration = 300;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }
}
