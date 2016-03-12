package com.boyzhang.projectmobilesafe.activity;

import android.support.v4.app.Fragment;

/**
 * 让Fragment继承的基类
 * 
 * @author HaiFeng
 * 
 */
public abstract class BaseFragment extends Fragment {

	// 如果Fragment中有数据输入,在切换Fragment时会调用此方法
	public abstract boolean checkInput();

}
