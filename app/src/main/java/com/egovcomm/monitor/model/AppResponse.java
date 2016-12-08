package com.egovcomm.monitor.model;

import com.egovcomm.monitor.net.BaseResponse;

/**
 * 请求网络返回实体基类
 * 
 * @author mengjk
 *
 *         2015年5月14日
 */

public class AppResponse extends BaseResponse {
	/**成功*/
	public static final String CODE_SUCCESS = "1";
	/**失败*/
	public static final String CODE_FAIL = "0";
	/**未登录*/
	public static final String CODE_UN_LOGIN = "2";
	private static final long serialVersionUID = 273842578285709923L;
	/** code: 为0000时，为正确的业务返回，非0000时，为异常情况，参照具体接⼝异常说明，此时可以根据需要，向客户端提⽰msg的信息 */
	private String code;
	/** 请求返回的信息，如果code为0000，则错误信息为空，非0000则会返回相应的错误信息 */
	private String errorMsg;
	
	private boolean success;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

}
