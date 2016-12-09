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
import java.util.HashMap;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;

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
public class PhotoCaptureActivity extends BaseActivity {

    private Camera mCamera;
    private TextureView mPreview;

    private static final String TAG = "Recorder";
    private ImageButton captureButton;
    private int screenOrientation=0;//屏幕方向
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_capture);
        BaseApplication.status=BaseApplication.STATUS_WORKING;
        mPreview = (TextureView) findViewById(R.id.surface_view);
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
    	if(mCamera!=null){
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
						media.setShootingLocation(BaseApplication.address);
						media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UN_UPLOAD+"");
						media.setUploadTime("");
						media.setUserId(SPUtils.getUser(PhotoCaptureActivity.this).getUserID());
						media.setMediaType(MonitorMediaGroup.TYPE_PHOTO);
						media.setThumbnailPath(thumpFilePath);
						media.setCreateTime(TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));

						DBHelper.getInstance(PhotoCaptureActivity.this).insertMonitorMedia(media);
						LogUtils.i(tag, "数据库插入图片文件数据成功");
					}else{
						LogUtils.e(tag, "保存文件失败");
					}
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
    	HashMap<String,Object> map=new HashMap<String,Object>();
    	map.put("mediaType",MonitorMediaGroup.TYPE_PHOTO);
    	openActivity(MediaDataActivity.class, map, false);
    }
    
    
    
    @Override
    protected void onPause() {
        super.onPause();
        if(mCamera!=null){
        	mCamera.release();
            mCamera = null;
        }
    }


	@Override
	public void dateUpdate(int id, Object obj) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onResume() {
		super.onResume();
		Point p= CommonViewUtils.getDisplaySize(PhotoCaptureActivity.this);
		 CameraHelper.startPreviewCamera(this, mPreview, p,screenOrientation, CameraHelper.MEDIA_TYPE_IMAGE, new CameraOpenCallBack() {
				
				@Override
				public void openSuccess(Camera camera) {
					//ToastUtils.toast(getApplicationContext(), "预览成功");
					mCamera=camera;
					
				}
				
				@Override
				public void openFail(String message) {
					ToastUtils.toast(getApplicationContext(), message);
				}
			});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.status=BaseApplication.STATUS_ONLINE;
	}
}