package com.egovcomm.monitor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.utils.ToastUtils;

public class TestActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_user);
	}
	

	public void onPhoto(View view){
		openActivity(PhotoCaptureActivity.class, null, true);
	}
	
	public void onVedio(View view){
		openActivity(VideoRecordActivity.class, null, true);
	}
	
	public void onData(View view){
		openActivity(MediaDataActivity.class, null, true);
	}
	
	public void onUpload(View view){
		ToastUtils.toast(getApplicationContext(), "上传");
		
	}

	public void onGetData(View view){
		ToastUtils.toast(getApplicationContext(), "获取数据");
		
	}
	
	public void onProfile(View view){
		ToastUtils.toast(getApplicationContext(), "个人中心");
		openActivity(ProfileActivity.class, null, true);
	}
	
	public void onUploadLocation(View view){
		mEBikeRequestService.uploadLocation(23.8978, 35.8739, "",1,"");
	}
	
	@Override
	public void dateUpdate(int id, Object obj) {
		
	}
	
	
	
}
