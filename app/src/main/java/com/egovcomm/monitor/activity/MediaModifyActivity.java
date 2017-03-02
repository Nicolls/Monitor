package com.egovcomm.monitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import com.egovcomm.monitor.R;
import com.egovcomm.monitor.common.BaseActivity;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.utils.ToastUtils;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;

/**
 * 视频
 *
 *
 */
public class MediaModifyActivity extends BaseActivity implements View.OnClickListener,DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

	private MonitorMedia media;
	private EditText mEtTitle;
	private EditText mEtTime;
	private EditText mEtLocation;
	private EditText mEtReason;

	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";

	private boolean isVibrate=true;

	final Calendar calendar = Calendar.getInstance();
	final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), isVibrate);
	final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

	private String dateString="";//年月日
	private String timeString="";//时分
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
		initData(savedInstanceState);
	}

	private void initData(Bundle savedInstanceState){
		mEtTitle.setText(media.getRemark());
		mEtTime.setText(media.getTime());
		mEtLocation.setText(media.getShootingLocation());
		mEtReason.setText(media.getReason());

		if (savedInstanceState != null) {
			DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
			if (dpd != null) {
				dpd.setOnDateSetListener(this);
			}

			TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
			if (tpd != null) {
				tpd.setOnTimeSetListener(this);
			}
		}
	}
	public void onConfirm(View view){
		media.setRemark(mEtTitle.getText().toString());
		media.setTime(mEtTime.getText().toString());
		media.setShootingLocation(mEtLocation.getText().toString());
		media.setReason(mEtReason.getText().toString());
		DBHelper.getInstance(this).updateMonitorMedia(media);
		Intent intent=new Intent();
		intent.putExtra("media",media);
		setResult(RESULT_OK,intent);
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
		if(v.getId()==R.id.modify_et_time){//改时间,先改年月日，不要自动消失
//			ToastUtils.toast(getApplicationContext(),"时间");
			datePickerDialog.setVibrate(isVibrate);
			datePickerDialog.setYearRange(1985, 2028);
			datePickerDialog.setCloseOnSingleTapDay(false);
			datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
		}
	}

	@Override
	public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
//		ToastUtils.toast(MediaModifyActivity.this, "new date:" + year + "-" + month + "-" + day);
		String yearStr=year+"";
		String monthStr=month+"";
		String dayStr=day+"";
		if(month<10){
			monthStr="0"+month;
		}
		if(day<10){
			dayStr="0"+day;
		}
		dateString=yearStr+"-"+monthStr+"-"+dayStr;
		//设置完日期，设置时间
		timePickerDialog.setVibrate(isVibrate);
		timePickerDialog.setCloseOnSingleTapMinute(false);
		timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		String hourStr=hourOfDay+"";
		String minuteStr=minute+"";
		if(hourOfDay<10){
			hourStr="0"+hourOfDay;
		}
		if(minute<10){
			minuteStr="0"+minute;
		}
		timeString=hourStr+":"+minuteStr;
		String timeValue=dateString+" "+timeString+":00";
		media.setTime(timeValue);
		mEtTime.setText(timeValue);
//		ToastUtils.toast(MediaModifyActivity.this, "new time:" + hourOfDay + "-" + minute);
	}
}
