package com.grayson.hivetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.box.androidlib.DAO.User;
import com.grayson.hivetracker.box.Authentication;
import com.grayson.hivetracker.box.BoxManager;
import com.grayson.hivetracker.box.DownloadFromBoxService;
import com.grayson.hivetracker.box.SyncAction;
import com.grayson.hivetracker.box.SyncWithBoxService;
import com.grayson.hivetracker.box.listeners.GetLoggedInUserListener;
import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.tools.Tools;

public class Sync extends Activity {

	protected ProgressBar progressBarSync;

	protected TextView textViewSyncStatus;

	protected TextView textViewSyncTimeRemaining;

	protected Button buttonCancelSync;
	
	protected Button buttonFullSync;

	protected BroadcastReceiver broadcastReceiver;

	protected boolean syncHasBeenCompleted = false;

	protected long syncStartTime;

	protected boolean loggingIn;

	protected boolean onFirstLaunch;

	protected boolean messageReceivedFromSyncService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sync);

		progressBarSync = (ProgressBar) this.findViewById(R.id.progressBarSync);
		progressBarSync.setIndeterminate(true);

		textViewSyncStatus = (TextView) this.findViewById(R.id.textViewSyncStatus);
		textViewSyncStatus.setText("");

		textViewSyncTimeRemaining = (TextView) this.findViewById(R.id.textViewSyncTimeRemaining);
		textViewSyncTimeRemaining.setText("Calculating time remaining...");

		buttonCancelSync = (Button) this.findViewById(R.id.buttonCancelSync);
		buttonCancelSync.setEnabled(false);

		buttonFullSync = (Button) this.findViewById(R.id.buttonForceSync);
		buttonFullSync.setEnabled(false);
		
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				String status = arg1.getStringExtra(Constants.INTENT_EXTRA_SYNC_STATUS);
				int progress = arg1.getIntExtra(Constants.INTENT_EXTRA_SYNC_PROGRESS, 0);
				int progressMax = arg1.getIntExtra(Constants.INTENT_EXTRA_SYNC_PROGRESS_MAX, 0);
				String message = arg1.getStringExtra(Constants.INTENT_EXTRA_SYNC_MESSAGE);

				onReceivedBroadcastMessage(status, progress, progressMax, message);
			}

		};
		
		onFirstLaunch = true;
		loggingIn = false;
		messageReceivedFromSyncService = false;
	}

	protected void getTheUsersLoginDetails() {
		Tools.logInfo(this, "Box", "getTheUsersLoginDetails()");
	
		loggingIn = true;
		
		BoxManager manager = BoxManager.getInstance();
		manager.getLoggedInUser(new GetLoggedInUserListener() {
			
			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onUserLogin(User user) {
				// it seems that this will be fired even if a timeout or error has occurred first and then the 
				// connection is later restored??? so to get around this we need to ignore any that happen
				// in this case.
				if(didError || didTimeout) return;
				
				updateTheStatusMessage("Logged into box as " + user.getLogin());

				loggingIn = false;
				
				// STEP 2 - kick off the sync service
				startTheSync();
			}

			@Override
			public void onUserLogout() {
				
			}

			@Override
			public void onError(String errorMsg) {
				if(didTimeout) return;
				didError = true;
						
				Tools.logInfo("Sync", "Box", "getTheUsersLoginDetails().onError()");
				updateTheStatusMessage("Error getting login details.");
				retry("SE02");	
			}

			@Override
			public void onUserIsNotLoggedIn() {
				if(didError || didTimeout) return;
				
				loggingIn = true;
				Intent intent = new Intent(getApplicationContext(), Authentication.class);
				startActivityForResult(intent, Constants.REQUEST_CODE_BOX_LOGIN);
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				Tools.logInfo("Sync", "Box", "getTheUsersLoginDetails().onTimeoutError()");
				updateTheStatusMessage("The sync has timed out, please check your internet connection and try again. " + errorMessage);
				if(App.getSyncAction() != SyncAction.NONE) {
					retry("SE01");
				}
			}
		});
	}
	
	protected void retry(String reason) {
		Tools.logInfo(this, "Box", "retry() " + reason);
		
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("Sync Failed").setMessage("But don't panic, just click OK to retry. [" + reason + "]")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateTheStatusMessage("Attempting to rety the sync...");
				getTheUsersLoginDetails();
			}
		}).show();
	}
	
	protected void doFullSync() {
		SyncAction currentAction = App.getSyncAction();
		if(currentAction == SyncAction.IMPORT_ALL_V1_FILES || currentAction == SyncAction.IMPORT_ALL_V2_FILES) {
			updateTheStatusMessage("Can't do a full sync while import hasn't completed.");
			retry("SE05");
			return;
		}
		
		syncHasBeenCompleted = false;
		buttonCancelSync.setEnabled(false);
		progressBarSync.setIndeterminate(true);
		progressBarSync.setProgress(0);
		buttonFullSync.setEnabled(false);
		textViewSyncTimeRemaining.setText("Starting...");
		
		App.setSyncAction(SyncAction.UPLOAD_ALL_V2_FILES);
		getTheUsersLoginDetails();
	}
	
	protected void startTheSync() {
		Tools.logInfo(this, "Box", "startTheSync(), SyncAction = " + App.getSyncAction().toString());
		
		progressBarSync.setIndeterminate(false);
		progressBarSync.setMax(100);

		
		// OK, we could have gotten to this point if,
		// 	sync previously failed part way and we are retrying OR
		// 	user has just installed app and wants to sync
		
		syncStartTime = System.currentTimeMillis();
		
		// Lets first check if an error occurred.. e.g. we were partway through a sync.
		if(App.getSyncAction() != SyncAction.NONE) {
			switch (App.getSyncAction()) {
				case IMPORT_ALL_V1_FILES:
				case IMPORT_ALL_V2_FILES:
					startDownloadService();
					break;
				case UPLOAD_ALL_V2_FILES:
				case UPLOAD_CHANGES:
					startSyncService();
					break;
				default:
					// shouldn't ever get here... shouldn't!
					break;
			}
		} else {
			AppDataSource db = AppDataSource.getInstance();
			
			db.open();
			boolean dbIsEmpty = db.isEmpty();
			db.close();
			
			if (dbIsEmpty) {
				startDownloadService();
			} else {
				startSyncService();
			}
		}
	}
	
	protected void startDownloadService() {
		Tools.logInfo(this, "Box", "startDownloadService()");
		
		updateTheStatusMessage("Starting the download service.");
		Intent intent = new Intent(getApplicationContext(), DownloadFromBoxService.class);
		startService(intent);
	}
	
	protected void startSyncService() {
		Tools.logInfo(this, "Box", "startSyncService()");
		
		new Thread() {
			public void run() {
				updateTheStatusMessage("Starting the upload service.");
				Intent intent = new Intent(getApplicationContext(), SyncWithBoxService.class);
				startService(intent);
			}
		}.run();
	}

	protected void updateTheProgressBar(int progress, int progressMax) {
		if (progress >= 0) {
			int progressPercentage = (int) ((progress * 1.0) / (progressMax * 1.0) * 100);
			progressBarSync.setProgress(progressPercentage);

			double secondsRemaining = ((progressMax * 1.0 - progress * 1.0) * 3.0);
			double minutesRemaining = (int) ((secondsRemaining * 1.0) / 60.0);
			secondsRemaining = secondsRemaining % 60;

			String timeRemainingMessage = "";
			if (minutesRemaining > 0) {
				timeRemainingMessage = String.format("Approximately %d mins and %d secs", (int) minutesRemaining, (int) secondsRemaining);
			} else {
				timeRemainingMessage = String.format("Approximately %d seconds", (int) secondsRemaining);
			}

			textViewSyncTimeRemaining.setText(String.format("%s remaining", timeRemainingMessage));
		} else {
			textViewSyncTimeRemaining.setText("This will probably take a few minutes");
			progressBarSync.setIndeterminate(true);
		}
	}

	protected void updateTheStatusMessage(String message) {
		textViewSyncStatus.append(message + "\n");
	}

	protected void checkIfTimeoutHasOccured() {
		Tools.logInfo(this, "Box", "checkIfTimeoutHasOccurred()");
		
		if (!messageReceivedFromSyncService) {
			
			// action != SyncAction.NONE to get here
			// therefore App.getSyncAction() won't be NONE
			
			switch (App.getSyncAction()) {
				case NONE:
					updateTheStatusMessage("Sync completed.");
					buttonCancelSync.setEnabled(true);
					syncHasBeenCompleted = true;
					break;
				case IMPORT_ALL_V1_FILES:
				case IMPORT_ALL_V2_FILES:
				case UPLOAD_ALL_V2_FILES:
				case UPLOAD_CHANGES:
				default:
					retry("SE03");
					break;
			}
		}
	}

	protected void onReceivedBroadcastMessage(String status, int progress, int progressMax, String message) {
		messageReceivedFromSyncService = true;

		updateTheStatusMessage(message);
		updateTheProgressBar(progress, progressMax);

		if (status.equals(Constants.SYNC_STATUS_STARTED)) {
			// nothing
		} else if (status.equals(Constants.SYNC_STATUS_COMPLETED)) {
			long duration = System.currentTimeMillis() - syncStartTime;
			int seconds = (int) ((duration * 1.0) / 1000.0);
			int minutes = (int) ((seconds * 1.0) / 60.0);
			seconds = seconds % 60;

			updateTheStatusMessage(String.format("Sync took %s minutes and %s seconds", minutes, seconds));
			
			if(App.getSyncAction() == SyncAction.UPLOAD_ALL_V2_FILES) {
				textViewSyncTimeRemaining.setText("Now syncing up new file type to Box.");
				getTheUsersLoginDetails();
			} else {
				syncHasBeenCompleted = true;
				buttonCancelSync.setEnabled(true);
				progressBarSync.setIndeterminate(false);
				progressBarSync.setProgress(100);
				
				buttonFullSync.setEnabled(true);
				
				textViewSyncTimeRemaining.setText("Done!");
			}
		} else if (status.equals(Constants.SYNC_STATUS_FAILED)) {
			retry("SE04");
		} else if (status.equals(Constants.SYNC_STATUS_IN_PROGRESS)) {
			// nothing
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// getTheUsersLoginDetails() attempts to get the user details... if it fails
		// due to the user being logged out then it will start the box login
		// intent. which when it completes will return here.. then we can 
		// call the getTheUsersLoginDetails() method again..
		
		if (requestCode == Constants.REQUEST_CODE_BOX_LOGIN) {
			if (intent != null) {
				String status = intent.getStringExtra("status");
				if (status != null && status.compareTo("login-ok") == 0) {
					getTheUsersLoginDetails();
				}
			} else {
				updateTheStatusMessage("Failed to login (intent is null), contact the developer.");
			}
		}
	}

	public void onButtonCancelSyncClicked(View v) {
		if (App.getSyncAction() == SyncAction.NONE) {
			finish();
		} 
	}
	
	public void onButtonForceSyncClicked(View v) {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle("Upload All").setMessage("Are you sure you want to force upload all files?")
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				updateTheStatusMessage("Attempting to upload all...");
				doFullSync();
			}
		}).setNegativeButton("No", null).show();
	}

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
		
		Tools.logInfo(this, "Box", "onResume() " + System.currentTimeMillis());
		
		LocalBroadcastManager.getInstance(getApplicationContext())
				.registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_SYNC_UPDATE));

		buttonFullSync.setEnabled(false);
		
		// regardless of whether the user returns to the app or starts it fresh - if the sync
		// flag is set then we will need to check what when on
		//boolean syncIsInProgress = App.getSharedPrefs().getBoolean(Constants.PREFS_SYNC_IS_IN_PROGRESS, false);
		//SyncAction action = App.getSyncAction();
		if (!App.syncCompleted()) {
			if (!loggingIn) {
				Tools.logInfo(this, "Box", "Returning to a sync session.");
				textViewSyncTimeRemaining.setText("Sync in progress, updating status...");
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						checkIfTimeoutHasOccured();
					}
				}, 15000);
			}
		} else if (onFirstLaunch) {
			Tools.logInfo(this, "Box", "Begining a new sync session.");
			//App.setPrefAsBoolean(Constants.PREFS_SYNC_IS_IN_PROGRESS, true);
			
			// we can reset it here because we've already checked for failed syncs
			App.setSyncAction(SyncAction.NONE);
			
			// STEP 1 - Make sure they are logged in and get their login details
			getTheUsersLoginDetails();
		} else if(!loggingIn) {
			progressBarSync.setIndeterminate(false);
			progressBarSync.setProgress(100);
			textViewSyncTimeRemaining.setText("Done!");
			syncHasBeenCompleted = true;
			buttonCancelSync.setEnabled(true);
		}

		onFirstLaunch = false;
		messageReceivedFromSyncService = false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
		
		Tools.logInfo(this, "Box", "onPause()");
		
	}
}
