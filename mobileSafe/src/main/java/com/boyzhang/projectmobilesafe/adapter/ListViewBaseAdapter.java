package com.boyzhang.projectmobilesafe.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * ===========================================================
 * <p/>
 * 版权 : 张海锋 版权所有(c)2016
 * <p/>
 * 作者 : 张海锋
 * <p/>
 * 版本 : 1.0
 * <p/>
 * 创建时间 : 2016-2-28 下午4:36:13
 * <p/>
 * 描述 : 对BaseAdapter的优化,将BaseAdapter的getCount(),getItem(),getItemId()提取到了父类中
 * 继承自ListViewBaseAdapter就能只关心getView()方法了
 * <p/>
 * <p/>
 * 修订历史 :
 * <p/>
 * <p/>
 * ===========================================================
 **/
abstract public class ListViewBaseAdapter<T> extends BaseAdapter {

    public List<T> lists;
    public Context mContext;

    //增加构造方法是为了让子类将list和mContext传递过来供另外三个方法使用
    public ListViewBaseAdapter(Context mContext, List<T> list) {
        this.lists = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
