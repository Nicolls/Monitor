package com.egovcomm.monitor.view.push2refreshlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
/**刷新加载PullableImageView*/
public class PullableImageView extends ImageView implements Pullable {

	public PullableImageView(Context context) {
		super(context);
	}

	public PullableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PullableImageView(Context context, AttributeSet attrs, int defStyle) {
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
