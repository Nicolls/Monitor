package com.egovcomm.monitor.ftp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.content.Intent;

import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.CommonUtil;
import com.egovcomm.monitor.utils.TimeUtils;
import com.egovcomm.monitor.utils.ToastUtils;

/**
 * ftp上传工具包
 * 
 * @author chuer
 * @date 2015年1月7日 下午2:31:39
 */
public class FTPMediaUtil {
	
	/**开启服务*/
	public static void startFTPService(Context context) {
		Intent service = new Intent(context, FTPService.class);
		context.startService(service);
	}
	
	public static void mediaUpload(Context context,MonitorMediaGroupUpload uploadMediaGroup) {
		if(context!=null){
			try {
				if(CommonUtil.checkNetWork(context)){
					Intent intent=new Intent(FTPService.FTP_BROAD_CAST_ACTION_MEDIA_HANDLE);
					intent.putExtra(FTPService.FTP_KEY_CODE,FTPService.FTP_CODE_HANDLE_UPLOAD);
					intent.putExtra(FTPService.FTP_KEY_GROUP_DATA, uploadMediaGroup);
					//intent.putExtra(FTPService.FTP_KEY_MEDIA_LIST_DATA, mediaList.toArray());
					context.sendBroadcast(intent);
				}else{
					ToastUtils.toast(context,"当前网络不可用");
				}
			}catch (Exception e){
				e.printStackTrace();
			}

		}
	}
	/**取消单个上传*/
	public static  void cancelMediaUpload(Context context,MonitorMediaGroupUpload uploadGroup) {
		Intent intent=new Intent(FTPService.FTP_BROAD_CAST_ACTION_MEDIA_HANDLE);
		intent.putExtra(FTPService.FTP_KEY_CODE, FTPService.FTP_CODE_HANDLE_CANCEL_GROUP_UPLOAD);
		intent.putExtra(FTPService.FTP_KEY_GROUP_DATA, uploadGroup);
		context.sendBroadcast(intent);
	}
	/**取消所有上传*/
	public static synchronized void cancelAllUpload(Context context) {
		Intent intent=new Intent(FTPService.FTP_BROAD_CAST_ACTION_MEDIA_HANDLE);
		intent.putExtra(FTPService.FTP_KEY_CODE, FTPService.FTP_CODE_HANDLE_CANCEL_ALL_UPLOAD);
		context.sendBroadcast(intent);
	}
	/**结束ftp工作*/
	public static void closeFTPWork(Context context) {
		Intent intent=new Intent(FTPService.FTP_BROAD_CAST_ACTION_MEDIA_HANDLE);
		intent.putExtra(FTPService.FTP_KEY_CODE, FTPService.FTP_CODE_HANDLE_CLOSE_FTP);
		context.sendBroadcast(intent);
	}
}