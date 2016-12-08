/**
 * 
 */
package com.egovcomm.monitor.view;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.egovcomm.monitor.R;

/**
 * 自定义切换菜单
 * 
 * @author mengjk
 *
 *         2015年5月18日
 */
public class TabBarView extends LinearLayout {

	private List<String> tabs;
	private Context context;
	private OnTabBarClickListener onTabBarClickListener;
	private ViewPager viewPager;
	private View lineView;
	private LinearLayout buttonContent;

	/**
	 * @param context
	 * @param attrs
	 */
	@SuppressLint("NewApi")
	public TabBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		setOrientation(LinearLayout.VERTICAL);

		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TabBar, 0, 0);
		try {
			// tabCount=typeArray.getInteger(R.styleable.TabBar_tab_count, 0);
		} finally {
			typeArray.recycle();
		}
		initView();
	}

	public void setOnTabBarClickListener(OnTabBarClickListener onTabBarClickListener) {
		this.onTabBarClickListener = onTabBarClickListener;
	}

	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		this.viewPager.setOnPageChangeListener(new OnCommonPagerSlider());
	}

	public void setTabCount(List<String> tabs) {
		this.tabs = tabs;
		invalidate();
		initView();
	}

	/**
	 * 自定义viewpager滑动监听器
	 * */
	public class OnCommonPagerSlider implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			if(pagerChangeLis!=null){
				pagerChangeLis.pageChange(arg0);
			}
			if (lineView != null) {
				lineView.setX(arg0 * lineView.getWidth());
			}
			if (buttonContent != null) {
				for (int i = 0; i < buttonContent.getChildCount(); i++) {
					buttonContent.getChildAt(i).setSelected(false);
				}
				buttonContent.getChildAt(arg0).setSelected(true);
			}
		}

	}

	private void initView() {
		this.removeAllViews();
		if (tabs != null) {
			LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_view_pager_tab,
					null);

			buttonContent = (LinearLayout) layout.findViewById(R.id.view_pager_tab_content);

			LinearLayout lineContent = (LinearLayout) layout.findViewById(R.id.view_pager_tab_line_ll);
			lineContent.setWeightSum(this.tabs.size());
			lineView = lineContent.findViewById(R.id.view_pager_tab_line_view);
			lineView.setLayoutParams(new LayoutParams(0, 8, 1));
			lineView.setPadding(3, 3, 3, 3);
			for (int i = 0; i < tabs.size(); i++) {
				Button button = (Button) LayoutInflater.from(context).inflate(R.layout.view_view_pager_tab_btn, null);
				button.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
				button.setText(tabs.get(i));
				button.setTag(i);
				// button.setTextSize(16);
				// button.setTextColor(getResources().getColor(R.drawable.text_selector_main_tab_btn));
				// button.setBackgroundColor(getResources().getColor(R.color.transparent));
				button.setOnClickListener(new OnTabButtonClickListener());
				if (i == 0) {
					button.setSelected(true);
				}
				buttonContent.addView(button);
			}

			this.addView(layout);
		}
	}

	/**
	 * 导航条按钮监听器
	 * */
	class OnTabButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			if (onTabBarClickListener != null) {
				onTabBarClickListener.onTabClick(position);
			}
		}

	}

	/** 导航按钮监听器 */
	public interface OnTabBarClickListener {
		/**
		 * 点击后触发此方法，
		 * 
		 * @param position
		 *            响应的按钮位置
		 * */
		void onTabClick(int position);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (lineView != null && viewPager != null) {
			lineView.setX(lineView.getWidth() * viewPager.getCurrentItem());
		}
	}

	public interface OnPagerChangeListener{
		void pageChange(int index);
	}
	
	private OnPagerChangeListener pagerChangeLis;

	public void setOnPagerChangeListener(OnPagerChangeListener lis) {
		this.pagerChangeLis=lis;
	}
}
