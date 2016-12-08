package com.egovcomm.monitor.model;

/**
 * @author Nicolls
 * @Description 更新版本返回实体
 * @date 2015年10月31日
 */
public class RspUploadMedia extends AppResponse {
	private static final long serialVersionUID = 1L;
	private Object data;
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
}
