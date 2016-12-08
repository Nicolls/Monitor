/**
 * 
 */
package com.egovcomm.monitor.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.egovcomm.monitor.R;
import com.egovcomm.monitor.model.AppErrorResponse;
import com.egovcomm.monitor.model.AppResponse;
import com.egovcomm.monitor.net.RequestService;

/**
 * 
 * 错误处理工具
 * 
 * @author Nicolls
 *
 *         2015年7月22日
 */
public class ErrorUtils {
	/**
	 * 发现处理后无错误，则返回到context页面 监听器
	 * */
	public interface SuccessListener {
		void successCompleted(int id, Object obj);
	}
	
	/**
	 * 有错误也可以返回的监听
	 * */
	public interface ErrorListener{
		void errorCompleted(int id, Object obj);
	}

	public static void handle(Activity context, int id, Object obj, SuccessListener lis,ErrorListener errorLis) {
		if(context!=null){//先判断如果是无网络
			if(!CommonUtil.checkNetWork(context)){//网络未链接
				ToastUtils.toast(context, context.getString(R.string.net_unavailable));
				if(errorLis!=null){
					errorLis.errorCompleted(id,null);
				}
				return;
			}
		}
		
		if(context!=null){
			if (id == RequestService.ID_REQUEST_ERROR) {
//				ToastUtils.toast(context, context.getString(R.string.request_server_error));
				if(errorLis!=null){
					errorLis.errorCompleted(id,null);
				}
			} else if (obj instanceof AppErrorResponse) {// 登录或者请求出问题
				AppErrorResponse errorRes = (AppErrorResponse) obj;
				if (!TextUtils.equals(errorRes.getCode(), AppResponse.CODE_SUCCESS) ) {
					LogUtils.systemOut("登录或者请求出问题：" + errorRes.getText());
					if(errorLis!=null){
						errorLis.errorCompleted(id,obj);
					}
					else if(TextUtils.equals(errorRes.getCode(),AppResponse.CODE_UN_LOGIN)) {// 说明token失效
						MyActivityManager.getAppManager().reLogin(context, true);
					}
					ToastUtils.toast(context, errorRes.getErrorMsg());
//					alertDialog(context);
				} else {
					if(errorLis!=null){
						errorLis.errorCompleted(id,obj);
					}
				}

			} else if(!TextUtils.equals(((AppResponse) obj).getCode(),AppResponse.CODE_SUCCESS)) {// 返回码不是正确码，说明有错误
				if(errorLis!=null){
					errorLis.errorCompleted(id,obj);
				}
				ToastUtils.toast(context, ((AppResponse) obj).getErrorMsg());
			}
			else if (!context.isFinishing()) {// activity还存在，返回码是正确码。成功返回
				lis.successCompleted(id, obj);
			}else{
				LogUtils.systemOut("数据正常返回但是activity不存在了!");
			}
		}else{
			LogUtils.systemOut("contxt为空!");
		}
	}

}
