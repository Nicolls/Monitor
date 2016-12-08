package com.egovcomm.monitor.model;

/**
 * @author Nicolls
 * @Description 用户登录返回实体
 * @date 2015年10月31日
 */
public class RspLogin extends AppResponse {
	private static final long serialVersionUID = 1L;
	private User data;
	public User getData() {
		return data;
	}
	public void setData(User data) {
		this.data = data;
	}
}
