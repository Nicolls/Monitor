package com.egovcomm.monitor.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.ftp.FTPConnection;
import com.egovcomm.monitor.model.AppRequest;
import com.egovcomm.monitor.model.User;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.utils.CommonUtil;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.MyActivityManager;
import com.egovcomm.monitor.utils.SPUtils;
import com.egovcomm.monitor.utils.ToastUtils;

public class SettingActivity extends BaseActivity {

	private EditText mEtServer;
	private EditText mEtPort;
	private EditText mEtFtpServer;
	private EditText mEtFtpPort;

	private EditText mEtFtpUserName;
	private EditText mEtFtpPassword;

	protected View mTopBar;
	protected ImageView mBack;
	protected TextView mTitle;
	protected TextView mRightTv;
	protected ImageView mRightIv;

	private boolean needReLogin=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		needReLogin=getIntent().getBooleanExtra("needReLogin", false);
		mTopBar = findViewById(R.id.nav_top_bar);
		mBack = (ImageView) findViewById(R.id.top_bar_left);
		mTitle = (TextView) findViewById(R.id.top_bar_title);
		mRightTv = (TextView) findViewById(R.id.top_bar_right_text);
		mRightIv = (ImageView) findViewById(R.id.top_bar_right_icon);
		mBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		mTitle.setText("应用配置");
		mRightTv.setVisibility(View.GONE);
		mRightIv.setVisibility(View.GONE);
		mEtServer = (EditText) findViewById(R.id.sign_et_server);
		mEtPort = (EditText) findViewById(R.id.sign_et_port);

		mEtFtpServer = (EditText) findViewById(R.id.sign_et_ftp_server);
		mEtFtpPort = (EditText) findViewById(R.id.sign_et_ftp_port);

		mEtFtpUserName = (EditText) findViewById(R.id.sign_et_ftp_username);
		mEtFtpPassword = (EditText) findViewById(R.id.sign_et_ftp_password);

		mEtServer.setText(SPUtils.getServerHost(getApplicationContext()));
		mEtPort.setText(SPUtils.getServerPort(getApplicationContext()) + "");

		mEtFtpServer.setText(SPUtils.getFtpServerHost(getApplicationContext()));
		mEtFtpPort.setText(SPUtils.getFtpServerPort(getApplicationContext())
				+ "");

		mEtFtpUserName.setText(SPUtils.getFtpUserName(getApplicationContext()));
		mEtFtpPassword.setText(SPUtils.getFtpPassword(getApplicationContext())
				+ "");

	}

	public void onConfirm(View view) {
		String serverHost = mEtServer.getText().toString();
		String serverPort = mEtPort.getText().toString();

		String ftpHost = mEtFtpServer.getText().toString();
		String ftpPort = mEtFtpPort.getText().toString();

		String ftpUserName = mEtFtpUserName.getText().toString();
		String ftpPassword = mEtFtpPassword.getText().toString();

		if (TextUtils.isEmpty(serverHost)) {
			ToastUtils.toast(getApplicationContext(), "服务器地址不能为空");
			return;
		}
		if (TextUtils.isEmpty(serverPort)) {
			ToastUtils.toast(getApplicationContext(), "端口号不能为空");
			return;
		}
		if (TextUtils.isEmpty(ftpHost)) {
			ToastUtils.toast(getApplicationContext(), "ftp服务器地址不能为空");
			return;
		}
		if (TextUtils.isEmpty(ftpPort)) {
			ToastUtils.toast(getApplicationContext(), "ftp端口号不能为空");
			return;
		}
		if (TextUtils.isEmpty(ftpUserName)) {
			ToastUtils.toast(getApplicationContext(), "ftp用户名不能为空");
			return;
		}
		if (TextUtils.isEmpty(ftpPassword)) {
			ToastUtils.toast(getApplicationContext(), "ftp用户密码不能为空");
			return;
		}

		if (!CommonUtil.isNumber(serverPort)) {
			ToastUtils.toast(getApplicationContext(), "端口号只能是数字");
			return;
		}

		if (!CommonUtil.isNumber(ftpPort)) {
			ToastUtils.toast(getApplicationContext(), "端口号只能是数字");
			return;
		}

		if(needReLogin){
			//需要重新登录则请求一次登录
			AppRequest.requestHost=serverHost;
			AppRequest.requestPort=Integer.parseInt(serverPort);
			FTPConnection.host=ftpHost;
			FTPConnection.port=Integer.parseInt(ftpPort);
			FTPConnection.userName=ftpUserName;
			FTPConnection.password=ftpPassword;
			User user = SPUtils.getUser(this);
			if (user != null && !TextUtils.isEmpty(user.getUserAccount()) && !TextUtils.isEmpty(user.getPassword())) {// 自动登录
				showLoading(false);
				ToastUtils.toast(getApplicationContext(), "验证服务器参数...");
				mEBikeRequestService.login(user.getUserAccount(), user.getPassword());
			} else {
				MyActivityManager.getAppManager().reLogin(SettingActivity.this, false);
			}
			
		}else{
			saveData();
			ToastUtils.toast(getApplicationContext(), "参数设置成功!");
			finish();
		}
	}
	/**存数据*/
	private void saveData(){
		String serverHost = mEtServer.getText().toString();
		String serverPort = mEtPort.getText().toString();

		String ftpHost = mEtFtpServer.getText().toString();
		String ftpPort = mEtFtpPort.getText().toString();

		String ftpUserName = mEtFtpUserName.getText().toString();
		String ftpPassword = mEtFtpPassword.getText().toString();
		try {
			SPUtils.setServerHost(getApplicationContext(), serverHost);
			SPUtils.setServerPort(getApplicationContext(),
					Integer.parseInt(serverPort));
			SPUtils.setFtpServerHost(getApplicationContext(), ftpHost);
			SPUtils.setFtpServerPort(getApplicationContext(),
					Integer.parseInt(ftpPort));
			
			SPUtils.setFtpUserName(getApplicationContext(), ftpUserName);
			SPUtils.setFtpPassword(getApplicationContext(), ftpPassword);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onCancel(View view) {
		finish();
	}

	@Override
	public void dateUpdate(int id, Object obj) {
		hideLoading();
		if(id==RequestService.ID_LOGIN){
			saveData();
			ToastUtils.toast(getApplicationContext(), "参数设置成功!");
			finish();
		}
	}

	/**请求错误会调用这个方法*/
	@Override
	protected void requestError(int id,Object obj){
		ToastUtils.toast(getApplicationContext(), "服务器验证失败,请重新设置!");
		LogUtils.i("SettingActivity", "验证失败重新使用本地参数数据!");
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
	}
}
