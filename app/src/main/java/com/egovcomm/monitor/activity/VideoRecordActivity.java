/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.egovcomm.monitor.activity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.AppConstant;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.db.MonitorTable;
import com.egovcomm.monitor.ftp.FTPMediaUtil;
import com.egovcomm.monitor.model.MonitorLocation;
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
import com.egovcomm.monitor.utils.CameraHelper.CameraOpenCallBack;
import com.egovcomm.monitor.utils.CameraHelper.VedioRecordCallBack;
import com.egovcomm.monitor.utils.CommonUtil;
import com.egovcomm.monitor.utils.CommonViewUtils;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.JsonUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.TimeUtils;
import com.egovcomm.monitor.utils.ToastUtils;
import com.google.gson.Gson;

/**
 * This activity uses the camera/camcorder as the A/V source for the
 * {@link MediaRecorder} API. A {@link TextureView}
 * is used as the camera preview which limits the code to API 14+. This can be
 * easily replaced with a {@link android.view.SurfaceView} to run on older
 * devices.
 */
public class VideoRecordActivity extends BaseActivity implements TextureView.SurfaceTextureListener{

	private static final long REQUEST_CAMERA_UPLOAD_LOCATION_SPACE_TIME=4;//视频拍摄间隔3秒上传一次位置
	private Camera mCamera;
	private TextureView mPreview;
	private MediaRecorder mMediaRecorder;
	private ImageButton mIBVideoData;

	private boolean isCameraStateReady=false;
	private boolean isRecording = false;
	private static final String TAG = VideoRecordActivity.class.getName();
	private ImageButton recordButton;
	private TextView timeTv;
	private String path = "";
	private String mediaId = "";
	private long recordTime = 0;// 单位是豪秒
	private int screenOrientation = 0;// 屏幕方向
	private AlertDialog dialog = null;
	private MonitorMedia media;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (VideoRecordActivity.this != null) {
				timeTv.setText(msg.obj.toString());
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_record);
		mIBVideoData = (ImageButton) findViewById(R.id.video_btn_data);
		mPreview = (TextureView) findViewById(R.id.surface_view);
		mPreview.setSurfaceTextureListener(this);
		recordButton = (ImageButton) findViewById(R.id.video_btn_record);
		timeTv = (TextView) findViewById(R.id.video_tv_time);
		int mCurrentOrientation = getResources().getConfiguration().orientation;
		if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
			LogUtils.i("info", "portrait"); // 竖屏
			screenOrientation = CameraHelper.SCREEN_PORTRAIT;
		} else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			LogUtils.i("info", "landscape"); // 横屏
			screenOrientation = CameraHelper.SCREEN_LANDSCAPE;
		}

	}

	/** 计时线程 */
	private synchronized void startTimeThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRecording) {
					recordTime += 10;
					if (recordTime % 1000 == 0) {
						//记录数据位置信息 ，每1秒记录一个位置
						/*
						if(!TextUtils.isEmpty(mediaId)){
							//插入数据的位置信息
							MonitorLocation location=new MonitorLocation();
							location.setId(UUID.randomUUID().toString());
							location.setMediaId(mediaId);
							location.setUserId(SPUtils.getUser(getApplicationContext()).getUserID());
							location.setLongitude(BaseApplication.longitude+"");
							location.setLatitude(BaseApplication.latitude+"");
							location.setAddress(BaseApplication.address);
							location.setCreateTime(TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
							location.setRemark("");
							location.setState("");
							DBHelper.getInstance(VideoRecordActivity.this).insertMonitorLocation(location);
						}*/
						Message msg = Message.obtain();
						msg.obj = TimeUtils.formatTimeMillisToHMS(recordTime);
						if (handler != null) {
							handler.sendMessage(msg);
						}
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/** 返回 */
	public void onBack(View view) {
		LogUtils.i(TAG, "####onBack");
		finish();
	}

	/** 我的数据 */
	public void onData(View view) {
		if(isCameraStateReady){
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("mediaType", MonitorMediaGroup.TYPE_VIDEO);
			openActivity(MediaDataActivity.class, map, false);
		}
	}

	/**
	 * The capture button controls all user interaction. When recording, the
	 * button click stops recording, releases
	 * {@link MediaRecorder} and {@link Camera}.
	 * When not recording, it prepares the {@link MediaRecorder}
	 * and starts recording.
	 * 
	 * @param view
	 *            the view generating the event.
	 */
	public  void onRecord(final View view) {
		if(isCameraStateReady){
			try {
				if (isRecording) {
					isCameraStateReady=false;
					recordButton.setSelected(false);
					if (mMediaRecorder != null) {
						mMediaRecorder.stop(); // stop the recording
						// clear recorder configuration
						mMediaRecorder.reset();
						// release the recorder object
						mMediaRecorder.release();
						mMediaRecorder = null;
						// Lock camera for later use i.e taking it back from
						// MediaRecorder.
						// MediaRecorder doesn't need it anymore and we will release
						// it if the activity pauses.
						// mCamera.lock();
					}
					recordButton.setSelected(false);
					isRecording = false;
					mCamera.stopPreview();
					BaseApplication.uploadLocationSpaceTime=BaseApplication.REQUEST_UPLOAD_LOCATION_SPACE_TIME;
					BaseApplication.status = BaseApplication.STATUS_ONLINE;
					BaseApplication.mediaId="";

					alertSaveData();
				} else {
					recordButton.setSelected(true);
					mIBVideoData.setVisibility(View.GONE);
					recordButton.setSelected(true);
					isCameraStateReady=false;
					if (mCamera != null) {
						Point p=CommonViewUtils.getDisplaySize(VideoRecordActivity.this);
						CameraHelper.startVideoRecord(this, mCamera, screenOrientation,
								p, new VedioRecordCallBack() {

									@Override
									public void startSuccess(
											MediaRecorder mediaRecorder,
											String videoPath) {

										recordTime = 0;
										isRecording = true;
										mMediaRecorder = mediaRecorder;
										// ToastUtils.toast(getApplicationContext(),
										// "开始录制：" + videoPath);
										path = videoPath;
										if(!TextUtils.isEmpty(path)){
											File file=new File(path);
											if(file!=null&&file.exists()){
												mediaId=FileUtils.getFileNameNoEx(file.getName());
												BaseApplication.uploadLocationSpaceTime=REQUEST_CAMERA_UPLOAD_LOCATION_SPACE_TIME;
												BaseApplication.mediaId=mediaId;
												BaseApplication.status = BaseApplication.STATUS_WORKING;
											}
										}
										isRecording = true;
										startTimeThread();
										isCameraStateReady=true;
									}

									@Override
									public void startFail(String message) {
										isCameraStateReady=true;
										ToastUtils.toast(getApplicationContext(),
												message);
									}
								});
					}
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	/** 提示是否保存数据 */
	private void alertSaveData() {
		new Builder(VideoRecordActivity.this).setTitle("录制完成，要保存此视频吗？").setCancelable(false)
		.setPositiveButton("保存", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mIBVideoData.setVisibility(View.VISIBLE);
				isCameraStateReady=true;
				// 保存数据
				try {
					File file = new File(path);
					if (file != null && file.exists()) {
						String thumpFilePath = FileUtils.saveMediaThumbnail(
								getApplicationContext(), file.getAbsolutePath(),
								MonitorMediaGroup.TYPE_VIDEO + "", true);

						media = new MonitorMedia();
						media.setId(FileUtils.getFileNameNoEx(file.getName()));
						media.setFileName(file.getName());
						media.setFileSize(file.length() + "");// 保存字节数
						media.setFileState(0 + "");
						media.setFileSuffix(FileUtils.getExtensionName(file.getName()));
						media.setGroupUploadId(MonitorTable.NULL_VALUE);// 0表示没有组
						media.setOrientation(screenOrientation + "");
						media.setPath(file.getAbsolutePath());
						media.setRemark("");
						media.setShootingLocation(BaseApplication.address==null?"":BaseApplication.address);
						media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UN_UPLOAD
								+ "");
						media.setUploadTime("");
						media.setUserId(SPUtils.getUser(VideoRecordActivity.this)
								.getUserID());
						media.setMediaType(MonitorMediaGroup.TYPE_VIDEO);
						media.setThumbnailPath(thumpFilePath);
						media.setCreateTime(TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
						media.setTime(TimeUtils.getFormatNowTime("yyyy-MM-dd HH:mm")+":00");
						media.setReason("");
						media.setLongitude(BaseApplication.longitude+"");
						media.setLatitude(BaseApplication.latitude+"");
						DBHelper.getInstance(VideoRecordActivity.this).insertMonitorMedia(
								media);
						LogUtils.i(tag, "数据库插入视频文件数据成功");
						alertUpload();//上传;
					} else {
						mCamera.startPreview();
						ToastUtils.toast(VideoRecordActivity.this, "保存数据失败");
					}
				} catch (Exception e) {
					ToastUtils.toast(VideoRecordActivity.this, "保存数据失败");
					LogUtils.e(tag, "保存文件失败");
					mCamera.startPreview();
				}
				
			}
		}).setNegativeButton("丢弃", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mIBVideoData.setVisibility(View.VISIBLE);
				isCameraStateReady=true;
				if(!TextUtils.isEmpty(mediaId)){//如果有创建则删除位置信息
					DBHelper.getInstance(VideoRecordActivity.this).deleteMonitorLocation(mediaId);
				}
				if(!TextUtils.isEmpty(path)){
					try {
						File f=new File(path);
						if(f.exists()){
							f.delete();
						}
					} catch (Exception e) {
						LogUtils.e(TAG, e.getMessage());
					}
				}
				mCamera.startPreview();
			}
		}).create().show();
	}
	
	/** 提示是否上传 */
	private void alertUpload() {
		new Builder(VideoRecordActivity.this).setTitle("保存数据完成，是否上传此视频?").setCancelable(false)
		.setPositiveButton("是", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//上传
				if(CommonUtil.checkNetWork(VideoRecordActivity.this)){
					showLoading(true);
					mEBikeRequestService.groupList(SPUtils.getUser(VideoRecordActivity.this).getUserID(),MonitorMediaGroup.TYPE_VIDEO,1,1000);
				}else{
					mCamera.startPreview();
					ToastUtils.toast(VideoRecordActivity.this, "当前网络不可用，请检查网络正常后再尝试上传");
				}
			}
		}).setNegativeButton("否", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCamera.startPreview();
			}
		}).create().show();
		
	}

	/** 退出页面 */
	private void exit() {
		LogUtils.i(TAG, "###exit" + mMediaRecorder);
		if (mMediaRecorder != null) {
			if (mMediaRecorder != null) {
				try {
					mMediaRecorder.stop(); // stop the recording
					// clear recorder configuration
					mMediaRecorder.reset();
					// release the recorder object
					mMediaRecorder.release();
					mMediaRecorder = null;
				}catch (Exception e){
					e.printStackTrace();
				}
				// Lock camera for later use i.e taking it back from
				// MediaRecorder.
				// MediaRecorder doesn't need it anymore and we will release it
				// if the activity pauses.
				// mCamera.lock();
			}
		}
		if (mCamera != null) {
			try {
				mCamera.unlock();
			}catch (Exception e){
				LogUtils.e(TAG,"解锁相机"+e.getMessage()+"");
			}
			mCamera.release();
			mCamera = null;
		}
		if (isRecording && !TextUtils.isEmpty(path)) {// 如果正在录制，则删除掉
			File f = new File(path);
			f.delete();
			if(!TextUtils.isEmpty(mediaId)){//如果有创建则删除位置信息
				DBHelper.getInstance(VideoRecordActivity.this).deleteMonitorLocation(mediaId);
			}
			LogUtils.i(TAG, "删除视频文件" + path);
		}
		recordButton.setSelected(false);
		isRecording = false;
	}


	@Override
	public void dateUpdate(int id, Object obj) {
		hideLoading();
		List<MonitorMedia> mediaList=new ArrayList<MonitorMedia>();
		mediaList.add(media);
		switch (id){
			case RequestService.ID_GROUPLIST://组数据
				RspGroupList rspGroupList= (RspGroupList) obj;
				//把回来的数据插入到数据库，重复的就不会再添加
				if(rspGroupList!=null&&rspGroupList.getData()!=null&&rspGroupList.getData().getData()!=null&&rspGroupList.getData().getData().size()>0){
					List<RspMediaGroup> list=rspGroupList.getData().getData();
					for(MonitorMediaGroup group:list){
						DBHelper.getInstance(this).insertMonitorMediaGroup(group);
					}
					showGroupList(mediaList);
				}else{//为空，或者没有组，则直接创建分组
					createMediaGroup(mediaList,null);
				}
				break;
			case RequestService.ID_GROUPCREATE:
				RspUploadMedia rsp= (RspUploadMedia) obj;
				if(rsp!=null&&rsp.getData()!=null){
					// 创建分组
					MonitorMediaGroup g =rsp.getData();
					DBHelper.getInstance(VideoRecordActivity.this).insertMonitorMediaGroup(g);
					confirmMediaUpload(g, mediaList);
				}else{
					ToastUtils.toast(this,"创建分组失败!");
				}
				break;
			default:
				break;
		}
	}

	/**请求错误会调用这个方法*/
	protected void requestError(int id,Object obj){
		List<MonitorMedia> mediaList=new ArrayList<MonitorMedia>();
		mediaList.add(media);
		switch (id){
			case RequestService.ID_GROUPLIST://请求分组数据，错误，则会直接创建组
				createMediaGroup(mediaList,null);
				break;
			case RequestService.ID_GROUPCREATE://请求创建组失败，则终止
				ToastUtils.toast(this,"创建分组失败!");
				break;
			default:
				break;
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.uploadLocationSpaceTime=BaseApplication.REQUEST_UPLOAD_LOCATION_SPACE_TIME;
		BaseApplication.status = BaseApplication.STATUS_ONLINE;
		BaseApplication.mediaId="";
	}
	
	
	/** 弹出组选择对话框 */
	private void showGroupList(final List<MonitorMedia> list) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		final List<MonitorMediaGroup> groupList = DBHelper.getInstance(VideoRecordActivity.this).listMonitorMediaGroup(
				SPUtils.getUser(VideoRecordActivity.this).getUserID(),MonitorMediaGroup.TYPE_VIDEO);
		if (groupList.size() > 0) {
			for (MonitorMediaGroup group : groupList) {
				map = new HashMap<String, Object>();
				map.put("name", group.getRemark());
				map.put("time", group.getCreateTime());
				map.put("location", group.getCreateAddr());
				dataList.add(map);
			}

			ListView listView = new ListView(VideoRecordActivity.this);
			listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			SimpleAdapter adapter = new SimpleAdapter(VideoRecordActivity.this, dataList, R.layout.item_dialog_group,
					new String[] { "name", "time", "location" }, new int[] { R.id.item_dialog_name,
							R.id.item_dialog_time, R.id.item_dialog_location });
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (dialog != null) {
						dialog.cancel();
					}
					//ToastUtils.toast(getActivity(), "点击的是第" + position + "个");
					MonitorMediaGroup group = groupList.get(position);
					LogUtils.i(TAG, group.toString());
					// 上传
					uploadMediaGroup(group, list);
				}
			});

			dialog = new Builder(VideoRecordActivity.this).setTitle("选择已有分组或新建一个分组来完成数据上传").setView(listView).setCancelable(false)
					.setPositiveButton("新建分组", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							createMediaGroup(list,groupList);
						}
					}).setNegativeButton("取消", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							hideLoading();
							mCamera.startPreview();
						}
					}).create();
			dialog.show();

		} else {
			createMediaGroup(list,groupList);
		}

	}

	/** 新建分组 */
	private void createMediaGroup(final List<MonitorMedia> list, final List<MonitorMediaGroup> groupList) {
		final EditText et = new EditText(VideoRecordActivity.this);
		final TextView tip=new TextView(VideoRecordActivity.this);
		tip.setTextColor(getResources().getColor(R.color.red));
		tip.setText("提示");
		tip.setVisibility(View.INVISIBLE);
		final LinearLayout layout=new LinearLayout(VideoRecordActivity.this);
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
//						LogUtils.i(tag,group.getRemark()+"---"+s.toString());
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

		Builder builder = new Builder(VideoRecordActivity.this);

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
							ToastUtils.toast(VideoRecordActivity.this,"此分组备注已存在，请更换！");
							createMediaGroup(list,groupList);
							return;
						}
					}
				}
				LogUtils.i(tag,"创建分组！");
				String data = "";
				ReqUploadMediaData req = new ReqUploadMediaData();
				User user = SPUtils.getUser(VideoRecordActivity.this);
				req.setId("");
				req.setUserId(user.getUserID());
				req.setUserName(user.getUserName());
				req.setOrgId(user.getOrgID());
				req.setOrgName(user.getOrgName());
				req.setCreateTime(TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
				req.setCreateAddr(BaseApplication.address);
				req.setLongitude(BaseApplication.longitude+"");
				req.setLatitude(BaseApplication.latitude+"");
				req.setMediaType(MonitorMediaGroup.TYPE_VIDEO);
				req.setRemark(et.getText().toString()+"");
				List<RspMedia> fileList = new ArrayList<RspMedia>();
				req.setFileList(fileList);
				data = JsonUtils.objectToJson(req, ReqUploadMediaData.class);
				LogUtils.i(TAG, "创建他组的数据是：" + data);
				showLoading(true);
				mEBikeRequestService.groupCreate(data);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				hideLoading();
				mCamera.startPreview();
			}
		});
		builder.create().show();
	}

	/** 新建分组 */
	private void confirmMediaUpload(final MonitorMediaGroup group, final List<MonitorMedia> list) {
		Builder builder = new Builder(VideoRecordActivity.this);

		builder.setTitle("提示");
		builder.setMessage("分组创建完成，是否立刻上传!");
		builder.setCancelable(false);
		builder.setPositiveButton("是", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				// 上传
				uploadMediaGroup(group, list);
			}
		});
		builder.setNegativeButton("否", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				hideLoading();
				mCamera.startPreview();
			}
		});
		builder.create().show();

	}

	/** 上传分组数据 */
	private void uploadMediaGroup(MonitorMediaGroup group, List<MonitorMedia> list) {
		// 存储组
		hideLoading();
		MonitorMediaGroupUpload uploadGroup = new MonitorMediaGroupUpload();
		uploadGroup.setId(UUID.randomUUID().toString());
		uploadGroup.setMediaGroup(group);
		uploadGroup.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING + "");
//		uploadGroup.setThumbnailPath(list.get(0).getThumbnailPath());
		FileUtils.saveMediaGroupThumbnail(this,list.get(0).getThumbnailPath(),uploadGroup.getId());
		uploadGroup.setRemoteDirectory(TimeUtils.getFormatNowTime("yyy-MM-dd"));//年月日为文件夹
		DBHelper.getInstance(VideoRecordActivity.this).insertMonitorMediaGroupUpload(uploadGroup);
		// 更改数据为在上传，并设置上传组
		for (MonitorMedia media : list) {
			media.setGroupUploadId(uploadGroup.getId());
			media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING + "");
		}
		DBHelper.getInstance(VideoRecordActivity.this).updateMonitorMediaList(list);
		// 调用FTP上传
		 FTPMediaUtil.mediaUpload(VideoRecordActivity.this, uploadGroup);
		ToastUtils.toast(VideoRecordActivity.this,"上传已成功提交，可到我的数据页面查看数据上传进度");
		 mCamera.startPreview();
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		LogUtils.i(TAG,"onSurfaceTextureAvailable");
		isCameraStateReady=false;
		CameraHelper.startPreviewCamera(this,mPreview,(ImageView) findViewById(R.id.photo_iv_focus),screenOrientation,
				CameraHelper.MEDIA_TYPE_VIDEO, new CameraOpenCallBack() {

					@Override
					public void openSuccess(Camera camera) {
						isCameraStateReady=true;
						// ToastUtils.toast(getApplicationContext(), "预览成功");
						try {
							// Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
							// with {@link SurfaceView}
							camera.setPreviewTexture(mPreview.getSurfaceTexture());
							camera.startPreview();//开启预览
							mCamera = camera;
						} catch (IOException e) {
							camera=null;
							LogUtils.e(TAG, "Surface texture is 不可用，或者大小不合适" + e.getMessage());
						}

					}

					@Override
					public void openFail(String message) {
						isCameraStateReady=true;
						ToastUtils.toast(getApplicationContext(), message);
					}
				});
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
		LogUtils.i(TAG,"onSurfaceTextureSizeChanged");

	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
		LogUtils.i(TAG,"onSurfaceTextureDestroyed");
		exit();//退出页面
		return false;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface) {

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if(isCameraStateReady){
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}