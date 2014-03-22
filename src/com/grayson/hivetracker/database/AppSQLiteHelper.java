package com.grayson.hivetracker.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grayson.hivetracker.App;

public class AppSQLiteHelper extends SQLiteOpenHelper {

	// category
	public static final String TABLE_CATEGORY = "category";
	public static final String COLUMN_CATEGORY_ID = "_id";
	public static final String COLUMN_CATEGORY_NAME = "name";
	public static final String COLUMN_CATEGORY_DIRTY = "dirty";
	public static final String COLUMN_CATEGORY_DELETED = "deleted";

	// category history
	public static final String TABLE_CATEGORY_HISTORY = "category_history";
	public static final String COLUMN_CATEGORY_HISTORY_ID = "_id";
	public static final String COLUMN_CATEGORY_HISTORY_NAME = "name";
	public static final String COLUMN_CATEGORY_HISTORY_TIMESTAMP = "timestamp";
	public static final String COLUMN_CATEGORY_HISTORY_DELETED = "deleted";
	public static final String COLUMN_CATEGORY_HISTORY_USERNAME = "username";

	// locations / sites
	public static final String TABLE_LOCATION = "location";
	public static final String COLUMN_LOCATION_ID = "_id";
	public static final String COLUMN_LOCATION_CATEGORY_ID = "category_id";
	public static final String COLUMN_LOCATION_NAME = "name";
	public static final String COLUMN_LOCATION_MAFID = "mafid";
	public static final String COLUMN_LOCATION_LATITUDE = "latitude";
	public static final String COLUMN_LOCATION_LONGITUDE = "longitude";
	public static final String COLUMN_LOCATION_DIRTY = "dirty";
	public static final String COLUMN_LOCATION_DELETED = "deleted";

	// location history
	public static final String TABLE_LOCATION_HISTORY = "location_history";
	public static final String COLUMN_LOCATION_HISTORY_ID = "_id";
	public static final String COLUMN_LOCATION_HISTORY_CATEGORY_ID = "category_id";
	public static final String COLUMN_LOCATION_HISTORY_NAME = "name";
	public static final String COLUMN_LOCATION_HISTORY_MAFID = "mafid";
	public static final String COLUMN_LOCATION_HISTORY_LATITUDE = "latitude";
	public static final String COLUMN_LOCATION_HISTORY_LONGITUDE = "longitude";
	public static final String COLUMN_LOCATION_HISTORY_TIMESTAMP = "timestamp";
	public static final String COLUMN_LOCATION_HISTORY_DELETED = "deleted";
	public static final String COLUMN_LOCATION_HISTORY_USERNAME = "username";

	// hives
	public static final String TABLE_HIVE = "hive";
	public static final String COLUMN_HIVE_ID = "_id"; // 0
	public static final String COLUMN_HIVE_LOCATION_ID = "location_id";
	public static final String COLUMN_HIVE_QRCODE = "qr_code";
	public static final String COLUMN_HIVE_QUEEN_TYPE = "queen_type";
	public static final String COLUMN_HIVE_BEE_ID = "bee_id";
	public static final String COLUMN_HIVE_BROOD_ID = "brood_id";
	public static final String COLUMN_HIVE_HEALTH_ID = "health_id";
	public static final String COLUMN_HIVE_FOOD_ID = "food_id";
	public static final String COLUMN_HIVE_VARROA_ID = "varroa_id";
	public static final String COLUMN_HIVE_VIRUS_ID = "virus_id";
	public static final String COLUMN_HIVE_POLLEN_ID = "pollen_id"; // 10
	public static final String COLUMN_HIVE_SPLIT_TYPE = "split_type";
	public static final String COLUMN_HIVE_TREATMENT_ID = "treatment_id";
	public static final String COLUMN_HIVE_TREATMENT_DATE = "treatment_date";
	public static final String COLUMN_HIVE_WINTERED_DATE = "wintered_date";
	public static final String COLUMN_HIVE_FED_DATE = "fed_date";
	public static final String COLUMN_HIVE_HARVESTED_DATE = "harvested_date";
	public static final String COLUMN_HIVE_MOVING = "moving";
	public static final String COLUMN_HIVE_DIRTY = "dirty";
	public static final String COLUMN_HIVE_DELETED = "deleted";
	public static final String COLUMN_HIVE_IS_VARROA_SAMPLE = "is_varroa_sample"; // 20
	public static final String COLUMN_HIVE_VARROA_COUNT = "varroa_count";
	public static final String COLUMN_HIVE_IS_GOOD_PRODUCER = "is_good_producer";
	public static final String COLUMN_HIVE_TREATMENT_IN = "treatment_in";

	// hives history
	public static final String TABLE_HIVE_HISTORY = "hive_history";
	public static final String COLUMN_HIVE_HISTORY_ID = "_id"; // 0
	public static final String COLUMN_HIVE_HISTORY_LOCATION_ID = "location_id";
	public static final String COLUMN_HIVE_HISTORY_QRCODE = "qr_code";
	public static final String COLUMN_HIVE_HISTORY_QUEEN_TYPE = "queen_type";
	public static final String COLUMN_HIVE_HISTORY_BEE_ID = "bee_id";
	public static final String COLUMN_HIVE_HISTORY_BROOD_ID = "brood_id";
	public static final String COLUMN_HIVE_HISTORY_HEALTH_ID = "health_id";
	public static final String COLUMN_HIVE_HISTORY_FOOD_ID = "food_id";
	public static final String COLUMN_HIVE_HISTORY_VARROA_ID = "varroa_id";
	public static final String COLUMN_HIVE_HISTORY_VIRUS_ID = "virus_id";
	public static final String COLUMN_HIVE_HISTORY_POLLEN_ID = "pollen_id"; // 10
	public static final String COLUMN_HIVE_HISTORY_SPLIT_TYPE = "split_type";
	public static final String COLUMN_HIVE_HISTORY_TREATMENT_ID = "treatment_id";
	public static final String COLUMN_HIVE_HISTORY_TREATMENT_DATE = "treatment_date";
	public static final String COLUMN_HIVE_HISTORY_WINTERED_DATE = "wintered_date";
	public static final String COLUMN_HIVE_HISTORY_FED_DATE = "fed_date";
	public static final String COLUMN_HIVE_HISTORY_HARVESTED_DATE = "harvested_date";
	public static final String COLUMN_HIVE_HISTORY_MOVING = "moving";
	public static final String COLUMN_HIVE_HISTORY_TIMESTAMP = "timestamp";
	public static final String COLUMN_HIVE_HISTORY_DELETED = "deleted";
	public static final String COLUMN_HIVE_HISTORY_USERNAME = "username"; // 20
	public static final String COLUMN_HIVE_HISTORY_IS_VARROA_SAMPLE = "is_varroa_sample";
	public static final String COLUMN_HIVE_HISTORY_VARROA_COUNT = "varroa_count";
	public static final String COLUMN_HIVE_HISTORY_IS_GOOD_PRODUCER = "is_good_producer";
	public static final String COLUMN_HIVE_HISTORY_TREATMENT_IN = "treatment_in";

	// task
	public static final String TABLE_TASK = "task";
	public static final String COLUMN_TASK_ID = "_id";
	public static final String COLUMN_TASK_DATE = "date";
	public static final String COLUMN_TASK_DESCRIPTION = "description";
	public static final String COLUMN_TASK_DONE = "done";
	public static final String COLUMN_TASK_HIVE_ID = "hive_id";
	public static final String COLUMN_TASK_DIRTY = "dirty";
	public static final String COLUMN_TASK_DELETED = "deleted";

	// public static final String TABLE_MOVE = "move";
	// public static final String COLUMN_MOVE_ID = "_id";
	// public static final String COLUMN_MOVE_HIVE_ID = "hive_id";
	// public static final String COLUMN_MOVE_QRCODE = "qr_code";
	// public static final String COLUMN_MOVE_LOCATION_ID = "location_id";

	// log entry
	public static final String TABLE_LOGENTRY = "log_entry";
	public static final String COLUMN_LOGENTRY_ID = "_id";
	public static final String COLUMN_LOGENTRY_DATE = "date";
	public static final String COLUMN_LOGENTRY_DESCRIPTION = "description";
	public static final String COLUMN_LOGENTRY_DIRTY = "dirty";
	public static final String COLUMN_LOGENTRY_DELETED = "deleted";

	// harvest info
	public static final String TABLE_HARVEST_RECORD = "harvest_record";
	public static final String COLUMN_HARVEST_RECORD_ID = "_id";
	public static final String COLUMN_HARVEST_RECORD_CATEGORY_ID = "category_id";
	public static final String COLUMN_HARVEST_RECORD_LOCATION_ID = "location_id";
	public static final String COLUMN_HARVEST_RECORD_DATE = "date";
	public static final String COLUMN_HARVEST_RECORD_DISEASE = "disease";
	public static final String COLUMN_HARVEST_RECORD_SUPERS = "supers";
	public static final String COLUMN_HARVEST_RECORD_FLORAL_TYPE_ID = "floral_type_id";
	
	// queens
	public static final String TABLE_LIST_QUEEN = "list_queen";
	public static final String COLUMN_LIST_QUEEN_ID = "_id";
	public static final String COLUMN_LIST_QUEEN_NAME = "name";

	// brood
	public static final String TABLE_LIST_BROOD = "list_brood";
	public static final String COLUMN_LIST_BROOD_ID = "_id";
	public static final String COLUMN_LIST_BROOD_NAME = "name";
	
	// bees
	public static final String TABLE_LIST_BEE = "list_bees";
	public static final String COLUMN_LIST_BEE_ID = "_id";
	public static final String COLUMN_LIST_BEE_NAME = "name";

	// food
	public static final String TABLE_LIST_FOOD = "list_food";
	public static final String COLUMN_LIST_FOOD_ID = "_id";
	public static final String COLUMN_LIST_FOOD_NAME = "name";

	// health
	public static final String TABLE_LIST_HEALTH = "list_health";
	public static final String COLUMN_LIST_HEALTH_ID = "_id";
	public static final String COLUMN_LIST_HEALTH_NAME = "name";

	// varroa
	public static final String TABLE_LIST_VARROA = "list_varroa";
	public static final String COLUMN_LIST_VARROA_ID = "_id";
	public static final String COLUMN_LIST_VARROA_NAME = "name";

	// viruses
	public static final String TABLE_LIST_VIRUS = "list_virus";
	public static final String COLUMN_LIST_VIRUS_ID = "_id";
	public static final String COLUMN_LIST_VIRUS_NAME = "name";

	// pollen
	public static final String TABLE_LIST_POLLEN = "list_pollen";
	public static final String COLUMN_LIST_POLLEN_ID = "_id";
	public static final String COLUMN_LIST_POLLEN_NAME = "name";

	// treatment
	public static final String TABLE_LIST_TREATMENT = "list_treatment";
	public static final String COLUMN_LIST_TREATMENT_ID = "_id";
	public static final String COLUMN_LIST_TREATMENT_NAME = "name";
	
	// floral type
	public static final String TABLE_LIST_FLORAL_TYPE = "list_floral_type";
	public static final String COLUMN_LIST_FLORAL_TYPE_ID = "_id";
	public static final String COLUMN_LIST_FLORAL_TYPE_NAME = "name";

	// db
	public static final String DATABASE_NAME = "appsqldb.db";

	/**
	 * V2 - made queen field into text entry rather than spinner
	 */
	public static final int DATABASE_VERSION = 4;

	// Database creation sql statement

	private static final String DATABASE_CREATE_CATEGORY = "create table " + TABLE_CATEGORY + "( " + COLUMN_CATEGORY_ID
			+ " integer primary key autoincrement, " + COLUMN_CATEGORY_NAME + " text not null, " + COLUMN_CATEGORY_DIRTY + " integer, "
			+ COLUMN_CATEGORY_DELETED + " integer );";

	private static final String DATABASE_CREATE_CATEGORY_HISTORY = "create table " + TABLE_CATEGORY_HISTORY + "( " + COLUMN_CATEGORY_HISTORY_ID
			+ " integer, " + COLUMN_CATEGORY_HISTORY_NAME + " text," + COLUMN_CATEGORY_HISTORY_TIMESTAMP + " real," + COLUMN_CATEGORY_HISTORY_DELETED
			+ " integer," + COLUMN_CATEGORY_HISTORY_USERNAME + " text" + ");";

	private static final String DATABASE_CREATE_LOCATION = "create table " + TABLE_LOCATION + "( " + COLUMN_LOCATION_ID
			+ " integer primary key autoincrement, " + COLUMN_LOCATION_CATEGORY_ID + " integer, " + COLUMN_LOCATION_NAME + " text not null, "
			+ COLUMN_LOCATION_MAFID + " integer, " + COLUMN_LOCATION_LATITUDE + " real, " + COLUMN_LOCATION_LONGITUDE + " real, "
			+ COLUMN_LOCATION_DIRTY + " integer, " + COLUMN_LOCATION_DELETED + " integer );";

	private static final String DATABASE_CREATE_LOCATION_HISTORY = "create table " + TABLE_LOCATION_HISTORY + "( " + COLUMN_LOCATION_HISTORY_ID
			+ " integer, " + COLUMN_LOCATION_HISTORY_CATEGORY_ID + " integer, " + COLUMN_LOCATION_HISTORY_NAME + " text, "
			+ COLUMN_LOCATION_HISTORY_MAFID + " integer, " + COLUMN_LOCATION_HISTORY_LATITUDE + " real, " + COLUMN_LOCATION_HISTORY_LONGITUDE
			+ " real," + COLUMN_LOCATION_HISTORY_TIMESTAMP + " real," + COLUMN_LOCATION_HISTORY_DELETED + " integer,"
			+ COLUMN_LOCATION_HISTORY_USERNAME + " text" + ");";

	private static final String DATABASE_CREATE_HIVE = "create table " + TABLE_HIVE + "( " + COLUMN_HIVE_ID + " integer primary key autoincrement, "
			+ COLUMN_HIVE_LOCATION_ID + " integer, " + COLUMN_HIVE_QRCODE + " text, " + COLUMN_HIVE_QUEEN_TYPE + " text, " + COLUMN_HIVE_BEE_ID
			+ " integer, " + COLUMN_HIVE_BROOD_ID + " integer, " + COLUMN_HIVE_HEALTH_ID + " integer, " + COLUMN_HIVE_FOOD_ID + " integer, "
			+ COLUMN_HIVE_VARROA_ID + " integer, " + COLUMN_HIVE_VIRUS_ID + " integer, " + COLUMN_HIVE_POLLEN_ID + " integer, "
			+ COLUMN_HIVE_TREATMENT_ID + " integer, " + COLUMN_HIVE_SPLIT_TYPE + " text, " + COLUMN_HIVE_TREATMENT_DATE + " integer, "
			+ COLUMN_HIVE_WINTERED_DATE + " integer, " + COLUMN_HIVE_FED_DATE + " integer, " + COLUMN_HIVE_HARVESTED_DATE + " integer, "
			+ COLUMN_HIVE_MOVING + " integer, " + COLUMN_HIVE_DIRTY + " integer, " + COLUMN_HIVE_DELETED + " integer, "
			+ COLUMN_HIVE_IS_VARROA_SAMPLE + " integer, " + COLUMN_HIVE_VARROA_COUNT + " integer, " + COLUMN_HIVE_IS_GOOD_PRODUCER + " integer, " + COLUMN_HIVE_TREATMENT_IN + " integer );";

	private static final String DATABASE_CREATE_HIVE_HISTORY = "create table " + TABLE_HIVE_HISTORY + "( " + COLUMN_HIVE_HISTORY_ID + " integer, "
			+ COLUMN_HIVE_HISTORY_LOCATION_ID + " integer, " + COLUMN_HIVE_HISTORY_QRCODE + " text, " + COLUMN_HIVE_HISTORY_QUEEN_TYPE + " text, "
			+ COLUMN_HIVE_HISTORY_BEE_ID + " integer, " + COLUMN_HIVE_HISTORY_BROOD_ID + " integer, " + COLUMN_HIVE_HISTORY_HEALTH_ID + " integer, "
			+ COLUMN_HIVE_HISTORY_FOOD_ID + " integer, " + COLUMN_HIVE_HISTORY_VARROA_ID + " integer, " + COLUMN_HIVE_HISTORY_VIRUS_ID + " integer, "
			+ COLUMN_HIVE_HISTORY_POLLEN_ID + " integer, " + COLUMN_HIVE_HISTORY_TREATMENT_ID + " integer, " + COLUMN_HIVE_HISTORY_SPLIT_TYPE
			+ " text, " + COLUMN_HIVE_HISTORY_TREATMENT_DATE + " real, " + COLUMN_HIVE_HISTORY_WINTERED_DATE + " real, "
			+ COLUMN_HIVE_HISTORY_FED_DATE + " real, " + COLUMN_HIVE_HISTORY_HARVESTED_DATE + " real, " + COLUMN_HIVE_HISTORY_MOVING + " integer,"
			+ COLUMN_HIVE_HISTORY_TIMESTAMP + " real," + COLUMN_HIVE_HISTORY_DELETED + " integer," + COLUMN_HIVE_HISTORY_USERNAME + " text, "
			+ COLUMN_HIVE_HISTORY_IS_VARROA_SAMPLE + " integer, " + COLUMN_HIVE_HISTORY_VARROA_COUNT + " integer, "
			+ COLUMN_HIVE_HISTORY_IS_GOOD_PRODUCER + " integer, " + COLUMN_HIVE_HISTORY_TREATMENT_IN + ");";

	private static final String DATABASE_CREATE_TASK = "create table " + TABLE_TASK + "( " + COLUMN_TASK_ID + " integer primary key autoincrement, "
			+ COLUMN_TASK_DATE + " real, " + COLUMN_TASK_DESCRIPTION + " text, " + COLUMN_TASK_DONE + " integer, " + COLUMN_TASK_HIVE_ID
			+ " integer, " + COLUMN_TASK_DIRTY + " integer, " + COLUMN_TASK_DELETED + " integer );";

	private static final String DATABASE_CREATE_LOGENTRY = "create table " + TABLE_LOGENTRY + "( " + COLUMN_LOGENTRY_ID
			+ " integer primary key autoincrement, " + COLUMN_LOGENTRY_DATE + " real, " + COLUMN_LOGENTRY_DESCRIPTION + " text, "
			+ COLUMN_LOGENTRY_DIRTY + " integer, " + COLUMN_LOGENTRY_DELETED + " integer );";

	private static final String DATABASE_CREATE_HARVEST_RECORD = "create table " + TABLE_HARVEST_RECORD + "(" + COLUMN_HARVEST_RECORD_ID + " integer primary key autoincrement, "
			+ COLUMN_HARVEST_RECORD_CATEGORY_ID + " integer, " + COLUMN_HARVEST_RECORD_LOCATION_ID + " integer, " + COLUMN_HARVEST_RECORD_DATE + " real, " + COLUMN_HARVEST_RECORD_DISEASE + " integer, "
			+ COLUMN_HARVEST_RECORD_SUPERS + " integer, " + COLUMN_HARVEST_RECORD_FLORAL_TYPE_ID + " integer );";
	
	// TODO THESE LIST TABLE SHOULD USE UNIQUE IDs SO THAT THEY DON'T BREAK THE
	// HISTORY TABLE IF EVER CHANGED

	private static final String DATABASE_CREATE_LIST_QUEEN = "create table " + TABLE_LIST_QUEEN + "( " + COLUMN_LIST_QUEEN_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_QUEEN_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_BROOD = "create table " + TABLE_LIST_BROOD + "( " + COLUMN_LIST_BROOD_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_BROOD_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_BEE = "create table " + TABLE_LIST_BEE + "( " + COLUMN_LIST_BEE_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_BEE_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_FOOD = "create table " + TABLE_LIST_FOOD + "( " + COLUMN_LIST_FOOD_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_FOOD_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_HEALTH = "create table " + TABLE_LIST_HEALTH + "( " + COLUMN_LIST_HEALTH_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_HEALTH_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_VARROA = "create table " + TABLE_LIST_VARROA + "( " + COLUMN_LIST_VARROA_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_VARROA_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_VIRUS = "create table " + TABLE_LIST_VIRUS + "( " + COLUMN_LIST_VIRUS_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_VIRUS_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_POLLEN = "create table " + TABLE_LIST_POLLEN + "( " + COLUMN_LIST_POLLEN_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_POLLEN_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_TREATMENT = "create table " + TABLE_LIST_TREATMENT + "( " + COLUMN_LIST_TREATMENT_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_TREATMENT_NAME + " text not null );";

	private static final String DATABASE_CREATE_LIST_FLORAL_TYPE = "create table " + TABLE_LIST_FLORAL_TYPE + "( " + COLUMN_LIST_FLORAL_TYPE_ID
			+ " integer primary key autoincrement, " + COLUMN_LIST_FLORAL_TYPE_NAME + " text not null );";

	// private static final String DATABASE_CREATE_SYNC = "create table " +
	// TABLE_SYNC + "( " + COLUMN_SYNC_ID
	// + " integer primary key autoincrement, " + COLUMN_SYNC_STATUS + " text, "
	// + COLUMN_SYNC_MESSAGE + " text );";

	public AppSQLiteHelper() {
		super(App.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {

		database.execSQL(DATABASE_CREATE_CATEGORY);
		database.execSQL(DATABASE_CREATE_CATEGORY_HISTORY);

		database.execSQL(DATABASE_CREATE_LOCATION);
		database.execSQL(DATABASE_CREATE_LOCATION_HISTORY);

		database.execSQL(DATABASE_CREATE_HIVE);
		database.execSQL(DATABASE_CREATE_HIVE_HISTORY);

		database.execSQL(DATABASE_CREATE_TASK);

		// database.execSQL(DATABASE_CREATE_MOVE);

		database.execSQL(DATABASE_CREATE_LOGENTRY);

		database.execSQL(DATABASE_CREATE_LIST_QUEEN);

		database.execSQL(DATABASE_CREATE_LIST_BROOD);

		database.execSQL(DATABASE_CREATE_LIST_BEE);

		database.execSQL(DATABASE_CREATE_LIST_FOOD);

		database.execSQL(DATABASE_CREATE_LIST_HEALTH);

		database.execSQL(DATABASE_CREATE_LIST_VARROA);

		database.execSQL(DATABASE_CREATE_LIST_VIRUS);

		database.execSQL(DATABASE_CREATE_LIST_POLLEN);

		database.execSQL(DATABASE_CREATE_LIST_TREATMENT);

		database.execSQL(DATABASE_CREATE_LIST_FLORAL_TYPE);

		database.execSQL(DATABASE_CREATE_HARVEST_RECORD);

		addDefaults(database);
	}

	private void addDefaults(SQLiteDatabase database) {
		// default queen rows
		database.execSQL("INSERT INTO " + TABLE_LIST_QUEEN + " VALUES (0, 'None') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_QUEEN + " VALUES (1, 'VSH1') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_QUEEN + " VALUES (2, 'VSH2') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_QUEEN + " VALUES (3, 'VSH3') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_QUEEN + " VALUES (4, 'VSH4') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_QUEEN + " VALUES (5, 'VSH5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_QUEEN + " VALUES (6, 'Russian') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_QUEEN + " VALUES (7, 'Other') ");

		// brood
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (0, '0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (1, '0.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (2, '1.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (3, '1.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (4, '2.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (5, '2.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (6, '3.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (7, '3.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (8, '4.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (9, '4.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (10, '5.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (11, '5.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (12, '6.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (13, '6.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (14, '7.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (15, '7.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (17, '8.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (18, '8.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (19, '9.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (20, '9.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BROOD + " VALUES (21, '10.0') ");

		// bees
		database.execSQL("INSERT INTO " + TABLE_LIST_BEE + " VALUES (0, '0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BEE + " VALUES (1, '0.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BEE + " VALUES (2, '1.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BEE + " VALUES (3, '1.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BEE + " VALUES (4, '2.0') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BEE + " VALUES (5, '2.5') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_BEE + " VALUES (6, '3.0') ");

		// food
		database.execSQL("INSERT INTO " + TABLE_LIST_FOOD + " VALUES (0, 'Unknown') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FOOD + " VALUES (1, 'Low') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FOOD + " VALUES (2, 'Medium') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FOOD + " VALUES (3, 'High') ");
		
		// health
		database.execSQL("INSERT INTO " + TABLE_LIST_HEALTH + " VALUES (0, 'Unknown') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_HEALTH + " VALUES (1, 'Poor') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_HEALTH + " VALUES (2, 'Average') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_HEALTH + " VALUES (3, 'Good') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_HEALTH + " VALUES (4, 'Excellent') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_HEALTH + " VALUES (5, 'AFB') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_HEALTH + " VALUES (6, 'PMS') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_HEALTH + " VALUES (7, 'Dead') ");

		// varroa
		database.execSQL("INSERT INTO " + TABLE_LIST_VARROA + " VALUES (0, 'None') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VARROA + " VALUES (1, 'Low') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VARROA + " VALUES (2, 'Medium') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VARROA + " VALUES (3, 'High') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VARROA + " VALUES (4, 'Very high') ");

		// virus
		database.execSQL("INSERT INTO " + TABLE_LIST_VIRUS + " VALUES (0, 'None') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VIRUS + " VALUES (1, 'Chaulk Brood') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VIRUS + " VALUES (2, 'Dysentry') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VIRUS + " VALUES (3, 'Nosema') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VIRUS + " VALUES (4, 'Queenless') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VIRUS + " VALUES (5, 'Laying Working') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_VIRUS + " VALUES (6, 'Wasps') ");

		// pollen
		database.execSQL("INSERT INTO " + TABLE_LIST_POLLEN + " VALUES (0, 'Unknown') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_POLLEN + " VALUES (1, 'Low') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_POLLEN + " VALUES (2, 'Good') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_POLLEN + " VALUES (3, 'High') ");

		// treatment
		database.execSQL("INSERT INTO " + TABLE_LIST_TREATMENT + " VALUES (0, 'None') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_TREATMENT + " VALUES (1, 'APISTAN') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_TREATMENT + " VALUES (2, 'BAYVROL') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_TREATMENT + " VALUES (3, 'THYMOVAR') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_TREATMENT + " VALUES (4, 'APILIFEVAR') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_TREATMENT + " VALUES (5, 'APIVAR') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_TREATMENT + " VALUES (6, 'APIGUARD') ");

		// floral type
		database.execSQL("INSERT INTO " + TABLE_LIST_FLORAL_TYPE + " VALUES (0, 'Manuka') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FLORAL_TYPE + " VALUES (1, 'Clover') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FLORAL_TYPE + " VALUES (2, 'Honeydew') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FLORAL_TYPE + " VALUES (3, 'Bush Blend') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FLORAL_TYPE + " VALUES (4, 'Willon') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FLORAL_TYPE + " VALUES (5, 'Kamahi') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FLORAL_TYPE + " VALUES (6, 'Rata') ");
		database.execSQL("INSERT INTO " + TABLE_LIST_FLORAL_TYPE + " VALUES (7, 'Blue Borage') ");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(AppSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");

		// TODO: in future for any db changes between versions 
		// do alter table to rename any changed tables
		// then copy data from old table to new table and set defaults for new fields
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_HISTORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION_HISTORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIVE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HIVE_HISTORY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGENTRY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_QUEEN);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_BEE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_BROOD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_FOOD);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_HEALTH);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_VARROA);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_VIRUS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_POLLEN);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_TREATMENT);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LIST_FLORAL_TYPE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HARVEST_RECORD);

		onCreate(db);
	}

}
