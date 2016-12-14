package com.egovcomm.monitor.net;

import java.util.List;

import android.content.Context;

import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;

/**
 * @author Nicolls
 * @Description 请求服务器接口类
 * @date 2015年10月25日
 */
public interface RequestService {

	/**自定义的错误请求ID，用于在发送请求后，请求中发生错误的返回ID标识*/
	static final int ID_REQUEST_ERROR = 0;
	/** 方法ID **/
	/** 普通登录 */
	static final int ID_LOGIN=1;
	
	/**注销*/
	static final int ID_LOGOUT=2;
	/**上传位置信息*/
	static final int ID_UPLOADLOCATION=3;
	/**设置上传图片，视频*/
	static final int ID_UPLOADMEDIA=4;
	/**以Ftp方式上传图片，视频*/
	static final int ID_FTPUPLOADMEDIA=5;
	/**获取图片，视频集合*/
	static final int ID_GETPHOTOMEDIA=6;
	
	/**获取图片，视频集合*/
	static final int ID_GETVIDEOMEDIA=7;
	
	/**下载图片视频URL*/
	static final int ID_DOWNLOADMEDIA=8;

	/**更新应用*/
	static final int ID_UPDATEMONITORAPP=9;
	
	
	/** 方法名 **/
	/** 普通登录 */
	static final String METHOD_LOGIN="/userAction.struts";
	/**注销*/
	static final String METHOD_LOGOUT="/userAction.struts";
	/**上传位置信息*/
	static final String METHOD_UPLOADLOCATION="/monitor/location.struts";
	/**设置上传图片，视频*/
	static final String METHOD_UPLOADMEDIA="/monitor/media.struts";
	
	/**以FTP上传图片，视频*/
	static final String METHOD_FTPUPLOADMEDIA="/monitor/media.struts";
	
	/**获取图片集合*/
	static final String METHOD_GETPHOTOMEDIA="/monitor/media.struts";
	/**获取视频集合*/
	static final String METHOD_GETVIDEOMEDIA="/monitor/media.struts";
	
	/**下载图片视频URL*/
	static final String METHOD_DOWNLOADMEDIA="/attach/mediaFile/";

	/**更新应用*/

	static final String METHOD_UPDATEMONITORAPP="/userAction.struts";
	/**
	 * 
	 * @param dataUpdateListener 监听器
	 * @return void
	 * @Description 监听请求返回
	 */
	void setUptateListener(DataUpdateListener dataUpdateListener);

	/**
	 * actionType ajaxLogin
	 * @param userAccount 用户名
	 * @param pwd 密码
	 * @Description 
	 */
	void login(String userAccount, String pwd);
	
	/**注销
	 *actionType ajaxLogout
	 * @Description 
	 */
	void logout();
	
	/**上传位置
	 * actionType updateLocation
	 * @param longitude 经度。不能使用时分秒的形式提交（23°01′13.93″），只能以小数形式提交，例：23.03
	 * @param latitude 纬度。不能使用时分秒的形式提交（113°45′6.35），只能以小数形式提交, 例：113.7
	 * @param status 状态，1为在线，2为正在拍传
	 * @Description 
	 */
	void uploadLocation(double longitude, double latitude, int status);
	
	/**设置上传到ftp的视频照片关联
	 * actionType uploadMedia
	 * @param data 上传的组数据json
	 * @Description 
	 */
	void uploadMedia(String data);
	
	
	
	/**获取照片列表数据
	 * actionType photoList
	 * @param userId (选填)：用户id，填写则只获取该用户的数据，不填则不会过滤用户id
	 * @param page (选填): 第几页，从1开始，不填默认为1
	 * @param count (选填): 每页多少条数据，不填默认为20
	 * @param data data(选填、json字符串)：数据筛选字段，可以只提交其中一个参数，若两个参数都提交，是“或”关系
	{
	createAddr: '创建时所在地址(使用高德地图获取)',
	remark: '备注'
	}
	 * @Description 
	 */
	void getPhotoMedia(String userId,String data, int page, int count);
	
	
	/**获取视频列表数据
	 * actionType videoList
	 * @param userId (选填)：用户id，填写则只获取该用户的数据，不填则不会过滤用户id
	 * @param page (选填): 第几页，从1开始，不填默认为1
	 * @param count (选填): 每页多少条数据，不填默认为20
	 * @param data data(选填、json字符串)：数据筛选字段，可以只提交其中一个参数，若两个参数都提交，是“或”关系
	{
	createAddr: '创建时所在地址(使用高德地图获取)',
	remark: '备注'
	}
	 * @Description 
	 */
	void getVideoMedia(String userId,String data, int page, int count);
	
	/**通过url下载视频或者照片
	 * @Description  url: http://[IP]:[PORT]/attach/mediaFile/[文件路径]
            文件路径说明：
           在调用获取图片（视频）列表接口后，响应数据中包含media对象数组，每个media就是一组图片或视频，
           每个media对象中，包含一个mediaFiles数组，一个mediaFile对象对应一张图片或视频。
           mediaFile对象中含有path文件路径，url中拼接上path，即可查看（下载）图片、视频。
	 */
	void downLoadMedia(Context context, MonitorMedia media);
	
	/**
	 * userAction.struts?actionType=updateMonitorApp
	 * @param installedVersion 当前应用版本号，如1.0.0
	 * @return void
	 * @Description 查询更新
	 */
	void updateMonitorApp(String installedVersion);


}
