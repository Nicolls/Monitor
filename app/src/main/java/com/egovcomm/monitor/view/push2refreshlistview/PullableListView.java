package com.egovcomm.monitor.view.push2refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/** 实现操作控制限制的listview */
public class PullableListView extends ListView implements Pullable {

	/** 是否可以上拉加载更多 */
	public boolean canPull2LoadMore = true;
	/**是否还有更多数据*/
	public boolean isNoMoreData=false;
	public PullableListView(Context context) {
		super(context);
	}

	public PullableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown() {
		if (getCount() == 0) {
			// 没有item的时候也可以下拉刷新
			return true;
		} else if (getChildAt(0)!=null&&getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0) {
			// 滑到ListView的顶部了
			return true;
		} else
			return false;
	}

	@Override
	public boolean canPullUp() {
		if (getCount() == 0 && canPull2LoadMore) {
			// 没有item的时候不可以上拉加载
			return false;
		} else if (getLastVisiblePosition() == (getCount() - 1) && canPull2LoadMore) {
			// 滑到底部了
			if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
					&& getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.dawning.gridview.view.push2refreshlistview.Pullable#isNoMoreData()
	 */
	@Override
	public boolean isNoMoreData() {
		return isNoMoreData;
	}
}
