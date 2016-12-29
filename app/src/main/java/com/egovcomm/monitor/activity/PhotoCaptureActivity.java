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
import java.util.HashMap;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.db.MonitorTable;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.CameraHelper;
import com.egovcomm.monitor.utils.CommonUtil;
import com.egovcomm.monitor.utils.CommonViewUtils;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.TimeUtils;
import com.egovcomm.monitor.utils.ToastUtils;
import com.egovcomm.monitor.utils.CameraHelper.CameraOpenCallBack;

/**
 *  This activity uses the camera/camcorder as the A/V source for the {@link android.media.MediaRecorder} API.
 *  A {@link TextureView} is used as the camera preview which limits the code to API 14+. This
 *  can be easily replaced with a {@link android.view.SurfaceView} to run on older devices.
 */
public class PhotoCaptureActivity extends BaseActivity implements TextureView.SurfaceTextureListener{

    private Camera mCamera;
    private TextureView mPreview;

    private static final String TAG = "Recorder";
    private ImageButton captureButton;
    private int screenOrientation=0;//屏幕方向
	private boolean isCameraStateReady=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
        BaseApplication.status=BaseApplication.STATUS_WORKING;
        mPreview = (TextureView) findViewById(R.id.surface_view);
		mPreview.setSurfaceTextureListener(this);
        captureButton = (ImageButton) findViewById(R.id.photo_btn_record);
        
        int mCurrentOrientation = getResources().getConfiguration().orientation;
        if ( mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT ) {
            LogUtils.i("info", "portrait"); // 竖屏
            screenOrientation=CameraHelper.SCREEN_PORTRAIT;
        } else if ( mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE ) {
        	LogUtils.i("info", "landscape"); // 横屏
        	screenOrientation=CameraHelper.SCREEN_LANDSCAPE;
        }
        
    }

    
    @SuppressWarnings("deprecation")
	public void onCapture(View view){
    	if(mCamera!=null&&isCameraStateReady){
			isCameraStateReady=false;
    		mCamera.takePicture(new ShutterCallback() {
				
				@Override
				public void onShutter() {
					
				}
			}, null, new PictureCallback() {
				
				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					File file=CameraHelper.saveData(data, CameraHelper.MEDIA_TYPE_IMAGE, CameraHelper.SCREEN_PORTRAIT);
					String thumpFilePath=FileUtils.saveMediaThumbnail(getApplicationContext(), file.getAbsolutePath(), MonitorMediaGroup.TYPE_PHOTO+"",true);
					mCamera.stopPreview();
					mCamera.startPreview();
					if(file!=null){
						MonitorMedia media=new MonitorMedia();
						media.setId(FileUtils.getFileNameNoEx(file.getName()));
						media.setFileName(file.getName());
						media.setFileSize(file.length()+"");//保存字节数
						media.setFileState(0+"");
						media.setFileSuffix(FileUtils.getExtensionName(file.getName()));
						media.setGroupUploadId(MonitorTable.NULL_VALUE);//0表示没有组
						media.setOrientation(screenOrientation+"");
						media.setPath(file.getAbsolutePath());
						media.setRemark("");
						media.setShootingLocation(BaseApplication.address==null?"":BaseApplication.address);
						media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UN_UPLOAD+"");
						media.setUploadTime("");
						media.setUserId(SPUtils.getUser(PhotoCaptureActivity.this).getUserID());
						media.setMediaType(MonitorMediaGroup.TYPE_PHOTO);
						media.setThumbnailPath(thumpFilePath);
						media.setCreateTime(TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
						media.setTime(TimeUtils.getFormatNowTime("yyyy-MM-dd HH:mm")+":00");
						media.setReason("");

						DBHelper.getInstance(PhotoCaptureActivity.this).insertMonitorMedia(media);
						LogUtils.i(tag, "数据库插入图片文件数据成功");
					}else{
						LogUtils.e(tag, "保存文件失败");
					}
					isCameraStateReady=true;
				}
			});
    	}
    	
    }

    /**返回*/
    public void onBack(View view){
    	finish();
    }
    
    /**我的数据*/
    public void onData(View view){
		if(isCameraStateReady){
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("mediaType",MonitorMediaGroup.TYPE_PHOTO);
			openActivity(MediaDataActivity.class, map, false);
		}
    }
    
    


	@Override
	public void dateUpdate(int id, Object obj) {
		// TODO Auto-generated method stub
		
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.status=BaseApplication.STATUS_ONLINE;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
		LogUtils.i(TAG,"onSurfaceTextureAvailable");
		Point p= new Point(mPreview.getWidth(),mPreview.getHeight());
		isCameraStateReady=false;
		CameraHelper.startPreviewCamera(this, mPreview,(ImageView) findViewById(R.id.photo_iv_focus),screenOrientation, CameraHelper.MEDIA_TYPE_IMAGE, new CameraOpenCallBack() {

			@Override
			public void openSuccess(Camera camera) {
				isCameraStateReady=true;
				//ToastUtils.toast(getApplicationContext(), "预览成功");
				try {
					// Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
					// with {@link SurfaceView}
					camera.setPreviewTexture(mPreview.getSurfaceTexture());
					camera.startPreview();//开启预览
					mCamera=camera;
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
		if(mCamera!=null){
			try {
				mCamera.unlock();
			}catch (Exception e){
				LogUtils.e(TAG,"解锁相机"+e.getMessage()+"");
			}
			mCamera.release();
			mCamera = null;
		}
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