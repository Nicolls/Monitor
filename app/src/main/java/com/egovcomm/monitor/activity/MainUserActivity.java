package com.egovcomm.monitor.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.common.AppConstant;
import com.egovcomm.monitor.ftp.FTPMediaUtil;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.service.MonitorLocationService;
import com.egovcomm.monitor.utils.MapUtils;

public class MainUserActivity extends BaseActivity {

	private long exitTime =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_user);
		FTPMediaUtil.startFTPService(getApplicationContext());// ftp服务
		MapUtils.doLocationService(getApplicationContext(),
				MonitorLocationService.CODE_START);// 定位服务
		BaseApplication.status = BaseApplication.STATUS_ONLINE;
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
		openActivity(ProfileActivity.class, null, false);
	}

	@Override
	public void dateUpdate(int id, Object obj) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.status = BaseApplication.STATUS_OFFLINE;
		MapUtils.doLocationService(getApplicationContext(),
				MonitorLocationService.CODE_CLOSE);// 定位服务

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > AppConstant.SPACE_TIME){  
	            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
