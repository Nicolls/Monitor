package com.egovcomm.monitor.model;


public class RspMedia extends MonitorMedia{
	private String delFlag;
	private String ext;
	private String mediaId;//这是组ID，没什么用
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
}

