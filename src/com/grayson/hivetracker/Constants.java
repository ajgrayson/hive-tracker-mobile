package com.grayson.hivetracker;

public class Constants {
	public static final String API_KEY = "yp38xxvpnokcpc9dd16d38222r84dqoc";

	public static final String PREFS_FILE_NAME = "prefs";
	public static final String PREFS_KEY_AUTH_TOKEN = "AUTH_TOKEN";
	public static final String PREFS_KEY_BOX_USERID = "BOX_USERID";
	public static final String PREFS_KEY_BOX_USERNAME = "BOX_USERNAME";
	public static final String PREFS_KEY_SYNC_STATUS = "SYNC_STATUS";
	public static final String PREFS_KEY_SYNC_MESSAGE = "SYNC_MESSAGE";
	public static final String PREFS_KEY_APP_USERNAME = "APP_USERNAME";
	//public static final String PREFS_SYNC_IS_IN_PROGRESS = "SYNC_IS_IN_PROGRESS";
	public static final String PREFS_SYNC_LAST_COMPLETED = "SYNC_LAST_COMPLETED";
	//public static final String PREFS_SYNC_LAST_TYPE = "SYNC_LAST_TYPE";
	public static final String PREFS_SYNC_ACTION = "SYNC_LAST_ACTION";
	
	public static final String BOX_APP_FOLDER_NAME = "Hive Tracker App Reports";
	public static final String BOX_APP_FOLDER_LOGENTRIES_NAME = "Log Entries";
	public static final String BOX_APP_FOLDER_INSPECTION_RECORDS_NAME = "Inspection Records";
	public static final String BOX_APP_FOLDER_BACKUP_NAME = "Backup";
	public static final String BOX_APP_FOLDER_HISTORY_NAME = "History";
	
	public static final String BOX_APP_FOLDER_BACKUP_HIVE_NAME = "Hive";
	public static final String BOX_APP_FOLDER_BACKUP_CATEGORY_NAME = "Category";
	public static final String BOX_APP_FOLDER_BACKUP_LOCATION_NAME = "Location";
	public static final String BOX_APP_FOLDER_BACKUP_TASK_NAME = "Task";
	public static final String BOX_APP_FOLDER_BACKUP_LOG_NAME = "Log";
	public static final String BOX_APP_FOLDER_CONFIG_NAME = "Config";
	
	public static final String BACKUP_FILENAME_CATEGORY = "category.xml";
	public static final String BACKUP_FILENAME_LOCATION = "location.xml";
	public static final String BACKUP_FILENAME_HIVE = "hive.xml";
	public static final String BACKUP_FILENAME_LOGENTRY = "log_entry.xml";
	public static final String BACKUP_FILENAME_TASK = "task.xml";
	public static final String BACKUP_FILENAME_CATEGORY_HISTORY = "category_history_%timestamp%.xml";
	public static final String BACKUP_FILENAME_LOCATION_HISTORY = "location_history_%timestamp%.xml";
	public static final String BACKUP_FILENAME_HIVE_HISTORY = "hive_history_%timestamp%.xml";
	
	public static final String APP_FOLDERNAME_BACKUP = "backup";
	public static final String APP_FOLDERNAME_HISTORY = "history";
	public static final String APP_FOLDERNAME_REPORTS = "reports";
	public static final String APP_FOLDERNAME_DOWNLOADS = "downloads";	
	
	public static final int REQUEST_CODE_PICK_LOCATION = 0x0000c0df;
	public static final int REQUEST_CODE_BOX_LOGIN = 0x0000c0ff;
	public static final int REQUEST_CODE_APP_LOGIN = 0x0000c0ef;
	public static final int REQUEST_CODE_GET_HARVEST_INFO = 0x0000c0af;
	
	public static final String ACTION_PICK = "com.grayson.intent.PICK";
	
	public static final String SYNC_STATUS_STARTED = "SYNC_STARTED";
	public static final String SYNC_STATUS_FAILED = "SYNC_FAILED";
	public static final String SYNC_STATUS_COMPLETED = "SYNC_COMPLETED";
	public static final String SYNC_STATUS_IN_PROGRESS = "SYNC_IN_PROGRESS";
	
	public static final String SYNC_TYPE_DOWNLOAD_V1 = "SYNC_TYPE_V1_DOWNLOAD";
	public static final String SYNC_TYPE_DOWNLOAD = "SYNC_TYPE_DOWNLOAD";
	public static final String SYNC_TYPE_UPLOAD = "SYNC_TYPE_UPLOAD";
	
	public static final String INTENT_SYNC_UPDATE = "SYNC_UPDATE";
	
	public static final String INTENT_EXTRA_SYNC_STATUS = "SYNC_UPDATE_STATUS";
	public static final String INTENT_EXTRA_SYNC_PROGRESS = "SYNC_UPDATE_PROGRESS";
	public static final String INTENT_EXTRA_SYNC_PROGRESS_MAX = "SYNC_UPDATE_PROGRESS_MAX";
	public static final String INTENT_EXTRA_SYNC_MESSAGE = "SYNC_UPDATE_MESSAGE";

}
