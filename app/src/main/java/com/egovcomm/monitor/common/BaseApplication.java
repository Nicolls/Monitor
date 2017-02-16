package com.egovcomm.monitor.common;

import com.egovcomm.monitor.ftp.FTPConnection;
import com.egovcomm.monitor.model.AppRequest;
import com.egovcomm.monitor.utils.FileUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.ToastUtils;

import android.app.Application;
import android.text.TextUtils;

/**
 * Application基类
 * */
public class BaseApplication extends Application{
	public static final int REQUEST_UPLOAD_LOCATION_SPACE_TIME=8;//上传位置时间间隔，秒
	public static final int FAIL_LOCATION_TIP_SPACE_TIME=5;//上传位置时间间隔，秒

	public static final int STATUS_OFFLINE=0;
	public static final int STATUS_ONLINE=1;
	public static final int STATUS_WORKING=2;
	//全局
	public static double longitude;
	public static double latitude;
	public static String address;
	public static int status=STATUS_OFFLINE;//状态，0离线，1在线，2，拍传
	public static String mediaId="";//正在拍传的媒体ID,只有当status=2时才会有值

	public static long uploadLocationSpaceTime=REQUEST_UPLOAD_LOCATION_SPACE_TIME;//上传位置间隔时间
	public static boolean isUpdating=false;//是否在更新状态
	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.setOpenLog(true);//是否打开日志调试功能
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

		//创建默认文件夹
		String originalPath=FileUtils.getAppStorageOriginalDirectoryPath(this);
		String thumbnailPath=FileUtils.getAppStorageThumbnailDirectoryPath(this);
		LogUtils.i("application","初始化application－源目录："+originalPath);
		LogUtils.i("application","初始化application－缩略图目录："+thumbnailPath);
		if(TextUtils.isEmpty(originalPath)||TextUtils.isEmpty(thumbnailPath)){
			ToastUtils.toast(this,"存储不可用，请检查设备状态!");
		}
	}

}
