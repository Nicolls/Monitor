/**
 * 
 */
package com.egovcomm.monitor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义基类View
 * 
 * @author mengjk
 * 
 *
 *         2015年5月20日
 */
public class CommonView extends View {

	private String name;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * @param name
	 *            名称
	 */
	public CommonView(Context context, String name) {
		super(context);
		this.name = name;
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CommonView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
