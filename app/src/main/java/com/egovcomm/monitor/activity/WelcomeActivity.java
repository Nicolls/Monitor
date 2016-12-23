package com.egovcomm.monitor.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.BaseApplication;
import com.egovcomm.monitor.common.LoginInfo;
import com.egovcomm.monitor.model.AppConfig;
import com.egovcomm.monitor.model.RspLogin;
import com.egovcomm.monitor.model.RspVersion;
import com.egovcomm.monitor.model.User;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.service.UpdateAPPService;
import com.egovcomm.monitor.utils.AppDownLoadAsyncTask;
import com.egovcomm.monitor.utils.AppUpdateUtils;
import com.egovcomm.monitor.utils.CommonUtil;
import com.egovcomm.monitor.utils.MyActivityManager;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.ToastUtils;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class WelcomeActivity extends BaseActivity {

	// private ImageView ib;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		// 解决安装完成后直接打开应用，按home键出现重启应用的问题
		if (!this.isTaskRoot()) { // 判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
			// 如果你就放在launcher Activity中话，这里可以直接return了
			Intent mainIntent = getIntent();
			String action = mainIntent.getAction();
			if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
				finish();
				return;// finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
			}
		}
		setContentView(R.layout.activity_welcome);

		/**在初始进入应用时做配置*/
		AppConfig config=new AppConfig();
		config.setLocaltionFailTipSpaceTime(5);//5秒
		config.setUploadLocationSpaceTime(8);//8秒
		SPUtils.setAppConfig(this,config);



		if(!BaseApplication.isUpdating){
			showLoading(true);
			mEBikeRequestService.updateMonitorApp(CommonUtil.getAppVersion(WelcomeActivity.this));
		}else{
			ToastUtils.toast(WelcomeActivity.this,"应用将自动退出，请等待应用更新完成后再使用");
			exitHandler.sendEmptyMessageDelayed(0,2000);
		}
	}

	private Handler exitHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			finish();
		}
	};


	/**登录*/
	private void requestLogin() {
		User user = SPUtils.getUser(this);
		if (user != null && !TextUtils.isEmpty(user.getUserAccount()) && !TextUtils.isEmpty(user.getPassword())) {// 自动登录
			showLoading(true);
			mEBikeRequestService.login(user.getUserAccount(), user.getPassword());
		} else {
			openActivity(SigninActivity.class, null, true);
		}
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		hideLoading();
		 if(id==RequestService.ID_UPDATEMONITORAPP){
			RspVersion version = (RspVersion) obj;
			 AppUpdateUtils.chargeUpdate(this, version, new AppUpdateUtils.AppUpdateChargeListener() {
				 @Override
				 public void chargeResult(RspVersion version,int operate) {
					 if(operate== AppUpdateUtils.AppUpdateChargeListener.OPERATE_NOT_UPDATE){//不需要更新
						requestLogin();
					 }
				 }
			 });
		} else if (id == RequestService.ID_LOGIN) {
			RspLogin login = (RspLogin) obj;
			LoginInfo.user = login.getData();
			LoginInfo.user.setPassword(SPUtils.getUser(getApplicationContext()).getPassword());
			SPUtils.setUser(getApplicationContext(), LoginInfo.user);
			if(TextUtils.equals("1", LoginInfo.user.getOrgAdminFlag())){//领导用户
				openActivity(MainManagerActivity.class, null, true);
			}else{//普通用户
				openActivity(MainUserActivity.class, null, true);
			}
			
		}
	}


	/** 请求出错 */
	@Override
	protected void requestError(int id,Object obj) {
		hideLoading();
		if(id==RequestService.ID_REQUEST_ERROR){
			openActivity(SigninActivity.class, null, true);
		}
		else if(id==RequestService.ID_UPDATEMONITORAPP){
			requestLogin();
		}else if(id==RequestService.ID_LOGIN){
			SPUtils.setUser(this, null);
			openActivity(SigninActivity.class, null, true);
		}
	}

}
