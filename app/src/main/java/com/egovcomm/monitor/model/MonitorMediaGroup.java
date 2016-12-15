package com.egovcomm.monitor.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import com.egovcomm.monitor.utils.LogUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class MonitorMediaGroup implements Parcelable {
	public static final String TYPE_PHOTO="0";//图片
	public static final String TYPE_VIDEO="1";//视频
	private static final String TAG=MonitorMediaGroup.class.getSimpleName();
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private String id;
	private String userId;
	private String userName;
	private String orgId;
	private String orgName;
	private String createTime;
	private String createAddr;
	private String longitude;
	private String latitude;
	private String mediaType;
	private String remark;
	private String thumbnailPath;//缩略图
	private String uploadState;
	private int showCheck=0;//是否显示全选按钮0，不显示，1，显示
	private int check=0;//是否被选中 0未被选中，1选中
	private int progress=0;//进度条
	public MonitorMediaGroup() {}
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
		dest.writeString(userName);
		dest.writeString(orgId);
		dest.writeString(orgName);
		dest.writeString(createTime);
		dest.writeString(createAddr);
		dest.writeString(longitude);
		dest.writeString(latitude);
		dest.writeString(mediaType);
		dest.writeString(remark);
		dest.writeString(thumbnailPath);
		dest.writeString(uploadState);
		dest.writeInt(showCheck);
		dest.writeInt(check);
		dest.writeInt(progress);
	}

	public static final Creator<MonitorMediaGroup> CREATOR = new Creator<MonitorMediaGroup>() {
		public MonitorMediaGroup createFromParcel(Parcel in) {
			return new MonitorMediaGroup(in);
		}

		public MonitorMediaGroup[] newArray(int size) {
			return new MonitorMediaGroup[size];
		}
	};

	private MonitorMediaGroup(Parcel in) {
		id = in.readString();
		userId=in.readString();
		userName=in.readString();
		orgId = in.readString();
		orgName = in.readString();
		createTime = in.readString();
		createAddr = in.readString();
		longitude = in.readString();
		latitude = in.readString();
		mediaType = in.readString();
		remark = in.readString();
		thumbnailPath = in.readString();
		uploadState = in.readString();
		showCheck = in.readInt();
		check = in.readInt();
		progress= in.readInt();
	}
	
	@Override
	public String toString() {
		String result="id="+id+",userId="+userId+",userName="+userName+",orgId="+orgId+
				",orgName="+orgName+",createTime="+createTime+",createAddr="+createAddr+
				",longitude="+longitude+",latitude="+latitude+",mediaType="+mediaType+",remark="+remark+",thumbnailPath="+thumbnailPath
				;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateAddr() {
		return createAddr;
	}
	public void setCreateAddr(String createAddr) {
		this.createAddr = createAddr;
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
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public int getShowCheck() {
		return showCheck;
	}
	public void setShowCheck(int showCheck) {
		this.showCheck = showCheck;
	}
	public int getCheck() {
		return check;
	}
	public void setCheck(int check) {
		this.check = check;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public String getUploadState() {
		return uploadState;
	}

	public void setUploadState(String uploadState) {
		this.uploadState = uploadState;
	}
}
