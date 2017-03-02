/**
 * 
 */
package com.egovcomm.monitor.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.adapter.EBBaseAdapter;
import com.egovcomm.monitor.adapter.MediaListAdapter;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.db.MonitorTable;
import com.egovcomm.monitor.fragment.BaseListFragment;
import com.egovcomm.monitor.ftp.FTPMediaUtil;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.model.ReqUploadMediaData;
import com.egovcomm.monitor.model.RspGroupList;
import com.egovcomm.monitor.model.RspMedia;
import com.egovcomm.monitor.model.RspMediaGroup;
import com.egovcomm.monitor.model.RspUploadMedia;
import com.egovcomm.monitor.model.User;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.utils.CameraHelper;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.JsonUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.TimeUtils;
import com.egovcomm.monitor.utils.ToastUtils;
import com.nicolls.ablum.MediaChooser;
import com.nicolls.ablum.activity.BucketHomeFragmentActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @author mengjk
 *
 *         2015年7月3日
 */
public class SystemMediaUploadActivity extends BaseListActivity<MonitorMedia> implements AdapterView.OnItemLongClickListener{
	private AlertDialog dialog = null;
	private String mediaType;
	private static final int REQUEST_MEDIA = 1008;
	private static final int REQUEST_UPDATE = 1009;
	private List<String> pathList;
//	private List<MediaItem> mMediaSelectedList;
//	private List<MonitorMedia> mediaList=new ArrayList<MonitorMedia>();
	// 初始化view
	@Override
	public void initView() {
//		mediaType=MonitorMediaGroup.TYPE_PHOTO;//默认图片
//		mediaList.clear();
		LogUtils.i(tag, "回到子类初始化");
		mListViewPulltorefreshLayout.setPull2RefreshEnable(false);
		mListView.setOnItemLongClickListener(this);
		mSearchBar.setVisibility(View.GONE);
		btnCancel.setVisibility(View.GONE);
		mListView.setOnItemLongClickListener(this);
		mTitle.setText("相册");
		mRightTv.setText("上传");
		mRightTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		mRightIv.setVisibility(View.VISIBLE);
		mRightIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				openAlbum();
				otherAlbum();
			}
		});
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mRightTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dataList==null||dataList.size()<=0){
					ToastUtils.toast(SystemMediaUploadActivity.this,"请选择要上传的媒体数据");
					return;
				}
				//上传
				//开始上传
				showLoading(true);
				mEBikeRequestService.groupList(SPUtils.getUser(SystemMediaUploadActivity.this).getUserID(),mediaType,1,1000);
			}
		});
//		mediaList.clear();
		SystemMediaUploadActivity.super.loadListView(null);
		mediaType=getIntent().getStringExtra("mediaType");
		//调用高级的
//		openAlbum();
		//调用低级的
		otherAlbum();

	}

//	private void openAlbum(){
//		//打开相册
//		MediaOptions.Builder builder = new MediaOptions.Builder();
//		MediaOptions options = null;
//		options = builder.canSelectBothPhotoVideo()
//				.canSelectMultiPhoto(true).canSelectMultiVideo(true)
//				.build();
//		if (options != null) {
////					mediaList.clear();
//			SystemMediaUploadActivity.super.loadListView(dataList);
//			MediaPickerActivity.open(SystemMediaUploadActivity.this, REQUEST_MEDIA, options);
//		}
//	}

	private void otherAlbum(){
		IntentFilter videoIntentFilter = new IntentFilter(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(videoBroadcastReceiver, videoIntentFilter);

		IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(imageBroadcastReceiver, imageIntentFilter);

		MediaChooser.setSelectionLimit(20);
		if(pathList==null){
			pathList=new ArrayList<>();
		}
		if(dataList!=null){
			for(MonitorMedia media:dataList){
				pathList.add(media.getPath());
			}
		}
		HashMap<String,Object> map=new HashMap<>();
		map.put("mediaType",mediaType);
		map.put("pathList",pathList);
		openActivity(BucketHomeFragmentActivity.class,map,false);
	}


	BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

//			Toast.makeText(SystemMediaUploadActivity.this, "yippiee Video ", Toast.LENGTH_SHORT).show();
//			Toast.makeText(SystemMediaUploadActivity.this, "Video SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();
			mediaType=MonitorMediaGroup.TYPE_VIDEO;
			pathList=intent.getStringArrayListExtra("list");
			parseMediaPath();
		}
	};


	BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
//			Toast.makeText(SystemMediaUploadActivity.this, "yippiee photo ", Toast.LENGTH_SHORT).show();
//			Toast.makeText(SystemMediaUploadActivity.this, "photo SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();
			mediaType=MonitorMediaGroup.TYPE_PHOTO;
			pathList=intent.getStringArrayListExtra("list");
			parseMediaPath();
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(imageBroadcastReceiver);
		unregisterReceiver(videoBroadcastReceiver);
		super.onDestroy();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_MEDIA&&resultCode == RESULT_OK) {

//				mMediaSelectedList = MediaPickerActivity
//						.getMediaItemSelected(data);
//				if (mMediaSelectedList != null) {
//					List<String> pathList=new ArrayList<>();
//					for (MediaItem mediaItem : mMediaSelectedList) {
//						String path=mediaItem.getPathOrigin(this);
//						if(mediaItem.getType()==MediaItem.PHOTO){
//							mediaType=MonitorMediaGroup.TYPE_PHOTO;
//						}else{
//							mediaType=MonitorMediaGroup.TYPE_VIDEO;
//						}
//						pathList.add(path);
//					}
//					parseMediaPath(pathList);
//				} else {
//					LogUtils.e(tag, "Error to get media, NULL");
//				}
		}else if (requestCode == REQUEST_UPDATE&&resultCode == RESULT_OK) {
			MonitorMedia media=data.getParcelableExtra("media");
			LogUtils.i(tag,media.toString());
			for(MonitorMedia m:dataList){
				if(TextUtils.equals(media.getId(),m.getId())){
					Collections.replaceAll(dataList,m,media);
					mAdapter.notifyDataSetChanged();
					break;
				}
			}

		}
	}

	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			hideLoading();
			List<MonitorMedia> mediaList= (List<MonitorMedia>) msg.obj;
			SystemMediaUploadActivity.super.loadListView(mediaList);
		}
	};

	private void parseMediaPath(){
		final List<MonitorMedia> mediaList=new ArrayList<MonitorMedia>();
		showLoading(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(String path:pathList){
					try {
						File sourceFile=new File(path);

						File file=null;
//							String mediaType="";
						int type=0;
						if(TextUtils.equals(mediaType,MonitorMediaGroup.TYPE_PHOTO)){
							type=CameraHelper.MEDIA_TYPE_IMAGE;
						}else{
							type=CameraHelper.MEDIA_TYPE_VIDEO;
						}
						file=FileUtils.getOutputMediaFile(SystemMediaUploadActivity.this,type);
						FileUtils.copyFile(sourceFile,file);


						String thumpFilePath=FileUtils.saveMediaThumbnail(getApplicationContext(), file.getAbsolutePath(), mediaType,true);

						MonitorMedia media=new MonitorMedia();
						media.setId(FileUtils.getFileNameNoEx(file.getName()));
						media.setFileName(file.getName());
						media.setFileSize(file.length()+"");//保存字节数
						media.setFileState(0+"");
						media.setFileSuffix(FileUtils.getExtensionName(file.getName()));
						media.setGroupUploadId(MonitorTable.SYSTEM_VALUE);//SYSTEM_VALUE表示是系统中提供的
						media.setOrientation(0+"");
						media.setPath(file.getAbsolutePath());
						media.setRemark("");
						media.setShootingLocation("");
						media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_SYSTEM+"");
						media.setUploadTime("");
						media.setUserId(SPUtils.getUser(SystemMediaUploadActivity.this).getUserID());
						media.setMediaType(mediaType);
						media.setThumbnailPath(thumpFilePath);
						media.setCreateTime(TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
						media.setTime(TimeUtils.getFormatNowTime("yyyy-MM-dd HH:mm")+":00");
						media.setReason("");
						media.setLongitude("");
						media.setLatitude("");
						mediaList.add(media);


					}catch (Exception e){
						LogUtils.e(tag,e.getMessage());
					}
				}
				Message msg=Message.obtain();
				msg.obj=mediaList;
				mHandler.sendMessage(msg);
			}
		}).start();

	}

	@Override
	public void dataBack(int id, Object obj) {//选择的数据
		hideLoading();
		switch (id){
			case RequestService.ID_GROUPLIST://组数据
				RspGroupList rspGroupList= (RspGroupList) obj;
				//把回来的数据插入到数据库，重复的就不会再添加
				if(rspGroupList!=null&&rspGroupList.getData()!=null&&rspGroupList.getData().getData()!=null&&rspGroupList.getData().getData().size()>0){
					List<RspMediaGroup> list=rspGroupList.getData().getData();
					for(MonitorMediaGroup group:list){
						DBHelper.getInstance(SystemMediaUploadActivity.this).insertMonitorMediaGroup(group);
					}
					showGroupList(dataList);
				}else{//为空，或者没有组，则直接创建分组
					createMediaGroup(null);
				}
				break;
			case RequestService.ID_GROUPCREATE:
				RspUploadMedia rsp= (RspUploadMedia) obj;
				if(rsp!=null&&rsp.getData()!=null){
					// 创建分组
					MonitorMediaGroup g =rsp.getData();
					DBHelper.getInstance(SystemMediaUploadActivity.this).insertMonitorMediaGroup(g);
					confirmMediaUpload(g);
				}else{
					ToastUtils.toast(SystemMediaUploadActivity.this,"创建分组失败!");
				}
				break;
			default:
				break;
		}

	}

	/**请求错误会调用这个方法*/
	protected void requestError(int id,Object obj){
		List<MonitorMedia> mediaList = new ArrayList<MonitorMedia>();
		for (MonitorMedia media : dataList) {
			if (media.getCheck() == 1) {
				mediaList.add(media);
			}
		}
		switch (id){
			case RequestService.ID_GROUPLIST://请求分组数据，错误，则会直接创建组
				createMediaGroup(null);
				break;
			case RequestService.ID_GROUPCREATE://请求创建组失败，则终止
				ToastUtils.toast(SystemMediaUploadActivity.this,"创建分组失败!");
				break;
			default:
				break;
		}
	}

	// 刷新
	@Override
	public void listViewRefresh() {
//		if (SystemMediaUploadActivity.this != null) {
//			List<MonitorMedia> list = DBHelper.getInstance(SystemMediaUploadActivity.this).listBySystemMonitorMediaByUserId(
//					SPUtils.getUser(SystemMediaUploadActivity.this).getUserID(), mediaType);
//			super.loadListView(list);
//			hideLoading();
//		} else {
//			LogUtils.e(tag, "activity为空了!#######");
//		}
	}

	// 加载更多
	@Override
	public void listViewLoadMore() {

	}

	// 关键字搜索
	@Override
	public List<MonitorMedia> onSearch(String key) {

		return dataList;
	}

	// 适配器
	@Override
	public EBBaseAdapter<MonitorMedia> initAdapter() {
		return new MediaListAdapter(SystemMediaUploadActivity.this);
	}

	@Override
	public void onListViewItemClick(final MonitorMedia item, int position) {
		if(mediaOperateView.getVisibility()==View.VISIBLE){//可选 就要选中
			if(item.getCheck()==1){
				item.setCheck(0);
			}else{
				item.setCheck(1);
			}
			mAdapter.notifyDataSetChanged();
		}else{
			try {
				if(!FileUtils.isFileExit(item.getPath())){//文件不存在提示
					new Builder(SystemMediaUploadActivity.this).setTitle("应用在存储卡中检测不到此源文件，是否删除此记录？").setCancelable(true).setPositiveButton("删除", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 删除数据库
							DBHelper.getInstance(SystemMediaUploadActivity.this).deleteMonitorMedia(item.getId());
							listViewRefresh();

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
					if (TextUtils.equals(MonitorMediaGroup.TYPE_PHOTO + "", item.getMediaType())) {// 图片
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

	@Override
	public void onFailRequest(int id,Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
//		List<MonitorMedia> list = new ArrayList<MonitorMedia>();
//		for (MonitorMedia media : dataList) {
//			if (media.getCheck() == 1) {
//				list.add(media);
//			}
//		}
//		switch (v.getId()) {
//
//		case R.id.view_operate_cancel:
//
//			break;
//		case R.id.view_operate_deleted:
//			deletedList(list);
//			break;
//		case R.id.view_operate_upload://要上传，请求组数据
//			showLoading(true);
//			mEBikeRequestService.groupList(SPUtils.getUser(SystemMediaUploadActivity.this).getUserID(),mediaType,1,1000);
//			break;
//
//		default:
//			break;
//
//		}
	}

	/** 删除数据 */
//	private void deletedList(final List<MonitorMedia> list) {
//		if(list!=null&&list.size()<=0){
//			ToastUtils.toast(SystemMediaUploadActivity.this, "请选择要操作的数据!");
//			return;
//		}
//		new Builder(SystemMediaUploadActivity.this).setTitle("确定要删除吗？").setCancelable(false).setPositiveButton("删除", new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// 删除数据库
//				DBHelper.getInstance(SystemMediaUploadActivity.this).deleteMonitorMediaList(list);
//				// 删除本地文件
//				for (MonitorMedia media : list) {
//					try {
//						File f = new File(media.getPath());
//						f.delete();
//					} catch (Exception e) {
//						LogUtils.e(tag, "删除文件失败");
//					}
//				}
//				listViewRefresh();
//
//			}
//		}).setNegativeButton("取消", new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//
//			}
//		}).create().show();
//	}



//	private void showHideCheckBox(boolean show) {
//		for (MonitorMedia media : dataList) {
//			if (show) {
//				media.setShowCheck(1);
//			} else {
//				media.setShowCheck(0);
//			}
//			media.setCheck(0);// 设置为默认值
//		}
//		mAdapter.notifyDataSetChanged();
//	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == R.id.view_operate_checkbox) {// 全选
			for (MonitorMedia media : dataList) {
				if (isChecked) {
					media.setCheck(1);// 选中
				} else {
					media.setCheck(0);// 取消选中
				}
			}

			mAdapter.notifyDataSetChanged();
		}
	}

	/** 弹出组选择对话框 */
	private void showGroupList(final List<MonitorMedia> list) {
		if(list!=null&&list.size()<=0){
			ToastUtils.toast(SystemMediaUploadActivity.this, "请选择媒体数据!");
			return;
		}
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		final List<MonitorMediaGroup> groupList = DBHelper.getInstance(SystemMediaUploadActivity.this).listMonitorMediaGroup(
				SPUtils.getUser(SystemMediaUploadActivity.this).getUserID(),mediaType);
		if (groupList.size() > 0) {
			for (MonitorMediaGroup group : groupList) {
				map = new HashMap<String, Object>();
				map.put("name", group.getRemark());
				map.put("time", group.getCreateTime());
				map.put("location", group.getCreateAddr());
				dataList.add(map);
			}

			ListView listView = new ListView(SystemMediaUploadActivity.this);
			listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			SimpleAdapter adapter = new SimpleAdapter(SystemMediaUploadActivity.this, dataList, R.layout.item_dialog_group,
					new String[] { "name", "time", "location" }, new int[] { R.id.item_dialog_name,
							R.id.item_dialog_time, R.id.item_dialog_location });
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (dialog != null) {
						dialog.cancel();
					}
					//ToastUtils.toast(SystemMediaUploadActivity.this, "点击的是第" + position + "个");
					MonitorMediaGroup group = groupList.get(position);
					LogUtils.i(tag, group.toString());
					// 上传
					uploadMediaGroup(group);
				}
			});

			dialog = new Builder(SystemMediaUploadActivity.this).setTitle("选择已有分组或新建一个分组来完成数据上传").setView(listView)
					.setCancelable(false).setPositiveButton("新建分组", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							createMediaGroup(groupList);
						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).create();
			dialog.show();

		} else {
			createMediaGroup(groupList);
		}

	}

	/** 新建分组 */
	private void createMediaGroup(final List<MonitorMediaGroup> groupList) {
		final EditText et = new EditText(SystemMediaUploadActivity.this);
		final TextView tip=new TextView(SystemMediaUploadActivity.this);
		tip.setTextColor(getResources().getColor(R.color.red));
		tip.setText("提示");
		tip.setVisibility(View.INVISIBLE);
		final LinearLayout layout=new LinearLayout(SystemMediaUploadActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				LogUtils.i(tag,"afterTextChanged"+s);
			}

			@Override
			public void afterTextChanged(Editable s) {
				LogUtils.i(tag,"afterTextChanged"+s.toString());
				tip.setVisibility(View.INVISIBLE);
				if(groupList!=null){
					for(MonitorMediaGroup group:groupList){
						if(TextUtils.equals(group.getRemark(),s.toString())){//有相同的
							tip.setVisibility(View.VISIBLE);
							tip.setText("此分组备注已存在，请更换！");
							break;
						}
					}
				}


			}
		});
		et.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		et.setHint("请输入分组备注");

		layout.addView(tip);
		layout.addView(et);

		Builder builder = new Builder(SystemMediaUploadActivity.this);

		builder.setTitle("创建上传数据分组");
		// builder.setMessage("请输入分组名称，按确定完成创建!");
		builder.setView(layout);
		builder.setCancelable(false);
		builder.setPositiveButton("创建", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

				if(groupList!=null){
					for(MonitorMediaGroup group:groupList){
						if(TextUtils.equals(group.getRemark(),et.getText().toString())){//有相同的
							ToastUtils.toast(SystemMediaUploadActivity.this,"此分组备注已存在，请更换！");
							createMediaGroup(groupList);
							return;
						}
					}
				}
				LogUtils.i(tag,"创建分组！");
				String data = "";
				ReqUploadMediaData req = new ReqUploadMediaData();
				User user = SPUtils.getUser(SystemMediaUploadActivity.this);
				req.setId("");
				req.setUserId(user.getUserID());
				req.setUserName(user.getUserName());
				req.setOrgId(user.getOrgID());
				req.setOrgName(user.getOrgName());
				req.setCreateTime(TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
				req.setCreateAddr(BaseApplication.address);
				req.setLongitude(BaseApplication.longitude+"");
				req.setLatitude(BaseApplication.latitude+"");
				req.setMediaType(mediaType);
				req.setRemark(et.getText().toString()+"");
				List<RspMedia> fileList = new ArrayList<RspMedia>();
				req.setFileList(fileList);
				data = JsonUtils.objectToJson(req, ReqUploadMediaData.class);
				LogUtils.i(tag, "创建分组的数据是：" + data);
				((BaseActivity) SystemMediaUploadActivity.this).showLoading(true);
				mEBikeRequestService.groupCreate(data);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				((BaseActivity) SystemMediaUploadActivity.this).hideLoading();
			}
		});
		builder.create().show();

	}

	/** 新建分组 */
	private void confirmMediaUpload(final MonitorMediaGroup group) {
		Builder builder = new Builder(SystemMediaUploadActivity.this);

		builder.setTitle("提示");
		builder.setMessage("分组创建完成，是否立刻上传!");
		builder.setCancelable(false);
		builder.setPositiveButton("是", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				// 上传
				uploadMediaGroup(group);
			}
		});
		builder.setNegativeButton("否", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				((BaseActivity) SystemMediaUploadActivity.this).hideLoading();
			}
		});
		builder.create().show();

	}

	/** 上传分组数据 */
	private void uploadMediaGroup(MonitorMediaGroup group) {

		((BaseActivity)SystemMediaUploadActivity.this).hideLoading();
		// 存储组
		MonitorMediaGroupUpload uploadGroup = new MonitorMediaGroupUpload();
		uploadGroup.setId(UUID.randomUUID().toString());
		uploadGroup.setMediaGroup(group);
		uploadGroup.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING + "");
//		uploadGroup.setThumbnailPath(list.get(0).getThumbnailPath());
		FileUtils.saveMediaGroupThumbnail(SystemMediaUploadActivity.this,dataList.get(0).getThumbnailPath(),uploadGroup.getId());
		uploadGroup.setRemoteDirectory(TimeUtils.getFormatNowTime("yyy-MM-dd"));//年月日为文件夹
		DBHelper.getInstance(SystemMediaUploadActivity.this).insertMonitorMediaGroupUpload(uploadGroup);
		// 更改数据为在上传，并设置上传组
		for (MonitorMedia media : dataList) {
			media.setGroupUploadId(uploadGroup.getId());
			media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING + "");
		}
		//上传时候再存储到数据库
		DBHelper.getInstance(SystemMediaUploadActivity.this).insertMonitorMediaList(dataList);
//		showHideBottomBar();
		//更新页面数据和进行切换

		//((MediaDataActivity) SystemMediaUploadActivity.this).freshAllData();// 重新刷新所有数据
		//((MediaDataActivity) SystemMediaUploadActivity.this).changeFragmentPager(1);//切换到上传页
		// 调用FTP上传
		 FTPMediaUtil.mediaUpload(SystemMediaUploadActivity.this, uploadGroup);

		ToastUtils.toast(SystemMediaUploadActivity.this,"提交上传！");
		//切换页
		Intent intent=new Intent(MediaDataActivity.ACTION_CHANGE_FRAGMENT_PAGER);
		intent.putExtra("index",1);//切换到上传页
		sendBroadcast(intent);
		finish();
	}

	/** 数据发生改变 */
	public void dataNodify() {

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
//		ToastUtils.toast(SystemMediaUploadActivity.this,"onItemLongClick");
		if (dataList != null && position < dataList.size()) {

			Builder builder = new Builder(SystemMediaUploadActivity.this);

			builder.setTitle("提示");
			builder.setMessage("请选择操作!");
			builder.setCancelable(false);
			builder.setPositiveButton("删除", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					MonitorMedia media=dataList.get(position);
					pathList.remove(position);
					dataList.remove(position);
					mAdapter.notifyDataSetChanged();
				}
			});
			builder.setNegativeButton("编辑", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					MonitorMedia media=dataList.get(position);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("media", media);
					((BaseActivity)SystemMediaUploadActivity.this).openActivity(MediaModifyActivity.class,map,false,true,REQUEST_UPDATE);
				}
			});
			builder.create().show();


		}
		return true;
	}
}
