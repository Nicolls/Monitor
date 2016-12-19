package com.egovcomm.monitor.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.AppConstant;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.ftp.FTPMediaUtil;
import com.egovcomm.monitor.model.AppConfig;
import com.egovcomm.monitor.model.AppResponse;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.RspUploadLocation;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.TimeUtils;
import com.egovcomm.monitor.utils.ToastUtils;

import java.util.HashMap;

public class MainUserActivity extends BaseActivity {

	private long exitTime =0;

	private static final int REQUEST_SPACE_TIME=8;//上传位置时间间隔，秒
	private static final int FRAIL_LOCATION_TIP_SPACE_TIME=5;//上传位置时间间隔，秒

	private AMapLocationClient locationClient = null;
	private long toastTime =System.currentTimeMillis();
	private long requestTime =System.currentTimeMillis();

	private AppConfig appConfig=new AppConfig();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_user);
		FTPMediaUtil.startFTPService(getApplicationContext());// ftp服务
		BaseApplication.status = BaseApplication.STATUS_ONLINE;
		appConfig= SPUtils.getAppConfig(this);
		if(appConfig.getLocaltionFailTipSpaceTime()<=0){
			appConfig.setLocaltionFailTipSpaceTime(FRAIL_LOCATION_TIP_SPACE_TIME);
		}
		if(appConfig.getUploadLocationSpaceTime()<=0){
			appConfig.setUploadLocationSpaceTime(REQUEST_SPACE_TIME);
		}
		toastTime =System.currentTimeMillis();
		requestTime =System.currentTimeMillis();
		IntentFilter filter=new IntentFilter(AppConstant.BROAD_CAST_DESTROY_LOCATION);
		registerReceiver(receiver,filter);
		LogUtils.writeLogtoFile("mjk：","MainUserActivity--onCreate 开始定位");
		startLocation();//开始定位
	}

	public void onPhoto(View view) {
		openActivity(PhotoCaptureActivity.class, null, false);
	}

	public void onVedio(View view) {
		openActivity(VideoRecordActivity.class, null, false);
	}

	public void onData(View view) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("mediaType", MonitorMediaGroup.TYPE_PHOTO);
		openActivity(MediaDataActivity.class, map, false);
		// openActivity(MediaListActivity.class, null, false);
	}

	public void onProfile(View view) {
		// ToastUtils.toast(getApplicationContext(), "个人中心");
		openActivity(ProfileActivity.class,null,false);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		LogUtils.writeLogtoFile("mjk：","MainUserActivity--onDestroy");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > AppConstant.SPACE_TIME){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
				LogUtils.writeLogtoFile("mjk：","按两次返回键");
				exitApp();
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}

	/**注销退出应用*/
	private void exitApp(){
		LogUtils.writeLogtoFile("mjk：","进入exitApp方法");
		BaseApplication.status = BaseApplication.STATUS_OFFLINE;
		LogUtils.writeLogtoFile("mjk：","发送最后一次位置");
		uploadLocation();// 结束的时候发送最后一次位置
		LogUtils.writeLogtoFile("jk：","开始执行destroyLocation");
		try {
			new Thread(){

				@Override
				public void run() {
					super.run();
					destroyLocation();
				}
			}.start();
		}catch (Exception e){
			e.printStackTrace();
		}
		LogUtils.writeLogtoFile("jk：","执行destroyLocation完成");
		finish();
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
		mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);// 可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);// 可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);// 可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(3000);// 可选，设置定位间隔。默认为3秒
		mOption.setNeedAddress(true);// 可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(false);// 可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(false);// 可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);// 可选，
		mOption.setSensorEnable(false);// 可选，设置是否使用传感器。默认是false
		return mOption;
	}

	/**
	 * 定位监听
	 */
	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation loc) {
			if (null != loc&&loc.getErrorCode()==0) {//成功
				BaseApplication.longitude = loc.getLongitude();
				BaseApplication.latitude = loc.getLatitude();
				BaseApplication.address = loc.getAddress() + "";
				long currentTime=System.currentTimeMillis();
				if((currentTime- requestTime)>=appConfig.getUploadLocationSpaceTime()*1000){
					//上传位置
					requestTime =currentTime;
					uploadLocation();
				}
			} else {//失败
				long currentTime=System.currentTimeMillis();
				if((currentTime- toastTime)>=appConfig.getLocaltionFailTipSpaceTime()*1000){
					ToastUtils.toast(MainUserActivity.this,"获取位置信息失败");
					toastTime =currentTime;
				}
				LogUtils.i(tag, "定位失败，loc is null");
			}
		}


	};

	/**监听销毁位置的广播
	 **/
	private BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent!=null&&intent.getAction()!=null&&TextUtils.equals(AppConstant.BROAD_CAST_DESTROY_LOCATION, intent.getAction())){
				LogUtils.i(tag, "收到location广播");
				LogUtils.writeLogtoFile("mjk：","收到location广播");
				exitApp();
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
		// 初始化client
		locationClient = new AMapLocationClient(this.getApplicationContext());
		// 设置定位参数
		locationClient.setLocationOption(getDefaultOption());
		// 设置定位监听
		locationClient.setLocationListener(locationListener);
		// 启动定位
		locationClient.startLocation();
	}


	/**
	 * 销毁定位
	 *
	 * @since 2.8.0
	 * @author hongming.wang
	 *
	 */
	private void destroyLocation() {
		LogUtils.i(tag, "结束定位"+ TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
		}
		LogUtils.i(tag,"结束定位执行完成"+ TimeUtils.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		LogUtils.i("MonitorLocationService", "请求服务器上传位置数据返回");
		if (this != null && id == RequestService.ID_UPLOADLOCATION) {
			RspUploadLocation rsp = (RspUploadLocation) obj;
			if (TextUtils.equals(rsp.getCode(), AppResponse.CODE_SUCCESS)) {// 成功
				LogUtils.i("MonitorLocationService", "位置上传成功");
			}
		}
	}

	/** 用来通知fragment数据请求失败，子类，如果需要监听可以复写此方法 */
	@Override
	public void requestError(int id,Object obj) {

	}

	/** 上传位置信息 */
	private void uploadLocation() {
		if (mEBikeRequestService != null) {
			mEBikeRequestService.uploadLocation(BaseApplication.longitude,
					BaseApplication.latitude, BaseApplication.status);
		}
	}


}
