package com.egovcomm.monitor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.model.RspMediaGroup;
import com.egovcomm.monitor.utils.FileUtils;

/**
 * 列表，Item两行数据适配器
 * 
 * @param <T>
 *            数据实体类
 * @author mengjk
 * 
 *         2015年6月15日
 */
public class GroupListAdapter extends EBBaseAdapter<RspMediaGroup> {
	private LayoutInflater inflater;
	// private int media_width;
	// private int media_height;
	private Context context;

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * */
	public GroupListAdapter(Context context) {
		super(context);
		this.context = context;
		// media_width =
		// context.getResources().getDimensionPixelSize(R.dimen.item_media_view_width);
		// media_height =
		// context.getResources().getDimensionPixelSize(R.dimen.item_media_view_height);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_group_data, null);
			holder = new ViewHolder();
			CheckBox item_checkbox = (CheckBox) convertView
					.findViewById(R.id.item_checkbox);
			item_checkbox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							MonitorMediaGroupUpload group = (MonitorMediaGroupUpload) buttonView
									.getTag();
							group.setCheck(isChecked ? 1 : 0);
						}
					});

			ImageView item_iv_media = (ImageView) convertView
					.findViewById(R.id.item_iv_media);
			ImageView item_iv_video_icon = (ImageView) convertView
					.findViewById(R.id.item_iv_video_icon);

			TextView item_name = (TextView) convertView
					.findViewById(R.id.item_name);
			TextView item_location = (TextView) convertView
					.findViewById(R.id.item_location);
			TextView item_time = (TextView) convertView
					.findViewById(R.id.item_time);
			TextView item_lat = (TextView) convertView
					.findViewById(R.id.item_lat);
			TextView item_lng = (TextView) convertView
					.findViewById(R.id.item_lng);
			ProgressBar item_progress = (ProgressBar) convertView
					.findViewById(R.id.item_progress);
			View item_view_uploadTip = convertView
					.findViewById(R.id.view_layout_tip);
			// Button item_btn_reupload = (Button)
			// convertView.findViewById(R.id.item_btn_reupload);

			TextView item_tv_tip = (TextView) convertView
					.findViewById(R.id.item_tv_tip);
			ImageView item_iv_tip = (ImageView) convertView
					.findViewById(R.id.item_iv_tip);

			holder.item_checkbox = item_checkbox;
			holder.item_iv_media = item_iv_media;
			holder.item_iv_video_icon = item_iv_video_icon;
			holder.item_name = item_name;
			holder.item_location = item_location;
			holder.item_time = item_time;
			holder.item_lat = item_lat;
			holder.item_lng = item_lng;
			holder.item_progress = item_progress;
			holder.item_view_uploadTip = item_view_uploadTip;
			// holder.item_btn_reupload = item_btn_reupload;
			holder.item_iv_tip = item_iv_tip;
			holder.item_tv_tip = item_tv_tip;

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (holder != null) {
			RspMediaGroup group = dataList.get(position);
			holder.item_checkbox.setTag(group);
			if (group.getShowCheck() == 1) {
				holder.item_checkbox.setVisibility(View.VISIBLE);
				if (group.getCheck() == 1) {
					holder.item_checkbox.setChecked(true);
				} else {
					holder.item_checkbox.setChecked(false);
				}
			} else {
				holder.item_checkbox.setVisibility(View.GONE);
				holder.item_checkbox.setChecked(false);
				group.setCheck(0);
			}
			if (TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO + "", group
					.getMediaType())) {// 图片
				holder.item_iv_video_icon.setVisibility(View.GONE);
				// holder.item_iv_media.setImageBitmap(FileUtils.getImageThumbnail(media.getPath(),
				// holder.item_iv_media.getWidth(),
				// holder.item_iv_media.getHeight()));

				holder.item_iv_media.setImageBitmap(FileUtils
						.getMediaThumbnail(context, group.getThumbnailPath(),
								group.getMediaType()));

			} else {// 视频
				holder.item_iv_video_icon.setVisibility(View.VISIBLE);
				holder.item_iv_media.setImageBitmap(FileUtils
						.getMediaThumbnail(context, group.getThumbnailPath(),
								group.getMediaType()));

			}
			holder.item_name.setText("备注："+group.getRemark());
			holder.item_location.setText("创建地点："+group.getCreateAddr());
			holder.item_time.setText("创建时间："+group.getCreateTime());
			holder.item_lat.setText("纬度："+group.getLatitude());
			holder.item_lng.setText("经度："+group.getLongitude());
			
			
			holder.item_view_uploadTip.setVisibility(View.GONE);
			holder.item_progress.setVisibility(View.GONE);
			
		}

		return convertView;
	}

	/**
	 * 列表数据Holder用于缓存
	 * 
	 */
	class ViewHolder {
		CheckBox item_checkbox;
		ImageView item_iv_media;
		ImageView item_iv_video_icon;
		TextView item_name;
		TextView item_location;
		TextView item_time;
		TextView item_lat;
		TextView item_lng;
		ProgressBar item_progress;
		View item_view_uploadTip;
		// Button item_btn_reupload;
		ImageView item_iv_tip;
		TextView item_tv_tip;
	}

}
