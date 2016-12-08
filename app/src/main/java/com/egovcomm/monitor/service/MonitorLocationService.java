package com.egovcomm.monitor.service;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.common.BaseService;
import com.egovcomm.monitor.model.AppResponse;
import com.egovcomm.monitor.model.RspUploadLocation;
import com.egovcomm.monitor.net.DataUpdateListener;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.net.RequestServiceFactory;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.ToastUtils;

/** 应用更新Service */
public class MonitorLocationService extends BaseService implements
		DataUpdateListener {

	
	public static final String KEY_CODE = "KEY_CODE";
	public static final int CODE_START = 0;
	public static final int CODE_STOP = 1;
	public static final int CODE_CLOSE = 2;
	private static final int REQUEST_SPACE_TIME=8;//上传位置时间间隔，秒

	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = new AMapLocationClientOption();
	private RequestService mEBikeRequestService = null;
	private AMapLocation lastLocation;// 最近一次定位
	
	private RequestLocationThread mRequestLocationThread;
	private boolean isRequestRunning=false;//

	private MyBinder mBinder = new MyBinder();

	public class MyBinder extends Binder {
		public MonitorLocationService getService() {
			return MonitorLocationService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mEBikeRequestService = RequestServiceFactory.getInstance(
				getApplicationContext(), RequestServiceFactory.REQUEST_VOLLEY);
		mEBikeRequestService.setUptateListener(this);
		// 初始化定位
		initLocation();
		startRequestThread();
	}

	@Override
	public IBinder onBind(Intent intent) {
		LogUtils.i(tag, "in onBind");
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		LogUtils.i(tag, "in onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int code = intent.getIntExtra(KEY_CODE, CODE_START);
		if (code == CODE_START) {
			startLocation();
		} else if (code == CODE_STOP) {
			stopLocation();
		} else if (code == CODE_CLOSE) {
			stopSelf();// 把服务也停止掉
		} else {
			startLocation();
		}
		LogUtils.i(tag, code + "");

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRequestRunning=false;
		destroyLocation();
		mEBikeRequestService = null;
	}

	/**
	 * 初始化定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void initLocation() {
		// 初始化client
		locationClient = new AMapLocationClient(this.getApplicationContext());
		// 设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);
	}

	/**
	 * 默认的定位参数
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private AMapLocationClientOption getDefaultOption() {
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);// 可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);// 可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);// 可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(3000);// 可选，设置定位间隔。默认为3秒
		mOption.setNeedAddress(true);// 可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(false);// 可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(false);// 可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);// 可选，
		mOption.setSensorEnable(false);// 可选，设置是否使用传感器。默认是false
		return mOption;
	}

	/**
	 * 定位监听
	 */
	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation loc) {
			lastLocation = loc;
			if (null != loc) {
				BaseApplication.longitude = loc.getLongitude();
				BaseApplication.latitude = loc.getLatitude();
				BaseApplication.address = loc.getAddress() + "";
				// 解析定位结果
				// String result = MapUtils.getLocationStr(loc);
				// LogUtils.i(tag, result);
			} else {
				LogUtils.i(tag, "定位失败，loc is null");
			}
		}
	};


	/**
	 * 开始定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void startLocation() {
		LogUtils.i(tag, "开始定位");
		if (locationClient != null) {
			// 设置定位参数
			locationClient.setLocationOption(locationOption);
			// 启动定位
			locationClient.startLocation();
		} else {
			initLocation();
			startLocation();
		}
	}

	/**
	 * 停止定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void stopLocation() {
		LogUtils.i(tag, "停止定位");
		// 停止定位
		if (locationClient != null) {
			locationClient.stopLocation();
		}
	}

	/**
	 * 销毁定位
	 * 
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void destroyLocation() {
		LogUtils.i(tag, "结束定位");
		if (lastLocation != null) {// 结束的时候发送最后一次位置
			mEBikeRequestService.uploadLocation(lastLocation.getLongitude(),
					lastLocation.getLatitude(), BaseApplication.status);

		}
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

	@Override
	public void update(int id, Object obj) {
		LogUtils.i("MonitorLocationService", "请求服务器上传位置数据返回");
		if (this != null && id == RequestService.ID_UPLOADLOCATION) {
			RspUploadLocation rsp = (RspUploadLocation) obj;
			if (TextUtils.equals(rsp.getCode(), AppResponse.CODE_SUCCESS)) {// 成功
				LogUtils.i("MonitorLocationService", "位置上传成功");
			}
		}
	}
	
	private void startRequestThread(){
		isRequestRunning=true;
		if(mRequestLocationThread==null){
			mRequestLocationThread=new RequestLocationThread();
			mRequestLocationThread.start();
		}
	}
	
	/**上传位置线程*/
	private class RequestLocationThread extends Thread {
		public void run() {
			while (isRequestRunning) {
				try {
					Thread.sleep(REQUEST_SPACE_TIME*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(hander!=null){
					hander.sendEmptyMessage(0);
				}
			}
			synchronized (MonitorLocationService.this) {
				LogUtils.i(tag, "设置线程为空!");
				mRequestLocationThread=null;
			}
			
		}
	}
	
	
	private Handler hander=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			uploadLocation();
		}
		
	};
	
	/** 上传位置信息 */
	private void uploadLocation() {
		if (mEBikeRequestService != null) {
			mEBikeRequestService.uploadLocation(BaseApplication.longitude,
					BaseApplication.latitude, BaseApplication.status);
		}
	}

}
