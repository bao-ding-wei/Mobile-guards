package com.boyzhang.projectmobilesafe.view;

import com.boyzhang.projectmobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义的组合控件, 继承自RelativeLayout,将setting_item1布局文件填充进去了
 * 
 * @author HaiFeng
 * 
 */
public class SettingItem1 extends RelativeLayout {

	// ctrl+shift+x:将选择内容转为大写
	// ctrl+shift+y:将选择内容转为小写
	private static final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
	private TextView tvSettingItemTitle;
	private TextView tvSettingItemDesc;
	private CheckBox cbSettingCheckBox;

	// 自定义属性的值
	private String title;
	private String desc_on;
	private String desc_off;

	public SettingItem1(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	public SettingItem1(Context context, AttributeSet attrs) {
		super(context, attrs);
		title = attrs.getAttributeValue(NAMESPACE, "title");
		desc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
		desc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
		// System.out.println(title+"---"+desc_on+"---"+desc_off);
		initView();
	}

	public SettingItem1(Context context) {
		super(context);
		initView();
	}

	/**
	 * 这里将setting_item1布局文件填充为View对象,并将SettingItem1作为了父ViewGroup
	 * 这样SettingItem就自带setting_item1了
	 */
	protected void initView() {

		// 参数说明
		// this:指定了R.layout.setting_item1布局文件的父节点
		View.inflate(getContext(), R.layout.view_setting_item1, this);

		tvSettingItemTitle = (TextView) findViewById(R.id.tv_settingItem1Title);
		tvSettingItemDesc = (TextView) findViewById(R.id.tv_settingItem1Desc);
		cbSettingCheckBox = (CheckBox) findViewById(R.id.cb_settingCheckBox);
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
		return cbSettingCheckBox.isChecked();
	}

	// 设置是否选中
	public void setChecked(boolean check) {
		cbSettingCheckBox.setChecked(check);
		// 根据用户的设置,予以更新状态不同文本
		if (check) {
			setDesc(desc_on);
		} else {
			setDesc(desc_off);
		}
	}

}
