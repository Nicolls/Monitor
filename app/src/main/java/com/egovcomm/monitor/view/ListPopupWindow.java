/**
 * 
 */
package com.egovcomm.monitor.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.adapter.PopuAdapter;
import com.egovcomm.monitor.model.ItemEntity;

/**
 * 列表弹出框
 * @author mengjk
 *
 *         2016年5月18日
 */
public class ListPopupWindow extends CommonPopupWindow {
	private List<ItemEntity> dataList=new ArrayList<ItemEntity>();
	private ListView mListView;
	private PopuAdapter mAdapter;
	private OnPopupListClickLstener popupListClickLstener;

	public ListPopupWindow(Context context) {
		super(context);
	}

	@Override
	public View loadPopupContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.layout_popup_list, null);
		mListView = (ListView) view.findViewById(R.id.popup_listview);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dismiss();
				if (popupListClickLstener != null) {
					for (ItemEntity entity : dataList) {
						entity.setSelected(false);
					}
					dataList.get(position).setSelected(true);
					popupListClickLstener.onPopupListClick(dataList.get(position));
				}

			}
		});
		if (mAdapter == null) {
			mAdapter = new PopuAdapter(context);
		}
		mAdapter.setData(dataList);
		mListView.setAdapter(mAdapter);
		return view;
	}
	public void setListClickListener(OnPopupListClickLstener lis) {
		this.popupListClickLstener = lis;
	}
	public void fresh(List<ItemEntity> list){
		dataList.clear();
		dataList.addAll(list);
		mAdapter.setData(dataList);
		mAdapter.notifyDataSetChanged();
		update();
	}
	/** Popup的ListView点击监听 */
	public interface OnPopupListClickLstener {
		void onPopupListClick(ItemEntity entity);
	}
}
