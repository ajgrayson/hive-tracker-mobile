package com.grayson.hivetracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		// long userId = BoxSyncManager.getInstance().getBoxUserId();
		// String authToken = BoxSyncManager.getInstance().getAuthToken();

		String userName = App.getSharedPrefs().getString(
				Constants.PREFS_KEY_APP_USERNAME, null);

		if (userName != null) {
		//	finish();
			EditText editTextAppUserName  = (EditText) this.findViewById(R.id.editTextAppUserName);
			editTextAppUserName.setText(userName);
		}
	}

	public void onBtnLoginClicked(View v) {
		// Intent intent = new Intent(getApplicationContext(),
		// Authentication.class);
		// startActivityForResult(intent, Constants.REQUEST_CODE_BOX_LOGIN);

		EditText textUserName = (EditText) this
				.findViewById(R.id.editTextAppUserName);
		String appUserName = textUserName.getText().toString();
		
		if (appUserName != null && !appUserName.isEmpty()) {
			final SharedPreferences.Editor editor = App.getSharedPrefs().edit();
			editor.putString(Constants.PREFS_KEY_APP_USERNAME, appUserName);
			editor.commit();
			finish();
		} else {
			Toast toast = Toast.makeText(this, "Please type in a username.",
					Toast.LENGTH_LONG);
			toast.show();
		}
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// intent) {
	// if (requestCode == Constants.REQUEST_CODE_BOX_LOGIN) {
	// if (intent != null) {
	// String status = intent.getStringExtra("status");
	//
	// if (status != null && status.compareTo("login-ok") == 0) {
	// getBoxUserId();
	// } else {
	// Toast toast = Toast.makeText(this, "Login failed, please try again.",
	// Toast.LENGTH_LONG);
	// toast.show();
	// }
	// }
	// } else {
	// Toast toast = Toast.makeText(this, "Login failed, please try again.",
	// Toast.LENGTH_LONG);
	// toast.show();
	// }
	// }

	// protected void getBoxUserId() {
	// BoxSyncManager.getInstance().checkLogin(new BoxLoginListener() {
	// @Override
	// public void onUserLoggedIn(long boxUserId, String boxUserName) {
	// // save the userID as we need this later for getting only user
	// // folders
	// final SharedPreferences.Editor editor = App.getSharedPrefs().edit();
	// editor.putLong(Constants.PREFS_KEY_BOX_USERID, boxUserId);
	// editor.putString(Constants.PREFS_KEY_BOX_USERNAME, boxUserName);
	// editor.commit();
	//
	// finish();
	// }
	//
	// @Override
	// public void onUserLoggedOut(String msg) {
	// // seems the user is logged out. lets hand off the the box
	// // authentication activity
	// Intent intent = new Intent(getApplicationContext(),
	// Authentication.class);
	// startActivityForResult(intent, Constants.REQUEST_CODE_BOX_LOGIN);
	// }
	//
	// @Override
	// public void onError(String msg) {
	// Tools.logError(this, "Box", "User login error, msg=" + msg);
	// }
	// });
	// }

	@Override
	public void onBackPressed() {
		// blocking while sync in progress
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
