package com.egovcomm.monitor.view.push2refreshlistview;

import android.os.Handler;
import android.os.Message;

import com.egovcomm.monitor.view.push2refreshlistview.PullToRefreshLayout.OnRefreshListener;
/**刷新加载监听器*/
public class PullToRefreshListener implements OnRefreshListener {

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 千万别忘了告诉控件刷新完毕了哦！
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 5000);
	}

	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// 千万别忘了告诉控件加载完毕了哦！
				pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
			}
		}.sendEmptyMessageDelayed(0, 5000);
	}

	/* (non-Javadoc)
	 * @see com.dawning.gridview.view.push2refreshlistview.PullToRefreshLayout.OnRefreshListener#onSlide(float)
	 */
	@Override
	public void onSlide(float distance) {
		// TODO Auto-generated method stub
		
	}

}
