package com.egovcomm.monitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.common.LoginInfo;
import com.egovcomm.monitor.model.RspLogin;
import com.egovcomm.monitor.model.RspVersion;
import com.egovcomm.monitor.model.User;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.service.UpdateAPPService;
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
		// ib=(ImageView) findViewById(R.id.iv_welcome);
		// Animation anim=AnimationUtils.loadAnimation(this,
		// R.anim.welcome_fade_in_scale);
		// ib.startAnimation(anim);
		// mEBikeRequestService.version("Android",
		// CommonUtil.getAppVersion(this));//新版本检查
		initData();
	}

	private void initData() {
		User user = SPUtils.getUser(this);
		if (user != null && !TextUtils.isEmpty(user.getUserAccount()) && !TextUtils.isEmpty(user.getPassword())) {// 自动登录
			showLoading(true);
			mEBikeRequestService.login(user.getUserAccount(), user.getPassword());
		} else {
			showLoading(true);
			Handler handler=new Handler(){

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					hideLoading();
					openActivity(SigninActivity.class, null, true);
				}
				
			};
			handler.sendEmptyMessageDelayed(0, 2000);//添加一个延迟
		}
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		hideLoading();
		// if(id==EBikeRequestService.ID_VERSION){
		if (id == 0) {
			RspVersion version = (RspVersion) obj;
			chargeUpdate(version);
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

	/** 判断更新 */
	private void chargeUpdate(RspVersion version) {
		if (version != null) {
			String newest = version.getData().getNewest();
			final String url = version.getData().getUrl();
			// final String url =
			// "http://www.saner5.com/index.aspx?appId=1&appDownLoadCount=55&appDownloadUrl=upload/app/2014_07_17_17_44_48ear.apk";
			int m = Integer.parseInt(version.getData().getForce_update());
			boolean isForceUpdate = (m == 0 ? false : true);
		} else {
			initData();
		}
	}

	/** 版本更新 */
	private void updateApk(String downloadUrl, boolean isfinish) {
		// final String downloadUrl =
		// "http://www.saner5.com/index.aspx?appId=1&appDownLoadCount=55&appDownloadUrl=upload/app/2014_07_17_17_44_48ear.apk";
		ToastUtils.toast(WelcomeActivity.this, getString(R.string.start_download));
		Intent intent = new Intent(UpdateAPPService.class.getName());
		intent.putExtra(UpdateAPPService.INTENT_DOWNLOAD_URL, downloadUrl);
		WelcomeActivity.this.startService(intent);
		if (isfinish) {
			finish();
		}
	}

	/** 请求出错 */
	@Override
	protected void requestError(int id,Object obj) {
		hideLoading();
		SPUtils.setUser(this, null);
		openActivity(SigninActivity.class, null, true);
	}

}
