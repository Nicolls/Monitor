/**
 * 
 */
package com.egovcomm.monitor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.egovcomm.monitor.R;

/**
 *
 * 自定义弹出框基类，本类用于实现弹出框的基础操作，如显示，隐藏，触摸等，所有需要此风格的弹出框，都应继承此类，去实现
 * loadPopupContent来加载弹出框中的contentView
 * @author mengjk
 *
 *         2016年5月18日
 */
public abstract class CommonPopupWindow extends PopupWindow {
	private Context context;
	public CommonPopupWindow(Context context) {
		super(context);
		this.context = context;
		//<color name="hui_80_perceent">#80000000</color><!--弹出的popuWindow背景透明灰色-->
		setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.monitor_black_70_percent)));
		initView();
	}


	/**留给子类，来实现，填充内容*/
	public abstract View loadPopupContent(Context context);

	@SuppressLint("InflateParams")
	private void initView() {
		RelativeLayout layout=new RelativeLayout(context);
		layout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		layout.addView(loadPopupContent(context));
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		setContentView(layout);
		setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
	}
}
