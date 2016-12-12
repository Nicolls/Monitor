package com.egovcomm.monitor.activity;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.LoginInfo;
import com.egovcomm.monitor.model.RspLogin;
import com.egovcomm.monitor.model.User;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.ToastUtils;

public class SigninActivity extends BaseActivity {

	private View mViewSetting;
	private EditText mEtUserAccount;
	private EditText mEtPassword;
	private TextView mTvLogin;
	private int signinType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin);
		initView();
		showLoading(false);
		initData();
	}
	
	private void initView(){
		mTvLogin= (TextView) findViewById(R.id.sign_tv_login);
		mTvLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onSignin();
			}
		});
		mViewSetting =findViewById(R.id.sigin_setting);
		mEtUserAccount=(EditText) findViewById(R.id.sign_et_account);
		mEtPassword=(EditText) findViewById(R.id.sign_et_password);
		mViewSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HashMap<String,Object> map=new HashMap<String,Object>();
				map.put("needReLogin", false);
				openActivity(SettingActivity.class, map, false);
			}
		});
	}
	/**初始化数据*/
	private void initData(){
		User user=SPUtils.getUser(this);
		if(user!=null){
			mEtUserAccount.setText(user.getUserAccount());
			mEtPassword.setText(user.getPassword());
		}
	}

	public void onSignin() {
		String account=mEtUserAccount.getText().toString();
		String password=mEtPassword.getText().toString();
		if(TextUtils.isEmpty(account)){
			ToastUtils.toast(this, getString(R.string.email_can_not_be_null));
			return;
		}
		if(TextUtils.isEmpty(password)){
			ToastUtils.toast(this, getString(R.string.password_can_not_be_null));
			return;
		}
		showLoading(true);
		mEBikeRequestService.login(account,password);
	}


	@Override
	public void dateUpdate(int id, Object obj) {//不管是哪种登录回来的都是一样的
		hideLoading();
		if(id==RequestService.ID_LOGIN){
			RspLogin login=(RspLogin) obj;
			LoginInfo.user=login.getData();
			String account=mEtUserAccount.getText().toString();
			String password=mEtPassword.getText().toString();
			LoginInfo.user.setPassword(password);
			SPUtils.setUser(this, LoginInfo.user);
			if(TextUtils.equals("1", LoginInfo.user.getOrgAdminFlag())){//领导用户
				openActivity(MainManagerActivity.class, null, true);
			}else{//普通用户
				openActivity(MainUserActivity.class, null, true);
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		hideLoading();
	}
	/**请求出错*/
	@Override
	protected void requestError(int id,Object obj){
		if (id == RequestService.ID_REQUEST_ERROR) {
			ToastUtils.toast(this, getString(R.string.request_server_error));
		}else{
			ToastUtils.toast(this, getString(R.string.login_fail));
		}
		hideLoading();
	}



}
