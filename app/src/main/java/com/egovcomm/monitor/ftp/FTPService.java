package com.egovcomm.monitor.ftp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.egovcomm.monitor.common.BaseService;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.SPUtils;

public class FTPService extends BaseService {
	/**上传数据过程中的消息，向外部发送*/
	public static final String FTP_BROAD_CAST_ACTION_MEDIA_UPLOAD="FTP_BROAD_CAST_ACTION_MEDIA_UPLOAD";
	/**对上传的数据进行操作的消息，外部向内部发送*/
	public static final String FTP_BROAD_CAST_ACTION_MEDIA_HANDLE="FTP_BROAD_CAST_ACTION_MEDIA_HANDLE";
	/**对服务本身进行操作的消息，外部向内部发送*/
	public static final String FTP_BROAD_CAST_ACTION_SERVICE_HANDLE="FTP_BROAD_CAST_ACTION_MEDIA_HANDLE";
	
	
	public static final String FTP_KEY_GROUP_ID="FTP_KEY_GROUP_ID";
	public static final String FTP_KEY_MEDIA_ID="FTP_KEY_MEDIA_ID";
	public static final String FTP_KEY_PROGRESS="FTP_KEY_PROGRESS";
	public static final String FTP_KEY_CODE="FTP_KEY_CODE";
	public static final String FTP_KEY_MESSAGE="FTP_KEY_MESSAGE";
	public static final String FTP_KEY_GROUP_DATA="FTP_KEY_GROUP_DATA";
//	public static final String FTP_KEY_MEDIA_LIST_DATA="FTP_KEY_MEDIA_LIST_DATA";
	
	public static final int FTP_CODE_HANDLE_UPLOAD=0;//上传
	public static final int FTP_CODE_HANDLE_CANCEL_GROUP_UPLOAD=1;//取消上传
	public static final int FTP_CODE_HANDLE_CANCEL_ALL_UPLOAD=2;//取消全部上传
	public static final int FTP_CODE_HANDLE_CLOSE_FTP=3;//关闭ftp
	
	public static final int FTP_CODE_SUCCESS=0;//成功
	public static final int FTP_CODE_OPEN_FTP_FAIL=1;//FTP链接失败
	public static final int FTP_CODE_FILE_ERROR=2;//上传的文件异常
	public static final int FTP_CODE_UPLOAD_GROUP_ERROR=3;//上传文件组失败
	public static final int FTP_CODE_UPLOAD_MEDIA_ERROR=4;//上传单个数据失败
	public static final int FTP_CODE_UPLOAD_GROUP_SUCCESS=5;//上传组成功
	public static final int FTP_CODE_UPLOAD_MEDIA_SUCCESS=6;//上传单个数据成功
	public static final int FTP_CODE_UPLOADING_GROUP=7;//正在上传组，返回组已上传的百分比
	public static final int FTP_CODE_UPLOADING_MEDIA=8;//正在上传某一个数据，返回某一个数据已上传的百分比
	public static final int FTP_CODE_CANCEL_GROUP=9;//取消正在上传的组
	public static final int FTP_CODE_CANCEL_MEDIA=10;//取消正在上传某一个数据
	public static final int FTP_CODE_CANCEL_ALL=10;//取消所有正在上传的数据
	
	
	public static final int POOL_SIZE = 50;
	private  ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(POOL_SIZE);
	private  List<Map<String,Object>> taskList = new ArrayList<Map<String,Object>>();
	
	/** 监听来自UI的操作链接广播 */
	private final BroadcastReceiver mHandleReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(TextUtils.equals(FTP_BROAD_CAST_ACTION_MEDIA_HANDLE, action)){
				int keyCode=intent.getIntExtra(FTP_KEY_CODE, FTP_CODE_HANDLE_UPLOAD);
				if(keyCode==FTP_CODE_HANDLE_UPLOAD){//上传
					MonitorMediaGroupUpload group=intent.getParcelableExtra(FTP_KEY_GROUP_DATA);
					List<MonitorMedia> mediaList=DBHelper.getInstance(FTPService.this).listMonitorMediaByGroupUploadId(group.getId());
					mediaUpload(FTPService.this, group, mediaList);
				}else if(keyCode==FTP_CODE_HANDLE_CANCEL_GROUP_UPLOAD){//取消某个组的上传
					MonitorMediaGroupUpload group=intent.getParcelableExtra(FTP_KEY_GROUP_DATA);
					cancelMediaUpload(FTPService.this, group);
				}else if(keyCode==FTP_CODE_HANDLE_CANCEL_ALL_UPLOAD){//取消全部
					
				}
				
			}
		}
	};
	
	/** 监听来自UI的操作链接广播 */
	private final BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
		}
	};
	
	private Handler sendDataControl=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
		}
		
	};

	/** 退出travel */
	private void exitService() {
		stopSelf();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtils.i(tag,"FTP---@@@@@@@@@@@@@@@@onCreate");
		startService();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//每次start都会执行一次
		LogUtils.i(tag,"FTP---###############onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopService();
	}


	/** 停止服务 */
	private void stopService() {
		// unregister
		unregisterReceiver(mServiceReceiver);
		unregisterReceiver(mHandleReceiver);
		newFixedThreadPool.shutdown();
	}

	/** 开启服务,如果已经开启，必须调用stopService关闭服务后，才可以再次调用 */
	private void startService() {
		taskList.clear();
		// 注册接收来自UI的广播
		IntentFilter filter = new IntentFilter(
				FTP_BROAD_CAST_ACTION_MEDIA_HANDLE);
		registerReceiver(mHandleReceiver, filter);
		// 注册广播
		filter = new IntentFilter(
				FTP_BROAD_CAST_ACTION_SERVICE_HANDLE);
		registerReceiver(mServiceReceiver, filter);
		upLoadingMediaReDo();
	}
	/**获取数据库正在上传的数据，并重新上传*/
	private void upLoadingMediaReDo(){
		List<MonitorMediaGroupUpload> groupList=DBHelper.getInstance(this).listMonitorMediaGroupUpload(SPUtils.getUser(this).getUserID(), MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADING+"");
		for(MonitorMediaGroupUpload group:groupList){
			List<MonitorMedia> list=DBHelper.getInstance(this).listMonitorMediaByGroupUploadId(group.getId());
			mediaUpload(this, group,list);
		}
	}
	
	public  synchronized void mediaUpload(Context context,MonitorMediaGroupUpload uploadMediaGroup,List<MonitorMedia> mediaList) {
		LogUtils.i(tag, "FTPService当前线程ID是"+Thread.currentThread().getId()+"--"+Thread.currentThread().getName());
		UploadTask upload=new UploadTask(context, uploadMediaGroup.getRemoteDirectory(), uploadMediaGroup, mediaList);
		newFixedThreadPool.submit(upload);
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("id", uploadMediaGroup.getId());
		map.put("submit", upload);
		taskList.add(map);
	}
	/**取消单个上传*/
	public  synchronized void cancelMediaUpload(Context context,MonitorMediaGroupUpload uploadGroup) {
		LogUtils.i(tag, "ftpServer 取消upload");
		Iterator<Map<String, Object>> it = taskList.iterator();
			while (it.hasNext()) {
				Map<String, Object> map = it.next();
				if(map.get("id").equals(uploadGroup.getId())){
					UploadTask submit =(UploadTask) map.get("submit");
					submit.cancel();
					it.remove();
					break;
				}
			}
	}
	/**取消所有上传*/
	public  synchronized void cancelAllUpload(Context context) {
		for(Map<String,Object> map:taskList){
			UploadTask submit =(UploadTask) map.get("submit");
			submit.cancel();
		}
		taskList.clear();
	}
	/**结束ftp工作*/
	public  void closeFTPWork() {
		newFixedThreadPool.shutdown();
	}
	
	/**发送广播消息*/
	private  void sendBroadCast(Context context,int code,String groupId,String mediaId,int progress,String message ){
		Intent intent=new Intent(FTP_BROAD_CAST_ACTION_MEDIA_UPLOAD);
		intent.putExtra(FTP_KEY_CODE, code);
		intent.putExtra(FTP_KEY_GROUP_ID, groupId);
		intent.putExtra(FTP_KEY_MEDIA_ID, mediaId);
		intent.putExtra(FTP_KEY_MESSAGE, message);
		intent.putExtra(FTP_KEY_PROGRESS, progress);
		context.sendBroadcast(intent);
	}

/*	
	private ReConnectThread mReConnectThread;
	private boolean isReconnectRunning=false;
	private class ReConnectThread extends Thread {
		public void run() {
			LogUtils.i(tag, "BEGIN ReConnectThread:");
			setName("ReConnectThread");
			while (true) {
			}
		}

		public void cancel() {
			isReconnectRunning = false;
		}
	}*/
}
