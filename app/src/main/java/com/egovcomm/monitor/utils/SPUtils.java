/**
 * 
 */
package com.egovcomm.monitor.utils;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.egovcomm.monitor.common.AppConstant;
import com.egovcomm.monitor.ftp.FTPConnection;
import com.egovcomm.monitor.model.AppRequest;
import com.egovcomm.monitor.model.User;
import com.google.gson.Gson;

/**
 * 存储shareprefrence数据工具类
 * 
 * @author mengjk
 * 
 *         2015年5月15日
 */
public class SPUtils {
	// 服务器相关
	public static final String SP_NET = "SP_NET";
	public static final String SP_HTTP_HOST = "SP_HTTP_HOST";
	public static final String SP_HTTP_PORT = "SP_HTTP_PORT";
	
	public static final String SP_FTP_HOST = "SP_FTP_HOST";
	public static final String SP_FTP_PORT = "SP_FTP_PORT";
	
	public static final String SP_FTP_USER_NAME = "SP_FTP_USER_NAME";
	public static final String SP_FTP_PASSWORD = "SP_FTP_PASSWORD";

	// app相关
	public static final String SP_APP = "SP_APP";
	public static final String SP_APP_ENTER = "SP_APP_ENTER";

	
	// 用户相关
	public static final String SP_USER = "SP_USER";
	public static final String SP_USER_AUTO_LOGIN = "SP_USER_AUTO_LOGIN";
	public static final String SP_USER_SAFE_CODE = "SP_USER_SAFE_CODE";
	public static final String SP_USER_SAFE_CODE_SWITCH = "SP_USER_SAFE_CODE_SWITCH";
	public static final String SP_USER_TOKEN = "SP_USER_TOKEN";

	public static final String SP_USER_DATA = "SP_USER_DATA";

	

	/** 获取服务器地址 */
	public static String getServerHost(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		String host = sp.getString(SP_HTTP_HOST, AppConstant.DEFAULT_HOST);
		AppRequest.requestHost = host;
		return host;
	}

	/** 设置服务器地址 */
	public static boolean setServerHost(Context context, String host) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_HTTP_HOST, host).commit();
		if (isOk) {
			AppRequest.requestHost = host;
		}
		return isOk;
	}
	
	

	/** 获取服务器端口 */
	public static int getServerPort(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		int port = sp.getInt(SP_HTTP_PORT, AppConstant.DEFAULT_PORT);
		AppRequest.requestPort = port;
		return port;
	}

	/** 设置服务器端口 */
	public static boolean setServerPort(Context context, int port) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putInt(SP_HTTP_PORT, port).commit();
		if (isOk) {
			AppRequest.requestPort = port;
		}
		return isOk;
	}
	
	
	/** 获取ftp服务器地址 */
	public static String getFtpServerHost(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		String host = sp.getString(SP_FTP_HOST, AppConstant.DEFAULT_FTP_HOST);
		FTPConnection.host=host;
		return host;
	}

	/** 设置服务器地址 */
	public static boolean setFtpServerHost(Context context, String host) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_FTP_HOST, host).commit();
		if (isOk) {
			FTPConnection.host=host;
		}
		return isOk;
	}
	
	

	/** 获取服务器端口 */
	public static int getFtpServerPort(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		int port = sp.getInt(SP_FTP_PORT, AppConstant.DEFAULT_FTP_PORT);
		FTPConnection.port=port;
		return port;
	}

	/** 设置服务器端口 */
	public static boolean setFtpServerPort(Context context, int port) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putInt(SP_FTP_PORT, port).commit();
		if (isOk) {
			FTPConnection.port=port;
		}
		return isOk;
	}

	/** 获取ftp用户名 */
	public static String getFtpUserName(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		String userName = sp.getString(SP_FTP_USER_NAME, AppConstant.DEFAULT_FTP_USER_NAME);
		FTPConnection.userName=userName;
		return userName;
	}

	/** 设置ftp用户名 */
	public static boolean setFtpUserName(Context context, String userName) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_FTP_USER_NAME, userName).commit();
		if (isOk) {
			FTPConnection.userName = userName;
		}
		return isOk;
	}
	
	/** 获取ftp密码 */
	public static String getFtpPassword(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		String password = sp.getString(SP_FTP_PASSWORD, AppConstant.DEFAULT_FTP_PASSWORD);
		FTPConnection.password=password;
		return password;
	}

	/** 设置ftp密码 */
	public static boolean setFtpPassword(Context context, String password) {
		SharedPreferences sp = context.getSharedPreferences(SP_NET, Context.MODE_PRIVATE);
		boolean isOk = sp.edit().putString(SP_FTP_PASSWORD, password).commit();
		if (isOk) {
			FTPConnection.password = password;
		}
		return isOk;
	}


	/** 清除本地存储的数据 */
	public static void cleanLocalData(Context context) {
		context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE).edit().clear().commit();
	}

	/** 获取User */
	public static User getUser(Context context) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		String data = sp.getString(SP_USER_DATA, "");
		data=EncryptUtils.decryptBase64(data);
		User user=new User();
		if(!TextUtils.isEmpty(data)){
			Gson gson=new Gson();
			user=gson.fromJson(data, User.class);
		}
		return user;
	}

	/** 保存User Base64压缩*/
	public static boolean setUser(Context context, User user) {
		SharedPreferences sp = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
		Gson gson=new Gson();
		String data=gson.toJson(user);
		data=EncryptUtils.encryptBase64(data);
		boolean isOk = sp.edit().putString(SP_USER_DATA, data).commit();
		return isOk;
	}

}
