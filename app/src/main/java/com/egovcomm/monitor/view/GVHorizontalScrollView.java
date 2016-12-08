/**
 * 
 */
package com.egovcomm.monitor.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * @author mengjk
 *
 * 2015年9月2日
 */
public class GVHorizontalScrollView extends HorizontalScrollView{

	private GVHorizontalScrollView mScrollView;
	/**
	 * @param context
	 */
	public GVHorizontalScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public GVHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	/**设置跟随ScrollView*/
	public void setFollowScrollView(GVHorizontalScrollView scrollView){
		this.mScrollView=scrollView;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		if(mScrollView!=null){
			mScrollView.scrollBy(oldl, oldl);
			mScrollView.scrollTo(l, t);
		}
	}

}
