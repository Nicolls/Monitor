/**
 * 
 */
package com.egovcomm.monitor.model;

import java.util.HashMap;

import com.egovcomm.monitor.common.AppConstant;
import com.egovcomm.monitor.net.BaseRequest;
import com.google.gson.Gson;

/**
 * 一个完整请求实体 包含请求服务器地址，服务器端口等
 * 
 * @author mengjk
 *
 *         2015年5月14日
 */
public class AppRequest extends BaseRequest {
	private static final long serialVersionUID = -1909392153140853242L;
	public static final String REQUEST_HEAD_HTTP = "http://";
	public static final String REQUEST_HEAD_HTTPS = "https://";
	private static Gson gson=new Gson();
	/** 请求服务器地址 */
	public static String requestHost = AppConstant.DEFAULT_HOST;
	/** 请求服务器端口 */
	public static int requestPort = AppConstant.DEFAULT_PORT;
	/** 协议类型 */
	private String httpHeadType = REQUEST_HEAD_HTTP;
	/**
	 * 构造方法
	 * @param methodUrl 传入的接口名称地址。必须以"/"开始
	 * */
	public AppRequest(String methodUrl) {
		String url = "";
		if(requestPort>0){
			url = httpHeadType + requestHost + ":" + requestPort + methodUrl;
		}else{
			url = httpHeadType + requestHost + methodUrl;
		}
		super.setReqeustURL(url);
	}
	public String getHttpHeadType() {
		return httpHeadType;
	}

	public void setHttpHeadType(String httpHeadType) {
		this.httpHeadType = httpHeadType;
	}
}
