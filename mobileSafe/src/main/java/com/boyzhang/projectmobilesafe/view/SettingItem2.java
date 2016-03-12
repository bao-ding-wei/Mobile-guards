package com.boyzhang.projectmobilesafe.view;

import com.boyzhang.projectmobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义的组合控件, 继承自RelativeLayout,将setting_item2布局文件填充进去了
 * 
 * @author HaiFeng
 * 
 */
public class SettingItem2 extends RelativeLayout {

	private boolean descStatus = false;

	// ctrl+shift+x:将选择内容转为大写
	// ctrl+shift+y:将选择内容转为小写
	private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
	private TextView tvSettingItemTitle;
	private TextView tvSettingItemDesc;

	// 自定义属性的值
	private String title;
	private String desc_on;
	private String desc_off;
	private String showMore;

	public SettingItem2(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingItem2(Context context, AttributeSet attrs) {
		super(context, attrs);
		title = attrs.getAttributeValue(NAMESPACE, "title");
		desc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
		desc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
		showMore = attrs.getAttributeValue(NAMESPACE, "showMore");
		// System.out.println(title+"---"+desc_on+"---"+desc_off);
		initView();
	}

	public SettingItem2(Context context) {
		super(context);
		initView();
	}

	/**
	 * 这里将setting_item布局文件填充为View对象,并将SettingItem2作为了父ViewGroup
	 * 这样SettingItem就自带setting_item2了
	 */
	protected void initView() {

		// 参数说明
		// this:指定了R.layout.setting_item2布局文件的父节点
		View.inflate(getContext(), R.layout.view_setting_item2, this);

		tvSettingItemTitle = (TextView) findViewById(R.id.tv_settingItem2Title);
		tvSettingItemDesc = (TextView) findViewById(R.id.tv_settingItem2Desc);
		ImageView ivMore = (ImageView) findViewById(R.id.iv_more);

		// 判断是否要显示More样式
		if (showMore.equals("false")) {
			ivMore.setVisibility(View.GONE);
		}

		// 设置成属性中的Title
		setTitle(title);
	}

	// 对外开放设置tvSettingItemTitle的接口
	public void setTitle(String title) {
		tvSettingItemTitle.setText(title);
	}

	// 对外开放设置tvSettingItemStatus的接口
	public void setDesc(String desc) {
		tvSettingItemDesc.setText(desc);
	}

	// 是否选中
	public boolean getChecked() {
		return descStatus;
	}

	// 设置是否选中
	public void setChecked(boolean check) {
		descStatus = check;
		// 根据用户的设置,予以更新状态不同文本
		if (check) {
			setDesc(desc_on);
		} else {
			setDesc(desc_off);
		}
	}

}
