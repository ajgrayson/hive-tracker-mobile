package com.grayson.hivetracker;

import com.grayson.hivetracker.box.SyncAction;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application {
	private static Context context;

	private static SharedPreferences prefs;
	
    public void onCreate(){
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getContext() {
        return App.context;
    }
    
    public static SharedPreferences getSharedPrefs() {
    	if (prefs == null) {
    		prefs = App.getContext().getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
    	}
    	return prefs;
    }
    
    public static void setPrefAsString(String key, String value) {
		final SharedPreferences.Editor editor = App.getSharedPrefs().edit();
		editor.putString(key, value);
		editor.commit();
	}
    
    public static void setPrefAsLong(String key, long value) {
		final SharedPreferences.Editor editor = App.getSharedPrefs().edit();
		editor.putLong(key, value);
		editor.commit();
	}
    
    public static void setPrefAsBoolean(String key, boolean value) {
		final SharedPreferences.Editor editor = App.getSharedPrefs().edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
    
    public static void setSyncAction(SyncAction action) {
    	final SharedPreferences.Editor editor = App.getSharedPrefs().edit();
		editor.putString(Constants.PREFS_SYNC_ACTION, action.toString());
		editor.commit();
    }
    
    public static SyncAction getSyncAction() {
    	String actionStr = App.getSharedPrefs().getString(Constants.PREFS_SYNC_ACTION, SyncAction.NONE.toString());
    	if(actionStr == "") {
    		actionStr = "NONE";
    	}
    	SyncAction action = SyncAction.valueOf(actionStr);
    	return action;
    }
    
    public static boolean syncCompleted() {
    	SyncAction action = getSyncAction();
    	return action == SyncAction.NONE;
    }
    
}
