package com.egovcomm.monitor.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.view.RecycleImageView;

/**
 * 列表，Item两行数据适配器
 * 
 * @param <T>
 *            数据实体类
 * @author mengjk
 *
 *         2015年6月15日
 */
public  class MediaListAdapter extends EBBaseAdapter<MonitorMedia> {
	private LayoutInflater inflater;
	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * */
	public MediaListAdapter(Context context) {
		super(context);
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_media_data, null);
			holder = new ViewHolder();
			CheckBox item_checkbox = (CheckBox) convertView.findViewById(R.id.item_checkbox);
			item_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					MonitorMedia media=(MonitorMedia) buttonView.getTag();
					media.setCheck(isChecked?1:0);
				}
			});
			RecycleImageView item_iv_media = (RecycleImageView) convertView.findViewById(R.id.item_iv_media);
			ImageView item_iv_video_icon = (ImageView) convertView.findViewById(R.id.item_iv_video_icon);
			ProgressBar item_progress = (ProgressBar) convertView
					.findViewById(R.id.item_progress);
			TextView item_name = (TextView) convertView.findViewById(R.id.item_name);
			TextView item_location = (TextView) convertView.findViewById(R.id.item_location);
			TextView item_size = (TextView) convertView.findViewById(R.id.item_size);
			TextView item_time = (TextView) convertView.findViewById(R.id.item_time);
			View item_view_Tip = convertView
					.findViewById(R.id.view_layout_tip);
			// Button item_btn_reupload = (Button)
			// convertView.findViewById(R.id.item_btn_reupload);

			TextView item_tv_tip = (TextView) convertView
					.findViewById(R.id.item_tv_tip);
			ImageView item_iv_tip = (ImageView) convertView
					.findViewById(R.id.item_iv_tip);
			
			holder.item_progress = item_progress;
			holder.item_checkbox = item_checkbox;
			holder.item_iv_media = item_iv_media;
			holder.item_iv_video_icon = item_iv_video_icon;
			holder.item_name = item_name;
			holder.item_location = item_location;
			holder.item_size = item_size;
			holder.item_time = item_time;
			holder.item_view_Tip = item_view_Tip;
			// holder.item_btn_reupload = item_btn_reupload;
			holder.item_iv_tip = item_iv_tip;
			holder.item_tv_tip = item_tv_tip;
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (holder != null) {
			MonitorMedia media=dataList.get(position);
			holder.item_checkbox.setTag(media);
			if(media.getShowCheck()==1){
				holder.item_checkbox.setVisibility(View.VISIBLE);
				if(media.getCheck()==1){
					holder.item_checkbox.setChecked(true);
				}else{
					holder.item_checkbox.setChecked(false);
				}
			}else{
				holder.item_checkbox.setVisibility(View.GONE);
				holder.item_checkbox.setChecked(false);
				media.setCheck(0);
			}
			if(TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO+"", media.getMediaType())){//图片
				holder.item_iv_video_icon.setVisibility(View.GONE);
				//holder.item_iv_media.setImageBitmap(FileUtils.getImageThumbnail(media.getPath(), holder.item_iv_media.getWidth(), holder.item_iv_media.getHeight()));
//				holder.item_iv_media.setImageBitmap(FileUtils.getImageThumbnail(media.getPath(), media_width, media_height));
				holder.item_iv_media.setImageBitmap(FileUtils.getMediaThumbnail(context, media.getThumbnailPath(), media.getMediaType()));
			}else{//视频
				holder.item_iv_video_icon.setVisibility(View.VISIBLE);
//				holder.item_iv_media.setImageBitmap(FileUtils.getVideoThumbnail(media.getPath(),media_width,media_height,MediaStore.Images.Thumbnails.MICRO_KIND));
//				holder.item_iv_media.setImageBitmap(BitmapFactory.decodeFile(media.getThumbnailPath()));
				holder.item_iv_media.setImageBitmap(FileUtils.getMediaThumbnail(context, media.getThumbnailPath(), media.getMediaType()));
			}
			
			if (TextUtils.equals(MonitorMediaGroupUpload.UPLOAD_STATE_SERVER_DATA
					+ "", media.getUploadState())&&media.getProgress()>0) {// 正在上传
				holder.item_progress.setVisibility(View.VISIBLE);
				holder.item_progress.setProgress(media.getProgress());
			} else {
				holder.item_progress.setVisibility(View.GONE);
				holder.item_progress.setProgress(0);
			}
			
			if(media.getDownloadState()==MonitorMedia.DOWNLOAD_STATE_YES){//正在下载
				holder.item_view_Tip.setVisibility(View.VISIBLE);
			}else{
				holder.item_view_Tip.setVisibility(View.GONE);
			}
			holder.item_name.setText("文件名："+media.getFileName());
			holder.item_location.setText("拍摄地点："+media.getShootingLocation());
			holder.item_size.setText("文件大小："+FileUtils.getFileSize(Long.parseLong(media.getFileSize())));;
			if (TextUtils.equals(MonitorMediaGroupUpload.UPLOAD_STATE_UN_UPLOAD
					+ "", media.getUploadState())||TextUtils.equals(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING
							+ "", media.getUploadState())) {// 未上传跟上传中显示创建时间
				holder.item_time.setVisibility(View.VISIBLE);
				holder.item_time.setText("创建时间："+media.getCreateTime());
			}else{//显示上传时间
				holder.item_time.setVisibility(View.VISIBLE);
				holder.item_time.setText("上传时间："+media.getUploadTime());
			}
		}

		return convertView;
	}


	/**
	 * 列表数据Holder用于缓存
	 *
	 */
	class ViewHolder {
		CheckBox item_checkbox;
		RecycleImageView item_iv_media;
		ProgressBar item_progress;
		ImageView item_iv_video_icon;
		TextView item_name;
		TextView item_location;
		TextView item_size;
		TextView item_time;
		View item_view_Tip;
		// Button item_btn_reupload;
		ImageView item_iv_tip;
		TextView item_tv_tip;
	}

}
