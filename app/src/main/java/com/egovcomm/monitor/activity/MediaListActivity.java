/**
 * 
 */
package com.egovcomm.monitor.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.adapter.EBBaseAdapter;
import com.egovcomm.monitor.adapter.MediaListAdapter;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.ftp.FTPMediaUtil;
import com.egovcomm.monitor.ftp.FTPService;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.model.RspDownLoadMedia;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.ToastUtils;

/**
 * 
 * 
 * @author mengjk
 *
 *         2015年7月3日
 */
public class MediaListActivity extends BaseListActivity<MonitorMedia> implements AdapterView.OnItemLongClickListener{

	private MonitorMediaGroupUpload uploadGroup;
	private List<MonitorMedia> mediaList;

	// 初始化view
	@Override
	public void initView() {
		LogUtils.i(tag, "回到子类初始化");
		mListViewPulltorefreshLayout.setPull2RefreshEnable(false);
		mListView.setOnItemLongClickListener(this);
		mSearchBar.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		uploadGroup = getIntent().getParcelableExtra("uploadGroup");
		if(uploadGroup!=null){
			LogUtils.i(tag, uploadGroup.toString());
		}
		mediaList = getIntent().getParcelableArrayListExtra("mediaList");
		mTitle.setText("" + uploadGroup.getMediaGroup().getRemark());
		if (TextUtils.equals(uploadGroup.getUploadState(),
				MonitorMediaGroupUpload.UPLOAD_STATE_SERVER_DATA)) {
			mRightTv.setText("下载");
		} else if (TextUtils.equals(uploadGroup.getUploadState(),
				MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING)) {
			mRightTv.setText("取消上传");
			mProgressBar.setVisibility(View.VISIBLE);
			setBroadCastListener();
		} else {
			mRightTv.setVisibility(View.GONE);
		}
		mRightTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		mRightIv.setVisibility(View.GONE);
		boolean isHasDownload=false;
		for (MonitorMedia media : mediaList) {
			if (isNeedDownLoad(media)) {
				isHasDownload=true;
				//去判断是否下载
//					mEBikeRequestService.downLoadMedia(getApplicationContext(),
//							media);
//					item.setDownloadState(MonitorMedia.DOWNLOAD_STATE_YES);
			}
		}
		if(!isHasDownload){//如果没有要下载的，则把下载隐藏掉
			mRightTv.setVisibility(View.GONE);
		}
		super.loadListView(mediaList);
	}

	@Override
	protected void onResume() {
		super.onResume();
		freshData();
	}

	private void freshData(){
		if(mediaList!=null&&uploadGroup!=null&&(TextUtils.equals(uploadGroup.getUploadState(),MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_FAIL)||
				TextUtils.equals(uploadGroup.getUploadState(),MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_CANCEL)
		)){
			for(MonitorMedia media:mediaList){
				MonitorMedia m=DBHelper.getInstance(this).findMonitorMediaById(media.getId());
				media.setTime(m.getTime());
				media.setRemark(m.getRemark());
				media.setShootingLocation(m.getShootingLocation());
				media.setReason(m.getReason());
			}
			mAdapter.notifyDataSetChanged();
		}

	}

	/** 是否需要下载，如果需要则直接下载 */
	private boolean isNeedDownLoad(MonitorMedia media) {
		boolean isNeed = false;
		// 只有是服务器的并且还没有下载的才去请求
		if (media.getDownloadState()!=MonitorMedia.DOWNLOAD_STATE_DOWNLOADING&&!FileUtils.isFileExit(FileUtils.getAppStorageOriginalDirectoryPath(MediaListActivity.this)
				+ File.separator + media.getFileName())) {// 通过本地服务器路径加上文件名来判断文件是否存在
			isNeed = true;
		} else {
			isNeed = false;
			media.setPath(FileUtils.getAppStorageOriginalDirectoryPath(MediaListActivity.this)
				+ File.separator + media.getFileName());
			media.setDownloadState(MonitorMedia.DOWNLOAD_STATE_DOWNLOADED);


			if (!FileUtils.isFileExit(FileUtils
					.getAppStorageThumbnailDirectoryPath(MediaListActivity.this)
					+ File.separator + media.getFileName())) {
				// 缩略图不存在，则存储一个缩略图
				String thump = FileUtils.saveMediaThumbnail(
						getApplicationContext(),
						FileUtils.getAppStorageOriginalDirectoryPath(MediaListActivity.this)
								+ File.separator + media.getFileName(),
						media.getMediaType(), false);
				media.setThumbnailPath(thump);
			} else {
				media.setThumbnailPath(FileUtils
						.getAppStorageThumbnailDirectoryPath(MediaListActivity.this)
						+ File.separator + media.getFileName());
			}

			/**在这里添加如果组没有缩略图则创建一个*/
			if(uploadGroup!=null&&!FileUtils.isFileExit(FileUtils.getAppStorageThumbnailDirectoryPath(MediaListActivity.this)+File.separator+uploadGroup.getId()+".jpg"))	{
				//不存在
				FileUtils.saveMediaGroupThumbnail(this,media.getThumbnailPath(),uploadGroup.getId());
			}

		}
		return isNeed;
	}

	/** 广播监听 */
	public void setBroadCastListener() {
		IntentFilter filter = new IntentFilter(
				FTPService.FTP_BROAD_CAST_ACTION_MEDIA_UPLOAD);
		registerReceiver(receiver, filter);
	}

	/**
	 * 监听FTP发送的广播
	 **/
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null
					&& intent.getAction() != null
					&& TextUtils.equals(
							FTPService.FTP_BROAD_CAST_ACTION_MEDIA_UPLOAD,
							intent.getAction())) {
				LogUtils.i(tag, "收到FTP广播");
				int code = intent.getIntExtra(FTPService.FTP_KEY_CODE,
						FTPService.FTP_CODE_SUCCESS);
				String groupId = intent
						.getStringExtra(FTPService.FTP_KEY_GROUP_ID);
				String mediaId = intent
						.getStringExtra(FTPService.FTP_KEY_MEDIA_ID);
				String message = intent
						.getStringExtra(FTPService.FTP_KEY_MESSAGE);
				int progress = intent.getIntExtra(FTPService.FTP_KEY_PROGRESS,
						0);
				LogUtils.i(tag, code + "-" + groupId + "-" + mediaId + "-"
						+ message + "-" + progress);
				if (TextUtils.equals(groupId, uploadGroup.getId())) {
					if (code == FTPService.FTP_CODE_UPLOADING_GROUP) {// 正在上传
						uploadGroup.setProgress(progress);
						mProgressBar.setProgress(progress);
					} else if (code == FTPService.FTP_CODE_UPLOAD_GROUP_SUCCESS) {// 上传完成
						ToastUtils.toast(getApplicationContext(), "上传完成");
						mProgressBar.setVisibility(View.GONE);
						mRightTv.setVisibility(View.GONE);
					} else if (code == FTPService.FTP_CODE_UPLOAD_GROUP_ERROR) {// 上传完成
						ToastUtils.toast(getApplicationContext(), "上传失败");
						mProgressBar.setVisibility(View.GONE);
						mRightTv.setVisibility(View.GONE);
					}
				}
			}
		}
	};



	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (TextUtils.equals(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING,
				uploadGroup.getUploadState())) {
			unregisterReceiver(receiver);
		}
	}

	@Override
	public void dataBack(int id, Object obj) {
		if(this!=null){
			if (id == RequestService.ID_DOWNLOADMEDIA) {// 下载数据
				RspDownLoadMedia rspMeida = (RspDownLoadMedia) obj;
				MonitorMedia media = rspMeida.getData();
				for (MonitorMedia m : dataList) {
					if (m.getId() == media.getId()) {//
						m.setPath(media.getPath());
						m.setThumbnailPath(media.getThumbnailPath());
						mAdapter.notifyDataSetChanged();
						break;
					}
				}
			}
		}
		
	}

	// 刷新
	@Override
	public void listViewRefresh() {
		// mEBikeRequestService.getPhotoMedia(SPUtils.getUser(this).getUserID(),
		// 0, 20);
	}

	// 加载更多
	@Override
	public void listViewLoadMore() {
		// mGVRequestService.getJobList(null, "", pageNow * pageSize, pageSize,
		// "");
	}

	// 关键字搜索
	@Override
	public List<MonitorMedia> onSearch(String key) {

		return dataList;
	}

	// 适配器
	@Override
	public EBBaseAdapter<MonitorMedia> initAdapter() {
		return new MediaListAdapter(this);
	}

	// item点击
	@Override
	public void onListViewItemClick(final MonitorMedia item, int position) {
		LogUtils.i(tag, ""+item.toString());
		if (TextUtils.equals(uploadGroup.getUploadState(),
				MonitorMediaGroupUpload.UPLOAD_STATE_SERVER_DATA)) {//服务器的数据，才要去判断，不是的话，则不用
			if ((item.getDownloadState() == MonitorMedia.DOWNLOAD_STATE_NONE||item.getDownloadState() == MonitorMedia.DOWNLOAD_STATE_DOWNLOAD_FAIL)
					&& isNeedDownLoad(item)) {
				item.setDownloadState(MonitorMedia.DOWNLOAD_STATE_DOWNLOADING);
//				ToastUtils.toast(getApplicationContext(), "下载文件...");
				mEBikeRequestService.downLoadMedia(getApplicationContext(), item);
				mAdapter.notifyDataSetChanged();
			} else if (item.getDownloadState() == MonitorMedia.DOWNLOAD_STATE_DOWNLOADING) {
				ToastUtils.toast(getApplicationContext(), "数据正在下载中，请稍候...");
			} else {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("media", item);
				map.put("mediaList", mediaList);
				if (TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO + "",
						item.getMediaType())) {// 图片
					openActivity(PhotoShowActivity.class, map, false);
				} else {// 视频
					openActivity(VideoPlayActivity.class, map, false);
				}

			}
		}else{
			try {
				if(!FileUtils.isFileExit(item.getPath())){//文件不存在提示
					new Builder(MediaListActivity.this).setTitle("系统在存储卡中检测不到此源文件，是否删除此记录？").setCancelable(true).setPositiveButton("删除", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 删除数据库
							int row=DBHelper.getInstance(MediaListActivity.this).deleteMonitorMedia(item.getId());
							if(row>0){
								ToastUtils.toast(MediaListActivity.this,"删除成功");
								Iterator<MonitorMedia> it=mediaList.iterator();
								while (it.hasNext()){
									MonitorMedia m=it.next();
									if(TextUtils.equals(m.getId(),item.getId())){
										it.remove();
									}
								}
								pageNow=1;
								operate=0;
								MediaListActivity.super.loadListView(mediaList);
							}

						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create().show();
				}else{
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("media", item);
					map.put("mediaList", mediaList);
					if (TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO + "",
							item.getMediaType())) {// 图片
						openActivity(PhotoShowActivity.class, map, false);
					} else {// 视频
						openActivity(VideoPlayActivity.class, map, false);
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}



		}
		
	}

	/** 用来通知fragment数据请求失败，子类，如果需要监听可以复写此方法 */
	@Override
	public void onFailRequest(int id,Object obj) {
		if(id==RequestService.ID_DOWNLOADMEDIA){
			RspDownLoadMedia rspMeida = (RspDownLoadMedia) obj;
			if(rspMeida!=null&&rspMeida.getData()!=null){
				MonitorMedia media = rspMeida.getData();
				for (MonitorMedia m : dataList) {
					if (m.getId() == media.getId()) {//
						ToastUtils.toast(getApplicationContext(), "下载失败，请重新下载");
						m.setDownloadState(MonitorMedia.DOWNLOAD_STATE_DOWNLOAD_FAIL);
						mAdapter.notifyDataSetChanged();
						break;
					}
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		List<MonitorMedia> list = new ArrayList<MonitorMedia>();
		for (MonitorMedia media : dataList) {
			if (media.getCheck() == 1) {
				list.add(media);
			}
		}
		switch (v.getId()) {
		case R.id.top_bar_left:
			finish();
			break;
		case R.id.top_bar_right_text:
			if (TextUtils.equals(uploadGroup.getUploadState(),
					MonitorMediaGroupUpload.UPLOAD_STATE_SERVER_DATA)) {
				// 全部下载
				boolean isHasDownload=false;
				for (MonitorMedia media : mediaList) {
					if (isNeedDownLoad(media)) {
						isHasDownload=true;
						media.setDownloadState(MonitorMedia.DOWNLOAD_STATE_DOWNLOADING);
						//去判断是否下载
						mEBikeRequestService.downLoadMedia(getApplicationContext(),
								media);
					}
				}
				if(!isHasDownload){//如果没有要下载的，则把下载隐藏掉
					ToastUtils.toast(MediaListActivity.this,"文件已全部加载完成!");
					mRightTv.setVisibility(View.GONE);
				}
				mAdapter.notifyDataSetChanged();
			} else if (TextUtils.equals(uploadGroup.getUploadState(),
					MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING)) {
				// 取消上传
				cancelGroupList(uploadGroup);
			}
			break;
		case R.id.top_bar_right_icon:
			break;
		case R.id.view_operate_cancel:

			break;
		case R.id.view_operate_deleted:

			break;
		case R.id.view_operate_upload:

			break;

		default:
			break;

		}
	}

	/** 显示底部栏，View.visibility */
	public void showHideBottomBar() {
		if (mediaOperateView != null) {
			if (mediaOperateView.getVisibility() == View.VISIBLE) {
				mediaOperateView.setVisibility(View.GONE);
			} else {
				mediaOperateView.setVisibility(View.VISIBLE);
			}
		}
	}

	/** 取消分组数据 */
	private void cancelGroupList(final MonitorMediaGroupUpload group) {
		if(MediaListActivity.this!=null){
			if (group == null) {
				ToastUtils.toast(MediaListActivity.this, "没有可操作的数据!");
				return;
			}
			new Builder(MediaListActivity.this).setTitle("确定要取消上传吗？")
					.setPositiveButton("是", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 将组下的数据都
							group.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_CANCEL
									+ "");
							List<MonitorMedia> mediaList = DBHelper.getInstance(
									MediaListActivity.this.getApplicationContext())
									.listMonitorMediaByGroupUploadId(group.getId());
							for (MonitorMedia media : mediaList) {
								media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_CANCEL
										+ "");
							}
							DBHelper.getInstance(MediaListActivity.this.getApplicationContext())
									.updateMonitorMediaGroupUpload(group);// 更新状态
							DBHelper.getInstance(MediaListActivity.this.getApplicationContext())
									.updateMonitorMediaList(mediaList);// 更新数据
							// 调用FTP取消
							FTPMediaUtil.cancelMediaUpload(MediaListActivity.this.getApplicationContext(),
									group);
							finish();
						}
					}).setNegativeButton("否", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).create().show();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		if (dataList != null && position < dataList.size()) {
			MonitorMedia media=dataList.get(position);
			//只有未上传，上传失败，上传取消，才可以修改数据
			if(TextUtils.equals(media.getUploadState(),MonitorMediaGroupUpload.UPLOAD_STATE_UN_UPLOAD)||
					TextUtils.equals(media.getUploadState(),MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_CANCEL)||
					TextUtils.equals(media.getUploadState(),MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_FAIL)
					){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("media", media);
				openActivity(MediaModifyActivity.class,map,false);
			}
		}
		return true;
	}
}
