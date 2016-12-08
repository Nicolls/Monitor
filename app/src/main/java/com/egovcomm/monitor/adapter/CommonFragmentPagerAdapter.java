/**
 * 
 */
package com.egovcomm.monitor.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.egovcomm.monitor.common.BaseFragment;

/**
 * 
 * Fragment左右滑动切换适配器
 * 
 * @param <T>
 *            继承于GVFragment的实体做为数据源模型
 * @author mengjk
 *
 *         2015年5月18日
 */
public class CommonFragmentPagerAdapter<T extends BaseFragment> extends FragmentPagerAdapter {

	private List<T> dataList;

	/**
	 * 构造函数
	 * 
	 * @param fm
	 *            传入FragmentManager
	 */
	public CommonFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	/**
	 * 设置数据源
	 * 
	 * @param dataList
	 *            继承于GVFragment的集合源数据
	 * */
	public void setData(List<T> dataList) {
		this.dataList = dataList;
	}

	@Override
	public Fragment getItem(int arg0) {
		return dataList == null ? null : dataList.get(arg0);
	}

	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return dataList == null ? "" : dataList.get(position).getName();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		//super.destroyItem(container, position, object);
	}
	
	

}
