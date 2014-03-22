package com.grayson.hivetracker.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

public class Tools {

	/**
	 * Parse a string to a date This will always return a date, defaulting to
	 * today's date.
	 * 
	 * @param str
	 *            string to parse
	 * @return date
	 */
	public static Date stringToDate(String str) {
		DateFormat df;
		df = new SimpleDateFormat("d-m-yyyy");

		Date date;

		try {
			date = (Date) df.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();

			final Calendar c = Calendar.getInstance();
			date = new Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		}
		return date;
	}

	/**
	 * removes new lines and truncates to specified length
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String tidyString(String str, int len) {
		if (str == null) {
			return "";
		}
		
		int strLen = len;

		if(len == 0)
			strLen = str.length();

		String cStr = str;
		cStr = cStr.replace("\n", " ");

		if (cStr.length() > strLen && strLen >= 4) {
			cStr = cStr.substring(0, strLen - 3) + "...";
		}

		return cStr;
	}

	public static Boolean isConnected(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		if (!i.isAvailable())
			return false;
		return true;
	}

	public static void logInfo(Object o, String tag, String msg) {
		String objectName = "";
		if (o != null)
			objectName = o.getClass().getSimpleName(); //.getName();

		//Log.i(tag, "[" + objectName + "] " + msg);
		//Log.i(tag, msg);
		logInfo(objectName, tag, msg);
	}

	public static void logInfo(String cls, String tag, String msg) {
		Log.i(tag, "[" + cls + "] " + msg);
	}
	
	public static void logError(Object o, String tag, String msg) {
		Tools.logError(o, tag, msg, null);
	}

	public static void logError(Object o, String tag, String msg, Exception e) {
		String exceptionMsg = "";
		if (e != null)
			exceptionMsg = ", Exception=" + e.getMessage();

		String objectName = "";
		if (o != null)
			objectName = o.getClass().getName();

		Log.e(tag, "Class=" + objectName + ", Message=" + msg + exceptionMsg);
		Tools.logStackTrace(tag);
	}

	public static void logStackTrace(String tag) {
		StackTraceElement[] e = Thread.currentThread().getStackTrace();
		int i = 0;
		for (i = 0; i < 7; i++) {
			Log.e(tag, "at " + e[i].getClass().getPackage().getName() + "(" + e[i].getClassName() + ":" + e[i].getLineNumber() + ")");
		}
		Log.e(tag, "End of StackTrace");
	}
	
	
	
//http://snipplr.com/view/19973/
//	Intent i = new Intent(Intent.ACTION_SEND);  
//	//i.setType("text/plain"); //use this line for testing in the emulator  
//	i.setType("message/rfc822") ; // use from live device
//	i.putExtra(Intent.EXTRA_EMAIL, new String []{"test@gmail.com"});  
//	i.putExtra(Intent.EXTRA_SUBJECT,"subject goes here");  
//	i.putExtra(Intent.EXTRA_TEXT,"body goes here");  
//	startActivity(Intent.createChooser(i, "Select email application."));
	
	public static Boolean isExternalStorageWritable() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    // We can read and write the media
		    mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    // We can only read the media
		    mExternalStorageAvailable = true;
		    mExternalStorageWriteable = false;
		} else {
		    // Something else is wrong. It may be one of many other states, but all we need
		    //  to know is we can neither read nor write
		    mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}
	
	public static String cleanFilename(String filename) {
		if(filename == null)
			return null;
		
		String cleanFilename = filename.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
		cleanFilename = cleanFilename.replace("__","_");
		cleanFilename = cleanFilename.toLowerCase();
		
		return cleanFilename;
	}
	
	public static String getStringNotNull(String str) {
		if (str == null) 
			return "";
		else
			return str;
	}
	
	public static String getNowAsTimestamp() {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return df.format(date);
	}
	
}
