package com.egovcomm.monitor.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.egovcomm.monitor.common.AppConstant;
import com.egovcomm.monitor.db.DBHelper;
import com.egovcomm.monitor.ftp.ProgressInputStream.UploadProgressListener;
import com.egovcomm.monitor.model.AppResponse;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.model.ReqUploadMediaData;
import com.egovcomm.monitor.model.RspMedia;
import com.egovcomm.monitor.model.RspUploadMedia;
import com.egovcomm.monitor.net.DataUpdateListener;
import com.egovcomm.monitor.net.RequestService;
import com.egovcomm.monitor.net.RequestServiceFactory;
import com.egovcomm.monitor.utils.JsonUtils;
import com.egovcomm.monitor.utils.LogUtils;
import com.egovcomm.monitor.utils.TimeUtils;
import com.egovcomm.monitor.utils.ToastUtils;
import com.google.gson.Gson;

/**
 * FTP 连接
 * 
 * @author chuer
 * @date 2015年1月7日 下午2:19:48
 */
public class FTPConnection implements DataUpdateListener {
	public static final String TAG = FTPConnection.class.getSimpleName();
	private FTPClient ftp = new FTPClient();
	private boolean is_connected = false;
	private String workingDirectory;

	public static int defaultTimeoutSecond = 60;
	public static int connectTimeoutSecond = 30;
	public static int dataTimeoutSecond = 3600;//一个小时

	public static String host=AppConstant.DEFAULT_FTP_HOST;
	public static int port=AppConstant.DEFAULT_FTP_PORT;
	public static String userName = AppConstant.DEFAULT_FTP_USER_NAME;
	public static String password = AppConstant.DEFAULT_FTP_PASSWORD;
	
	
	private Context context;

	private long groupSize;// 整个组文件大小
	private long groupUploadSize;// 已上传的大

	private MonitorMediaGroupUpload uploadGroup;
	private List<MonitorMedia> mediaList;

	/**
	 * 构造函数
	 */
	public FTPConnection(Context context, MonitorMediaGroupUpload uploadMediaGroup) {
		LogUtils.i(TAG, "FTPConnection当前线程ID是" + Thread.currentThread().getId()
				+ "--" + Thread.currentThread().getName());
		this.context = context;
		this.uploadGroup=uploadMediaGroup;
		this.workingDirectory = "/" + this.uploadGroup.getRemoteDirectory() + "/";
		is_connected = false;
		/** 设置ftp参数值 */
		ftp.setDefaultTimeout(defaultTimeoutSecond * 1000);
		ftp.setConnectTimeout(connectTimeoutSecond * 1000);
		ftp.setDataTimeout(dataTimeoutSecond * 1000);
		try {
			initConnect(host, port, userName, password);
		} catch (IOException e) {
			updateUploadGroupState(false);
			e.printStackTrace();
		}
	}

	/**
	 * 初始化连接
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @throws IOException
	 */
	private void initConnect(String host, int port, String user, String password) throws IOException {
		System.out.println("初始化链接" + workingDirectory);
		ftp.connect(host, port);

		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			disconnect();
		}

		if (user == "") {
			user = "anonymous";
		}

		if (!ftp.login(user, password)) {
			is_connected = false;
			disconnect();
		} else {
			is_connected = true;
			// 设置工作路径
			setWorkingDirectory(workingDirectory);
		}

	}

	/**取消上传*/
	public void cancel(){
		if(ftp!=null){
			LogUtils.i(TAG, "取消上传");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					//
					LogUtils.i(TAG, "新建一个线程来取消它");
					try {
						ftp.abort();
					} catch (IOException e) {
						e.printStackTrace();
					}
					//删除创建的文件
					if(mediaList!=null){
						for(MonitorMedia media:mediaList){
							media.setUploadTime(TimeUtils
									.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
							media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_CANCEL);
						}
						DBHelper.getInstance(context).updateMonitorMediaList(mediaList);
					}
					LogUtils.i(TAG, "取消上传完成");
				}
			}).start();
		}
	}
	
	/**
	 * 上传多个文件
	 * 
	 * @throws IOException
	 */
	public void uploadFileList(List<MonitorMedia> mediaList) throws IOException {
		this.mediaList=mediaList;
		groupSize = 0;
		groupUploadSize = 0;
		for (MonitorMedia media : mediaList) {// 计算文件大小
			groupSize += Long.parseLong(media.getFileSize());
		}
		for (MonitorMedia media : mediaList) {// 每上传一个就要把它的大小算上
			upload(this.uploadGroup, media);
			groupUploadSize += Long.parseLong(media.getFileSize());
		}
		requestSetMediaUpload(this.uploadGroup, mediaList);// 服务器数据设置上传完成

	}

	/**
	 * 上传文件
	 * 
	 * @throws IOException
	 */
	private void upload(final MonitorMediaGroupUpload uploadMediaGroup,
			final MonitorMedia media) throws IOException {
		System.out.println("上传－－" + workingDirectory);
		File file = new File(media.getPath());
		// 检查本地文件是否存在
		if (!file.exists()) {
			throw new IOException("Can't upload '" + media.getPath()
					+ "'. This file doesn't exist.");
		}
		// 上传
		InputStream in = null;
		try {
			// 被动模式
			ftp.setBufferSize(1024);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			ftp.enterLocalPassiveMode();
			UploadProgressListener listenner = null;
			listenner = new UploadProgressListener() {

				@Override
				public void onUploadProgress(long uploadSize, File file) {
					uploadSize = uploadSize + groupUploadSize;
					float percent = uploadSize / (groupSize * 1.0f);
					int progress = (int) (percent * 100);
					LogUtils.i("AAA", file.getName() + "文件大小是：" + file.length()
							+ "总大小＝" + groupSize + "-总上传了：" + uploadSize
							+ "--总上传百分比=" + progress);

					sendBroadCast(context, FTPService.FTP_CODE_UPLOADING_GROUP,
							uploadMediaGroup.getId(), media.getId(), progress,
							"");
				}
			};
			in = new ProgressInputStream(file, listenner,
					ProgressInputStream.PROGRESSSTYLE_FILE_SIZE);
			// in = new BufferedInputStream(new FileInputStream(localFile));
			//System.out.println("文件上传了" + workingDirectory);
			// 保存文件
			if (!ftp.storeFile(media.getFileName(), in)) {
				throw new IOException("Can't upload file '"
						+ media.getFileName()
						+ "' to FTP server. Check FTP permissions and path.");
			}
			media.setUploadTime(TimeUtils
					.getFormatNowTime(TimeUtils.SIMPLE_FORMAT));
			media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED);
			DBHelper.getInstance(context).updateMonitorMedia(media);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 关闭连接
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		if (ftp.isConnected()) {
			try {
				ftp.logout();
				ftp.disconnect();
				is_connected = false;
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 设置工作路径
	 * 
	 * @param dir
	 * @return
	 */
	private boolean setWorkingDirectory(String dir) {
		try {
			ftp.changeWorkingDirectory("/");// 回到根目录
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (!is_connected) {
			return false;
		}
		// 如果目录不存在创建目录
		try {
			if (createDirecroty(dir)) {
				return ftp.changeWorkingDirectory(dir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 是否连接
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return is_connected;
	}

	/**
	 * 创建目录
	 * 
	 * @param remote
	 * @return
	 * @throws IOException
	 */
	private boolean createDirecroty(String remote) throws IOException {
		boolean success = true;
		String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
		// 如果远程目录不存在，则递归创建远程服务器目录
		if (!directory.equalsIgnoreCase("/")
				&& !ftp.changeWorkingDirectory(new String(directory))) {
			int start = 0;
			int end = 0;
			if (directory.startsWith("/")) {
				start = 1;
			} else {
				start = 0;
			}
			end = directory.indexOf("/", start);
			while (true) {
				String subDirectory = new String(remote.substring(start, end));
				if (!ftp.changeWorkingDirectory(subDirectory)) {
					if (ftp.makeDirectory(subDirectory)) {
						ftp.changeWorkingDirectory(subDirectory);
					} else {
						System.out.println("mack directory error :/"
								+ subDirectory);
						return false;
					}
				}
				start = end + 1;
				end = directory.indexOf("/", start);
				// 检查所有目录是否创建完毕
				if (end <= start) {
					break;
				}
			}
		}
		return success;
	}

	/** 发送广播消息 */
	private void sendBroadCast(Context context, int code, String groupId,
			String mediaId, int progress, String message) {
		Intent intent = new Intent(
				FTPService.FTP_BROAD_CAST_ACTION_MEDIA_UPLOAD);
		intent.putExtra(FTPService.FTP_KEY_CODE, code);
		intent.putExtra(FTPService.FTP_KEY_GROUP_ID, groupId);
		intent.putExtra(FTPService.FTP_KEY_MEDIA_ID, mediaId);
		intent.putExtra(FTPService.FTP_KEY_MESSAGE, message);
		intent.putExtra(FTPService.FTP_KEY_PROGRESS, progress);
		context.sendBroadcast(intent);
	}

	/** 上传成功，则要请求数据 */
	private void requestSetMediaUpload(MonitorMediaGroupUpload group,
			List<MonitorMedia> list) {
		if (context != null) {
			RequestService service = RequestServiceFactory
					.getInstance(context.getApplicationContext(),
							RequestServiceFactory.REQUEST_VOLLEY);
			service.setUptateListener(this);
			String data = "";
			ReqUploadMediaData req = new ReqUploadMediaData();
			req.setUserId(group.getMediaGroup().getUserId());
			req.setUserName(group.getMediaGroup().getUserName());
			req.setOrgId(group.getMediaGroup().getOrgId());
			req.setOrgName(group.getMediaGroup().getOrgName());
			req.setCreateTime(group.getMediaGroup().getCreateTime());
			req.setCreateAddr(group.getMediaGroup().getCreateAddr());
			req.setLongitude(group.getMediaGroup().getLongitude());
			req.setLatitude(group.getMediaGroup().getLatitude());
			req.setMediaType(group.getMediaGroup().getMediaType());
			req.setRemark(group.getMediaGroup().getRemark());
			List<RspMedia> fileList = new ArrayList<RspMedia>();
			// id: 'uuid',
			// shootingLocation: '拍摄地点1',
			// fileName: '文件名称1',
			// uploadTime: '上传时间1(yyyy-MM-dd HH:mm:ss)',
			// fileSize: '文件大小(数字类型，字节为单位，如文件大小是1m,则是1024)',
			// path: '文件路径(yyyyMMdd/{跟本对象中的uuid一致}.jpg)',
			// ext: '扩展名',
			// remark: '备注'
			RspMedia m = null;
			for (MonitorMedia media : list) {
				m = new RspMedia();
				m.setId(media.getId());
				m.setShootingLocation(media.getShootingLocation());
				m.setFileName(media.getFileName());
				m.setUploadTime(media.getUploadTime());
				m.setFileSize(media.getFileSize());
				m.setPath(group.getRemoteDirectory() + "/"
						+ media.getFileName());
				m.setExt(media.getFileSuffix());
				m.setRemark(media.getRemark());
				fileList.add(m);
			}
			req.setFileList(fileList);

			Gson g = new Gson();
			data = JsonUtils.objectToJson(req, ReqUploadMediaData.class);
			LogUtils.i(TAG, "上传的数据是：" + data);
			service.uploadMedia(data);
		}
	}

	@Override
	public void update(int id, Object obj) {
		LogUtils.i(TAG, "请求服务器上传设置数据返回");
		if (context != null && uploadGroup != null
				&& id == RequestService.ID_UPLOADMEDIA) {
			RspUploadMedia rsp = (RspUploadMedia) obj;
			if (TextUtils.equals(rsp.getCode(), AppResponse.CODE_SUCCESS)) {// 成功
				updateUploadGroupState(true);
			} else {// 失败
				updateUploadGroupState(false);
			}
		}
	}

	/**更新组数据并发送通知*/
	private void updateUploadGroupState(boolean isSuccess){

		if (isSuccess) {// 成功
			sendBroadCast(context,
					FTPService.FTP_CODE_UPLOAD_GROUP_SUCCESS,
					uploadGroup.getId(), "", 0, uploadGroup.getMediaGroup()
							.getRemark() + "上传成功");

			uploadGroup.setProgress(0);
			uploadGroup
					.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED
							+ "");
			List<MonitorMedia> mediaList = DBHelper.getInstance(context)
					.listMonitorMediaByGroupUploadId(uploadGroup.getId());
			for (MonitorMedia media : mediaList) {
				media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOADED
						+ "");
			}
			DBHelper.getInstance(context).updateMonitorMediaGroupUpload(
					uploadGroup);// 更新状态
			DBHelper.getInstance(context).updateMonitorMediaList(mediaList);// 更新数据
		} else {// 失败
			sendBroadCast(context, FTPService.FTP_CODE_UPLOAD_GROUP_ERROR,
					uploadGroup.getId(), "", 0, uploadGroup.getMediaGroup()
							.getRemark() + "上传失败");
			uploadGroup.setProgress(0);
			uploadGroup
					.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_FAIL
							+ "");
			List<MonitorMedia> mediaList = DBHelper.getInstance(context)
					.listMonitorMediaByGroupUploadId(uploadGroup.getId());
			for (MonitorMedia media : mediaList) {
				media.setUploadState(MonitorMediaGroupUpload.UPLOAD_STATE_UPLOAD_FAIL
						+ "");
			}
			DBHelper.getInstance(context).updateMonitorMediaGroupUpload(
					uploadGroup);// 更新状态
			DBHelper.getInstance(context).updateMonitorMediaList(mediaList);// 更新数据
		}
	}

}
