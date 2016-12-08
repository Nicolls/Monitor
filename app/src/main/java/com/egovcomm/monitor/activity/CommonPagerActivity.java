/**
 * 
 */
package com.egovcomm.monitor.activity;

import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.view.CommonViewPager;
import com.egovcomm.monitor.view.SlidingTabLayout;
import com.egovcomm.monitor.view.TabBarView;
import com.egovcomm.monitor.view.TabBarView.OnPagerChangeListener;
import com.egovcomm.monitor.view.TabBarView.OnTabBarClickListener;

/**
 * 嵌入ViewPager可左右滑动的通用Activity
 * 
 * @author mengjk
 *
 *         2015年5月18日
 */
public class CommonPagerActivity extends BaseActivity implements OnTabBarClickListener {

	static final String TAG = CommonPagerActivity.class.getSimpleName();
	protected TabBarView mTabBar;
	protected CommonViewPager mViewPager;
	protected PagerTabStrip mPagerTab;
	protected SlidingTabLayout mSlidingTabLayout;
	private boolean isFlipPagerEnable = true;// 是否允许左右滑动
	private boolean isShowPagerAnima = true;// 是否点击的时候显示滑动动画

	protected View mTopBar;
	protected ImageView mBack;
	protected TextView mTitle;
	protected TextView mRightTv;
	protected ImageView mRightIv;

	
	@Override
	public void dateUpdate(int id, Object obj) {

	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_activity_page_view);

		mTopBar=findViewById(R.id.nav_top_bar);
		mBack = (ImageView) findViewById(R.id.top_bar_left);
		mTitle = (TextView) findViewById(R.id.top_bar_title);
		mRightTv = (TextView) findViewById(R.id.top_bar_right_text);
		mRightIv = (ImageView) findViewById(R.id.top_bar_right_icon);


		mTabBar = (TabBarView) findViewById(R.id.view_tab_bar);
		mTabBar.setOnTabBarClickListener(this);
		mViewPager = (CommonViewPager) findViewById(R.id.view_pager_view);
		mPagerTab = (PagerTabStrip) findViewById(R.id.view_pager_view_pagertab);
		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.view_tab_sliding_bar);
		setPagerScoll(isFlipPagerEnable, isShowPagerAnima);
	}
	/**翻页*/
	public void setOnPagerChangeListener(OnPagerChangeListener lis){
		mTabBar.setOnPagerChangeListener(lis);
	}

	/**
	 * @param isFlipPagerEnable
	 *            是否可以通过手指来滑动翻页
	 * @param isShowPagerAnima
	 *            当点击导航时是否显示滑动动画
	 */
	public void setPagerScoll(boolean isFlipPagerEnable, boolean isShowPagerAnima) {
		this.isShowPagerAnima = isShowPagerAnima;
		this.isFlipPagerEnable = isFlipPagerEnable;
		mViewPager.setPagerScroll(isFlipPagerEnable);

	}

	@Override
	public void onTabClick(int position) {
		mViewPager.setCurrentItem(position, isShowPagerAnima);
	}


}
