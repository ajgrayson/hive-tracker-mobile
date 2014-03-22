package com.grayson.hivetracker.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.grayson.hivetracker.App;
import com.grayson.hivetracker.Constants;

public class AppDataSource {

	// Database fields
	private SQLiteDatabase database;
	private AppSQLiteHelper dbHelper;

	// private long boxUserId;

	private String userName;

	private String[] allCategoryColumns = { AppSQLiteHelper.COLUMN_CATEGORY_ID, AppSQLiteHelper.COLUMN_CATEGORY_NAME,
			AppSQLiteHelper.COLUMN_CATEGORY_DIRTY, AppSQLiteHelper.COLUMN_CATEGORY_DELETED };

	private String[] allCategoryHistoryColumns = { AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_ID, AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_NAME,
			AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_TIMESTAMP, AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_DELETED,
			AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_USERNAME };

	private String[] allLocationColumns = { AppSQLiteHelper.COLUMN_LOCATION_ID, AppSQLiteHelper.COLUMN_LOCATION_CATEGORY_ID,
			AppSQLiteHelper.COLUMN_LOCATION_NAME, AppSQLiteHelper.COLUMN_LOCATION_MAFID, AppSQLiteHelper.COLUMN_LOCATION_LATITUDE,
			AppSQLiteHelper.COLUMN_LOCATION_LONGITUDE, AppSQLiteHelper.COLUMN_LOCATION_DIRTY, AppSQLiteHelper.COLUMN_LOCATION_DELETED };

	private String[] allLocationHistoryColumns = { AppSQLiteHelper.COLUMN_LOCATION_HISTORY_ID, AppSQLiteHelper.COLUMN_LOCATION_HISTORY_CATEGORY_ID,
			AppSQLiteHelper.COLUMN_LOCATION_HISTORY_NAME, AppSQLiteHelper.COLUMN_LOCATION_HISTORY_MAFID,
			AppSQLiteHelper.COLUMN_LOCATION_HISTORY_LATITUDE, AppSQLiteHelper.COLUMN_LOCATION_HISTORY_LONGITUDE,
			AppSQLiteHelper.COLUMN_LOCATION_HISTORY_TIMESTAMP, AppSQLiteHelper.COLUMN_LOCATION_HISTORY_DELETED,
			AppSQLiteHelper.COLUMN_LOCATION_HISTORY_USERNAME };

	private String[] allHiveColumns = { AppSQLiteHelper.COLUMN_HIVE_ID, AppSQLiteHelper.COLUMN_HIVE_LOCATION_ID, AppSQLiteHelper.COLUMN_HIVE_QRCODE,
			AppSQLiteHelper.COLUMN_HIVE_QUEEN_TYPE, AppSQLiteHelper.COLUMN_HIVE_BEE_ID, AppSQLiteHelper.COLUMN_HIVE_BROOD_ID,
			AppSQLiteHelper.COLUMN_HIVE_HEALTH_ID, AppSQLiteHelper.COLUMN_HIVE_FOOD_ID, AppSQLiteHelper.COLUMN_HIVE_VARROA_ID,
			AppSQLiteHelper.COLUMN_HIVE_VIRUS_ID, AppSQLiteHelper.COLUMN_HIVE_POLLEN_ID, AppSQLiteHelper.COLUMN_HIVE_SPLIT_TYPE,
			AppSQLiteHelper.COLUMN_HIVE_TREATMENT_ID, AppSQLiteHelper.COLUMN_HIVE_TREATMENT_DATE, AppSQLiteHelper.COLUMN_HIVE_WINTERED_DATE,
			AppSQLiteHelper.COLUMN_HIVE_FED_DATE, AppSQLiteHelper.COLUMN_HIVE_HARVESTED_DATE, AppSQLiteHelper.COLUMN_HIVE_MOVING,
			AppSQLiteHelper.COLUMN_HIVE_DIRTY, AppSQLiteHelper.COLUMN_HIVE_DELETED, AppSQLiteHelper.COLUMN_HIVE_IS_VARROA_SAMPLE,
			AppSQLiteHelper.COLUMN_HIVE_VARROA_COUNT, AppSQLiteHelper.COLUMN_HIVE_IS_GOOD_PRODUCER, AppSQLiteHelper.COLUMN_HIVE_TREATMENT_IN };

	private String[] allHiveHistoryColumns = { AppSQLiteHelper.COLUMN_HIVE_HISTORY_ID, AppSQLiteHelper.COLUMN_HIVE_HISTORY_LOCATION_ID,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_QRCODE, AppSQLiteHelper.COLUMN_HIVE_HISTORY_QUEEN_TYPE, AppSQLiteHelper.COLUMN_HIVE_HISTORY_BEE_ID,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_BROOD_ID, AppSQLiteHelper.COLUMN_HIVE_HISTORY_HEALTH_ID, AppSQLiteHelper.COLUMN_HIVE_HISTORY_FOOD_ID,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_VARROA_ID, AppSQLiteHelper.COLUMN_HIVE_HISTORY_VIRUS_ID,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_POLLEN_ID, AppSQLiteHelper.COLUMN_HIVE_HISTORY_SPLIT_TYPE,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_TREATMENT_ID, AppSQLiteHelper.COLUMN_HIVE_HISTORY_TREATMENT_DATE,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_WINTERED_DATE, AppSQLiteHelper.COLUMN_HIVE_HISTORY_FED_DATE,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_HARVESTED_DATE, AppSQLiteHelper.COLUMN_HIVE_HISTORY_MOVING,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_TIMESTAMP, AppSQLiteHelper.COLUMN_HIVE_HISTORY_DELETED, AppSQLiteHelper.COLUMN_HIVE_HISTORY_USERNAME,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_IS_VARROA_SAMPLE, AppSQLiteHelper.COLUMN_HIVE_HISTORY_VARROA_COUNT,
			AppSQLiteHelper.COLUMN_HIVE_HISTORY_IS_GOOD_PRODUCER, AppSQLiteHelper.COLUMN_HIVE_HISTORY_TREATMENT_IN };

	private String[] allTaskColumns = { AppSQLiteHelper.COLUMN_TASK_ID, AppSQLiteHelper.COLUMN_TASK_DATE, AppSQLiteHelper.COLUMN_TASK_DESCRIPTION,
			AppSQLiteHelper.COLUMN_TASK_DONE, AppSQLiteHelper.COLUMN_TASK_HIVE_ID, AppSQLiteHelper.COLUMN_TASK_DIRTY,
			AppSQLiteHelper.COLUMN_TASK_DELETED };

	// private String[] allMoveColumns = { AppSQLiteHelper.COLUMN_MOVE_ID,
	// AppSQLiteHelper.COLUMN_MOVE_HIVE_ID, AppSQLiteHelper.COLUMN_MOVE_QRCODE
	// };

	private String[] allLogEntryColumns = { AppSQLiteHelper.COLUMN_LOGENTRY_ID, AppSQLiteHelper.COLUMN_LOGENTRY_DATE,
			AppSQLiteHelper.COLUMN_LOGENTRY_DESCRIPTION, AppSQLiteHelper.COLUMN_LOGENTRY_DIRTY, AppSQLiteHelper.COLUMN_LOGENTRY_DELETED };

	private String[] allHarvestRecordColumns = { AppSQLiteHelper.COLUMN_HARVEST_RECORD_ID, AppSQLiteHelper.COLUMN_HARVEST_RECORD_CATEGORY_ID,
			AppSQLiteHelper.COLUMN_HARVEST_RECORD_LOCATION_ID, AppSQLiteHelper.COLUMN_HARVEST_RECORD_DATE,
			AppSQLiteHelper.COLUMN_HARVEST_RECORD_DISEASE, AppSQLiteHelper.COLUMN_HARVEST_RECORD_SUPERS,
			AppSQLiteHelper.COLUMN_HARVEST_RECORD_FLORAL_TYPE_ID };

	private String[] allListTables = { AppSQLiteHelper.TABLE_LIST_QUEEN, AppSQLiteHelper.TABLE_LIST_BEE, AppSQLiteHelper.TABLE_LIST_BROOD,
			AppSQLiteHelper.TABLE_LIST_FOOD, AppSQLiteHelper.TABLE_LIST_HEALTH, AppSQLiteHelper.TABLE_LIST_POLLEN, AppSQLiteHelper.TABLE_LIST_VARROA,
			AppSQLiteHelper.TABLE_LIST_VIRUS, AppSQLiteHelper.TABLE_LIST_TREATMENT, AppSQLiteHelper.TABLE_LIST_FLORAL_TYPE };

	// private String[] allSyncColumns = { AppSQLiteHelper.COLUMN_SYNC_ID,
	// AppSQLiteHelper.COLUMN_SYNC_STATUS, AppSQLiteHelper.COLUMN_SYNC_MESSAGE
	// };

	private List<String> tableList = Arrays.asList(allListTables);

	private String[] allListTableColumns = { "_id", "name" };

	// private String[] allQueenColumns = {
	// AppSQLiteHelper.COLUMN_LIST_QUEEN_ID,
	// AppSQLiteHelper.COLUMN_LIST_QUEEN_NAME };
	//
	// private String[] allBroodColumns = {
	// AppSQLiteHelper.COLUMN_LIST_BROOD_ID,
	// AppSQLiteHelper.COLUMN_LIST_BROOD_NAME };
	//
	// private String[] allBeeColumns = { AppSQLiteHelper.COLUMN_LIST_BEE_ID,
	// AppSQLiteHelper.COLUMN_LIST_BEE_NAME };
	//
	// private String[] allFoodColumns = { AppSQLiteHelper.COLUMN_LIST_FOOD_ID,
	// AppSQLiteHelper.COLUMN_LIST_FOOD_NAME };
	//
	// private String[] allHealthColumns = {
	// AppSQLiteHelper.COLUMN_LIST_HEALTH_ID,
	// AppSQLiteHelper.COLUMN_LIST_HEALTH_NAME };
	//
	// private String[] allVarroaColumns = {
	// AppSQLiteHelper.COLUMN_LIST_VARROA_ID,
	// AppSQLiteHelper.COLUMN_LIST_VARROA_NAME };
	//
	// private String[] allVirusColumns = {
	// AppSQLiteHelper.COLUMN_LIST_VIRUS_ID,
	// AppSQLiteHelper.COLUMN_LIST_VIRUS_NAME };
	//
	// private String[] allPollenColumns = {
	// AppSQLiteHelper.COLUMN_LIST_POLLEN_ID,
	// AppSQLiteHelper.COLUMN_LIST_POLLEN_NAME };

	private static AppDataSource instance = null;

	protected AppDataSource() {
		dbHelper = new AppSQLiteHelper();
	}

	public static AppDataSource getInstance() {
		if (instance == null) {
			instance = new AppDataSource();
		}
		return instance;
	}

	public AppDataSource open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	public AppDataSource close() {
		dbHelper.close();
		return this;
	}

	public void clearDatabase() {
		database.delete(AppSQLiteHelper.TABLE_CATEGORY, null, null);
		database.delete(AppSQLiteHelper.TABLE_CATEGORY_HISTORY, null, null);
		database.delete(AppSQLiteHelper.TABLE_HIVE, null, null);
		database.delete(AppSQLiteHelper.TABLE_HIVE_HISTORY, null, null);
		database.delete(AppSQLiteHelper.TABLE_LOCATION, null, null);
		database.delete(AppSQLiteHelper.TABLE_LOCATION_HISTORY, null, null);
		database.delete(AppSQLiteHelper.TABLE_LOGENTRY, null, null);
		database.delete(AppSQLiteHelper.TABLE_TASK, null, null);
	}

	protected String getUserName() {
		if (userName == null) {
			userName = App.getSharedPrefs().getString(Constants.PREFS_KEY_APP_USERNAME, null);
		}
		return userName;
	}

	/**
	 * Returns true if there is no data in the database. looks at the following
	 * tables - hive - category - location - task - log - change
	 * 
	 * This method opens/closes the DB so these are not required externally.
	 * 
	 * @return true if the database is empty
	 */
	public boolean isEmpty() {

		boolean empty = true;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, allCategoryColumns, null, null, null, null, null);
		empty &= cursor.getCount() == 0;
		cursor.close();

		cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, null, null, null, null, null);
		empty &= cursor.getCount() == 0;
		cursor.close();

		cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, null, null, null, null, null);
		empty &= cursor.getCount() == 0;
		cursor.close();

		cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, null, null, null, null, null);
		empty &= cursor.getCount() == 0;
		cursor.close();

		cursor = database.query(AppSQLiteHelper.TABLE_LOGENTRY, allLogEntryColumns, null, null, null, null, null);
		empty &= cursor.getCount() == 0;
		cursor.close();

		return empty;
	}

	public long getTimeStamp() {

		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");

		Date date = new Date();

		return date.getTime(); // df.format(date);
	}

	private void logCategoryHistory(long id, Boolean deleted) {
		if (id < 0)
			return;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, allCategoryColumns, "_id = " + String.valueOf(id), null, null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			return;
		}

		cursor.moveToFirst();

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_ID, cursor.getLong(0));
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_NAME, cursor.getString(1));
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_TIMESTAMP, getTimeStamp());
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_DELETED, deleted ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_USERNAME, getUserName());

		cursor.close();

		database.insert(AppSQLiteHelper.TABLE_CATEGORY_HISTORY, null, values);

	}

	private void logLocationHistory(long id, Boolean deleted) {
		if (id < 0)
			return;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, "_id = " + String.valueOf(id), null, null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			return;
		}

		cursor.moveToFirst();

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOCATION_HISTORY_ID, cursor.getLong(0));
		values.put(AppSQLiteHelper.COLUMN_LOCATION_CATEGORY_ID, cursor.getString(1));
		values.put(AppSQLiteHelper.COLUMN_LOCATION_HISTORY_NAME, cursor.getString(2));
		values.put(AppSQLiteHelper.COLUMN_LOCATION_HISTORY_MAFID, cursor.getString(3));
		values.put(AppSQLiteHelper.COLUMN_LOCATION_HISTORY_LATITUDE, cursor.getString(4));
		values.put(AppSQLiteHelper.COLUMN_LOCATION_HISTORY_LONGITUDE, cursor.getString(5));
		values.put(AppSQLiteHelper.COLUMN_LOCATION_HISTORY_TIMESTAMP, getTimeStamp());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_HISTORY_DELETED, deleted ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_LOCATION_HISTORY_USERNAME, getUserName());

		cursor.close();

		database.insert(AppSQLiteHelper.TABLE_LOCATION_HISTORY, null, values);

	}

	private void logHiveHistory(long id, Boolean deleted) {
		if (id < 0)
			return;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, "_id = " + String.valueOf(id), null, null, null, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			return;
		}

		cursor.moveToFirst();

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_ID, cursor.getLong(0));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_LOCATION_ID, cursor.getString(1));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_QRCODE, cursor.getString(2));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_QUEEN_TYPE, cursor.getString(3));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_BEE_ID, cursor.getString(4));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_BROOD_ID, cursor.getString(5));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_HEALTH_ID, cursor.getString(6));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_FOOD_ID, cursor.getString(7));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_VARROA_ID, cursor.getString(8));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_VIRUS_ID, cursor.getString(9));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_POLLEN_ID, cursor.getString(10));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_TREATMENT_ID, cursor.getString(11));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_SPLIT_TYPE, cursor.getString(12));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_TREATMENT_DATE, cursor.getString(13));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_WINTERED_DATE, cursor.getString(14));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_FED_DATE, cursor.getString(15));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_HARVESTED_DATE, cursor.getString(16));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_MOVING, cursor.getString(17));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_TIMESTAMP, getTimeStamp());
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_DELETED, deleted ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_USERNAME, getUserName());
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_IS_VARROA_SAMPLE, cursor.getLong(20));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_VARROA_COUNT, cursor.getLong(21));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_IS_GOOD_PRODUCER, cursor.getLong(22));
		values.put(AppSQLiteHelper.COLUMN_HIVE_HISTORY_TREATMENT_IN, cursor.getLong(23));

		cursor.close();

		database.insert(AppSQLiteHelper.TABLE_HIVE_HISTORY, null, values);

	}

	public Boolean isCategoryHistory() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY_HISTORY, new String[] { AppSQLiteHelper.COLUMN_CATEGORY_HISTORY_ID }, null,
				null, null, null, null);
		int rows = cursor.getCount();
		cursor.close();
		return rows > 0;
	}

	public Boolean isLocationHistory() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION_HISTORY, new String[] { AppSQLiteHelper.COLUMN_LOCATION_HISTORY_ID }, null,
				null, null, null, null);
		int rows = cursor.getCount();
		cursor.close();
		return rows > 0;
	}

	public Boolean isHiveHistory() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE_HISTORY, new String[] { AppSQLiteHelper.COLUMN_HIVE_HISTORY_ID }, null, null, null,
				null, null);
		int rows = cursor.getCount();
		cursor.close();
		return rows > 0;
	}

	public boolean isCategoryEmpty(long categoryId) {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, "category_id = " + String.valueOf(categoryId), null, null,
				null, null);
		Boolean empty = cursor.getCount() == 0;
		cursor.close();
		return empty;
	}

	/**
	 * are any hives associated with this location.
	 * 
	 * @param locationId
	 * @return
	 */
	public boolean isLocationEmpty(long locationId) {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, "location_id = " + String.valueOf(locationId), null, null, null,
				null);
		Boolean empty = cursor.getCount() == 0;
		cursor.close();
		return empty;
	}

	public boolean isValidListTablename(String tablename) {
		if (!tableList.contains(tablename)) {
			return false;
		}
		return true;
	}

	public void clearCategoryHistory() {
		database.delete(AppSQLiteHelper.TABLE_CATEGORY_HISTORY, null, null);
	}

	public void clearLocationHistory() {
		database.delete(AppSQLiteHelper.TABLE_LOCATION_HISTORY, null, null);
	}

	public void clearHiveHistory() {
		database.delete(AppSQLiteHelper.TABLE_HIVE_HISTORY, null, null);
	}

	public void clearHarvestRecords() {
		database.delete(AppSQLiteHelper.TABLE_HARVEST_RECORD, null, null);
	}

	public void clearListValuesTable(String tablename) {
		if (!tableList.contains(tablename)) {
			return;
		}
		database.delete(tablename, null, null);
	}

	public void clearCategoryDirtyFlag(long id) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_DIRTY, 0);

		database.update(AppSQLiteHelper.TABLE_CATEGORY, values, "_id = " + String.valueOf(id), null);
	}

	public void clearLocationDirtyFlag(long id) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOCATION_DIRTY, 0);

		database.update(AppSQLiteHelper.TABLE_LOCATION, values, "_id = " + String.valueOf(id), null);
	}

	public void clearHiveDirtyFlag(long id) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 0);

		database.update(AppSQLiteHelper.TABLE_HIVE, values, "_id = " + String.valueOf(id), null);
	}

	public void clearLogEntryDirtyFlag(long id) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DIRTY, 0);

		database.update(AppSQLiteHelper.TABLE_LOGENTRY, values, "_id = " + String.valueOf(id), null);
	}

	public void clearTaskDirtyFlag(long id) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_TASK_DIRTY, 0);

		database.update(AppSQLiteHelper.TABLE_TASK, values, "_id = " + String.valueOf(id), null);
	}

	public boolean doesCategoryAlreadyExist(String name) {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, new String[] { AppSQLiteHelper.COLUMN_CATEGORY_ID },
				"lower(" + AppSQLiteHelper.COLUMN_CATEGORY_NAME + ") = '" + name.toLowerCase() + "'", null, null, null, null);

		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}

	public boolean doesLocationAlreadyExist(String name) {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, new String[] { AppSQLiteHelper.COLUMN_LOCATION_ID },
				"lower(" + AppSQLiteHelper.COLUMN_LOCATION_NAME + ") = '" + name.toLowerCase() + "'", null, null, null, null);

		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}

	public Category createCategory(String category) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_NAME, category);
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_DIRTY, 1);

		long insertId = database.insert(AppSQLiteHelper.TABLE_CATEGORY, null, values);

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, allCategoryColumns, AppSQLiteHelper.COLUMN_CATEGORY_ID + " = " + insertId,
				null, null, null, null);

		cursor.moveToFirst();

		Category newCategory = cursorToCategory(cursor);

		cursor.close();

		logCategoryHistory(insertId, false);

		return newCategory;
	}

	public Location createLocation(String location) {
		return createLocation(location, 0, 0, 0, 0);
	}

	public Location createLocation(String location, long category_id, long mafId) {
		return createLocation(location, category_id, mafId, 0, 0);
	}

	public Location createLocation(String location, long category_id) {
		return createLocation(location, category_id, 0, 0, 0);
	}

	public Location createLocation(String location, long category_id, long mafId, double latitude, double longitude) {

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOCATION_NAME, location);
		values.put(AppSQLiteHelper.COLUMN_LOCATION_CATEGORY_ID, category_id);
		values.put(AppSQLiteHelper.COLUMN_LOCATION_MAFID, mafId);
		values.put(AppSQLiteHelper.COLUMN_LOCATION_LATITUDE, latitude);
		values.put(AppSQLiteHelper.COLUMN_LOCATION_LONGITUDE, longitude);
		values.put(AppSQLiteHelper.COLUMN_LOCATION_DIRTY, 1);

		long insertId = database.insert(AppSQLiteHelper.TABLE_LOCATION, null, values);

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, AppSQLiteHelper.COLUMN_LOCATION_ID + " = " + insertId,
				null, null, null, null);

		cursor.moveToFirst();

		Location newLocation = cursorToLocation(cursor);

		cursor.close();

		logLocationHistory(insertId, false);

		return newLocation;
	}

	public Hive createHive(String qrCode) {
		Hive hive = getHive(qrCode);
		if (hive != null) {
			return hive;
		} else {
			ContentValues values = new ContentValues();
			values.put(AppSQLiteHelper.COLUMN_HIVE_QRCODE, qrCode);
			values.put(AppSQLiteHelper.COLUMN_HIVE_QUEEN_TYPE, "");
			
			// foreign keys
			values.put(AppSQLiteHelper.COLUMN_HIVE_BEE_ID, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_BROOD_ID, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_HEALTH_ID, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_FOOD_ID, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_VARROA_ID, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_VIRUS_ID, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_POLLEN_ID, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_ID, 0);
			
			values.put(AppSQLiteHelper.COLUMN_HIVE_SPLIT_TYPE, "");
			values.put(AppSQLiteHelper.COLUMN_HIVE_MOVING, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);
			values.put(AppSQLiteHelper.COLUMN_HIVE_IS_VARROA_SAMPLE, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_VARROA_COUNT, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_IS_GOOD_PRODUCER, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_FED_DATE, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_HARVESTED_DATE, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_DATE, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_WINTERED_DATE, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_IN, 0);
			values.put(AppSQLiteHelper.COLUMN_HIVE_DELETED, 0);

			long insertId = database.insert(AppSQLiteHelper.TABLE_HIVE, null, values);

			Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, AppSQLiteHelper.COLUMN_HIVE_ID + " = " + insertId, null, null,
					null, null);

			cursor.moveToFirst();

			Hive newHive = cursorToHive(cursor);

			cursor.close();

			logHiveHistory(insertId, false);

			return newHive;
		}
	}

	public Task createTask(String desc, long date, long hiveId) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_TASK_DESCRIPTION, desc);
		values.put(AppSQLiteHelper.COLUMN_TASK_DATE, date);
		values.put(AppSQLiteHelper.COLUMN_TASK_DONE, 0);
		values.put(AppSQLiteHelper.COLUMN_TASK_HIVE_ID, hiveId);
		values.put(AppSQLiteHelper.COLUMN_TASK_DIRTY, 1);

		long insertId = database.insert(AppSQLiteHelper.TABLE_TASK, null, values);

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, AppSQLiteHelper.COLUMN_TASK_ID + " = " + insertId, null, null,
				null, null);

		cursor.moveToFirst();

		Task newEntry = cursorToTask(cursor);

		cursor.close();

		return newEntry;
	}

	public LogEntry createLogEntry(String desc, long date) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DESCRIPTION, desc);
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DATE, date);
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DIRTY, 1);

		long insertId = database.insert(AppSQLiteHelper.TABLE_LOGENTRY, null, values);

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOGENTRY, allLogEntryColumns, AppSQLiteHelper.COLUMN_LOGENTRY_ID + " = " + insertId,
				null, null, null, null);

		cursor.moveToFirst();

		LogEntry newEntry = cursorToLogEntry(cursor);

		cursor.close();

		return newEntry;
	}

	public HarvestRecord enterHarvestRecord(long categoryId, long locationId, long date, boolean disease, long supers, long floralType) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HARVEST_RECORD_CATEGORY_ID, categoryId);
		values.put(AppSQLiteHelper.COLUMN_HARVEST_RECORD_LOCATION_ID, locationId);
		values.put(AppSQLiteHelper.COLUMN_HARVEST_RECORD_DATE, date);
		values.put(AppSQLiteHelper.COLUMN_HARVEST_RECORD_DISEASE, disease ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_HARVEST_RECORD_SUPERS, supers);
		values.put(AppSQLiteHelper.COLUMN_HARVEST_RECORD_FLORAL_TYPE_ID, floralType);

		long insertId = database.insert(AppSQLiteHelper.TABLE_HARVEST_RECORD, null, values);

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HARVEST_RECORD, allHarvestRecordColumns, AppSQLiteHelper.COLUMN_HARVEST_RECORD_ID
				+ " = " + insertId, null, null, null, null);

		cursor.moveToFirst();

		HarvestRecord newEntry = cursorToHarvestRecord(cursor);

		cursor.close();

		return newEntry;
	}

	/**
	 * this method requires the category to already have its primary key value
	 * etc. only use if importing from a backup file.
	 * 
	 * @param category
	 */
	public boolean importCategory(Category category) {
		if (category == null)
			return false;

		if (category.getId() < 0)
			return false;

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_ID, category.getId());
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_NAME, category.getName());
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_DELETED, category.isDeleted() ? 1 : 0);
		
		boolean success = false;
		
		Category existingCategory = getCategory(category.getId());
		if(existingCategory != null) {
			String str = String.valueOf(category.getId());
			int updatedRows = database.update(AppSQLiteHelper.TABLE_CATEGORY, values, "_id = " + str, null);
			success = updatedRows > 0;
		} else {
			long insertId = database.insert(AppSQLiteHelper.TABLE_CATEGORY, null, values);
			success = insertId > 0;
		}
		// dont want to do it here because we could end up with duplicates
		// logCategoryHistory(insertId, false);

		return success;
	}

	public boolean importLocation(Location location) {
		if (location == null)
			return false;

		if (location.getId() < 0)
			return false;

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOCATION_ID, location.getId());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_CATEGORY_ID, location.getCategoryId());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_NAME, location.getName());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_MAFID, location.getMafId());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_LATITUDE, location.getLatitude());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_LONGITUDE, location.getLongitude());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_DELETED, location.isDeleted() ? 1 : 0);

		boolean success = false;
		
		Location existingLocation = getLocation(location.getId());
		if(existingLocation != null) {
			String str = String.valueOf(location.getId());
			int updatedRows = database.update(AppSQLiteHelper.TABLE_LOCATION, values, "_id = " + str, null);
			success = updatedRows > 0;
		} else {
			long insertId = database.insert(AppSQLiteHelper.TABLE_LOCATION, null, values);
			success = insertId > 0;
		}
		
		// dont want to do it here because we could end up with duplicates
		// logLocationHistory(insertId, false);

		return success;
	}

	public boolean importHive(Hive hive) {
		if (hive == null) {
			return false;
		}

		if (hive.getId() < 0) {
			return false;
		}

		// ended up with some duplication for some reason
		// which really could only happen with hives since it has
		// a PK and a QR code which should both be unique
		if (getHive(hive.getQrCode()) != null) {
			return false; // this QR code already exists
		}

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_ID, hive.getId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_QRCODE, hive.getQrCode());
		values.put(AppSQLiteHelper.COLUMN_HIVE_LOCATION_ID, hive.getLocationId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_QUEEN_TYPE, hive.getQueenType());
		values.put(AppSQLiteHelper.COLUMN_HIVE_BEE_ID, hive.getBeeId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_BROOD_ID, hive.getBroodId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_HEALTH_ID, hive.getHealthId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_FOOD_ID, hive.getFoodId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_VARROA_ID, hive.getVarroaId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_VIRUS_ID, hive.getVirusId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_POLLEN_ID, hive.getPollenId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_ID, hive.getTreatmentId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_SPLIT_TYPE, hive.getSplitType());
		values.put(AppSQLiteHelper.COLUMN_HIVE_MOVING, hive.getMoving() ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_IS_GOOD_PRODUCER, hive.getIsGoodProducer() ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_IS_VARROA_SAMPLE, hive.getIsVarroaSample() ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_VARROA_COUNT, hive.getVarroaCount());
		values.put(AppSQLiteHelper.COLUMN_HIVE_DELETED, hive.isDeleted() ? 1 : 0);

		boolean success = false;
		
		Hive existingHive = getHive(hive.getQrCode());
		if(existingHive != null) {
			String str = String.valueOf(hive.getId());
			int updatedRows = database.update(AppSQLiteHelper.TABLE_HIVE, values, "_id = " + str, null);
			success = updatedRows > 0;
		} else {
			long insertId = database.insert(AppSQLiteHelper.TABLE_HIVE, null, values);
			success = insertId > 0;
		}
		
		// don't want to do it here because we could end up with duplicates
		// logHiveHistory(insertId, false);

		return success;
	}

	public boolean importLogEntry(LogEntry logEntry) {
		if (logEntry == null)
			return false;

		if (logEntry.getId() < 0)
			return false;

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_ID, logEntry.getId());
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DESCRIPTION, logEntry.getDescription());
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DATE, logEntry.getDate());
		
		boolean success = false;
		
		LogEntry existingLogEntry = getLogEntry(logEntry.getId());
		if(existingLogEntry != null) {
			String str = String.valueOf(logEntry.getId());
			int updatedRows = database.update(AppSQLiteHelper.TABLE_LOGENTRY, values, "_id = " + str, null);
			success = updatedRows > 0;
		} else {
			long insertId = database.insert(AppSQLiteHelper.TABLE_LOGENTRY, null, values);
			success = insertId > 0;
		}

		return success;
	}

	public boolean importTask(Task task) {
		if (task == null)
			return false;

		if (task.getId() < 0)
			return false;

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_TASK_ID, task.getId());
		values.put(AppSQLiteHelper.COLUMN_TASK_HIVE_ID, task.getHiveId());
		values.put(AppSQLiteHelper.COLUMN_TASK_DESCRIPTION, task.getDescription());
		values.put(AppSQLiteHelper.COLUMN_TASK_DONE, task.getDone() ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_TASK_DATE, task.getDate());

		boolean success = false;
		
		Task existingTask = getTask(task.getId());
		if(existingTask != null) {
			String str = String.valueOf(task.getId());
			int updatedRows = database.update(AppSQLiteHelper.TABLE_TASK, values, "_id = " + str, null);
			success = updatedRows > 0;
		} else {
			long insertId = database.insert(AppSQLiteHelper.TABLE_TASK, null, values);
			success = insertId > 0;
		}

		return success;
	}

	public boolean importListValue(String tablename, ListValue value) {
		if (value == null)
			return false;

		if (value.getId() < 0)
			return false;

		if (!tableList.contains(tablename)) {
			return false;
		}

		ContentValues values = new ContentValues();
		values.put("_id", value.getId());
		values.put("name", value.getName());

		boolean success = false;
		
		Cursor cursor = database.query(tablename, allListTableColumns, "_id = " + String.valueOf(value.getId()), null, null, null, null);
		boolean doesExist = cursor.getCount() > 0;
		cursor.close();
		
		if (doesExist) {
			String str = String.valueOf(value.getId());
			int updatedRows = database.update(tablename, values, "_id = " + str, null);
			success = updatedRows > 0;
		} else {
			long insertId = database.insert(tablename, null, values);
			success = insertId > 0;
		}
		
		return success;
	}

	public Cursor getCategoryHistoryCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY_HISTORY, allCategoryHistoryColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getLocationHistoryCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION_HISTORY, allLocationHistoryColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getHiveHistoryCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE_HISTORY, allHiveHistoryColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getCategoryCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, allCategoryColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getLocationCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getHiveCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getLogEntryCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOGENTRY, allLogEntryColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getTaskCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getHarvestRecordCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HARVEST_RECORD, allHarvestRecordColumns, null, null, null, null, null);
		return cursor;
	}

	public Cursor getTasksOrderedByDateCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, null, null, null, null, AppSQLiteHelper.COLUMN_TASK_DATE
				+ " DESC ");

		return cursor;
	}

	public Cursor getDirtyCategoryCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, allCategoryColumns, "dirty=1", null, null, null, null);
		return cursor;
	}

	public Cursor getDirtyLocationCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, "dirty=1", null, null, null, null);
		return cursor;
	}

	public Cursor getDirtyHiveCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, "dirty=1", null, null, null, null);
		return cursor;
	}

	public Cursor getDirtyTaskCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, "dirty=1 AND deleted IS NOT 1", null, null, null, null);
		return cursor;
	}

	public Cursor getDirtyLogEntryCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOGENTRY, allLogEntryColumns, "dirty=1 AND deleted IS NOT 1", null, null, null, null);
		return cursor;
	}

//	public Cursor getDeletedCategoryCursor() {
//		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, allCategoryColumns, "deleted IS 1", null, null, null, null);
//		return cursor;
//	}
//
//	public Cursor getDeletedLocationCursor() {
//		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, "deleted IS 1", null, null, null, null);
//		return cursor;
//	}
//
//	public Cursor getDeletedHiveCursor() {
//		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, "deleted IS 1", null, null, null, null);
//		return cursor;
//	}

	public Cursor getDeletedTaskCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, "deleted IS 1", null, null, null, null);
		return cursor;
	}

	public Cursor getDeletedLogEntryCursor() {
		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOGENTRY, allLogEntryColumns, "deleted IS 1", null, null, null, null);
		return cursor;
	}

	public List<Category> getAllCategories() {
		List<Category> categories = new ArrayList<Category>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, allCategoryColumns, "deleted IS NOT 1", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Category category = cursorToCategory(cursor);
			categories.add(category);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return categories;
	}

	public List<Location> getAllLocations() {
		List<Location> locations = new ArrayList<Location>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, "deleted IS NOT 1", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return locations;

	}

	public List<Location> getAllLocationsForCategory(long categoryId) {
		List<Location> locations = new ArrayList<Location>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, "category_id = " + categoryId + " AND deleted IS NOT 1",
				null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return locations;
	}

	public List<Hive> getAllHives() {
		List<Hive> hives = new ArrayList<Hive>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, "deleted IS NOT 1", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Hive hive = cursorToHive(cursor);
			hives.add(hive);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return hives;
	}

	public List<Hive> getAllHivesBeingMoved() {
		List<Hive> hives = new ArrayList<Hive>();

		// Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE,
		// allHiveColumns, "moving = 1", null, null,
		// null, null);

		final String ALL_HIVES_W_LOCATION_QUERY = "SELECT hives.*, locations.name FROM " + AppSQLiteHelper.TABLE_HIVE + " hives LEFT JOIN "
				+ AppSQLiteHelper.TABLE_LOCATION + " locations ON hives.location_id=locations._id WHERE hives.moving=1 AND hives.deleted IS NOT 1";

		Cursor cursor = database.rawQuery(ALL_HIVES_W_LOCATION_QUERY, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Hive hive = cursorToHive(cursor);
			hives.add(hive);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return hives;
	}

	public List<Hive> getAllHivesWithFieldNames() {
		List<Hive> hives = new ArrayList<Hive>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, "deleted IS NOT 1", null, null, null, null);

		List<ListValue> bee = getAllValues(AppSQLiteHelper.TABLE_LIST_BEE);
		List<ListValue> brood = getAllValues(AppSQLiteHelper.TABLE_LIST_BROOD);
		List<ListValue> food = getAllValues(AppSQLiteHelper.TABLE_LIST_FOOD);
		List<ListValue> health = getAllValues(AppSQLiteHelper.TABLE_LIST_HEALTH);
		List<ListValue> pollen = getAllValues(AppSQLiteHelper.TABLE_LIST_POLLEN);
		// List<ListValue> queen =
		// getAllValues(AppSQLiteHelper.TABLE_LIST_QUEEN);
		// List<ListValue> treatment =
		// getAllValues(AppSQLiteHelper.TABLE_LIST_TREATMENT);
		List<ListValue> varroa = getAllValues(AppSQLiteHelper.TABLE_LIST_VARROA);
		List<ListValue> virus = getAllValues(AppSQLiteHelper.TABLE_LIST_VIRUS);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Hive hive = cursorToHive(cursor);

			int index = getIndexSafe(bee, hive.getBeeId());
			String name = bee.get(index).getName();
			hive.setBeeName(name);

			index = getIndexSafe(brood, hive.getBroodId());
			name = brood.get(index).getName();
			hive.setBroodName(name);

			index = getIndexSafe(food, hive.getFoodId());
			name = food.get(index).getName();
			hive.setFoodName(name);

			index = getIndexSafe(health, hive.getHealthId());
			name = health.get(index).getName();
			hive.setHealthName(name);

			index = getIndexSafe(pollen, hive.getPollenId());
			name = pollen.get(index).getName();
			hive.setPollenName(name);

			// index = getIndex(treatment, hive.getTreatmentId());
			// name = treatment.get(index).getName();
			// hive.setTreatmentName(name);

			index = getIndexSafe(varroa, hive.getVarroaId());
			name = varroa.get(index).getName();
			hive.setVarroaName(name);

			index = getIndexSafe(virus, hive.getVirusId());
			name = virus.get(index).getName();
			hive.setVirusName(name);

			// index = getIndex(queen, hive.getSplitId());
			// name = queen.get(index).getName();
			// hive.setSplitName(name);

			hives.add(hive);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return hives;
	}

	public List<Hive> getAllHivesForLocation(long locationId) {
		List<Hive> hives = new ArrayList<Hive>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, "location_id = " + locationId + " AND deleted IS NOT 1", null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Hive hive = cursorToHive(cursor);
			hives.add(hive);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return hives;
	}

	public List<Hive> getAllHivesForCategory(long categoryId) {
		List<Hive> hives = new ArrayList<Hive>();

		final String ALL_HIVES_FOR_CATEGORY = "SELECT hives.* FROM " + AppSQLiteHelper.TABLE_HIVE + " hives LEFT JOIN "
				+ AppSQLiteHelper.TABLE_LOCATION + " locations ON hives.location_id=locations._id WHERE locations.category_id=" + categoryId
				+ " AND hives.deleted IS NOT 1";

		Cursor cursor = database.rawQuery(ALL_HIVES_FOR_CATEGORY, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Hive hive = cursorToHive(cursor);
			hives.add(hive);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return hives;
	}

	public List<LogEntry> getAllLogEntries() {
		List<LogEntry> entries = new ArrayList<LogEntry>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOGENTRY, allLogEntryColumns, "deleted IS NOT 1", null, null, null,
				AppSQLiteHelper.COLUMN_LOGENTRY_DATE + " DESC ");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			LogEntry entry = cursorToLogEntry(cursor);
			entries.add(entry);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return entries;
	}

	public List<Task> getAllTasks() {
		List<Task> tasks = new ArrayList<Task>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, "deleted IS NOT 1", null, null, null,
				AppSQLiteHelper.COLUMN_TASK_DATE + " DESC ");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTask(cursor);
			tasks.add(task);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tasks;
	}

	public List<Task> getAllTasksBySite() {
		List<Task> tasks = new ArrayList<Task>();

		final String ALL_TASKS_W_LOCATION_QUERY = "SELECT tasks.*, locations.name, hives.location_id FROM (" + AppSQLiteHelper.TABLE_TASK
				+ " tasks LEFT JOIN " + AppSQLiteHelper.TABLE_HIVE + " hives ON tasks.hive_id=hives._id) LEFT JOIN " + AppSQLiteHelper.TABLE_LOCATION
				+ " locations ON hives.location_id=locations._id WHERE tasks.deleted IS NOT 1 ORDER BY hives.location_id ASC";

		Cursor cursor = database.rawQuery(ALL_TASKS_W_LOCATION_QUERY, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTaskWithLocation(cursor);
			tasks.add(task);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tasks;
	}

	public List<Task> getAllTasksForHive(long hiveId) {
		if (hiveId < 0)
			return null;

		List<Task> tasks = new ArrayList<Task>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, "hive_id = " + hiveId + " AND deleted IS NOT 1", null, null, null,
				AppSQLiteHelper.COLUMN_TASK_DATE + " DESC ");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTask(cursor);
			tasks.add(task);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tasks;
	}

	public List<HarvestRecord> getAllHarvestRecords() {
		List<HarvestRecord> records = new ArrayList<HarvestRecord>();

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HARVEST_RECORD, allHarvestRecordColumns, null, null, null, null,
				AppSQLiteHelper.COLUMN_HARVEST_RECORD_DATE + " DESC ");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			HarvestRecord record = cursorToHarvestRecord(cursor);
			records.add(record);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return records;
	}

	public List<ListValue> getAllValues(String table) {
		if (!tableList.contains(table)) {
			return null;
		}

		List<ListValue> values = new ArrayList<ListValue>();

		Cursor cursor = database.query(table, allListTableColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			ListValue value = cursorToListValue(cursor);
			values.add(value);
			cursor.moveToNext();
		}
		cursor.close();

		return values;
	}

	public Category getCategory(long id) {

		if (id < 0)
			return null;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_CATEGORY, allCategoryColumns, "_id = " + String.valueOf(id), null, null, null, null);

		if (cursor.getCount() == 0)
			return null;

		cursor.moveToFirst();

		Category category = cursorToCategory(cursor);

		// Make sure to close the cursor
		cursor.close();

		return category;

	}

	public Location getLocation(long id) {

		if (id < 0)
			return null;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOCATION, allLocationColumns, "_id = " + String.valueOf(id), null, null, null, null);

		if (cursor.getCount() == 0)
			return null;

		cursor.moveToFirst();

		Location location = cursorToLocation(cursor);

		// Make sure to close the cursor
		cursor.close();

		return location;
	}

	public Hive getHive(String qrCode) {

		if (qrCode == null)
			return null;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, AppSQLiteHelper.COLUMN_HIVE_QRCODE + " = '" + qrCode + "'", null,
				null, null, null);

		if (cursor == null || cursor.getCount() == 0)
			return null;

		cursor.moveToFirst();

		Hive hive = cursorToHive(cursor);

		// Make sure to close the cursor
		cursor.close();

		return hive;

	}

	public Hive getHiveById(long hiveId) {
		if (hiveId < 0)
			return null;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_HIVE, allHiveColumns, AppSQLiteHelper.COLUMN_HIVE_ID + " = '" + hiveId + "'", null,
				null, null, null);

		if (cursor.getCount() == 0)
			return null;

		cursor.moveToFirst();

		Hive hive = cursorToHive(cursor);

		// Make sure to close the cursor
		cursor.close();

		return hive;

	}

	public LogEntry getLogEntry(long id) {

		if (id < 0)
			return null;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_LOGENTRY, allLogEntryColumns, "_id = " + String.valueOf(id), null, null, null, null);

		if (cursor.getCount() == 0)
			return null;

		cursor.moveToFirst();

		LogEntry entry = cursorToLogEntry(cursor);

		// Make sure to close the cursor
		cursor.close();

		return entry;

	}

	public Task getTask(long id) {

		if (id < 0)
			return null;

		Cursor cursor = database.query(AppSQLiteHelper.TABLE_TASK, allTaskColumns, "_id = " + String.valueOf(id), null, null, null, null);

		if (cursor.getCount() == 0)
			return null;

		cursor.moveToFirst();

		Task task = cursorToTask(cursor);

		// Make sure to close the cursor
		cursor.close();

		return task;
	}

	public int getNumberOfRecordsInTable(String tableName, String whereClause) {
		if (tableName == null || tableName == "") {
			return 0;
		}

		String sqlQuery = String.format("SELECT Count(*) FROM %s", tableName);
		if (whereClause != null && whereClause.length() > 0) {
			sqlQuery = String.format("%s WHERE %s", sqlQuery, whereClause);
		}

		Cursor cursor = database.rawQuery(sqlQuery, null);
		if (cursor == null)
			return 0;

		if (!cursor.moveToFirst()) {
			cursor.close();
			return 0;
		} else {
			int count = cursor.getInt(0);
			cursor.close();
			return count;
		}
	}

	public int getCategoryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_CATEGORY, null);
	}

	public int getLocationCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_LOCATION, null);
	}

	public int getHiveCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_HIVE, null);
	}

	public int getTaskCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_TASK, null);
	}

	public int getLogEntryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_LOGENTRY, null);
	}

	public int getCategoryHistoryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_CATEGORY_HISTORY, null);
	}

	public int getLocationHistoryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_LOCATION_HISTORY, null);
	}

	public int getHiveHistoryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_HIVE_HISTORY, null);
	}

	public int getHarvestRecordCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_HARVEST_RECORD, null);
	}

	public int getDirtyCategoryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_CATEGORY, "dirty=1");
	}

	public int getDirtyLocationCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_LOCATION, "dirty=1");
	}

	public int getDirtyHiveCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_HIVE, "dirty=1");
	}

	public int getDirtyTaskCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_TASK, "dirty=1");
	}

	public int getDirtyLogEntryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_LOGENTRY, "dirty=1");
	}

	public int getDeletedCategoryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_CATEGORY, " deleted IS 1");
	}

	public int getDeletedLocationCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_LOCATION, " deleted IS 1");
	}

	public int getDeletedHiveCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_HIVE, " deleted IS 1");
	}

	public int getDeletedTaskCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_TASK, "deleted IS 1");
	}

	public int getDeletedLogEntryCount() {
		return getNumberOfRecordsInTable(AppSQLiteHelper.TABLE_LOGENTRY, "deleted IS 1");
	}

	public void deleteCategory(Category category) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_DELETED, 1);
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_DIRTY, 1);

		String str = String.valueOf(category.getId());

		database.update(AppSQLiteHelper.TABLE_CATEGORY, values, "_id = " + str, null);

		// database.delete(AppSQLiteHelper.TABLE_CATEGORY,
		// AppSQLiteHelper.COLUMN_CATEGORY_ID + " = " + category.getId(), null);
		logCategoryHistory(category.getId(), true);
	}

	public void deleteLocation(Location location) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOCATION_DELETED, 1);
		values.put(AppSQLiteHelper.COLUMN_LOCATION_DIRTY, 1);

		String str = String.valueOf(location.getId());

		database.update(AppSQLiteHelper.TABLE_LOCATION, values, "_id = " + str, null);
		// database.delete(AppSQLiteHelper.TABLE_LOCATION,
		// AppSQLiteHelper.COLUMN_LOCATION_ID + " = " + location.getId(), null);
		logLocationHistory(location.getId(), true);
	}

	public void deleteHive(Hive hive) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_DELETED, 1);
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);

		String str = String.valueOf(hive.getId());

		database.update(AppSQLiteHelper.TABLE_HIVE, values, "_id = " + str, null);
		// database.delete(AppSQLiteHelper.TABLE_HIVE,
		// AppSQLiteHelper.COLUMN_HIVE_ID + " = " + hive.getId(), null);
		logHiveHistory(hive.getId(), true);
	}

	public void deleteTask(Task task) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_TASK_DELETED, 1);
		//values.put(AppSQLiteHelper.COLUMN_TASK_DIRTY, 1);

		String str = String.valueOf(task.getId());

		database.update(AppSQLiteHelper.TABLE_TASK, values, "_id = " + str, null);
		// long id = task.getId();
		// database.delete(AppSQLiteHelper.TABLE_TASK,
		// AppSQLiteHelper.COLUMN_TASK_ID + " = " + id, null);
	}

	public void deleteLogEntry(LogEntry entry) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DELETED, 1);
		//values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DIRTY, 1);
		
		String str = String.valueOf(entry.getId());

		database.update(AppSQLiteHelper.TABLE_LOGENTRY, values, "_id = " + str, null);
		// long id = entry.getId();
		// database.delete(AppSQLiteHelper.TABLE_LOGENTRY,
		// AppSQLiteHelper.COLUMN_LOGENTRY_ID + " = " + id, null);
	}

	public void deleteAllCategoriesMarkedForDeletion() {
		//database.delete(AppSQLiteHelper.TABLE_CATEGORY, " deleted IS 1", null);
	}

	public void deleteAllLocationsMarkedForDeletion() {
		//database.delete(AppSQLiteHelper.TABLE_LOCATION, " deleted IS 1", null);
	}

	public void deleteAllHivesMarkedForDeletion() {
		//database.delete(AppSQLiteHelper.TABLE_HIVE, " deleted IS 1", null);
	}

	public void deleteAllLogEntriesMarkedForDeletion() {
		database.delete(AppSQLiteHelper.TABLE_LOGENTRY, " deleted IS 1", null);
	}

	public void deleteAllTasksMarkedForDeletion() {
		database.delete(AppSQLiteHelper.TABLE_TASK, " deleted IS 1", null);
	}

	public boolean updateCategory(Category category) {

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_ID, category.getId());
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_NAME, category.getName());
		values.put(AppSQLiteHelper.COLUMN_CATEGORY_DIRTY, 1);

		String str = String.valueOf(category.getId());

		int updatedRows = database.update(AppSQLiteHelper.TABLE_CATEGORY, values, "_id = " + str, null);

		logCategoryHistory(category.getId(), false);

		return updatedRows > 0;
	}

	public boolean updateLocation(Location location) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOCATION_ID, location.getId());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_CATEGORY_ID, location.getCategoryId());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_NAME, location.getName());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_MAFID, location.getMafId());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_LATITUDE, location.getLatitude());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_LONGITUDE, location.getLongitude());
		values.put(AppSQLiteHelper.COLUMN_LOCATION_DIRTY, 1);

		String str = String.valueOf(location.getId());

		int updatedRows = database.update(AppSQLiteHelper.TABLE_LOCATION, values, "_id = " + str, null);

		logLocationHistory(location.getId(), false);

		return updatedRows > 0;
	}

	public boolean updateHive(Hive hive) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_ID, hive.getId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_QRCODE, hive.getQrCode());
		values.put(AppSQLiteHelper.COLUMN_HIVE_LOCATION_ID, hive.getLocationId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_QUEEN_TYPE, hive.getQueenType());
		values.put(AppSQLiteHelper.COLUMN_HIVE_BEE_ID, hive.getBeeId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_BROOD_ID, hive.getBroodId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_HEALTH_ID, hive.getHealthId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_FOOD_ID, hive.getFoodId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_VARROA_ID, hive.getVarroaId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_VIRUS_ID, hive.getVirusId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_POLLEN_ID, hive.getPollenId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_ID, hive.getTreatmentId());
		values.put(AppSQLiteHelper.COLUMN_HIVE_SPLIT_TYPE, hive.getSplitType());
		values.put(AppSQLiteHelper.COLUMN_HIVE_MOVING, hive.getMoving() ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);
		values.put(AppSQLiteHelper.COLUMN_HIVE_IS_VARROA_SAMPLE, hive.getIsVarroaSample() ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_VARROA_COUNT, hive.getVarroaCount());
		values.put(AppSQLiteHelper.COLUMN_HIVE_IS_GOOD_PRODUCER, hive.getIsGoodProducer() ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_IN, hive.isTreatmentIn() ? 1 : 0);

		// values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_DATE, hive
		// .getTreatmentDate().toGMTString());
		// values.put(AppSQLiteHelper.COLUMN_HIVE_WINTERED_DATE, hive
		// .getWinteredDate().toGMTString());
		// values.put(AppSQLiteHelper.COLUMN_HIVE_FED_DATE, hive.getFedDate()
		// .toGMTString());
		// values.put(AppSQLiteHelper.COLUMN_HIVE_HARVESTED_DATE, hive
		// .getHarvestedDate().toGMTString());

		String str = String.valueOf(hive.getId());

		int updatedRows = database.update(AppSQLiteHelper.TABLE_HIVE, values, "_id = " + str, null);

		logHiveHistory(hive.getId(), false);

		return updatedRows > 0;
	}

	public boolean updateLocationOnMovingHives(long locationId) {
		if (locationId < 0)
			return false;

		List<Hive> hives = getAllHivesBeingMoved();

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_LOCATION_ID, locationId);
		values.put(AppSQLiteHelper.COLUMN_HIVE_MOVING, 0);

		int updatedRows = database.update(AppSQLiteHelper.TABLE_HIVE, values, "moving = 1", null);

		Iterator<Hive> itr = hives.iterator();
		while (itr.hasNext()) {
			Hive hive = itr.next();
			logHiveHistory(hive.getId(), false);
		}

		return updatedRows > 0;
	}

	public boolean updateLogEntry(LogEntry entry) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_ID, entry.getId());
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DESCRIPTION, entry.getDescription());
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DATE, entry.getDate());
		values.put(AppSQLiteHelper.COLUMN_LOGENTRY_DIRTY, 1);

		String str = String.valueOf(entry.getId());

		int updatedRows = database.update(AppSQLiteHelper.TABLE_LOGENTRY, values, "_id = " + str, null);

		return updatedRows > 0;
	}

	public boolean updateTask(Task task) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_TASK_ID, task.getId());
		values.put(AppSQLiteHelper.COLUMN_TASK_DESCRIPTION, task.getDescription());
		values.put(AppSQLiteHelper.COLUMN_TASK_DATE, task.getDate());
		values.put(AppSQLiteHelper.COLUMN_TASK_DONE, task.getDone() ? 1 : 0);
		values.put(AppSQLiteHelper.COLUMN_TASK_HIVE_ID, task.getHiveId());
		values.put(AppSQLiteHelper.COLUMN_TASK_DIRTY, 1);

		String str = String.valueOf(task.getId());

		int updatedRows = database.update(AppSQLiteHelper.TABLE_TASK, values, "_id = " + str, null);

		return updatedRows > 0;
	}

	public boolean bulkUpdateHives(long categoryId, long locationId, ContentValues values) {

		int updatedRows = 0;
		if (locationId >= 0) {
			updatedRows = database.update(AppSQLiteHelper.TABLE_HIVE, values, "location_id = " + locationId, null);

			List<Hive> hives = getAllHivesForLocation(locationId);
			Iterator<Hive> itr = hives.iterator();
			while (itr.hasNext()) {
				Hive hive = itr.next();
				logHiveHistory(hive.getId(), false);
			}

		} else if (categoryId >= 0) {
			String locationIds = "";
			List<Location> locations = getAllLocationsForCategory(categoryId);
			for (int i = 0; i < locations.size(); i++) {
				if (locationIds != "") {
					locationIds += ",";
				}
				locationIds += locations.get(i).getId();
			}

			updatedRows = database.update(AppSQLiteHelper.TABLE_HIVE, values, "location_id IN (" + locationIds + ")", null);

			List<Hive> hives = getAllHivesForCategory(categoryId);
			Iterator<Hive> itr = hives.iterator();
			while (itr.hasNext()) {
				Hive hive = itr.next();
				logHiveHistory(hive.getId(), false);
			}
		}

		return updatedRows > 0;
	}

	public boolean updateHarvestDateOnHives(long categoryId, long locationId, long harvestDate) {

		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_HARVESTED_DATE, harvestDate);
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);

		return bulkUpdateHives(categoryId, locationId, values);
	}

	public boolean updateTreatmentDateOnHives(long categoryId, long locationId, long treatmentTypeId, long treatmentDate) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_ID, treatmentTypeId);
		values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_DATE, treatmentDate);
		values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_IN, 1);
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);

		return bulkUpdateHives(categoryId, locationId, values);
	}

	public boolean updateLastFedDateOnHives(long categoryId, long locationId, long fedDate) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_FED_DATE, fedDate);
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);

		return bulkUpdateHives(categoryId, locationId, values);
	}

	public boolean updateWinterDownDateOnHives(long categoryId, long locationId, long winterDownDate) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_WINTERED_DATE, winterDownDate);
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);

		return bulkUpdateHives(categoryId, locationId, values);
	}

	public boolean removeTreatmentFromHives(long categoryId, long locationId) {
		ContentValues values = new ContentValues();
		//values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_ID, null);
		//values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_DATE, null);
		values.put(AppSQLiteHelper.COLUMN_HIVE_TREATMENT_IN, 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);

		return bulkUpdateHives(categoryId, locationId, values);
	}

	private Category cursorToCategory(Cursor cursor) {
		Category category = new Category();
		category.setId(cursor.getLong(0));
		category.setName(cursor.getString(1));
		return category;
	}

	private Location cursorToLocation(Cursor cursor) {
		Location location = new Location();
		location.setId(cursor.getLong(0));
		location.setCategoryId(cursor.getLong(1));
		location.setName(cursor.getString(2));
		location.setMafId(cursor.getInt(3));
		location.setLatitude(cursor.getDouble(4));
		location.setLongitude(cursor.getDouble(5));
		return location;
	}

	private Hive cursorToHive(Cursor cursor) {
		Hive hive = new Hive();
		hive.setId(cursor.getLong(0));
		hive.setLocationId(cursor.getLong(1));
		hive.setQrCode(cursor.getString(2));
		hive.setQueenType(getStringNotNull(cursor.getString(3)));
		hive.setBeeId(cursor.getLong(4));
		hive.setBroodId(cursor.getLong(5));
		hive.setHealthId(cursor.getLong(6));
		hive.setFoodId(cursor.getLong(7));
		hive.setVarroaId(cursor.getLong(8));
		hive.setVirusId(cursor.getLong(9));
		hive.setPollenId(cursor.getLong(10));
		hive.setSplitType(getStringNotNull(cursor.getString(11)));
		hive.setTreatmentId(cursor.getLong(12));

		hive.setTreatmentDate(cursor.getLong(13));
		hive.setWinteredDate(cursor.getLong(14));
		hive.setFedDate(cursor.getLong(15));
		hive.setHarvestedDate(cursor.getLong(16));

		hive.setMoving(cursor.getLong(17) > 0);

		hive.setIsVarroaSample(cursor.getLong(20) > 0);
		hive.setVarroaCount(cursor.getLong(21));
		hive.setIsGoodProducer(cursor.getLong(22) > 0);
		hive.setTreatmentIn(cursor.getLong(23) > 0);

		if (cursor.getColumnCount() > 24) {
			hive.setLocationName(cursor.getString(24));
		}

		return hive;
	}

	private LogEntry cursorToLogEntry(Cursor cursor) {
		LogEntry value = new LogEntry();
		value.setId(cursor.getLong(0));
		value.setDate(cursor.getLong(1));
		value.setDescription(cursor.getString(2));
		return value;
	}

	private Task cursorToTask(Cursor cursor) {
		Task value = new Task();
		value.setId(cursor.getLong(0));
		value.setDate(cursor.getLong(1));
		value.setDescription(cursor.getString(2));
		value.setDone(cursor.getInt(3) != 0);
		value.setHiveId(cursor.getInt(4));
		return value;
	}

	private Task cursorToTaskWithLocation(Cursor cursor) {
		Task value = new Task();
		value.setId(cursor.getLong(0));
		value.setDate(cursor.getLong(1));
		value.setDescription(cursor.getString(2));
		value.setDone(cursor.getInt(3) != 0);
		value.setHiveId(cursor.getInt(4));
		value.setSiteName(cursor.getString(7));
		return value;
	}

	private HarvestRecord cursorToHarvestRecord(Cursor cursor) {
		HarvestRecord value = new HarvestRecord();
		value.setId(cursor.getLong(0));
		value.setCategoryId(cursor.getLong(1));
		value.setLocationId(cursor.getLong(2));
		value.setDate(cursor.getLong(3));
		value.setDisease(cursor.getInt(4) > 0);
		value.setSupers(cursor.getLong(5));
		value.setFloralType(cursor.getLong(6));
		return value;
	}

	private ListValue cursorToListValue(Cursor cursor) {
		ListValue value = new ListValue();
		value.setId(cursor.getLong(0));
		value.setName(cursor.getString(1));
		return value;
	}

	public boolean removeHiveFromMoveList(Hive hive) {
		ContentValues values = new ContentValues();
		values.put(AppSQLiteHelper.COLUMN_HIVE_MOVING, 0);
		values.put(AppSQLiteHelper.COLUMN_HIVE_DIRTY, 1);

		String str = String.valueOf(hive.getId());

		int updatedRows = database.update(AppSQLiteHelper.TABLE_HIVE, values, "_id = " + str, null);

		logHiveHistory(hive.getId(), false);

		return updatedRows > 0;
	}

	public static int getIndexSafe(List<ListValue> values, long id) {
		int index = getIndex(values, id);
		if (index < 0) {
			index = 0;
		}
		return index;
	}

	public static int getIndex(List<ListValue> values, long id) {
		ListValue value = null;
		int index = -1;
		int length = values.size();
		int i = 0;

		value = values.get(i);
		i++;

		while (value.getId() != id && i < length) {
			value = values.get(i);
			i++;
		}

		if (value.getId() != id)
			value = null;

		if (value != null)
			index = values.indexOf(value);

		return index;
	}

	public String getStringNotNull(String str) {
		if (str == null)
			return "";
		else
			return str;
	}

}