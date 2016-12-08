package com.egovcomm.monitor.view.push2refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**刷新加载PullableTextView*/
public class PullableTextView extends TextView implements Pullable {

	public PullableTextView(Context context) {
		super(context);
	}

	public PullableTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown() {
		return true;
	}

	@Override
	public boolean canPullUp() {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.dawning.gridview.view.push2refreshlistview.Pullable#isNoMoreData()
	 */
	@Override
	public boolean isNoMoreData() {
		// TODO Auto-generated method stub
		return false;
	}

}
