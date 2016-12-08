package com.egovcomm.monitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.model.ItemEntity;

/**
 * 弹出框适配器
 * @author mengjk
 *
 *         2016年5月15日
 */
public  class PopuAdapter extends EBBaseAdapter<ItemEntity> {
	private LayoutInflater inflater;

	/**
	 * 构造函数
	 *
	 * @param context
	 *            上下文
	 * */
	public PopuAdapter(Context context) {
		super(context);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Holder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_list_popu, null);
			holder = new Holder();
			/**图标 */
			holder.icon=(ImageView) convertView.findViewById(R.id.item_iv_icon);
			/** 标题 */
			holder.title = (TextView) convertView.findViewById(R.id.item_tv_title);
			convertView.setTag(holder);

		} else {
			holder = (Holder) convertView.getTag();
		}
		if (holder != null) {
			ItemEntity item=dataList.get(position);
//			holder.icon = icon;
			holder.title.setText(item.getTitle());
		}

		return convertView;
	}



	/**
	 * 列表数据Holder用于缓存
	 *
	 */
	class Holder {
		/**标题*/
		TextView title;
		/**图标*/
		ImageView icon;
	}

}
