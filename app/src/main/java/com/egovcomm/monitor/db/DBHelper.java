package com.egovcomm.monitor.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.egovcomm.monitor.db.MonitorTable.LocationEntry;
import com.egovcomm.monitor.db.MonitorTable.MediaEntry;
import com.egovcomm.monitor.db.MonitorTable.MediaGropEntry;
import com.egovcomm.monitor.db.MonitorTable.MediaGropUploadEntry;
import com.egovcomm.monitor.model.MonitorMedia;
import com.egovcomm.monitor.model.MonitorMediaGroup;
import com.egovcomm.monitor.model.MonitorMediaGroupUpload;
import com.egovcomm.monitor.utils.LogUtils;
import com.google.gson.Gson;

/** 数据库帮助类 */
public class DBHelper extends SQLiteOpenHelper {
	private final static String TAG = DBHelper.class.getSimpleName();
	private static DBHelper _instance;
	private static final String DB_NAME = "monitor.db";
	private static final int DB_VERSION = 1;
	private static Context mContext;
	private static Gson gson = new Gson();

	public static DBHelper getInstance(Context context) {
		mContext = context;
		if (_instance == null)
			_instance = new DBHelper(context, DB_NAME, null, DB_VERSION);
		return _instance;
	}

	public DBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase) {
		// 建立表

		// 组数据表
		sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + MediaGropEntry.TABLE_NAME + " (" + MediaGropEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + MediaGropEntry.COLUMN_UUID + " TEXT,"
				+ MediaGropEntry.COLUMN_USER_ID + " TEXT,"
				+ MediaGropEntry.COLUMN_USER_NAME + " TEXT," + MediaGropEntry.COLUMN_ORG_ID + " TEXT,"
				+ MediaGropEntry.COLUMN_ORG_NAME + " TEXT," + MediaGropEntry.COLUMN_CREATE_TIME + " TEXT,"
				+ MediaGropEntry.COLUMN_CREATE_ADDR + " TEXT," + MediaGropEntry.COLUMN_LONGITUDE + " TEXT,"
				+ MediaGropEntry.COLUMN_LATITUDE + " TEXT," + MediaGropEntry.COLUMN_MEDIA_TYPE + " TEXT,"
				+ MediaGropEntry.COLUMN_REMARK + " TEXT," 
				+ MediaGropEntry.COLUMN_PATH_THUMBNAIL + " TEXT" +");");
		// 组上传表
		sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + MediaGropUploadEntry.TABLE_NAME + " ("
				+ MediaGropUploadEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MediaGropUploadEntry.COLUMN_UUID + " TEXT,"
				+ MediaGropUploadEntry.COLUMN_GROUP_ID + " TEXT," + MediaGropUploadEntry.COLUMN_UPLOAD_STATE
				+ " TEXT," + MediaGropUploadEntry.COLUMN_PATH_THUMBNAIL+" TEXT,"
				+ MediaGropUploadEntry.COLUMN_REMOTE_DIRECTORY+" TEXT"+");");
		// 数据表
		sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + MediaEntry.TABLE_NAME + " (" + MediaEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + MediaEntry.COLUMN_UUID + " TEXT,"
				+ MediaEntry.COLUMN_UPLOAD_GROUP_ID + " TEXT,"
				+ MediaEntry.COLUMN_USER_ID + " TEXT,"
				+ MediaEntry.COLUMN_SHOOTING_LOCATION + " TEXT," + MediaEntry.COLUMN_REMARK + " TEXT,"
				+ MediaEntry.COLUMN_UPLOAD_TIME + " TEXT," + MediaEntry.COLUMN_FILE_NAME + " TEXT,"
				+ MediaEntry.COLUMN_FILE_SIZE + " TEXT," + MediaEntry.COLUMN_PATH + " TEXT,"
				+ MediaEntry.COLUMN_FILE_SUFFIX + " TEXT," + MediaEntry.COLUMN_FILE_STATE + " TEXT,"
				+ MediaEntry.COLUMN_ORIENTATIONE + " TEXT," + MediaEntry.COLUMN_UPLOAD_STATE+" TEXT,"
				+ MediaEntry.COLUMN_MEDIA_TYPE+" TEXT,"+ MediaEntry.COLUMN_PATH_THUMBNAIL+" TEXT,"
				+ MediaEntry.COLUMN_CREATE_TIME+" TEXT,"+ MediaEntry.COLUMN_TIME+" TEXT,"
				+ MediaEntry.COLUMN_REASON+" TEXT"+");");

		// 位置表
		sqliteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + LocationEntry.TABLE_NAME + " (" + LocationEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + LocationEntry.COLUMN_USER_ID + " TEXT,"
				+ LocationEntry.COLUMN_DEPARTMENT_ID + " TEXT," + LocationEntry.COLUMN_CREATE_TIME + " TEXT,"
				+ LocationEntry.COLUMN_LONGITUDE + " TEXT," + LocationEntry.COLUMN_LATITUDE + " TEXT,"
				+ LocationEntry.COLUMN_REMARK + " TEXT," + LocationEntry.COLUMN_STATE + " TEXT" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + MediaGropEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MediaEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
		onCreate(db);
	}

	// 分组操作
	/** 插入一个数据 */
	public long insertMonitorMediaGroup(MonitorMediaGroup mediaGroup) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaGropEntry.COLUMN_UUID, mediaGroup.getId());
		contentValues.put(MediaGropEntry.COLUMN_CREATE_ADDR, mediaGroup.getCreateAddr());
		contentValues.put(MediaGropEntry.COLUMN_CREATE_TIME, mediaGroup.getCreateTime());
		contentValues.put(MediaGropEntry.COLUMN_ORG_ID, mediaGroup.getOrgId());
		contentValues.put(MediaGropEntry.COLUMN_ORG_NAME, mediaGroup.getOrgName());
		contentValues.put(MediaGropEntry.COLUMN_LATITUDE, mediaGroup.getLatitude());
		contentValues.put(MediaGropEntry.COLUMN_LONGITUDE, mediaGroup.getLongitude());
		contentValues.put(MediaGropEntry.COLUMN_REMARK, mediaGroup.getRemark());
		contentValues.put(MediaGropEntry.COLUMN_MEDIA_TYPE, mediaGroup.getMediaType());
		contentValues.put(MediaGropEntry.COLUMN_USER_ID, mediaGroup.getUserId());
		contentValues.put(MediaGropEntry.COLUMN_USER_NAME, mediaGroup.getUserName());
		contentValues.put(MediaGropEntry.COLUMN_PATH_THUMBNAIL, mediaGroup.getThumbnailPath());

		long id = sqliteDatabase.insert(MediaGropEntry.TABLE_NAME, null, contentValues);
		LogUtils.i(TAG, "insertMonitorMediaGroup" + id);
		return id;
	}

	/** 更新一个数据 */
	public void updateMonitorMediaGroup(MonitorMediaGroup mediaGroup) {
		//LogUtils.i(TAG, "更新mediaGroup－－" + mediaGroup.toString());
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaGropEntry.COLUMN_UUID, mediaGroup.getId());
		contentValues.put(MediaGropEntry.COLUMN_CREATE_ADDR, mediaGroup.getCreateAddr());
		contentValues.put(MediaGropEntry.COLUMN_CREATE_TIME, mediaGroup.getCreateTime());
		contentValues.put(MediaGropEntry.COLUMN_ORG_ID, mediaGroup.getOrgId());
		contentValues.put(MediaGropEntry.COLUMN_ORG_NAME, mediaGroup.getOrgName());
		contentValues.put(MediaGropEntry.COLUMN_LATITUDE, mediaGroup.getLatitude());
		contentValues.put(MediaGropEntry.COLUMN_LONGITUDE, mediaGroup.getLongitude());
		contentValues.put(MediaGropEntry.COLUMN_REMARK, mediaGroup.getRemark());
		contentValues.put(MediaGropEntry.COLUMN_MEDIA_TYPE, mediaGroup.getMediaType());
		contentValues.put(MediaGropEntry.COLUMN_USER_ID, mediaGroup.getUserId());
		contentValues.put(MediaGropEntry.COLUMN_USER_NAME, mediaGroup.getUserName());
		contentValues.put(MediaGropEntry.COLUMN_PATH_THUMBNAIL, mediaGroup.getThumbnailPath());

		int row = sqliteDatabase.update(MediaGropEntry.TABLE_NAME, contentValues, MediaGropEntry.COLUMN_UUID + "=?",
				new String[] { mediaGroup.getId() + "" });
		LogUtils.i(TAG, "updateMonitorMediaGroup" + row);
	}

	/** 获取表数据 */
	public List<MonitorMediaGroup> listMonitorMediaGroup(String userId,String mediaType) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + MediaGropEntry.TABLE_NAME + " WHERE "
				+ MediaGropEntry.COLUMN_USER_ID + "=? and "+ MediaGropEntry.COLUMN_MEDIA_TYPE + "=? ORDER BY "+MediaGropEntry.COLUMN_CREATE_TIME+" DESC ", new String[] { userId + "" ,mediaType});
		List<MonitorMediaGroup> list = new ArrayList<MonitorMediaGroup>();
		while (result.moveToNext()) {
			MonitorMediaGroup mediaGroup = new MonitorMediaGroup();
			mediaGroup.setId(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_UUID)));
			mediaGroup
					.setCreateAddr(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_CREATE_ADDR)));
			mediaGroup.setCreateTime(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_CREATE_TIME)));
			mediaGroup.setOrgId(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_ORG_ID)));
			mediaGroup
					.setOrgName(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_ORG_NAME)));
			mediaGroup.setLatitude(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_LATITUDE)));
			mediaGroup.setLongitude(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_LONGITUDE)));
			mediaGroup.setRemark(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_REMARK)));
			mediaGroup.setMediaType(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_MEDIA_TYPE)));
			mediaGroup.setUserId(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_USER_ID)));
			mediaGroup.setUserName(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_USER_NAME)));
			mediaGroup.setThumbnailPath(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_PATH_THUMBNAIL)));

			list.add(mediaGroup);
		}
		LogUtils.i(TAG, "listMonitorMediaGroup" + list.size());
		result.close();
		return list;
	}

	/** 通过id查询一个数据 */
	public MonitorMediaGroup findMonitorMediaGroupById(String monitorMediaGroupId) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + MediaGropEntry.TABLE_NAME + " WHERE "
				+ MediaGropEntry.COLUMN_UUID + "=?", new String[] { monitorMediaGroupId + "" });
		MonitorMediaGroup mediaGroup = new MonitorMediaGroup();
		while (result.moveToNext()) {
			mediaGroup.setId(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_UUID)));
			mediaGroup
					.setCreateAddr(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_CREATE_ADDR)));
			mediaGroup.setCreateTime(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_CREATE_TIME)));
			mediaGroup.setOrgId(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_ORG_ID)));
			mediaGroup
					.setOrgName(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_ORG_NAME)));
			mediaGroup.setLatitude(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_LATITUDE)));
			mediaGroup.setLongitude(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_LONGITUDE)));
			mediaGroup.setRemark(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_REMARK)));
			mediaGroup.setMediaType(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_MEDIA_TYPE)));
			mediaGroup.setUserId(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_USER_ID)));
			mediaGroup.setUserName(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_USER_NAME)));
			mediaGroup.setThumbnailPath(result.getString(result.getColumnIndex(MediaGropEntry.COLUMN_PATH_THUMBNAIL)));

			break;
		}
		LogUtils.i(TAG, "findMonitorMediaGroupById" + monitorMediaGroupId);
		result.close();
		return mediaGroup;
	}

	/** 删除表数据 */
	public void deleteMonitorMediaGroupList(List<MonitorMediaGroup> monitorMediaGroupList) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < monitorMediaGroupList.size(); i++) {
			if (i != monitorMediaGroupList.size() - 1) {
				sb.append("'"+monitorMediaGroupList.get(i).getId() + "',");
			} else {// 最后一个
				sb.append("'"+monitorMediaGroupList.get(i).getId() + "'");
			}
		}
		int row = sqlDb.delete(MediaGropEntry.TABLE_NAME, MediaGropEntry.COLUMN_UUID + " in (" + sb.toString() + ")", null);
		LogUtils.i(TAG, "deleteMonitorMediaGroup-row=" + row);
	}

	/** 删除一个数据 */
	public void deleteMonitorMediaGroup(String monitorMediaGroupId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int row = sqlDb.delete(MediaGropEntry.TABLE_NAME, MediaGropEntry.COLUMN_UUID + " = '" + monitorMediaGroupId+"'", null);
		LogUtils.i(TAG, "deleteMonitorMediaGroup-row=" + row);
	}

	// 上传组操作
	/** 插入一个数据 */
	public long insertMonitorMediaGroupUpload(MonitorMediaGroupUpload mediaGroupUpload) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaGropUploadEntry.COLUMN_UUID, mediaGroupUpload.getId());
		contentValues.put(MediaGropUploadEntry.COLUMN_GROUP_ID, mediaGroupUpload.getMediaGroup().getId());
		contentValues.put(MediaGropUploadEntry.COLUMN_UPLOAD_STATE, mediaGroupUpload.getUploadState());
		contentValues.put(MediaGropUploadEntry.COLUMN_PATH_THUMBNAIL, mediaGroupUpload.getThumbnailPath());
		contentValues.put(MediaGropUploadEntry.COLUMN_REMOTE_DIRECTORY, mediaGroupUpload.getRemoteDirectory());

		long id = sqliteDatabase.insert(MediaGropUploadEntry.TABLE_NAME, null, contentValues);
		LogUtils.i(TAG, "insertMonitorMediaGroupUpload" + id);
		return id;
	}

	/** 更新一个数据 */
	public void updateMonitorMediaGroupUpload(MonitorMediaGroupUpload mediaGroupUpload) {
		//LogUtils.i(TAG, "更新mediaGroupUpload－－" + mediaGroupUpload.toString());
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaGropUploadEntry.COLUMN_UUID, mediaGroupUpload.getId());
		contentValues.put(MediaGropUploadEntry.COLUMN_GROUP_ID, mediaGroupUpload.getMediaGroup().getId());
		contentValues.put(MediaGropUploadEntry.COLUMN_UPLOAD_STATE, mediaGroupUpload.getUploadState());
		contentValues.put(MediaGropUploadEntry.COLUMN_PATH_THUMBNAIL, mediaGroupUpload.getThumbnailPath());
		contentValues.put(MediaGropUploadEntry.COLUMN_REMOTE_DIRECTORY, mediaGroupUpload.getRemoteDirectory());

		int row = sqliteDatabase.update(MediaGropUploadEntry.TABLE_NAME, contentValues,
				MediaGropUploadEntry.COLUMN_UUID + "=?", new String[] { mediaGroupUpload.getId() + "" });
		LogUtils.i(TAG, "updateMonitorMediaGroupUpload" + row);
	}

	/** 获取自定义状态下的组数据 */
	public List<MonitorMediaGroupUpload> listMonitorMediaGroupUploadByUploadState(String userId, String[] uploadStateList) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < uploadStateList.length; i++) {
			if (i != uploadStateList.length - 1) {
				sb.append("'"+uploadStateList[i] + "',");
			} else {// 最后一个
				sb.append("'"+uploadStateList[i] + "'");
			}
		}
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + MediaGropUploadEntry.TABLE_NAME + " WHERE "
				+ MediaGropUploadEntry.COLUMN_UPLOAD_STATE + " in("+sb.toString()+")",null);
		List<MonitorMediaGroupUpload> list = new ArrayList<MonitorMediaGroupUpload>();
		while (result.moveToNext()) {
			MonitorMediaGroupUpload mediaGroupUpload = new MonitorMediaGroupUpload();
			mediaGroupUpload.setId(result.getString(result.getColumnIndex(MediaGropUploadEntry.COLUMN_UUID)));
			String groupId = result.getString(result.getColumnIndex(MediaGropUploadEntry.COLUMN_GROUP_ID));
			MonitorMediaGroup group = findMonitorMediaGroupById(groupId);
			if (group != null && TextUtils.equals(userId, group.getUserId())) {// 是此用户的数据
				mediaGroupUpload.setMediaGroup(group);
				mediaGroupUpload.setUploadState(result.getString(result
						.getColumnIndex(MediaGropUploadEntry.COLUMN_UPLOAD_STATE)));
				mediaGroupUpload.setThumbnailPath(result.getString(result
						.getColumnIndex(MediaGropUploadEntry.COLUMN_PATH_THUMBNAIL)));
				mediaGroupUpload.setRemoteDirectory(result.getString(result
						.getColumnIndex(MediaGropUploadEntry.COLUMN_REMOTE_DIRECTORY)));
				list.add(mediaGroupUpload);
			}
		}
		sortUploadGroupListByTime(list);
		LogUtils.i(TAG, "listMonitorMediaGroupUpload" + list.size());
		result.close();
		return list;
	}
	
	/** 获取表数据 */
	public List<MonitorMediaGroupUpload> listMonitorMediaGroupUpload(String userId, String uploadState) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + MediaGropUploadEntry.TABLE_NAME + " WHERE "
				+ MediaGropUploadEntry.COLUMN_UPLOAD_STATE + "=?", new String[] { uploadState + "" });
		List<MonitorMediaGroupUpload> list = new ArrayList<MonitorMediaGroupUpload>();
		while (result.moveToNext()) {
			MonitorMediaGroupUpload mediaGroupUpload = new MonitorMediaGroupUpload();
			mediaGroupUpload.setId(result.getString(result.getColumnIndex(MediaGropUploadEntry.COLUMN_UUID)));
			String groupId = result.getString(result.getColumnIndex(MediaGropUploadEntry.COLUMN_GROUP_ID));
			MonitorMediaGroup group = findMonitorMediaGroupById(groupId);
			if (group != null && TextUtils.equals(userId, group.getUserId())) {// 是此用户的数据
				mediaGroupUpload.setMediaGroup(group);
				mediaGroupUpload.setUploadState(result.getString(result
						.getColumnIndex(MediaGropUploadEntry.COLUMN_UPLOAD_STATE)));
				mediaGroupUpload.setThumbnailPath(result.getString(result
						.getColumnIndex(MediaGropUploadEntry.COLUMN_PATH_THUMBNAIL)));
				mediaGroupUpload.setRemoteDirectory(result.getString(result
						.getColumnIndex(MediaGropUploadEntry.COLUMN_REMOTE_DIRECTORY)));
				list.add(mediaGroupUpload);
			}
		}
		sortUploadGroupListByTime(list);//排序
		LogUtils.i(TAG, "listMonitorMediaGroupUpload" + list.size());
		result.close();
		return list;
	}

	/** 通过id查询一个数据 */
	public MonitorMediaGroupUpload findMonitorMediaGroupUploadById(String monitorMediaGroupUploadId) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + MediaGropUploadEntry.TABLE_NAME + " WHERE "
				+ MediaGropUploadEntry.COLUMN_UUID + "=?", new String[] { monitorMediaGroupUploadId + "" });
		MonitorMediaGroupUpload mediaGroupUpload = new MonitorMediaGroupUpload();
		while (result.moveToNext()) {
			mediaGroupUpload.setId(result.getString(result.getColumnIndex(MediaGropUploadEntry.COLUMN_UUID)));
			String groupId = result.getString(result.getColumnIndex(MediaGropUploadEntry.COLUMN_GROUP_ID));
			MonitorMediaGroup group = findMonitorMediaGroupById(groupId);
			mediaGroupUpload.setMediaGroup(group);
			mediaGroupUpload.setUploadState(result.getString(result
					.getColumnIndex(MediaGropUploadEntry.COLUMN_UPLOAD_STATE)));
			mediaGroupUpload.setThumbnailPath(result.getString(result
					.getColumnIndex(MediaGropUploadEntry.COLUMN_PATH_THUMBNAIL)));
			mediaGroupUpload.setRemoteDirectory(result.getString(result
					.getColumnIndex(MediaGropUploadEntry.COLUMN_REMOTE_DIRECTORY)));
			break;
		}
		LogUtils.i(TAG, "findMonitorMediaGroupUploadById" + monitorMediaGroupUploadId);
		result.close();
		return mediaGroupUpload;
	}

	/** 删除表数据 */
	public void deleteMonitorMediaGroupUploadList(List<MonitorMediaGroupUpload> monitorMediaGroupUploadList) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < monitorMediaGroupUploadList.size(); i++) {
			if (i != monitorMediaGroupUploadList.size() - 1) {
				sb.append("'"+monitorMediaGroupUploadList.get(i).getId()+ "',");
			} else {// 最后一个
				sb.append("'"+monitorMediaGroupUploadList.get(i).getId() + "'");
			}
		}
		int row = sqlDb.delete(MediaGropUploadEntry.TABLE_NAME, MediaGropUploadEntry.COLUMN_UUID + " in (" + sb.toString()
				+ ")", null);
		LogUtils.i(TAG, "deleteMonitorMediaGroupUploadList-row=" + row);
	}

	/** 删除一个数据 */
	public void deleteMonitorMediaGroupUpload(String monitorMediaGroupUploadId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int row = sqlDb.delete(MediaGropUploadEntry.TABLE_NAME, MediaGropUploadEntry.COLUMN_UUID + " = '"
				+ monitorMediaGroupUploadId+"'", null);
		LogUtils.i(TAG, "deleteMonitorMediaGroupUpload-row=" + row);
	}

	// 拍照数据操作
	/** 插入一个数据 */
	public long insertMonitorMedia(MonitorMedia media) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaEntry.COLUMN_UUID, media.getId());
		contentValues.put(MediaEntry.COLUMN_SHOOTING_LOCATION, media.getShootingLocation());
		contentValues.put(MediaEntry.COLUMN_FILE_NAME, media.getFileName());
		contentValues.put(MediaEntry.COLUMN_PATH, media.getPath());
		contentValues.put(MediaEntry.COLUMN_FILE_SIZE, media.getFileSize());
		contentValues.put(MediaEntry.COLUMN_FILE_STATE, media.getFileState());
		contentValues.put(MediaEntry.COLUMN_FILE_SUFFIX, media.getFileSuffix());
		contentValues.put(MediaEntry.COLUMN_REMARK, media.getRemark());
		contentValues.put(MediaEntry.COLUMN_UPLOAD_GROUP_ID, media.getGroupUploadId());
		contentValues.put(MediaEntry.COLUMN_UPLOAD_TIME, media.getUploadTime());
		contentValues.put(MediaEntry.COLUMN_USER_ID, media.getUserId());
		contentValues.put(MediaEntry.COLUMN_ORIENTATIONE, media.getOrientation());
		contentValues.put(MediaEntry.COLUMN_UPLOAD_STATE, media.getUploadState());
		contentValues.put(MediaEntry.COLUMN_MEDIA_TYPE, media.getMediaType());
		contentValues.put(MediaEntry.COLUMN_PATH_THUMBNAIL, media.getThumbnailPath());
		contentValues.put(MediaEntry.COLUMN_CREATE_TIME, media.getCreateTime());
		contentValues.put(MediaEntry.COLUMN_TIME, media.getTime());
		contentValues.put(MediaEntry.COLUMN_REASON, media.getReason());

		long id = sqliteDatabase.insert(MediaEntry.TABLE_NAME, null, contentValues);
		LogUtils.i(TAG, "insertMonitorMedia" + id);
		return id;
	}
	
	/** 插入一组数据 */
	public void insertMonitorMediaList(List<MonitorMedia> mediaList) {
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		for(MonitorMedia media:mediaList){
			contentValues.put(MediaEntry.COLUMN_UUID, media.getId());
			contentValues.put(MediaEntry.COLUMN_SHOOTING_LOCATION, media.getShootingLocation());
			contentValues.put(MediaEntry.COLUMN_FILE_NAME, media.getFileName());
			contentValues.put(MediaEntry.COLUMN_PATH, media.getPath());
			contentValues.put(MediaEntry.COLUMN_FILE_SIZE, media.getFileSize());
			contentValues.put(MediaEntry.COLUMN_FILE_STATE, media.getFileState());
			contentValues.put(MediaEntry.COLUMN_FILE_SUFFIX, media.getFileSuffix());
			contentValues.put(MediaEntry.COLUMN_REMARK, media.getRemark());
			contentValues.put(MediaEntry.COLUMN_UPLOAD_GROUP_ID, media.getGroupUploadId());
			contentValues.put(MediaEntry.COLUMN_UPLOAD_TIME, media.getUploadTime());
			contentValues.put(MediaEntry.COLUMN_USER_ID, media.getUserId());
			contentValues.put(MediaEntry.COLUMN_ORIENTATIONE, media.getOrientation());
			contentValues.put(MediaEntry.COLUMN_UPLOAD_STATE, media.getUploadState());
			contentValues.put(MediaEntry.COLUMN_MEDIA_TYPE, media.getMediaType());
			contentValues.put(MediaEntry.COLUMN_PATH_THUMBNAIL, media.getThumbnailPath());
			contentValues.put(MediaEntry.COLUMN_CREATE_TIME, media.getCreateTime());
			contentValues.put(MediaEntry.COLUMN_TIME, media.getTime());
			contentValues.put(MediaEntry.COLUMN_REASON, media.getReason());
			long id = sqliteDatabase.insert(MediaEntry.TABLE_NAME, null, contentValues);
			LogUtils.i(TAG, "insertMonitorMedia" + id);
		}
		
	}

	/** 更新一个数据 */
	public void updateMonitorMedia(MonitorMedia media) {
		//LogUtils.i(TAG, "更新media－－" + media.toString());
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaEntry.COLUMN_UUID, media.getId());
		contentValues.put(MediaEntry.COLUMN_SHOOTING_LOCATION, media.getShootingLocation());
		contentValues.put(MediaEntry.COLUMN_FILE_NAME, media.getFileName());
		contentValues.put(MediaEntry.COLUMN_PATH, media.getPath());
		contentValues.put(MediaEntry.COLUMN_FILE_SIZE, media.getFileSize());
		contentValues.put(MediaEntry.COLUMN_FILE_STATE, media.getFileState());
		contentValues.put(MediaEntry.COLUMN_FILE_SUFFIX, media.getFileSuffix());
		contentValues.put(MediaEntry.COLUMN_REMARK, media.getRemark());
		contentValues.put(MediaEntry.COLUMN_UPLOAD_GROUP_ID, media.getGroupUploadId());
		contentValues.put(MediaEntry.COLUMN_UPLOAD_TIME, media.getUploadTime());
		contentValues.put(MediaEntry.COLUMN_USER_ID, media.getUserId());
		contentValues.put(MediaEntry.COLUMN_ORIENTATIONE, media.getOrientation());
		contentValues.put(MediaEntry.COLUMN_UPLOAD_STATE, media.getUploadState());
		contentValues.put(MediaEntry.COLUMN_MEDIA_TYPE, media.getMediaType());
		contentValues.put(MediaEntry.COLUMN_PATH_THUMBNAIL, media.getThumbnailPath());
		contentValues.put(MediaEntry.COLUMN_CREATE_TIME, media.getCreateTime());
		contentValues.put(MediaEntry.COLUMN_TIME, media.getTime());
		contentValues.put(MediaEntry.COLUMN_REASON, media.getReason());
		int row = sqliteDatabase.update(MediaEntry.TABLE_NAME, contentValues,
				MediaEntry.COLUMN_UUID + "=?", new String[] { media.getId() + "" });
		LogUtils.i(TAG, "updateMonitorMedia" + row);
	}
	
	
	/** 更新一组数据 */
	public void updateMonitorMediaList(List<MonitorMedia> mediaList) {
		//LogUtils.i(TAG, "更新media－－" + mediaList);
		SQLiteDatabase sqliteDatabase = getWritableDatabase();
		
		for(MonitorMedia media:mediaList){
			ContentValues contentValues = new ContentValues();
			contentValues.put(MediaEntry.COLUMN_UUID, media.getId());
			contentValues.put(MediaEntry.COLUMN_SHOOTING_LOCATION, media.getShootingLocation());
			contentValues.put(MediaEntry.COLUMN_FILE_NAME, media.getFileName());
			contentValues.put(MediaEntry.COLUMN_PATH, media.getPath());
			contentValues.put(MediaEntry.COLUMN_FILE_SIZE, media.getFileSize());
			contentValues.put(MediaEntry.COLUMN_FILE_STATE, media.getFileState());
			contentValues.put(MediaEntry.COLUMN_FILE_SUFFIX, media.getFileSuffix());
			contentValues.put(MediaEntry.COLUMN_REMARK, media.getRemark());
			contentValues.put(MediaEntry.COLUMN_UPLOAD_GROUP_ID, media.getGroupUploadId());
			contentValues.put(MediaEntry.COLUMN_UPLOAD_TIME, media.getUploadTime());
			contentValues.put(MediaEntry.COLUMN_USER_ID, media.getUserId());
			contentValues.put(MediaEntry.COLUMN_ORIENTATIONE, media.getOrientation());
			contentValues.put(MediaEntry.COLUMN_UPLOAD_STATE, media.getUploadState());
			contentValues.put(MediaEntry.COLUMN_MEDIA_TYPE, media.getMediaType());
			contentValues.put(MediaEntry.COLUMN_PATH_THUMBNAIL, media.getThumbnailPath());
			contentValues.put(MediaEntry.COLUMN_CREATE_TIME, media.getCreateTime());
			contentValues.put(MediaEntry.COLUMN_TIME, media.getTime());
			contentValues.put(MediaEntry.COLUMN_REASON, media.getReason());
			int row = sqliteDatabase.update(MediaEntry.TABLE_NAME, contentValues,
					MediaEntry.COLUMN_UUID + "=?", new String[] { media.getId() + "" });
			LogUtils.i(TAG, "updateMonitorMedia" + row);
		}
	}


	/** 获取表数据 ,未上传按创建时间，其他按上传时间排序*/
	public List<MonitorMedia> listUnUploadMonitorMediaByUserId(String userId,String mediaType) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		String sql="";
		String[] args;
		if(!TextUtils.isEmpty(mediaType)){
			sql="SELECT * FROM " + MediaEntry.TABLE_NAME + " WHERE "
					+ MediaEntry.COLUMN_USER_ID + "=?"+" and "+MediaEntry.COLUMN_UPLOAD_GROUP_ID+"=? and "+MediaEntry.COLUMN_MEDIA_TYPE+"=? ORDER BY "+MediaEntry.COLUMN_CREATE_TIME+" DESC ";
			args=new String[] { userId + "",MonitorTable.NULL_VALUE,mediaType};
		}else{
			sql="SELECT * FROM " + MediaEntry.TABLE_NAME + " WHERE "
					+ MediaEntry.COLUMN_USER_ID + "=?"+" and "+MediaEntry.COLUMN_UPLOAD_GROUP_ID+"=? ORDER BY "+MediaEntry.COLUMN_CREATE_TIME+" DESC ";
			args=new String[] { userId + "",MonitorTable.NULL_VALUE};
		}
		
		Cursor result = sqliteDatabase.rawQuery(sql, args);
		List<MonitorMedia> list = new ArrayList<MonitorMedia>();
		while (result.moveToNext()) {
			MonitorMedia media = new MonitorMedia();
			media.setId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UUID)));
			media.setShootingLocation(result.getString(result.getColumnIndex(MediaEntry.COLUMN_SHOOTING_LOCATION)));
			media.setFileName(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_NAME)));
			media.setPath(result.getString(result.getColumnIndex(MediaEntry.COLUMN_PATH)));
			media.setFileSize(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_SIZE)));
			media.setFileState(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_STATE)));
			media.setFileSuffix(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_SUFFIX)));
			media.setRemark(result.getString(result.getColumnIndex(MediaEntry.COLUMN_REMARK)));
			media.setGroupUploadId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_GROUP_ID)));
			media.setUploadTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_TIME)));
			media.setUserId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_USER_ID)));
			media.setOrientation(result.getString(result.getColumnIndex(MediaEntry.COLUMN_ORIENTATIONE)));
			media.setUploadState(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_STATE)));
			media.setMediaType(result.getString(result.getColumnIndex(MediaEntry.COLUMN_MEDIA_TYPE)));
			media.setThumbnailPath(result.getString(result.getColumnIndex(MediaEntry.COLUMN_PATH_THUMBNAIL)));
			media.setCreateTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_CREATE_TIME)));
			media.setTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_TIME)));
			media.setReason(result.getString(result.getColumnIndex(MediaEntry.COLUMN_REASON)));

			list.add(media);
		}
		LogUtils.i(TAG, "listUnUploadMonitorMediaByUserId" + list.size());
		result.close();
		return list;
	}

	/** 获取表数据 ,未上传按创建时间，其他按上传时间排序*/
	public List<MonitorMedia> listMonitorMediaByGroupUploadId(String groupUploadID) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + MediaEntry.TABLE_NAME + " WHERE "
				+ MediaEntry.COLUMN_UPLOAD_GROUP_ID + "=? ORDER BY "+MediaEntry.COLUMN_UPLOAD_TIME+" DESC ", new String[] { groupUploadID + "" });
		List<MonitorMedia> list = new ArrayList<MonitorMedia>();
		while (result.moveToNext()) {
			MonitorMedia media = new MonitorMedia();
			media.setId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UUID)));
			media.setShootingLocation(result.getString(result.getColumnIndex(MediaEntry.COLUMN_SHOOTING_LOCATION)));
			media.setFileName(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_NAME)));
			media.setPath(result.getString(result.getColumnIndex(MediaEntry.COLUMN_PATH)));
			media.setFileSize(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_SIZE)));
			media.setFileState(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_STATE)));
			media.setFileSuffix(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_SUFFIX)));
			media.setRemark(result.getString(result.getColumnIndex(MediaEntry.COLUMN_REMARK)));
			media.setGroupUploadId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_GROUP_ID)));
			media.setUploadTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_TIME)));
			media.setUserId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_USER_ID)));
			media.setOrientation(result.getString(result.getColumnIndex(MediaEntry.COLUMN_ORIENTATIONE)));
			media.setUploadState(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_STATE)));
			media.setMediaType(result.getString(result.getColumnIndex(MediaEntry.COLUMN_MEDIA_TYPE)));
			media.setThumbnailPath(result.getString(result.getColumnIndex(MediaEntry.COLUMN_PATH_THUMBNAIL)));
			media.setCreateTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_CREATE_TIME)));
			media.setTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_TIME)));
			media.setReason(result.getString(result.getColumnIndex(MediaEntry.COLUMN_REASON)));
			list.add(media);
		}
		LogUtils.i(TAG, "listMonitorMediaByGroupUploadId" + list.size());
		result.close();
		return list;
	}
	
	
	/** 通过id查询一个数据 */
	public MonitorMedia findMonitorMediaById(String monitorMediaId) {
		SQLiteDatabase sqliteDatabase = getReadableDatabase();
		Cursor result = sqliteDatabase.rawQuery("SELECT * FROM " + MediaEntry.TABLE_NAME + " WHERE "
				+ MediaEntry.COLUMN_UUID + "=?", new String[] { monitorMediaId + "" });
		MonitorMedia media = new MonitorMedia();
		while (result.moveToNext()) {
			media.setId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UUID)));
			media.setShootingLocation(result.getString(result.getColumnIndex(MediaEntry.COLUMN_SHOOTING_LOCATION)));
			media.setFileName(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_NAME)));
			media.setPath(result.getString(result.getColumnIndex(MediaEntry.COLUMN_PATH)));
			media.setFileSize(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_SIZE)));
			media.setFileState(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_STATE)));
			media.setFileSuffix(result.getString(result.getColumnIndex(MediaEntry.COLUMN_FILE_SUFFIX)));
			media.setRemark(result.getString(result.getColumnIndex(MediaEntry.COLUMN_REMARK)));
			media.setGroupUploadId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_GROUP_ID)));
			media.setUploadTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_TIME)));
			media.setUserId(result.getString(result.getColumnIndex(MediaEntry.COLUMN_USER_ID)));
			media.setOrientation(result.getString(result.getColumnIndex(MediaEntry.COLUMN_ORIENTATIONE)));
			media.setUploadState(result.getString(result.getColumnIndex(MediaEntry.COLUMN_UPLOAD_STATE)));
			media.setMediaType(result.getString(result.getColumnIndex(MediaEntry.COLUMN_MEDIA_TYPE)));
			media.setThumbnailPath(result.getString(result.getColumnIndex(MediaEntry.COLUMN_PATH_THUMBNAIL)));
			media.setCreateTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_CREATE_TIME)));
			media.setTime(result.getString(result.getColumnIndex(MediaEntry.COLUMN_TIME)));
			media.setReason(result.getString(result.getColumnIndex(MediaEntry.COLUMN_REASON)));
			break;
		}
		LogUtils.i(TAG, "findMonitorMediaById" + monitorMediaId);
		result.close();
		return media;
	}

	/** 删除表数据 */
	public void deleteMonitorMediaList(List<MonitorMedia> monitorMediaList) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < monitorMediaList.size(); i++) {
			if (i != monitorMediaList.size() - 1) {
				sb.append("'"+monitorMediaList.get(i).getId() + "',");
			} else {// 最后一个
				sb.append("'"+monitorMediaList.get(i).getId()+"'");
			}
		}
		int row = sqlDb.delete(MediaEntry.TABLE_NAME, MediaEntry.COLUMN_UUID + " in (" + sb.toString()
				+ ")", null);
		LogUtils.i(TAG, "deleteMonitorMediaList-row=" + row);
	}

	/** 删除一个数据 */
	public int deleteMonitorMedia(String monitorMediaId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int row = sqlDb
				.delete(MediaEntry.TABLE_NAME, MediaEntry.COLUMN_UUID + " = '" + monitorMediaId+"'", null);
		LogUtils.i(TAG, "deleteMonitorMedia-row=" + row);
		return row;
	}
	
	/** 删除数据 */
	public void deleteMonitorMediaByUploadGroupId(String uploadGroupId) {
		SQLiteDatabase sqlDb = getWritableDatabase();
		int row = sqlDb
				.delete(MediaEntry.TABLE_NAME, MediaEntry.COLUMN_UPLOAD_GROUP_ID + " = '" + uploadGroupId+"'", null);
		LogUtils.i(TAG, "deleteMonitorMedia-row=" + row);
	}
	
	

	
	//排序相关
	
	private void sortUploadGroupListByTime(List<MonitorMediaGroupUpload> list){
		if(list!=null){
			Collections.sort(list, groupUploadComparator);
		}
	}
	private static Comparator<MonitorMediaGroupUpload> groupUploadComparator=new Comparator<MonitorMediaGroupUpload>() {
		
		@Override
		public int compare(MonitorMediaGroupUpload arg0,
				MonitorMediaGroupUpload arg1) {
			int result=0;
			if(arg0!=null&&arg1!=null&&arg0.getMediaGroup()!=null&&arg1.getMediaGroup()!=null){
				String timeA=arg0.getMediaGroup().getCreateTime();
				String timeB=arg1.getMediaGroup().getCreateTime();
				try {
//					LogUtils.i(TAG, "对比前timeA="+timeA+"timeB="+timeB);
					timeA=timeA.trim();
					timeA=timeA.replace(" ", "");
					timeA=timeA.replace("-", "");
					timeA=timeA.replace(":", "");
					
					timeB=timeB.trim();
					timeB=timeB.replace(" ", "");
					timeB=timeB.replace("-", "");
					timeB=timeB.replace(":", "");
//					LogUtils.i(TAG, "对比后timeA="+timeA+"timeB="+timeB);
					if(Long.parseLong(timeA)>Long.parseLong(timeB)){
						result=-1;
					}else{
						result=1;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return result;
		}
	};
	

}
