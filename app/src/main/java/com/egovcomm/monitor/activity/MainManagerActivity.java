package com.egovcomm.monitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.common.AppConstant;
import com.egovcomm.monitor.service.MonitorLocationService;
import com.egovcomm.monitor.utils.MapUtils;
import com.egovcomm.monitor.utils.SPUtils;

public class MainManagerActivity extends BaseActivity {

	private long exitTime =0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_manager);
		BaseApplication.status=BaseApplication.STATUS_ONLINE;
	}
	
	public void onData(View view){
		openActivity(GroupMediaListActivity.class, null, false);
	}
	
	public void onProfile(View view){
//		ToastUtils.toast(getApplicationContext(), "个人中心");
		openActivity(ProfileActivity.class,null,false,true,ProfileActivity.REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(ProfileActivity.REQUEST_CODE==requestCode&&resultCode==ProfileActivity.RESULT_CODE_EXIT){//需要注销
			SPUtils.cleanLocalData(this);
			openActivity(SigninActivity.class,null,true);
		}
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.status=BaseApplication.STATUS_OFFLINE;
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
