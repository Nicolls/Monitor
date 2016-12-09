package com.egovcomm.monitor.activity;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.model.RspVersion;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.utils.AppUpdateUtils;
import com.egovcomm.monitor.utils.CommonUtil;
import com.egovcomm.monitor.utils.MyActivityManager;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.ToastUtils;

public class ProfileActivity extends BaseActivity implements OnClickListener{

	protected View mTopBar;
	protected ImageView mBack;
	protected TextView mTitle;
	protected TextView mRightTv;
	protected ImageView mRightIv;
	private TextView mTvName;
	private TextView mTvEmail;
	private TextView mTvPhone;
	private TextView mTvOrg;
	private TextView mTvOrgHib;
	private TextView mTvVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		mTopBar=findViewById(R.id.nav_top_bar);
		mBack = (ImageView) findViewById(R.id.top_bar_left);
		mTitle = (TextView) findViewById(R.id.top_bar_title);
		mRightTv = (TextView) findViewById(R.id.top_bar_right_text);
		mRightIv = (ImageView) findViewById(R.id.top_bar_right_icon);
		
		mTvName=(TextView) findViewById(R.id.profile_name);
		mTvEmail=(TextView) findViewById(R.id.profile_email);
		mTvPhone=(TextView) findViewById(R.id.profile_phone);
		mTvOrg=(TextView) findViewById(R.id.profile_org);
		mTvOrgHib=(TextView) findViewById(R.id.profile_org_hib);
		mTvVersion=(TextView) findViewById(R.id.profile_version);
		mRightTv.setVisibility(View.GONE);
		mRightIv.setVisibility(View.GONE);
		mTitle.setText("个人中心");
		mBack.setOnClickListener(this);
		mRightTv.setOnClickListener(this);
		mRightIv.setOnClickListener(this);
		initData();
	}
	
	private void initData(){
		mTvVersion.setText("当前应用版本号："+CommonUtil.getAppVersion(getApplicationContext()));
		mTvName.setText("人员姓名："+SPUtils.getUser(getApplicationContext()).getUserName());
		mTvEmail.setText("邮箱："+SPUtils.getUser(getApplicationContext()).getEmail());
		mTvPhone.setText("电话："+SPUtils.getUser(getApplicationContext()).getPhone());
		mTvOrg.setText("职位："+SPUtils.getUser(getApplicationContext()).getOrgName());
		mTvOrgHib.setText("架构："+SPUtils.getUser(getApplicationContext()).getOrgHiberarchy());

	}
	
	public void onResetPassword(View view){
		finish();
	}
	
	public void onSetting(View view){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("needReLogin", true);
		openActivity(SettingActivity.class, map, false);
	}
	
	public void onCheckUpdate(View view){
		mEBikeRequestService.updateMonitorApp(CommonUtil.getAppVersion(this));
	}
	
	public void onExit(View view){
		MyActivityManager.getAppManager().reLogin(getApplicationContext(), true);;
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		if(id== RequestService.ID_UPDATEMONITORAPP){
			RspVersion version = (RspVersion) obj;
			AppUpdateUtils.chargeUpdate(this, version, new AppUpdateUtils.AppUpdateChargeListener() {
				@Override
				public void chargeResult(RspVersion version,boolean isNeedToUpdate) {
					if(!isNeedToUpdate){//不需要更新
						if(version!=null&&version.getData()!=null&&!version.getData().isCanUpdate()){
							ToastUtils.toast(getApplicationContext(), "当前为最新版，无须更新");
						}
					}
				}
			});
		}
	}

	/** 请求出错 */
	@Override
	protected void requestError(int id,Object obj) {
		hideLoading();
		if(id==RequestService.ID_UPDATEMONITORAPP){
			ToastUtils.toast(getApplicationContext(), "未檢查到版本信息");
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.top_bar_left:
			finish();
			break;
		case R.id.top_bar_right_text:
			break;
		case R.id.top_bar_right_icon:
			break;

		default:
			break;
		}
	}
	
}
