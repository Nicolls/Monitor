package com.egovcomm.monitor.service;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;

import com.egovcomm.monitor.common.BaseService;
import com.egovcomm.monitor.utils.AppDownLoadAsyncTask;
import com.egovcomm.monitor.utils.LogUtils;

/** 应用更新Service */
public class UpdateAPPService extends BaseService {

	public static final String INTENT_DOWNLOAD_URL = "_INTENT_DOWNLOAD_URL";
	public static final String INTENT_DOWNLOAD_APP_MD5_CODE = "_INTENT_DOWNLOAD_APP_MD5_CODE";
	private static final String TAG = "UpdateAPPService";
	private static AppDownloadListener mUpdateAppListener;
	private Notification mNotification;
	private String appDownLoadUrl;
	private String appMd5Code = "";

	public static void setUpdateAppListener(AppDownloadListener updateAppListener) {
		mUpdateAppListener = updateAppListener;
	}

	/** 应用更新完成监听器 */
	public interface AppDownloadListener {
		void updateAppCompleted(String message);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (null != intent && intent.hasExtra(INTENT_DOWNLOAD_URL)) {
			appDownLoadUrl = intent.getStringExtra(INTENT_DOWNLOAD_URL);
			appMd5Code = intent.getStringExtra(INTENT_DOWNLOAD_APP_MD5_CODE);
			LogUtils.i(TAG, "客户端更新包下载地址" + appDownLoadUrl + "－－客户端更新包md5验证码＝" + appMd5Code);
			if (mNotification == null) {
				startDownLoad();
			} else {
				LogUtils.i(TAG, "应用仍在更新中");
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/** 开始下载 */
	private void startDownLoad() {

		new AppDownLoadAsyncTask(this, mUpdateAppListener).execute(appDownLoadUrl);
	}
}
