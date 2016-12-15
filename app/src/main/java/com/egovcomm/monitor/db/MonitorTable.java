package com.egovcomm.monitor.db;

import android.provider.BaseColumns;

public final class MonitorTable {
	// To prevent someone from accidentally instantiating the contract class,
	// give it an empty constructor.
	public static final String NULL_VALUE="0";
	private MonitorTable() {
	}

	/** 数据组表 */
	public static abstract class MediaGropEntry implements BaseColumns {
		public static final String TABLE_NAME = "media_group";
		public static final String COLUMN_UUID = "UUID";
		public static final String COLUMN_USER_ID = "USER_ID";
		public static final String COLUMN_USER_NAME = "USER_NAME";
		public static final String COLUMN_ORG_ID = "ORG_ID";
		public static final String COLUMN_ORG_NAME = "ORG_NAME";
		public static final String COLUMN_CREATE_TIME = "CREATE_TIME";
		public static final String COLUMN_CREATE_ADDR = "CREATE_ADDR";
		public static final String COLUMN_LONGITUDE = "LONGITUDE";
		public static final String COLUMN_LATITUDE = "LATITUDE";
		public static final String COLUMN_MEDIA_TYPE = "MEDIA_TYPE";
		public static final String COLUMN_REMARK = "MARK";
		public static final String COLUMN_PATH_THUMBNAIL = "PATH_THUMBNAIL";

	}
	
	/** 上传组表 */
	public static abstract class MediaGropUploadEntry implements BaseColumns {
		public static final String TABLE_NAME = "media_upload_group";
		public static final String COLUMN_UUID = "UUID";
		public static final String COLUMN_GROUP_ID = "GROUP_ID";
		public static final String COLUMN_UPLOAD_STATE = "UPLOAD_STATE";
		public static final String COLUMN_PATH_THUMBNAIL = "PATH_THUMBNAIL";
		public static final String COLUMN_REMOTE_DIRECTORY = "REMOTE_DIRECTORY";

	}

	/** 数据表 */
	public static abstract class MediaEntry implements BaseColumns {
		public static final String TABLE_NAME = "media";
		public static final String COLUMN_UUID = "UUID";
		public static final String COLUMN_USER_ID = "USER_ID";
		public static final String COLUMN_UPLOAD_GROUP_ID = "UPLOAD_GROUP_ID";
		public static final String COLUMN_SHOOTING_LOCATION = "SHOOTING_LOCATION";
		public static final String COLUMN_REMARK = "REMARK";
		public static final String COLUMN_UPLOAD_TIME = "UPLOAD_TIME";
		public static final String COLUMN_FILE_NAME = "FILE_NAME";
		public static final String COLUMN_FILE_SIZE = "FILE_SIZE";
		public static final String COLUMN_PATH = "PATH";
		public static final String COLUMN_FILE_SUFFIX = "FILE_SUFFIX";
		public static final String COLUMN_FILE_STATE = "FILE_STATE";
		public static final String COLUMN_ORIENTATIONE = "ORIENTATION";
		public static final String COLUMN_UPLOAD_STATE = "UPLOAD_STATE";
		public static final String COLUMN_MEDIA_TYPE = "MEDIA_TYPE";
		public static final String COLUMN_PATH_THUMBNAIL = "PATH_THUMBNAIL";
		public static final String COLUMN_CREATE_TIME = "CREATE_TIME";
	}

	/** 位置表 */
	public static abstract class LocationEntry implements BaseColumns {
		public static final String TABLE_NAME = "location";
		public static final String COLUMN_UUID = "UUID";
		public static final String COLUMN_USER_ID = "USER_ID";
		public static final String COLUMN_DEPARTMENT_ID = "DEPARTMENT_ID";
		public static final String COLUMN_CREATE_TIME = "CREATE_TIME";
		public static final String COLUMN_LONGITUDE = "LONGITUDE";
		public static final String COLUMN_LATITUDE = "LATITUDE";
		public static final String COLUMN_REMARK = "REMARK";
		public static final String COLUMN_STATE = "STATE";
	}

}
