package com.egovcomm.monitor.view.push2refreshlistview;
/**刷新view接口*/
public interface Pullable {
	/**
	 * 判断是否可以下拉，如果不需要下拉功能可以直接return false
	 * 
	 * @return true如果可以下拉否则返回false
	 */
	boolean canPullDown();

	/**
	 * 判断是否可以上拉，如果不需要上拉功能可以直接return false
	 * 
	 * @return true如果可以上拉否则返回false
	 */
	boolean canPullUp();
	
	/**
	 * 判断是否还有更多数据，没有更多数据的时候是可以上拉的，但是不能加载更多
	 * */
	boolean isNoMoreData();
}
