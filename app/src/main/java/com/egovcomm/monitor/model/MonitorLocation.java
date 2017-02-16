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
	private String id ="";
	private String userId;
	private String mediaId;
	private String createTime;
	private String longitude;
	private String latitude;
	private String remark;
	private String state;
	private String address;
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
		dest.writeString(id);
		dest.writeString(userId);
		dest.writeString(mediaId);
		dest.writeString(createTime);
		dest.writeString(longitude);
		dest.writeString(latitude);
		dest.writeString(remark);
		dest.writeString(state);
		dest.writeString(address);
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
		id = in.readString();
		userId=in.readString();
		mediaId = in.readString();
		createTime = in.readString();
		longitude = in.readString();
		latitude = in.readString();
		remark = in.readString();
		state = in.readString();
		address = in.readString();
	}
	
	@Override
	public String toString() {
		String result="id="+id+",userId="+userId+",mediaId="+mediaId+
				",createTime="+createTime+
				",longitude="+longitude+",latitude="+latitude+",remark="+remark
				+",state="+state+",address="+address;
		return result;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMediaId() {
		return mediaId;
	}

	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}

}
