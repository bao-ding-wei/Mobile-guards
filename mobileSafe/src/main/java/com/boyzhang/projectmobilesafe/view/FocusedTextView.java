package com.boyzhang.projectmobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * 这是一个可以自动获取焦点的TextView,只是实验,项目中并没有使用用到这个类, 目的是为了跑马灯
 * 
 * @author HaiFeng
 * 
 */

public class FocusedTextView extends TextView {

	// 这个构造是在TextView设置了style样式后悔被系统调用
	public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	// 这个构造是在TextView设置了属性值后调用
	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	// 这个是开发者new TextView对象时用的
	public FocusedTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	// 这个方法重写强制返回了true
	// 在跑马灯中,TextView必须是获得了焦点的TextView才能够滚动起来,强制返回true跑马灯就能滚动了
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}

}
