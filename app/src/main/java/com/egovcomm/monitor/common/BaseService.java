package com.egovcomm.monitor.common;

import com.egovcomm.monitor.utils.LogUtils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * service基类
 * */
public class BaseService extends Service{

	protected String tag = "BaseService";
	@Override
	public void onCreate() {
		tag = this.getClass().getSimpleName();
		super.onCreate();
		LogUtils.i(tag,"onCreate");
	}
	@Override
	public IBinder onBind(Intent intent) {
		LogUtils.i(tag,"onBind");
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		LogUtils.i(tag, "onStart");
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.i(tag,"onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		LogUtils.i(tag,"onDestroy");
		super.onDestroy();
	}
	@Override
	public boolean onUnbind(Intent intent) {
		LogUtils.i(tag,"onUnbind");
		return super.onUnbind(intent);
	}
	@Override
	public void onRebind(Intent intent) {
		LogUtils.i(tag, "onRebind");
		super.onRebind(intent);
	}

	
}
