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

package com.egovcomm.monitor.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

/**
 * Camera related utilities.
 */
public class CameraHelper {

	private static final String TAG=CameraHelper.class.getSimpleName();
	
	public static final int SCREEN_PORTRAIT = 0;
	public static final int SCREEN_LANDSCAPE = 1;
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	/**获取打开摄像头，的回调*/
	public interface CameraOpenCallBack{
		void openSuccess(Camera camera);
		void openFail(String message);
	}
	
	/**获取打开摄像头，的回调*/
	public interface VedioRecordCallBack{
		void startSuccess(MediaRecorder mediaRecorder, String videoPath);
		void startFail(String message);
	}

	/**获取最佳大小*/
	public static Camera.Size getOptimalSize(String type,List<Camera.Size> sizes, int w, int h) {
		LogUtils.i(TAG, "类型："+type+" 寻找：w-h:"+w+"-"+h);
		Camera.Size optimalSize=null;
		Collections.sort(sizes, new Comparator<Camera.Size>() {

			@Override
			public int compare(Size lhs, Size rhs) {
				int result=0;
				if(lhs.width>lhs.height&&rhs.width>rhs.height){//判断是手机是直屏幕的情况下
					if(lhs.width>rhs.width){
						result=1;
					}else if(lhs.height>rhs.height){
						result=1;
					}else{
						result=-1;
					}
				}else{

				}
				return result;
			}
		});

		for(int i=0;i<sizes.size();i++){
			LogUtils.i(TAG, "排序：w-h:"+sizes.get(i).width+"-"+sizes.get(i).height);
		}

		boolean isExit=false;
		LogUtils.i(TAG, "寻找：w-h:"+w+"-"+h);
		for(int i=0;i<sizes.size();i++){
			LogUtils.i(TAG, "遍历：w-h:"+sizes.get(i).width+"-"+sizes.get(i).height);
			if(!isExit){
				if(sizes.get(i).width>=w){
					optimalSize=sizes.get(i);
					LogUtils.i(TAG, "找到合适的宽度 ：w-h:"+optimalSize.width+"-"+optimalSize.height);
					isExit=true;
					if(sizes.get(i).height>=h){
						LogUtils.i(TAG, "找到合适的高度 ：w-h:"+optimalSize.width+"-"+optimalSize.height);
						break;
					}
				}
			}else if(sizes.get(i).height>=h){
				optimalSize=sizes.get(i);
				LogUtils.i(TAG, "找到合适的高度 ：w-h:"+optimalSize.width+"-"+optimalSize.height);
				break;
			}
		}
		LogUtils.i(TAG, type+"最合适的宽度-高度分别是："+optimalSize.width+"-"+optimalSize.height);

		return optimalSize;
	}

	/**找出最合适的大小*/
	public static Camera.Size getOptimalSize2(String type,List<Camera.Size> sizes, int w, int h) {
		LogUtils.i(TAG, "类型："+type+" 寻找：w-h:"+w+"-"+h);
		Camera.Size optimalSize=null;
		Collections.sort(sizes, new Comparator<Camera.Size>() {

			@Override
			public int compare(Size lhs, Size rhs) {
				int result=0;
				if(lhs.width>lhs.height&&rhs.width>rhs.height){//判断是手机是直屏幕的情况下
					if(lhs.width>rhs.width){
						result=-1;
					}else if(lhs.height>rhs.height){
						result=-1;
					}else{
						result=1;
					}
				}else{

				}
				return result;
			}
		});

		for(int i=0;i<sizes.size();i++){
			LogUtils.i(TAG, "排序：w-h:"+sizes.get(i).width+"-"+sizes.get(i).height);
		}

		boolean isExit=false;
		LogUtils.i(TAG, "寻找：w-h:"+w+"-"+h);
		for(int i=0;i<sizes.size();i++){
			LogUtils.i(TAG, "遍历：w-h:"+sizes.get(i).width+"-"+sizes.get(i).height);
			if(!isExit){
				if(sizes.get(i).width<=w){
					optimalSize=sizes.get(i);
					LogUtils.i(TAG, "找到合适的宽度 ：w-h:"+optimalSize.width+"-"+optimalSize.height);
					isExit=true;
					if(sizes.get(i).height<=h){
						LogUtils.i(TAG, "找到合适的高度 ：w-h:"+optimalSize.width+"-"+optimalSize.height);
						break;
					}
				}
			}else if(sizes.get(i).height<=h){
				optimalSize=sizes.get(i);
				LogUtils.i(TAG, "找到合适的高度 ：w-h:"+optimalSize.width+"-"+optimalSize.height);
				break;
			}
		}
		LogUtils.i(TAG, type+"最合适的宽度-高度分别是："+optimalSize.width+"-"+optimalSize.height);

		return optimalSize;
	}


	/**
	 * @return the default camera on the device. Return null if there is no
	 *         camera on the device.
	 */
	public static Camera getDefaultCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			LogUtils.i(TAG, ""+e.getMessage());
		}
		return c; // returns null if camera is unavailable
	}

	/**获取一个打开的摄像头，开始预览画面 
	 * 
	 * size是view的大小，不管怎么样size.x=长，size.y=宽
	 * */
	public static void startPreviewCamera(final Context context,final Point size,final int screenOrientation,final int mediaType,final CameraOpenCallBack callBack){
		new AsyncTask<String, Integer, Camera>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}
			
			@Override
			protected Camera doInBackground(String... params) {
				Camera camera = CameraHelper.getDefaultCameraInstance();
				
				if(camera!=null){
					//如果是已在预览，则停止 预览
					try {
						camera.stopPreview();
					} catch (Exception e) {
						LogUtils.e(CameraHelper.class.getName(), "无法停止预览，摄像头可能并不在预览状态!");
					}
					
					if(screenOrientation==SCREEN_LANDSCAPE){
						camera.setDisplayOrientation(0);
						Log.i(TAG, "水平");
					}else if(screenOrientation==SCREEN_PORTRAIT){
						Log.i(TAG, "垂直");
						camera.setDisplayOrientation(90);
					}
					//用于处理摄像头看到的画面
					Camera.Parameters parameters = camera.getParameters();

			        List<Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
			        List<Size> mSupportedPictureSizes = parameters.getSupportedPictureSizes();
					List<Size> mSupportedVideoSizes=parameters.getSupportedVideoSizes();
			        Size previewSize=null;
			        Size pictureSize=null;
					Size videoSize=null;
			        LogUtils.i("view的宽度－高度是：", size.x+"-"+size.y);

			        //不管屏幕的方向如何，CameraSize默认是水平来看，所以cameraSize中的值都是 长－高 的。如1920-1080 1280-720
			        //当垂直的时候，应该拿屏幕的高去跟size的长对比，当水平时，则拿屏幕的宽和size的长对比，返回的值是cameraSize，所以值也是长－高
			        if(screenOrientation==SCREEN_LANDSCAPE){
			        	previewSize = CameraHelper.getOptimalSize("预览",mSupportedPreviewSizes,
								size.x, size.y);
				        pictureSize = CameraHelper.getOptimalSize("图像",mSupportedPictureSizes,
								size.x, size.y);
						videoSize = CameraHelper.getOptimalSize("视频录制",mSupportedVideoSizes,
								size.x, size.y);

					}else {
						previewSize = CameraHelper.getOptimalSize("预览",mSupportedPreviewSizes,
								size.y, size.x);
				        pictureSize = CameraHelper.getOptimalSize("图像",mSupportedPictureSizes,
								size.y, size.x);
						videoSize = CameraHelper.getOptimalSize("视频录制",mSupportedVideoSizes,
								size.y, size.x);

					}
			        //预览参数图片大小等值也是按默认的值给的，所以无论屏幕如何放置，给的参数都是长－高，也就是CameraSize，所以只要把算得的Size设置给它就可以了
			        parameters.setPreviewSize(previewSize.width, previewSize.height);
//					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启方便测试
			       
			        
			        // likewise for the camera object itself.
			        List<String> focusModes = parameters.getSupportedFocusModes();
			        if(mediaType==MEDIA_TYPE_IMAGE){
						parameters.setPictureSize(pictureSize.width,pictureSize.height);
			        	parameters.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
			        	if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
			        		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
						}
			        }else if(mediaType==MEDIA_TYPE_VIDEO){
			        	if(focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)){
			        		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
						}
			        	
			        }
			        
			        camera.setParameters(parameters);
					//把设置相机SurfaceView的代码移到UI上层来实现
//			        try {
//			                // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
//			                // with {@link SurfaceView}
//			                camera.setPreviewTexture(preview.getSurfaceTexture());
//			                camera.startPreview();//开启预览
//			        } catch (IOException e) {
//			        	camera=null;
//			        	LogUtils.e(TAG, "Surface texture is 不可用，或者大小不合适" + e.getMessage());
//			        }
				}
				return camera;
			}

			@Override
			protected void onPostExecute(Camera result) {
				super.onPostExecute(result);
				if(callBack!=null){
					if(result==null){
						callBack.openFail("启动录制失败!");
					}else{
						callBack.openSuccess(result);
					}
				}
			}
		}.execute();
	}
	
	public static void startVideoRecord(final Context context,final Camera camera,final int screenOrientation,final Point size,final VedioRecordCallBack callBack){
		new AsyncTask<Void, Integer, MediaRecorder>(){

			private String path="";
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			@Override
			protected MediaRecorder doInBackground(Void... params) {
		        try {
		        	camera.unlock();
				} catch (Exception e) {
					LogUtils.i(TAG, "摄像头未锁定，或者摄像头无法解锁");
				}
				// BEGIN_INCLUDE (configure_media_recorder)
				MediaRecorder mMediaRecorder = new MediaRecorder();
		        //这里设置是视频的画面，跟照相机的没关系 ，这里的画面方向是用于保存视频用的,上传的时候是平的
				if(screenOrientation==SCREEN_LANDSCAPE){
					mMediaRecorder.setOrientationHint(0);
				}else if(screenOrientation==SCREEN_PORTRAIT){
					mMediaRecorder.setOrientationHint(90);
				}
		        // Step 1: Unlock and set camera to MediaRecorder
		        
		        mMediaRecorder.setCamera(camera);

		        // Step 2: Set sources
		        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC );
		        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		        
		        // Use the same size for recording profile.
				CamcorderProfile profile=null;
				if(size.x<480){
					LogUtils.i(TAG, "使用低于480的值");
					profile = CamcorderProfile.get(CamcorderProfile.QUALITY_QVGA);
				}else if(size.x>480&&size.x<720){
					LogUtils.i(TAG, "使用>=480 <720的值");
					profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
				}else if(size.x>=720&&size.x<1080){
					LogUtils.i(TAG, "使用>=720 <1080的值");
					profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
				}else{
					LogUtils.i(TAG, "使用>=1080的值");
					profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
				}

		        LogUtils.i(TAG, "传进来视频的宽度－高度是："+size.x+"-"+size.y);
		        LogUtils.i(TAG, "录制视频的宽度－高度是："+profile.videoFrameWidth+"-"+profile.videoFrameHeight);

		         //用于处理摄像头看到的画面
		      //视频的大小是按默认的值给的，所以无论屏幕如何放置，给的参数都是长－高，也就是CameraSize，所以只要把算得的Size设置给它就可以了
//		        profile.videoFrameWidth = optimalSize.width;
//		        profile.videoFrameHeight =optimalSize.height;
		        mMediaRecorder.setProfile(profile);
		        //特别的设置，这里的设置能使最终
		        //video的大小决定视频分辨率，EncodingBitRate决定了存储的视频数据的清晰度，值越大越清晰，
//		        mMediaRecorder.setVideoEncodingBitRate(1024*1024);
		        //mMediaRecorder.setVideoSize(800,480);
		        
				//mMediaRecorder.setVideoFrameRate(20);
				//mMediaRecorder.setAudioEncodingBitRate();
				//mMediaRecorder.setAudioChannels()
				//mMediaRecorder.setAudioSamplingRate()


		        // Step 4: Set output file
		        path=getOutputMediaFile(
		                CameraHelper.MEDIA_TYPE_VIDEO).toString();
		        mMediaRecorder.setOutputFile(path);
		        // END_INCLUDE (configure_media_recorder)

		        // Step 5: Prepare configured MediaRecorder
		        try {
		            mMediaRecorder.prepare();
		            mMediaRecorder.start();
		        } catch (IllegalStateException e) {
		            LogUtils.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
		            if (mMediaRecorder != null) {
			            // clear recorder configuration
		            	mMediaRecorder.reset();
			            // release the recorder object
		            	mMediaRecorder.release();
		            	mMediaRecorder = null;
			            // Lock camera for later use i.e taking it back from MediaRecorder.
			            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
			            //mCamera.lock();
			        }
		            return null;
		        } catch (IOException e) {
		            LogUtils.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
		            if (mMediaRecorder != null) {
			            // clear recorder configuration
		            	mMediaRecorder.reset();
			            // release the recorder object
		            	mMediaRecorder.release();
		            	mMediaRecorder = null;
			            // Lock camera for later use i.e taking it back from MediaRecorder.
			            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
			            //mCamera.lock();
			        }
		            return null;
		        }
				return mMediaRecorder;
			}

			@Override
			protected void onPostExecute(MediaRecorder result) {
				super.onPostExecute(result);
				if(callBack!=null){
					if(result==null){
						callBack.startFail("启动录制失败!");
					}else{
						callBack.startSuccess(result,path);
					}
				}
			}
		}.execute();
	}

	/**
	 * @return the default rear/back facing camera on the device. Returns null
	 *         if camera is not available.
	 */
	public static Camera getDefaultBackFacingCameraInstance() {
		return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
	}

	/**
	 * @return the default front facing camera on the device. Returns null if
	 *         camera is not available.
	 */
	public static Camera getDefaultFrontFacingCameraInstance() {
		return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
	}

	/**
	 *
	 * @param position
	 *            Physical position of the camera i.e
	 *            Camera.CameraInfo.CAMERA_FACING_FRONT or
	 *            Camera.CameraInfo.CAMERA_FACING_BACK.
	 * @return the default camera on the device. Returns null if camera is not
	 *         available.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private static Camera getDefaultCamera(int position) {
		// Find the total number of cameras available
		int mNumberOfCameras = Camera.getNumberOfCameras();

		// Find the ID of the back-facing ("default") camera
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int i = 0; i < mNumberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == position) {
				return Camera.open(i);

			}
		}

		return null;
	}

	/**
	 * Creates a media file in the {@code Environment.DIRECTORY_PICTURES}
	 * directory. The directory is persistent and available to other
	 * applications like gallery.
	 *
	 * @param type
	 *            Media type. Can be video or image.
	 * @return A file object pointing to the newly created file.
	 */
	public static File getOutputMediaFile(int type) {
		File mediaStorageDir=new File(FileUtils.getAppStorageOriginalDirectoryPath());
		// Create a media file name
		//String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String timeStamp =UUID.randomUUID().toString();
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator  + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator  + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}
	
	public static File saveData(byte[] data,int mediaType,int screenOrientation){
		String path="";
		File saveFile=null;
		saveFile=getOutputMediaFile(mediaType);
		path=saveFile.getAbsolutePath();
		if(mediaType==MEDIA_TYPE_IMAGE){
			Bitmap b = null;
			if(null != data){
				b = BitmapFactory.decodeByteArray(data, 0, data.length);
			}
			if(null != b){
				if(screenOrientation==SCREEN_PORTRAIT){
					b = getRotateBitmap(b, 90.0f);
				}
				try {
					FileOutputStream fos=new FileOutputStream(saveFile);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();
					
				} catch (Exception e) {
					LogUtils.e(TAG, "保存图片数据的时候有问题");
				}
			}
			
			
		}else if(mediaType==MEDIA_TYPE_VIDEO){
			
		}
		
		return saveFile;
	}
	
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
		Matrix matrix = new Matrix();
		matrix.postRotate((float)rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
		return rotaBitmap;
	}

}
