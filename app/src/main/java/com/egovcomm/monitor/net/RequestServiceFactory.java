/**
 * 
 */
package com.egovcomm.monitor.net;

import android.content.Context;

/**
 * 获取与服务器请求的工厂类 单例模式，请求通过getInstance方法来获取请求实例
 * 
 * @author Nicolls
 */
public class RequestServiceFactory {
	public static final int REQUEST_APACHE = 0;
	public static final int REQUEST_VOLLEY = 1;

	private RequestServiceFactory() {
	}

	public static RequestService getInstance(Context context) {
		return getInstance(context, REQUEST_APACHE);
	}

	public static RequestService getInstance(Context context, int requestMode) {
		RequestService mRequestService = null;
		switch (requestMode) {
		case REQUEST_APACHE:

			break;
		case REQUEST_VOLLEY:
			mRequestService = new RequestServiceVolleyImpl(context);
			break;
		default:
			mRequestService = new RequestServiceVolleyImpl(context);
			break;
		}
		return mRequestService;
	}
}
