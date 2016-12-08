package com.egovcomm.monitor.view.push2refreshlistview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ScrollView;

/** 可刷新scollview */
public class PullableScrollView extends ScrollView implements Pullable {

	/** 是否可以上拉加载更多 */
	public boolean canPull2LoadMore = false;
	/**是否还有更多数据*/
	public boolean isNoMoreData=false;
	public int speed=1;
	private int webHeight;

	public PullableScrollView(Context context) {
		super(context);
	}

	public PullableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableScrollView(Context context, AttributeSet attrs, int defStyle) {
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
		if (getScrollY() >= (getChildAt(0).getHeight() - getMeasuredHeight()) && canPull2LoadMore)
			return true;
		else
			return false;
	}

	public void setWebHeight(int webHeight) {
		this.webHeight = webHeight;
	}


	public void smoothScrollShowWeb(){
		new Thread(){
			int i=getScrollY();
			@Override
			public void run() {
				super.run();
				while(i>=0){
					i-=10;
					handler.sendEmptyMessage(i);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}.start();
	}
	
	public void smoothScrollHideWeb(){
		new Thread(){
			int i=getScrollY();
			@Override
			public void run() {
				super.run();
				while(i<=webHeight){
					i+=10;
					handler.sendEmptyMessage(i);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}.start();
	}
	
	private Handler handler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			smoothScrollBy(getScrollX(), getScrollY());
			smoothScrollTo(getScrollX(), msg.what);
		}
		
	};

	/* (non-Javadoc)
	 * @see com.dawning.gridview.view.push2refreshlistview.Pullable#isNoMoreData()
	 */
	@Override
	public boolean isNoMoreData() {
		return isNoMoreData;
	}
	
	

}
