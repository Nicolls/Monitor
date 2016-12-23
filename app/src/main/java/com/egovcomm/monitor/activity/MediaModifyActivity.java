package com.egovcomm.monitor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.utils.ToastUtils;

/**
 * 视频
 *
 * @author 胡汉三
 *
 */
public class MediaModifyActivity extends BaseActivity implements View.OnClickListener{

	private MonitorMedia media;
	private EditText mEtTitle;
	private EditText mEtTime;
	private EditText mEtLocation;
	private EditText mEtReason;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_media_modify);
		media = getIntent().getParcelableExtra("media");
		mEtTitle= (EditText) findViewById(R.id.modify_et_title);
		mEtTime= (EditText) findViewById(R.id.modify_et_time);
		mEtTime.setFocusable(false);
		mEtTime.setOnClickListener(this);
		mEtLocation= (EditText) findViewById(R.id.modify_et_location);
		mEtReason= (EditText) findViewById(R.id.modify_et_reason);
		initData();
	}

	private void initData(){
		mEtTitle.setText(media.getRemark());
		mEtTime.setText(media.getTime());
		mEtLocation.setText(media.getShootingLocation());
		mEtReason.setText(media.getReason());
	}
	public void onConfirm(View view){
		media.setRemark(mEtTitle.getText().toString());
		media.setTime(mEtTime.getText().toString());
		media.setShootingLocation(mEtLocation.getText().toString());
		media.setReason(mEtReason.getText().toString());
		DBHelper.getInstance(this).updateMonitorMedia(media);
		finish();
	}
	public void onCancel(View view){
		finish();
	}


	@Override
	public void dateUpdate(int id, Object obj) {

	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.modify_et_time){//改时间
			ToastUtils.toast(getApplicationContext(),"时间");
		}
	}
}
