package com.egovcomm.monitor.common;

import com.egovcomm.monitor.ftp.FTPConnection;
import com.egovcomm.monitor.model.AppRequest;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;

import android.app.Application;

/**
 * Application基类
 * */
public class BaseApplication extends Application{
	public static final int STATUS_OFFLINE=0;
	public static final int STATUS_ONLINE=1;
	public static final int STATUS_WORKING=2;
	//全局
	public static double longitude;
	public static double latitude;
	public static String address;
	public static int status=STATUS_OFFLINE;//状态，0离线，1在线，2，拍传

	public static boolean isUpdating=false;//是否在更新状态
	@Override
	public void onCreate() {
		super.onCreate();
		initData();
	}

	private void initData(){
		isUpdating=false;
		LogUtils.i("BaseApplication", "初始化数据!!!!!");
		String serverHost=SPUtils.getServerHost(getApplicationContext());
		int serverPort=SPUtils.getServerPort(getApplicationContext());
		String ftpHost=SPUtils.getFtpServerHost(getApplicationContext());
		int ftpPort=SPUtils.getFtpServerPort(getApplicationContext());
		String ftpUserName=SPUtils.getFtpUserName(getApplicationContext());
		String ftpPassword=SPUtils.getFtpPassword(getApplicationContext());
		AppRequest.requestHost=serverHost;
		AppRequest.requestPort=serverPort;
		FTPConnection.host=ftpHost;
		FTPConnection.port=ftpPort;
		FTPConnection.userName=ftpUserName;
		FTPConnection.password=ftpPassword;
	}

}
