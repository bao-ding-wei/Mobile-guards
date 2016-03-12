package com.boyzhang.projectmobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.adapter.ListViewBaseAdapter;
import com.boyzhang.projectmobilesafe.bean.BlackListNumber;
import com.boyzhang.projectmobilesafe.db.dao.BlacklistDao;
import com.boyzhang.projectmobilesafe.db.dao.DbAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单Activity
 * 分页显示条目
 * 这里的：ListView进行了高度优化
 */
public class BlacklistByPageActivity extends Activity {

    private ListView mlvBlacklist;
    private LinearLayout llLoadProgress;
    private TextView tvBlacklistPageText;

    private BlacklistDao blacklistDao;

    private ArrayList<BlackListNumber> blackListNumbers;

    private int totalItem = -1;     //条目数

    private int currentPage = 0;    //当前显示的页面
    private int pageSize = 20;      //每页显示的条目数
    private int pageCount = -1;     //总共的页面数目

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            //将圆圈隐藏
            llLoadProgress.setVisibility(View.INVISIBLE);

            //更新UI
            initView();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist_by_page);

        //获取控件
        mlvBlacklist = (ListView) findViewById(R.id.lv_blacklist);
        llLoadProgress = (LinearLayout) findViewById(R.id.ll_loadProgress);
        tvBlacklistPageText = (TextView) findViewById(R.id.tv_blacklist_pageText);

        if (tvBlacklistPageText == null) {
            System.out.println("null****");
        }
        System.out.println(tvBlacklistPageText);

        //黑名单数据库DAO
        blacklistDao = new BlacklistDao(BlacklistByPageActivity.this);

        //显示加载的圆圈
        //llLoadProgress.setVisibility(View.VISIBLE);

        //初始化数据
        initData();

    }


    /**
     * 初始化数据,防止数据过多导致主线程阻塞,因此另外开启一个线程取数据,在用消息队列刷新UI
     */
    private void initData() {

        new Thread() {
            @Override
            public void run() {
                super.run();

                //获取黑名单个数
                if (totalItem == -1) {
                    totalItem = blacklistDao.getCount();
                    pageCount = totalItem / pageSize;
                }

                //拿到所有的黑名单列表
                blackListNumbers = blacklistDao.selectByPage(currentPage, pageSize);

                //通知刷新UI
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 初始化View
     */
    private void initView() {

        //更改页码显示
        changePageText();

        //设置适配器
        mlvBlacklist.setAdapter(new MyAdapter(BlacklistByPageActivity.this, blackListNumbers));

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
                convertView = View.inflate(BlacklistByPageActivity.this, R.layout.view_blacklist_item, null);
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

            //设置删除按钮监听
            holder.ivBlacklistDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BlacklistByPageActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
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
     * 上一页
     *
     * @param v
     */
    public void prevPage(View v) {

        if (currentPage <= 0) {
            Toast.makeText(BlacklistByPageActivity.this, "已经是首页了", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPage--;

        initData();
    }

    /**
     * 下一页
     *
     * @param v
     */
    public void nextPage(View v) {

        if (currentPage > pageCount - 1) {
            Toast.makeText(BlacklistByPageActivity.this, "已经是最后了", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPage++;

        initData();
    }

    /**
     * 改变页码显示
     */
    public void changePageText() {

        if (tvBlacklistPageText == null) {
            System.out.println("nullnull");
        }

        tvBlacklistPageText.setText((currentPage + 1) + "/" + (pageCount + 1));

    }

}
