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

public class MonitorMedia implements Parcelable {
	private static final String TAG=MonitorMedia.class.getSimpleName();
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public static final int DOWNLOAD_STATE_NONE=0;//默认
	public static final int DOWNLOAD_STATE_DOWNLOADING=1;//下载中
	public static final int DOWNLOAD_STATE_DOWNLOADED=2;//已下载
	public static final int DOWNLOAD_STATE_DOWNLOAD_FAIL=3;//下载失败
	private String id;
	//private MonitorMediaGroupUpload mediaGroupUpload;
	private String groupUploadId;
	private String userId;
	private String shootingLocation;
	private String remark;
	private String uploadTime;
	private String fileName;
	private String fileSize;
	private String path;
	private String fileSuffix;
	private String fileState;
	private String orientation;//多媒体的录制方向
	private String uploadState;
	private String mediaType;//媒体类型
	private String thumbnailPath;//缩略图
	private int progress;//进度条
	private int showCheck=0;//是否显示全选按钮0，不显示，1，显示
	private int check=0;//是否被选中 0未被选中，1选中
	private int downloadState=DOWNLOAD_STATE_NONE;//下载状态
	private String createTime;//创建时间
	private String time;//时间
	private String reason;//事由
	public MonitorMedia() {}
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
		//dest.writeParcelable(mediaGroupUpload, flags);
		dest.writeString(groupUploadId);
		dest.writeString(userId);
		dest.writeString(shootingLocation);
		dest.writeString(remark);
		dest.writeString(uploadTime);
		dest.writeString(fileName);
		dest.writeString(fileSize);
		dest.writeString(path);
		dest.writeString(fileSuffix);
		dest.writeString(fileState);
		dest.writeString(orientation);
		dest.writeString(uploadState);
		dest.writeString(mediaType);
		dest.writeString(thumbnailPath);
		dest.writeInt(progress);
		dest.writeInt(showCheck);
		dest.writeInt(check);
		dest.writeInt(downloadState);
		dest.writeString(createTime);
		dest.writeString(time);
		dest.writeString(reason);
	}

	public static final Creator<MonitorMedia> CREATOR = new Creator<MonitorMedia>() {
		public MonitorMedia createFromParcel(Parcel in) {
			return new MonitorMedia(in);
		}

		public MonitorMedia[] newArray(int size) {
			return new MonitorMedia[size];
		}
	};

	private MonitorMedia(Parcel in) {
		id = in.readString();
		//mediaGroupUpload = in.readParcelable(MonitorMediaGroupUpload.class.getClassLoader());
		groupUploadId=in.readString();
		userId=in.readString();
		shootingLocation=in.readString();
		remark=in.readString();
		uploadTime = in.readString();
		fileName = in.readString();
		fileSize = in.readString();
		path = in.readString();
		fileSuffix = in.readString();
		fileState = in.readString();
		orientation = in.readString();
		uploadState = in.readString();
		mediaType = in.readString();
		thumbnailPath = in.readString();
		progress = in.readInt();
		showCheck = in.readInt();
		check = in.readInt();
		downloadState=in.readInt();
		createTime= in.readString();
		time= in.readString();
		reason=in.readString();
	}
	
	@Override
	public String toString() {
		String result="id="+id+",userId="+userId+",groupUploadId="+groupUploadId+",shootingLocation="+shootingLocation+",mark="+remark+
				",uploadTime="+uploadTime+",time="+time+",reason="+reason+",fileName="+fileName+",fileSize="+fileSize+
				",path="+path+",fileSuffix="+fileSuffix+",fileState="+fileState+",orientation="+orientation+",uploadState="+uploadState+",thumbnailPath"+thumbnailPath+",mediaType="+mediaType+",showCheck="+showCheck+",check="+check+",createTime="+createTime;
		return result;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getShootingLocation() {
		return shootingLocation;
	}
	public void setShootingLocation(String shootingLocation) {
		this.shootingLocation = shootingLocation;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getFileSuffix() {
		return fileSuffix;
	}
	public void setFileSuffix(String fileSuffix) {
		this.fileSuffix = fileSuffix;
	}
	public String getFileState() {
		return fileState;
	}
	public void setFileState(String fileState) {
		this.fileState = fileState;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	public String getUploadState() {
		return uploadState;
	}
	public void setUploadState(String uploadState) {
		this.uploadState = uploadState;
	}
	public int getCheck() {
		return check;
	}
	public void setCheck(int check) {
		this.check = check;
	}
	public int getShowCheck() {
		return showCheck;
	}
	public void setShowCheck(int showCheck) {
		this.showCheck = showCheck;
	}
	public String getGroupUploadId() {
		return groupUploadId;
	}
	public void setGroupUploadId(String groupUploadId) {
		this.groupUploadId = groupUploadId;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public int getDownloadState() {
		return downloadState;
	}
	public void setDownloadState(int downloadState) {
		this.downloadState = downloadState;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
