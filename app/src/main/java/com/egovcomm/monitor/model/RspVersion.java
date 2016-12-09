package com.egovcomm.monitor.model;

/**
 * @author Nicolls
 * @Description 更新版本返回实体
 * @date 2015年10月31日
 */
public class RspVersion extends AppResponse {
	private static final long serialVersionUID = 1L;
	private ResultData data;
	public ResultData getData() {
		return data;
	}
	public void setData(ResultData data) {
		this.data = data;
	}
	public class ResultData{

		private boolean canUpdate;
		private String deviceType;
		private boolean forceUpdate;
		private String installedVersion;
		private String latestVersioUri;
		private String latestVersion;

		public boolean isCanUpdate() {
			return canUpdate;
		}

		public void setCanUpdate(boolean canUpdate) {
			this.canUpdate = canUpdate;
		}

		public String getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}

		public boolean isForceUpdate() {
			return forceUpdate;
		}

		public void setForceUpdate(boolean forceUpdate) {
			this.forceUpdate = forceUpdate;
		}

		public String getInstalledVersion() {
			return installedVersion;
		}

		public void setInstalledVersion(String installedVersion) {
			this.installedVersion = installedVersion;
		}

		public String getLatestVersioUri() {
			return latestVersioUri;
		}

		public void setLatestVersioUri(String latestVersioUri) {
			this.latestVersioUri = latestVersioUri;
		}

		public String getLatestVersion() {
			return latestVersion;
		}

		public void setLatestVersion(String latestVersion) {
			this.latestVersion = latestVersion;
		}

	}
}
