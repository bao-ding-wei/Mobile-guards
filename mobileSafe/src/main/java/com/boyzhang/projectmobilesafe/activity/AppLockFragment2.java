package com.boyzhang.projectmobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;
import com.boyzhang.projectmobilesafe.adapter.ListViewBaseAdapter;
import com.boyzhang.projectmobilesafe.bean.AppInfos;
import com.boyzhang.projectmobilesafe.db.dao.AppLockDao;
import com.boyzhang.projectmobilesafe.utils.ApplicationUtils;

import java.util.List;


public class AppLockFragment2 extends Fragment {

    private TextView tv_Locked;
    private ListView lv_appLock;

    private LockedAdapter lockedAdapter;
    private List<String> allLockedAppPackageName;
    private List<AppInfos> lockedAppInfo;

    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_app_lock_fragment2, null);


        //获取控件
        tv_Locked = (TextView) v.findViewById(R.id.tv_Locked);
        lv_appLock = (ListView) v.findViewById(R.id.lv_appLock);

        return v;
    }

    /**
     * 初始化数据
     */
    @Override
    public void onStart() {
        super.onStart();

        AppLockDao appLockDao = new AppLockDao(getActivity());
        allLockedAppPackageName = appLockDao.getAllLockedAppPackageName();

        //根据包名获取应用信息
        lockedAppInfo = ApplicationUtils.getAppInfoByPackageName(getActivity(), allLockedAppPackageName);

        lv_appLock.setLayoutAnimation(getAnimationController());
        lockedAdapter = new LockedAdapter(getActivity(), lockedAppInfo);
        lv_appLock.setAdapter(lockedAdapter);
    }

    /**
     * Adapter
     */
    class LockedAdapter extends ListViewBaseAdapter<AppInfos> {

        public LockedAdapter(Context mContext, List list) {
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

            holder.iv_appLock_Locked_icon = (ImageView) convertView.findViewById(R.id.iv_appLock_Locked_icon);
            holder.iv_appLock_Locked_appName = (TextView) convertView.findViewById(R.id.iv_appLock_Locked_appName);
            holder.iv_appLock_Locked_lockImg = (ImageView) convertView.findViewById(R.id.iv_appLock_Locked_lockImg);

            holder.needInflate = false;
            convertView.setTag(holder);

            return holder;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {//如果convertView是null就填充布局

                convertView = View.inflate(getActivity(), R.layout.view_list_applock_locked_item, null);
                holder = getViewHolder(convertView);

            } else if (((ViewHolder) convertView.getTag()).needInflate) {//如果需要重新填充布局就填充___!!!##$$$$
                /**
                 * 重新填充布局很重要,否则可能会有异常,因为convertView被移除了
                 */

                convertView = View.inflate(getActivity(), R.layout.view_list_applock_locked_item, null);
                holder = getViewHolder(convertView);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iv_appLock_Locked_icon.setImageDrawable(lockedAppInfo.get(position).getAppLogo());
            holder.iv_appLock_Locked_appName.setText(lockedAppInfo.get(position).getAppName());

            final View finalConvertView = convertView;

            holder.iv_appLock_Locked_lockImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //deleteCell(finalConvertView, position);//删除节点

                    //从数据库中删除
                    AppLockDao appLockDao = new AppLockDao(getActivity());
                    boolean res = appLockDao.deleteFromLocked(lockedAppInfo.get(position).getAppPackageName());
                    System.out.println(res);

                    deleteItem(finalConvertView, position);//删除节点

                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            tv_Locked.setText("已加锁(" + lockedAppInfo.size() + ")");//更新TextView
            return super.getCount();
        }

    }

    /**
     * ViewHolder
     */
    public class ViewHolder {

        public ImageView iv_appLock_Locked_icon;
        public TextView iv_appLock_Locked_appName;
        public ImageView iv_appLock_Locked_lockImg;

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

    /**
     * 删除一个ListView节点
     *
     * @param view
     * @param position
     */
    public void deleteItem(final View view, final int position) {

        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                lockedAppInfo.remove(position);

                ViewHolder vh = (ViewHolder) view.getTag();
                vh.needInflate = true;

                lockedAdapter.notifyDataSetChanged();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF,
                -1.0F, Animation.RELATIVE_TO_SELF, 0F, Animation.RELATIVE_TO_SELF, 0F);
        ta.setDuration(500);
        ta.setAnimationListener(animationListener);
        view.startAnimation(ta);
    }
}
