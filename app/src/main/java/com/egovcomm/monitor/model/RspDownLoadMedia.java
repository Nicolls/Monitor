package com.egovcomm.monitor.model;

/**
 * @author Nicolls
 * @Description 用户登录返回实体
 * @date 2015年10月31日
 */
public class RspDownLoadMedia extends AppResponse {
	private static final long serialVersionUID = 1L;
	private MonitorMedia data;
	public MonitorMedia getData() {
		return data;
	}
	public void setData(MonitorMedia data) {
		this.data = data;
	}
}
