package com.grayson.hivetracker;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grayson.hivetracker.tools.Tools;

public class Home extends Activity {

	// private TextView textViewStatus;

	// private TextView textLoggedInAs;

	// private ImageButton buttonSync;

	private MenuItem menuItem;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Constants.API_KEY == null) {
			finish();
			return;
		}

		setContentView(R.layout.home);

		// textViewStatus = (TextView) this.findViewById(R.id.textStatus);
		// buttonSync = (ImageButton) this.findViewById(R.id.buttonSync);
		// textLoggedInAs = (TextView) this.findViewById(R.id.textLoggedInAs);

		// look up previous sync status to see if it failed e.g. the user ran
		// sync, closed the app, sync failed and then they came back to the app
		// String syncStatus =
		// App.getSharedPrefs().getString(Constants.PREFS_KEY_SYNC_STATUS, "");

		String userName = App.getSharedPrefs().getString(Constants.PREFS_KEY_APP_USERNAME, null);

		if (userName == null || userName.isEmpty()) {
			Intent target = new Intent(getApplicationContext(), Login.class);
			startActivityForResult(target, Constants.REQUEST_CODE_APP_LOGIN);
		} else {
			// textLoggedInAs.setText(userName);
		}

		//boolean syncIsInProgress = App.getSharedPrefs().getBoolean(Constants.PREFS_SYNC_IS_IN_PROGRESS, false);
		if (!App.syncCompleted()) {//syncIsInProgress) {
			Intent target = new Intent(this, Sync.class);
			startActivity(target);
		}
	}

	public void onBtnCategoryClicked(View v) {
		Intent target = new Intent(this, CategoryList.class);
		startActivity(target);
	}

	public void onBtnScanClicked(View v) {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	public void onBtnWorkLogClicked(View v) {
		Intent target = new Intent(this, LogList.class);
		startActivity(target);
	}

	public void onBtnMoveClicked(View v) {
		Intent target = new Intent(this, MoveHives.class);
		startActivity(target);
		//AppDataSource dbsource = AppDataSource.getInstance();
		//dbsource.open();
		//dbsource.clearDatabase();
		//dbsource.close();
	}

	public void onBtnAllTasksClicked(View v) {
		Intent target = new Intent(this, TaskList.class);
		startActivity(target);
	}

	public void onBtnSyncClicked(View v) {
		if (!Tools.isExternalStorageWritable()) {
			// this app stores data for export/import in external sd card.
			// if one is not available then this app won't sync.
			Toast.makeText(getApplicationContext(), "No SD card available.", Toast.LENGTH_LONG).show();
			return;
		}

		// check Internet connection is available
		if (!Tools.isConnected(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), "No internet connection available.", Toast.LENGTH_LONG).show();
		} else {
			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Sync").setMessage("Are you sure?")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent target = new Intent(getApplicationContext(), Sync.class);
							startActivity(target);
						}
					}).setNegativeButton("No", null).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// was QR code scanned
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && scanResult.getContents() != null) {

			Tools.logInfo(this, "Scan", "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "]");

			Intent target = new Intent(this, HiveDetails.class);
			target.putExtra("qr_code", scanResult.getContents());

			startActivity(target);

		}
		// otherwise probably a scan failed.
		else if (requestCode == Constants.REQUEST_CODE_APP_LOGIN) {
			String userName = App.getSharedPrefs().getString(Constants.PREFS_KEY_APP_USERNAME, null);
			if (userName != null || !userName.isEmpty()) {
				// textLoggedInAs.setText(userName);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);

		if (menu != null) {
			menuItem = menu.getItem(0);
			String userName = App.getSharedPrefs().getString(Constants.PREFS_KEY_APP_USERNAME, null);

			if (userName != null && !userName.isEmpty()) {
				menuItem.setTitle(userName);
			}
		}
		//
		// actionBarMenu = menu;
		// if(!hasHives) {
		// MenuItem item = menu.getItem(0);
		// if(item != null) {
		// item.setEnabled(false);
		// }
		// }
		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.home_login_button:

			Intent target = new Intent(getApplicationContext(), Login.class);
			startActivityForResult(target, Constants.REQUEST_CODE_APP_LOGIN);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		// if the user navigates to other activities in this app then returns we
		// want to
		// display the result of the sync if it completed.
		String syncStatus = App.getSharedPrefs().getString(Constants.PREFS_KEY_SYNC_STATUS, "");
		// if (syncStatus.compareTo(Constants.SYNC_STATUS_FAILED) == 0) {
		// textViewStatus.setText("Sync failed!");
		// } else {
		// textViewStatus.setText("");

		long lastSyncDate = App.getSharedPrefs().getLong(Constants.PREFS_SYNC_LAST_COMPLETED, new Date().getTime());
		long dateDiff = (new Date().getTime()) - lastSyncDate;

		// 86400000ms in 24hrs
		// if last sync was more than 3 days ago we want to alert the user.
		if (dateDiff > (86400000 * 3)) {
			Toast.makeText(getApplicationContext(), "WARNING! Your last sync was more than 3 days ago!", Toast.LENGTH_LONG).show();
		}
		// }

		if (menuItem != null) {
			String userName = App.getSharedPrefs().getString(Constants.PREFS_KEY_APP_USERNAME, null);

			if (userName != null || !userName.isEmpty()) {
				menuItem.setTitle(userName);
			}
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
