package com.egovcomm.monitor.model;

import java.util.List;

public class ReqUploadMediaData extends MonitorMediaGroup {
	private static final long serialVersionUID = 1L;
	private List<RspMedia> fileList;
	public List<RspMedia> getFileList() {
		return fileList;
	}
	public void setFileList(List<RspMedia> fileList) {
		this.fileList = fileList;
	}
}
