package com.boyzhang.projectmobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.adapter.ListViewBaseAdapter;
import com.boyzhang.projectmobilesafe.bean.AppInfos;
import com.boyzhang.projectmobilesafe.db.dao.AppLockDao;
import com.boyzhang.projectmobilesafe.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment中 :
 * -------------onCreateView()方法用来初始化View
 * -------------onActivityCreated()方法用来初始化数据
 * -------------onStart()方法也常用来初始化数据
 */

public class AppLockFragment1 extends Fragment {

    private TextView tv_unLock;
    private ListView lv_appLock;
    private List<AppInfos> applicationInfos;
    private UnLockAdapter unLockAdapter;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            initUI();//更新UI
        }
    };

    /**
     * 初始化UI
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_app_lock_fragment1, null);

        //获取控件
        tv_unLock = (TextView) v.findViewById(R.id.tv_unLock);
        lv_appLock = (ListView) v.findViewById(R.id.lv_appLock);

        return v;
    }

    /**
     * 初始化数据
     */
    @Override
    public void onStart() {
        super.onStart();

        new Thread() {

            @Override
            public void run() {
                super.run();

                applicationInfos = ApplicationUtils.getApplicationInfos(getActivity());

                AppLockDao appLockDao = new AppLockDao(getActivity());
                List<String> allLockedAppPackageName = appLockDao.getAllLockedAppPackageName();

                List<AppInfos> removeApp = new ArrayList<>();
                //过滤掉已经加锁的App
                for (String lockedAppPackageName : allLockedAppPackageName) {

                    for (AppInfos applicationInfo : applicationInfos) {
                        if (applicationInfo.getAppPackageName().equals(lockedAppPackageName)) {
                            removeApp.add(applicationInfo);
                        }
                    }
                }
                applicationInfos.removeAll(removeApp);//移除

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 更新UI
     */
    public void initUI() {
        lv_appLock.setLayoutAnimation(getAnimationController());//为ListView设置一个进入动画
        unLockAdapter = new UnLockAdapter(getActivity(), applicationInfos);
        lv_appLock.setAdapter(unLockAdapter);
    }

    /**
     * Adapter
     */
    private class UnLockAdapter extends ListViewBaseAdapter {

        public UnLockAdapter(Context mContext, List<AppInfos> list) {
            super(mContext, list);
        }

        /**
         * 获取ViewHolder
         *
         * @param convertView
         * @return
         */
        public ViewHolder getViewHolder(View convertView) {
            ViewHolder holder = new ViewHolder();

            holder.iv_appLock_unLock_icon = (ImageView) convertView.findViewById(R.id.iv_appLock_unLock_icon);
            holder.iv_appLock_unLock_appName = (TextView) convertView.findViewById(R.id.iv_appLock_unLock_appName);
            holder.iv_appLock_unLock_lockImg = (ImageView) convertView.findViewById(R.id.iv_appLock_unLock_lockImg);

            holder.needInflate = false;
            convertView.setTag(holder);

            return holder;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            if (convertView == null) {//如果convertView是null就填充布局

                convertView = View.inflate(getActivity(), R.layout.view_list_applock_unlock_item, null);
                holder = getViewHolder(convertView);

            } else if (((ViewHolder) convertView.getTag()).needInflate) {//如果需要重新填充布局就填充___!!!##$$$$
                /**
                 * 重新填充布局很重要,否则可能会有异常,因为convertView被移除了
                 */

                convertView = View.inflate(getActivity(), R.layout.view_list_applock_unlock_item, null);
                holder = getViewHolder(convertView);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iv_appLock_unLock_icon.setImageDrawable(applicationInfos.get(position).getAppLogo());
            holder.iv_appLock_unLock_appName.setText(applicationInfos.get(position).getAppName());

            final View finalConvertView = convertView;

            holder.iv_appLock_unLock_lockImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //deleteCell(finalConvertView, position);//删除节点

                    //保存到数据库
                    AppLockDao appLockDao = new AppLockDao(getActivity());
                    appLockDao.addAppToLock(applicationInfos.get(position).getAppPackageName());
                    deleteItem(finalConvertView, position);//删除节点

                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            tv_unLock.setText("未加锁(" + applicationInfos.size() + ")");//更新TextView
            return super.getCount();
        }
    }

    /**
     * ViewHolder
     */
    public class ViewHolder {

        public ImageView iv_appLock_unLock_icon;
        public TextView iv_appLock_unLock_appName;
        public ImageView iv_appLock_unLock_lockImg;

        public boolean needInflate = false;

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

    //---------------------------------------ListView节点删除----------------------------------------
    //-----------------------------------------带向右滑动移除动画--------------------------------------
    public void deleteItem(final View view, final int position) {

        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                applicationInfos.remove(position);

                ViewHolder vh = (ViewHolder) view.getTag();
                vh.needInflate = true;

                unLockAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF,
                1.0F, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 0F);
        ta.setDuration(500);
        ta.setAnimationListener(animationListener);
        view.startAnimation(ta);
    }


    //---------------------------------------ListView节点删除---------------------------------------
    //----------------------------------------带高度缓慢缩小动画--------------------------------------
    private void deleteCell(final View v, final int index) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                applicationInfos.remove(index);

                ViewHolder vh = (ViewHolder) v.getTag();
                vh.needInflate = true;

                unLockAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        };

        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al != null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(500);
        v.startAnimation(anim);
    }
    //---------------------------------------------------------------------------------------------
}
