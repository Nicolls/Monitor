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

public class MonitorMediaGroupUpload implements Parcelable {
	private static final String TAG=MonitorMediaGroupUpload.class.getSimpleName();
	/**获取全部*/
	public static final String UPLOAD_STATE_ALL="-1";
	/**获取全部*/
	public static final String UPLOAD_STATE_UN_UPLOAD="0";
	/**获取正在上传的*/
	public static final String UPLOAD_STATE_UPLOADING="1";
	
	//完成的包括，已上传，上传失败，已取消。上传失败跟已取消可以再资上传
	
	/**获取已上传的*/
	public static final String UPLOAD_STATE_UPLOADED="2";
	/**获取上传失败的*/
	public static final String UPLOAD_STATE_UPLOAD_FAIL="3";
	/**获取已取消的*/
	public static final String UPLOAD_STATE_UPLOAD_CANCEL="4";
	
	/**获取服务器上返回的*/
	public static final String UPLOAD_STATE_SERVER_DATA="5";
	
	static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	private String id;
	private MonitorMediaGroup mediaGroup;
	private String uploadState;
	private String thumbnailPath;//缩略图
	private String remoteDirectory;//用于存储远程服务器上，此组所属的目录
	private int showCheck=0;//是否显示全选按钮0，不显示，1，显示
	private int check=0;//是否被选中 0未被选中，1选中
	private int progress=0;//进度条
	public MonitorMediaGroupUpload() {}
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
		dest.writeParcelable(mediaGroup, flags);
		dest.writeString(uploadState);
		dest.writeString(thumbnailPath);
		dest.writeString(remoteDirectory);
		dest.writeInt(showCheck);
		dest.writeInt(check);
		dest.writeInt(progress);
	}

	public static final Creator<MonitorMediaGroupUpload> CREATOR = new Creator<MonitorMediaGroupUpload>() {
		public MonitorMediaGroupUpload createFromParcel(Parcel in) {
			return new MonitorMediaGroupUpload(in);
		}

		public MonitorMediaGroupUpload[] newArray(int size) {
			return new MonitorMediaGroupUpload[size];
		}
	};

	private MonitorMediaGroupUpload(Parcel in) {
		id = in.readString();
		mediaGroup=in.readParcelable(MonitorMediaGroup.class.getClassLoader());
		uploadState = in.readString();
		thumbnailPath = in.readString();
		remoteDirectory = in.readString();
		showCheck = in.readInt();
		check = in.readInt();
		progress= in.readInt();
	}
	
	@Override
	public String toString() {
		String result="id="+id+",mediaGroup="+mediaGroup.toString()+",uploadState="+uploadState+",thumbnailPath="+thumbnailPath+",remoteDirectory="+remoteDirectory+",showCheck="+showCheck+",check="+check+",progress="+progress;
		return result;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUploadState() {
		return uploadState;
	}
	public void setUploadState(String uploadState) {
		this.uploadState = uploadState;
	}
	public MonitorMediaGroup getMediaGroup() {
		return mediaGroup;
	}
	public void setMediaGroup(MonitorMediaGroup mediaGroup) {
		this.mediaGroup = mediaGroup;
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
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getRemoteDirectory() {
		return remoteDirectory;
	}
	public void setRemoteDirectory(String remoteDirectory) {
		this.remoteDirectory = remoteDirectory;
	}

}
