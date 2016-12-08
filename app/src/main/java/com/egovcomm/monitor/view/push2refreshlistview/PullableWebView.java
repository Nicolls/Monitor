package com.egovcomm.monitor.view.push2refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/** 可刷新webview */
public class PullableWebView extends WebView implements Pullable {
	/** 是否可以上拉加载更多 */
	public boolean canPull2LoadMore = false;

	public PullableWebView(Context context) {
		super(context);
	}

	public PullableWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown() {
		if (getScrollY() == 0)
			return true;
		else
			return false;
	}

	@Override
	public boolean canPullUp() {
		if (getScrollY() >= (getContentHeight() * getScale() - getMeasuredHeight())
				&& canPull2LoadMore)
			return true;
		else
			return false;
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
