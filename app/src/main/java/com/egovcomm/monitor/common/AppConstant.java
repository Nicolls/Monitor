package com.egovcomm.monitor.common;

/** 常量类 */
public class AppConstant {

	public static final long SPACE_TIME = 2000;// 1秒内按下，则退出应用
	/** 存储文件夹 */
	public static final String FILE_DIR = "com.monitor";
	/** 用来存储本地照相，录视频等产生的文件 */
	public static final String FILE_DIR_LOCAL = "local";
	/** 用来存储服务器上返回的文件 */
	public static final String FILE_DIR_SERVER = "server";
	/** 用来存储服务器上返回的文件缩略图 */
	public static final String FILE_DIR_THUMBNAIL = "thumbnail";
	/** 概览页面资源利用 率html */
	public static final String APP_DOWNLOAD_APK_NAME = "monitor.apk";
	// 默认服务器地址端口
	public static final String DEFAULT_HOST = "";
	public static final int DEFAULT_PORT = 33333;
	// 默认ftp地址ftp端口
	public static final String DEFAULT_FTP_HOST = "";
	public static final int DEFAULT_FTP_PORT = 2121;

	// 默认ftp用户名和密码
	public static final String DEFAULT_FTP_USER_NAME = "";
	public static final String DEFAULT_FTP_PASSWORD = "";

	/** 统一时间格式化 */
	public static final String TIME_FORMAT_STYLE = "yyyy-MM-dd HH:mm:ss";

	/** 统一通过startActivityForResult返回成功为result_code_ok=0 */
	public static final int ACTIVITY_START_FOR_RESULT_CODE_OK = 0;
	/** 统一通过startActivityForResult返回失败为result_code_fail */
	public static final int ACTIVITY_START_FOR_RESULT_CODE_FAIL = -1;
	/** 打开蓝牙 */
	public static final int ACTIVITY_START_FOR_RESULT_ENABLE_BLUETOOTH = 1001;




}
