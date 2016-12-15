/**
 * 
 */
package com.egovcomm.monitor.net;

import java.lang.reflect.Type;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.egovcomm.monitor.model.AppRequest;
import com.egovcomm.monitor.model.AppResponse;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.RspDownLoadMedia;
import com.egovcomm.monitor.model.RspGroupList;
import com.egovcomm.monitor.model.RspLogin;
import com.egovcomm.monitor.model.RspLogout;
import com.egovcomm.monitor.model.RspMedia;
import com.egovcomm.monitor.model.RspUploadLocation;
import com.egovcomm.monitor.model.RspUploadMedia;
import com.egovcomm.monitor.model.RspVersion;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.MediaDownLoadAsyncTask;
import com.egovcomm.monitor.utils.MediaDownLoadAsyncTask.MediaDownloadListener;

/**
 * 通过volley框架来实现的与服务器交互接口的请求类
 * 
 * @author Nicolls
 * 
 */
public class RequestServiceVolleyImpl implements RequestService {
	private static final String TAG=RequestServiceVolleyImpl.class.getSimpleName();
	protected DataUpdateListener dataUpdateListener;
	private Context context;

	public RequestServiceVolleyImpl(Context context) {
		this.context = context;
	}

	@Override
	public void setUptateListener(DataUpdateListener dataUpdateListener) {
		this.dataUpdateListener = dataUpdateListener;
	}

	private void dataNotify(int what, Object obj) {
		if (dataUpdateListener != null) {
			dataUpdateListener.update(what, obj);
		}
	}

	/** 集成发送方法 */
	private <T extends AppResponse> void sendRequest(AppRequest ebRequest, final int methodId, Type type) {
		ebRequest.setCharset("GBK");//返回数据的格式化为GBK编码
		VolleyGsonRequest<T> request = new VolleyGsonRequest<T>(context, ebRequest, type, new Response.Listener<T>() {

			@Override
			public void onResponse(T response) {
				dataNotify(methodId, response);
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				LogUtils.e(RequestServiceVolleyImpl.class.getSimpleName(), "请求服务器异常:" + error.getMessage());
				dataNotify(ID_REQUEST_ERROR, null);
			}

		});
		if (methodId == RequestService.ID_LOGIN) {// 登录的请求要把所有值还原，比如cookie
			request.clearCookie();
		}
		VolleyRequestQueue.getInstance(context).addToRequestQueue(request);
	}

	@Override
	public void login(String userAccount,String pwd){
		AppRequest ebReq = new AppRequest(RequestService.METHOD_LOGIN);
		ebReq.setReqeustParam("actionType", "ajaxLogin");
		ebReq.setReqeustParam("userAccount", userAccount);
		ebReq.setReqeustParam("pwd", pwd);
		sendRequest(ebReq, RequestService.ID_LOGIN, RspLogin.class);
	}



	@Override
	public void updateMonitorApp(String installedVersion) {
		AppRequest ebReq = new AppRequest(RequestService.METHOD_UPDATEMONITORAPP);
		ebReq.setReqeustParam("actionType", "updateMonitorApp");
		ebReq.setReqeustParam("deviceType", "android");
		ebReq.setReqeustParam("installedVersion",installedVersion);
		sendRequest(ebReq, RequestService.ID_UPDATEMONITORAPP, RspVersion.class);
	}


	@Override
	public void logout() {
		AppRequest ebReq = new AppRequest(RequestService.METHOD_LOGOUT);
		ebReq.setReqeustParam("actionType", "ajaxLogout");
		sendRequest(ebReq, RequestService.ID_LOGOUT, RspLogout.class);
	}

	@Override
	public void uploadLocation(double longitude, double latitude, int status) {
		AppRequest ebReq = new AppRequest(RequestService.METHOD_UPLOADLOCATION);
		ebReq.setReqeustParam("actionType", "updateLocation");
		ebReq.setReqeustParam("longitude", longitude+"");
		ebReq.setReqeustParam("latitude", latitude+"");
		ebReq.setReqeustParam("status", status+"");
		sendRequest(ebReq, RequestService.ID_UPLOADLOCATION, RspUploadLocation.class);
	}

	@Override
	public void uploadMedia(String data) {
		AppRequest ebReq = new AppRequest(RequestService.METHOD_UPLOADMEDIA);
		ebReq.setReqeustSubmitType(BaseRequest.Method.POST);
		ebReq.setReqeustParam("actionType", "uploadMedia");
		ebReq.setReqeustParam("data",data);
		sendRequest(ebReq, RequestService.ID_UPLOADMEDIA, RspUploadMedia.class);
	}
	
	@Override
	public void getPhotoMedia(String userId,String data, int page, int count) {
		AppRequest ebReq = new AppRequest(RequestService.METHOD_GETPHOTOMEDIA);
		ebReq.setReqeustParam("actionType", "photoList");
		ebReq.setReqeustParam("userId", userId+"");
		ebReq.setReqeustParam("data", data+"");
		ebReq.setReqeustParam("page", page+"");
		ebReq.setReqeustParam("count", count+"");
		sendRequest(ebReq, RequestService.ID_GETPHOTOMEDIA, RspGroupList.class);
	}

	@Override
	public void getVideoMedia(String userId, String data,int page, int count) {
		AppRequest ebReq = new AppRequest(RequestService.METHOD_GETVIDEOMEDIA);
		ebReq.setReqeustParam("actionType", "videoList");
		ebReq.setReqeustParam("userId", userId+"");
		ebReq.setReqeustParam("data", data+"");
		ebReq.setReqeustParam("page", page+"");
		ebReq.setReqeustParam("count", count+"");
		sendRequest(ebReq, RequestService.ID_GETVIDEOMEDIA, RspGroupList.class);
	}

	@Override
	public void downLoadMedia(final Context context,final MonitorMedia media) {
		AppRequest ebReq = new AppRequest(RequestService.METHOD_DOWNLOADMEDIA + media.getPath());
		if(media!=null){
			MediaDownLoadAsyncTask task = new MediaDownLoadAsyncTask(context, media, new MediaDownloadListener() {

				@Override
				public void downLoadCompleted(MonitorMedia media) {
					RspDownLoadMedia rsp = new RspDownLoadMedia();
					rsp.setCode(AppResponse.CODE_SUCCESS);
					rsp.setSuccess(true);
					rsp.setErrorMsg("下载成功");
					rsp.setData(media);
					dataNotify(RequestService.ID_DOWNLOADMEDIA, rsp);
				}

				@Override
				public void downLoading(MonitorMedia media) {
					RspDownLoadMedia rsp = new RspDownLoadMedia();
					rsp.setCode(AppResponse.CODE_SUCCESS);
					rsp.setSuccess(true);
					rsp.setErrorMsg("正在下载");
					rsp.setData(media);
					dataNotify(RequestService.ID_DOWNLOADMEDIA, rsp);
				}

				@Override
				public void downLoadFail(MonitorMedia media) {
					RspDownLoadMedia rsp = new RspDownLoadMedia();
					rsp.setCode(AppResponse.CODE_FAIL);
					rsp.setSuccess(false);
					rsp.setErrorMsg("下载失败");
					rsp.setData(media);
					dataNotify(RequestService.ID_DOWNLOADMEDIA, rsp);
				}
			});
			task.execute(ebReq.getReqeustURL());
	}
	}


}
