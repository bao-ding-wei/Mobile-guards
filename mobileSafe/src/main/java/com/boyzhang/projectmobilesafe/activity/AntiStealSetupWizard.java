package com.boyzhang.projectmobilesafe.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.boyzhang.projectmobilesafe.R;

public class AntiStealSetupWizard extends FragmentActivity {

	private int currentPage = 0;

	// Fragment的个数
	private int fragmentsCount = 2;
	private ImageView bAntiStealPageprev;
	private ImageView bAntiStealPagenext;

	// 页面标题
	String[] title = { "欢迎使用手机防盗", "设置安全号码", "设置防盗密码" };
	private TextView pageTitle;

	GestureDetector detector;
	Fragment fragment = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.antisteal_setup_wizard);

		// 获取控件
		pageTitle = (TextView) findViewById(R.id.t_setup_title);
		bAntiStealPageprev = (ImageView) findViewById(R.id.b_antisteal_pageprev);
		bAntiStealPagenext = (ImageView) findViewById(R.id.b_antisteal_pagenext);

		changePageButtonStatus();
		setCurrentPageTitle();
		fragment = new AntiStealSetupWizardFragment1();
		showFragment(fragment, 2);

		// 屏幕滑动事件处理
		// GestureDetector类是Android为处理屏幕触摸事件而创建的一个类,方便开发者完成屏幕的触摸事件
		// SimpleOnGestureListener是GestureDetector的内部类是一个OnGestureListener接口的简单实现类,相当于适配器
		detector = new GestureDetector(this, new SimpleOnGestureListener() {

			// onFling方法是用户滑动屏幕是调用的
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {

				// 限制Y上的滑动距离,如果大于200就不return
				if (Math.abs(e1.getRawY() - e2.getRawY()) > 200) {
					return true;
				}

				// 速度限制,如果纵向速度小于50,就return
				if (Math.abs(velocityX) < 50) {
					return true;
				}

				// 上一页
				if (e2.getRawX() - e1.getRawX() > 150) {
					pagePrev();
				}
				// 下一页
				if (e1.getRawX() - e2.getRawX() > 150) {
					pageNext();
				}

				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	/**
	 * 创建fragment
	 */
	public void getFragment() {

		switch (currentPage) {
		case 0:
			fragment = new AntiStealSetupWizardFragment1();
			break;
		case 1:
			fragment = new AntiStealSetupWizardFragment2();
			break;
		case 2:
			fragment = new AntiStealSetupWizardFragment3();
			break;
		}
	}

	/**
	 * 上一页
	 *
	 */
	public void pagePrev() {
		// 如果是第一页就不允许向前换页
		if (currentPage == 0) {
			return;
		}
		currentPage -= 1;
		getFragment();//切换Fragment

		changePageButtonStatus();
		setCurrentPageTitle();
		showFragment(fragment, 0);
	}

	/**
	 * 下一页
	 *
	 */
	public void pageNext() {
		// 如果是最后一页就不允许在向后换页
		// checkInput()是对Fragment中输入的数据做校验的
		if (currentPage == fragmentsCount
				|| !((BaseFragment) fragment).checkInput()) {
			return;
		}
		currentPage += 1;
		getFragment();// 切换Fragment

		changePageButtonStatus();
		setCurrentPageTitle();
		showFragment(fragment, 1);
	}

	// 点击按钮翻页
	public void prev(View v) {
		pagePrev();
	}

	// 点击按钮翻页
	public void next(View v) {
		pageNext();
	}

	/**
	 * 设置当前页面的标题
	 */
	public void setCurrentPageTitle() {
		pageTitle.setText(title[currentPage]);
	}

	/**
	 * 控制翻页按钮的显示与隐藏
	 */
	public void changePageButtonStatus() {
		if (currentPage == 0) {
			bAntiStealPageprev.setVisibility(View.INVISIBLE);
			bAntiStealPagenext.setVisibility(View.VISIBLE);
		} else if (currentPage == fragmentsCount) {
			bAntiStealPageprev.setVisibility(View.VISIBLE);
			bAntiStealPagenext.setVisibility(View.INVISIBLE);
		} else {
			bAntiStealPagenext.setVisibility(View.VISIBLE);
			bAntiStealPageprev.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 用于切换Fragment
	 * 
	 * @param fragment
	 *            要切换到的Fragment对象
	 * @param direction
	 *            动画方向
	 */
	public void showFragment(Fragment fragment, int direction) {

		// 使用FragmentActivity中的getSupportFragmentManager()方法得到一个fragmentManager对象
		FragmentManager fragmentManager = getSupportFragmentManager();
		// 开启事务
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		// 将当前的Fragment对象填充到帧布局FrameLayout中
		// 添加专场动画
		if (direction == 1) {
			fragmentTransaction.setCustomAnimations(R.anim.push_left_in,
					R.anim.push_left_out);
		} else if (direction == 0) {
			fragmentTransaction.setCustomAnimations(R.anim.push_right_in,
					R.anim.push_right_out);
		}
		fragmentTransaction.replace(R.id.fg_antiStealSetupWizard, fragment);
		// 提交事务
		fragmentTransaction.commit();

	}

	/**
	 * 重写屏幕滑动事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 这里将event事件传递给GestureDetector处理
		// 如果这里不传递,onTouchEvent方法不会生效
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

}
