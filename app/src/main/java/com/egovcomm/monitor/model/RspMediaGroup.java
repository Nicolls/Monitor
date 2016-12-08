package com.egovcomm.monitor.model;

import java.util.List;

/**
 * @author Nicolls
 * @Description 更新版本返回实体
 * @date 2015年10月31日
 */
public class RspMediaGroup extends MonitorMediaGroup {
	private static final long serialVersionUID = 1L;
	private List<RspMedia> mediaFiles;
	public List<RspMedia> getMediaFiles() {
		return mediaFiles;
	}
	public void setMediaFiles(List<RspMedia> mediaFiles) {
		this.mediaFiles = mediaFiles;
	}
}
