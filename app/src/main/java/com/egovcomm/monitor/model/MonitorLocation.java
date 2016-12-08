package com.egovcomm.monitor.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.egovcomm.monitor.utils.LogUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class MonitorLocation implements Parcelable {
	private static final String TAG=MonitorLocation.class.getSimpleName();
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private long id = -1;
	private String userId;
	private String departmentId;
	private String createTime;
	private double longitude;
	private double latitude;
	private String mark;
	private int state;
	public MonitorLocation() {}
	public String formatTime(Date date) {
		String result = "";
		if (date != null) {
			result = format.format(date);
		}
		return result;
	}

	public Date parseTime(String time) {
		Date date = null;
		if (!TextUtils.isEmpty(time)) {
			try {
				date = format.parse(time);
			} catch (ParseException e) {
				LogUtils.e(TAG, e.getMessage());
			}
		}
		return date;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(userId);
		dest.writeString(departmentId);
		dest.writeString(createTime);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeString(mark);
		dest.writeInt(state);
	}

	public static final Creator<MonitorLocation> CREATOR = new Creator<MonitorLocation>() {
		public MonitorLocation createFromParcel(Parcel in) {
			return new MonitorLocation(in);
		}

		public MonitorLocation[] newArray(int size) {
			return new MonitorLocation[size];
		}
	};

	private MonitorLocation(Parcel in) {
		id = in.readLong();
		userId=in.readString();
		departmentId = in.readString();
		createTime = in.readString();
		longitude = in.readDouble();
		latitude = in.readDouble();
		mark = in.readString();
		state = in.readInt();
	}
	
	@Override
	public String toString() {
		String result="id="+id+",userId="+userId+",departmentId="+departmentId+
				",createTime="+createTime+
				",longitude="+longitude+",latitude="+latitude+",mark="+mark
				+",state="+state;
		return result;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

}
