/**
 * 
 */
package com.egovcomm.monitor.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义基类ViewPager
 * 
 * @author mengjk
 *
 *         2015年5月18日
 */
public class CommonViewPager extends ViewPager {

	private boolean isPagerScroll = true;

	/**
	 * @param context
	 *            上下文
	 * @param attrs
	 *            属性
	 */
	public CommonViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public boolean isPagerScroll() {
		return isPagerScroll;
	}

	public void setPagerScroll(boolean isPagerScroll) {
		this.isPagerScroll = isPagerScroll;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (isPagerScroll) {
			return super.onTouchEvent(arg0);
		} else {
			return false;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		if (isPagerScroll) {
			return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}
	}

}
