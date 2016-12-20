package com.egovcomm.monitor.common;

/** 常量类 */
public class AppConstant {

	public static final long SPACE_TIME = 2000;// 1秒内按下，则退出应用
	/** 存储文件夹 */
	public static final String FILE_DIR = "com.monitor";
	/** 用来存储照相，录视频的源文件 */
	public static final String FILE_DIR_ORIGINAL= "original";
	/** 用来存储日志件 */
	public static final String FILE_DIR_LOG= "log";
	/** 用来存储屏幕录制 */
	public static final String FILE_DIR_SCREEN= "screen";
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


}
