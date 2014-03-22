package com.grayson.hivetracker.box;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.box.androidlib.DAO.BoxFile;
import com.grayson.hivetracker.App;
import com.grayson.hivetracker.Constants;
import com.grayson.hivetracker.box.listeners.DownloadThisFileListener;
import com.grayson.hivetracker.box.listeners.DownloadV1BackupsListener;
import com.grayson.hivetracker.box.listeners.GetListOfFilesInThisFolderListener;
import com.grayson.hivetracker.box.listeners.InitializeBoxFoldersListener;
import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.Category;
import com.grayson.hivetracker.database.Hive;
import com.grayson.hivetracker.database.ListValue;
import com.grayson.hivetracker.database.Location;
import com.grayson.hivetracker.database.LogEntry;
import com.grayson.hivetracker.database.Task;
import com.grayson.hivetracker.tools.Tools;

public class DownloadFromBoxService extends IntentService {

	protected enum Stage {
		INITIALIZE, CHECK_FOR_OVERIDE, CHECK_FOR_BACKUPS, DOWNLOAD_CATEGORY, DOWNLOAD_LOCATION, DOWNLOAD_HIVE, DOWNLOAD_TASK, DOWNLOAD_LOG, DOWNLOAD_CONFIGS, DONE
	}

	protected int currentStageIndex;

	protected Stage[] stages = { Stage.INITIALIZE, Stage.CHECK_FOR_OVERIDE, Stage.CHECK_FOR_BACKUPS, Stage.DOWNLOAD_CATEGORY, Stage.DOWNLOAD_LOCATION, Stage.DOWNLOAD_HIVE,
			Stage.DOWNLOAD_TASK, Stage.DOWNLOAD_LOG, Stage.DOWNLOAD_CONFIGS, Stage.DONE };

	protected int maxProgressValue;

	protected int currentProgress;

	protected List<? extends BoxFile> listOfFilesToDownload;

	protected Iterator<? extends BoxFile> listOfFilesToDownloadIterator;

	protected AppDataSource db;

	public DownloadFromBoxService() {
		super("DownloadFromBoxService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Tools.logInfo(this, "Box", "onHandleIntent()");
		
		try {
			setup();
			startTheSync();
		} catch (Exception e) {
			Tools.logError(this, "Box", String.format("onHandleIntent().exception = %s", e.getMessage()));
			syncHasFailed();
		}
	}

	protected void syncHasFailed() {
		Tools.logInfo(this, "Box", "syncHasFailed()");
		
		tearDown();
		broadcastMessage(Constants.SYNC_STATUS_FAILED, getCurrentProgress(), "The sync has failed.");
		stopSelf();
	}

	protected void syncTimeoutHasOccurred() {
		Tools.logInfo(this, "Box", "syncTimeoutHasOccurred()");
		
		broadcastProgressUpdateMessage("The sync has failed due to a timeout error. Please check your internet connection.");
		syncHasFailed();
	}
	
	protected void syncErrorHasOccurred() {
		Tools.logInfo(this, "Box", "syncErrorHasOccurred()");
		
		broadcastProgressUpdateMessage("The sync error occurred. Please check your internet connection.");
		syncHasFailed();
	}
	
	protected void syncHasBeenCompleted() {
		syncHasBeenCompleted(SyncAction.NONE);
	}
	
	protected void syncHasBeenCompleted(SyncAction nextAction) {
		Tools.logInfo(this, "Box", "syncHasBeenCompleted(), " + nextAction.toString());
		
		tearDown();
		App.setPrefAsLong(Constants.PREFS_SYNC_LAST_COMPLETED, new Date().getTime());
		App.setSyncAction(nextAction);
		broadcastMessage(Constants.SYNC_STATUS_COMPLETED, getCurrentProgress(), "The sync has been completed.");
		stopSelf();
	}
	
	protected void broadcastMessage(String status, int progress, String message) {
		Intent broadcastIntent = new Intent(Constants.INTENT_SYNC_UPDATE);
		broadcastIntent.putExtra(Constants.INTENT_EXTRA_SYNC_STATUS, status);
		broadcastIntent.putExtra(Constants.INTENT_EXTRA_SYNC_PROGRESS, getCurrentProgress());
		broadcastIntent.putExtra(Constants.INTENT_EXTRA_SYNC_PROGRESS_MAX, maxProgressValue);
		broadcastIntent.putExtra(Constants.INTENT_EXTRA_SYNC_MESSAGE, message);
		LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(broadcastIntent);
	}

	protected void broadcastProgressUpdateMessage(String message) {
		//Tools.logInfo(this, "Box", "Broadcast '" + message + "'");
		// -1 is infinite since we don't know and I couldn't be bothered
		// making the calls to find out how many files need to be downloaded :)
		broadcastMessage(Constants.SYNC_STATUS_IN_PROGRESS, currentProgress, message);
	}
	
	protected int getCurrentProgress() {
		return -1;
	}

	protected void startTheSync() {
		// ok, so we could get to here if
		// 	sync previously failed partway
		//	this is a new sync session
		// in any case, we need to do the init,
		// then we can determine the appropriate path
		
		currentStageIndex = -1;
		moveToNextStage();
	}

	protected void moveToNextStage() {
		currentStageIndex++;
		if (currentStageIndex >= stages.length)
			return; // this should never happen - DONE stage should result in
					// exiting

		Stage currentStage = stages[currentStageIndex];
		
		Tools.logInfo(this, "Box", "moveToNextStage(), stage = " + currentStage.toString());

		switch (currentStage) {
		case INITIALIZE:
			doStageInitialize();
			break;
		case CHECK_FOR_OVERIDE:
			doStageCheckForOveride();
			break;
		case CHECK_FOR_BACKUPS:
			doStageCheckForBackups();
			break;
		case DOWNLOAD_CATEGORY:
			doStageDownloadCategory();
			break;
		case DOWNLOAD_LOCATION:
			doStageDownloadLocation();
			break;
		case DOWNLOAD_HIVE:
			doStageDownloadHive();
			break;
		case DOWNLOAD_LOG:
			doStageDownloadLog();
			break;
		case DOWNLOAD_TASK:
			doStageDownloadTask();
			break;
		case DOWNLOAD_CONFIGS:
			doStageDownloadConfigs();
			break;
		case DONE:
			//App.setPrefAsString(Constants.PREFS_SYNC_LAST_TYPE, Constants.SYNC_TYPE_DOWNLOAD);
			syncHasBeenCompleted();
			break;
		default:
			break;
		}
	}

	protected void doStageInitialize() {
		BoxManager.getInstance().initializeBoxFolders(new InitializeBoxFoldersListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onInitializedBoxFolders() {
				if(didError || didTimeout) return;
				
				Tools.logInfo(this, "Box", "doStageInitialize().onInitializedBoxFolders()");
				broadcastProgressUpdateMessage("The box folders have been initialized.");
				moveToNextStage();
			}
			
			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				Tools.logError(this, "Box", "doStageInitialize().onError(), " + errorMessage);
				syncHasFailed();
			}

			@Override
			public void onProgressUpdate(String message, int progress) {
				broadcastProgressUpdateMessage(message);
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				Tools.logError(this, "Box", "doStageInitialize().onTimeoutError(), " + errorMessage);
				syncTimeoutHasOccurred();
			}
		});
	}
	
	protected void doStageCheckForOveride() {
		Tools.logInfo(this, "Box", "doStageCheckForOveride()");
		
		// Lets first check if it previously failed
		switch(App.getSyncAction()) {
			case IMPORT_ALL_V1_FILES:
				broadcastProgressUpdateMessage(String.format("Looks like previous import failed. Trying again now. [V1]"));
				checkForV1BackupFiles();
				break;
			case IMPORT_ALL_V2_FILES:
				broadcastProgressUpdateMessage(String.format("Looks like previous import failed. Trying again now. [V2]"));
				currentStageIndex = 2; // this will move us into DOWNLOAD_CATEGORY stage
			default:
				// probably a new session
				//App.setSyncAction(SyncAction.IMPORT_ALL_V2_FILES); NOTE: this is set in checkForV2Backups...!!!!
				
				if(App.getSyncAction() == SyncAction.NONE) {
					broadcastProgressUpdateMessage(String.format("Attempting to find existing files to import."));
				}
				
				moveToNextStage();
				break;
		}
	}
	
	protected void doStageCheckForBackups() {
		checkForV2BackupFiles();
	}

	protected void doStageDownloadCategory() {
		getListOfFilesToDownload();
	}

	protected void doStageDownloadLocation() {
		getListOfFilesToDownload();
	}

	protected void doStageDownloadHive() {
		getListOfFilesToDownload();
	}

	protected void doStageDownloadLog() {
		getListOfFilesToDownload();
	}

	protected void doStageDownloadTask() {
		getListOfFilesToDownload();
	}
	
	List<? extends BoxFile> listOfConfigurationFiles;
	Iterator<? extends BoxFile> listOfConfigurationFilesIterator;
	
	protected void doStageDownloadConfigs() {
		broadcastProgressUpdateMessage("Downloading configs");
		GetListOfFilesInThisFolderListener listener = new GetListOfFilesInThisFolderListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(List<? extends BoxFile> list) {
				if(didError || didTimeout) return;
				
				Tools.logInfo(this, "Box", "doStageDownloadConfigs().onComplete()");
				
				if (list != null && list.size() > 0) {
					listOfConfigurationFiles = list;
					listOfConfigurationFilesIterator = listOfConfigurationFiles.iterator();
					downloadTheNextConfigurationFile();
				} else {
					moveToNextStage();
				}
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				Tools.logError(this, "Box", "doStageDownloadConfigs().onError(), " + errorMessage);
				moveToNextStage();
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				Tools.logError(this, "Box", "doStageDownloadConfigs().onTimeoutError(), " + errorMessage);
				syncTimeoutHasOccurred();
			}
		};
		BoxManager.getInstance().getTheListOfFilesInTheConfigFolder(listener);
	}
	
	protected void downloadTheNextConfigurationFile() {
		if (!listOfConfigurationFilesIterator.hasNext()) {
			moveToNextStage();
			return;
		}

		DownloadThisFileListener listener = new DownloadThisFileListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(ByteArrayOutputStream fileStream) {
				if(didError || didTimeout) return;
				
				Tools.logInfo(this, "Box", "downloadTheNextConfigurationFile().onComplete()");
				importConfigrationFileIntoTheDatabase(fileStream);
				downloadTheNextConfigurationFile();
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				Tools.logError(this, "Box", "downloadTheNextConfigurationFile().onError(), " + errorMessage);
				downloadTheNextConfigurationFile();
			}

			@Override
			public void onProgressUpdate(String message) {

			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				Tools.logError(this, "Box", "downloadTheNextConfigurationFile().onTimeoutError(), " + errorMessage);
				syncTimeoutHasOccurred();
			}

		};

		BoxFile file = (BoxFile) listOfConfigurationFilesIterator.next();
		broadcastProgressUpdateMessage(String.format("Importing config %s", file.getFileName()));
		BoxManager.getInstance().downloadThisFile(file.getId(), listener);
	}

	protected void importConfigrationFileIntoTheDatabase(ByteArrayOutputStream stream) {
		ConfigFile file = XmlParser.parseConfigFileXml(stream);
		if (file != null && file.getTablename() != null && file.getTablename() != "" && file.getValues() != null) {
			List<ListValue> values = file.getValues();
			if (db.isValidListTablename(file.getTablename())) {
				db.clearListValuesTable(file.getTablename());
				for (int i = 0; i < values.size(); i++) {
					db.importListValue(file.getTablename(), values.get(i));
				}
			}
		}
	}

	protected void getListOfFilesToDownload() {
		Tools.logInfo(this, "Box", "getListOfFilesToDownload()");
		
		GetListOfFilesInThisFolderListener listener = new GetListOfFilesInThisFolderListener() {
			
			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(List<? extends BoxFile> list) {
				if(didError || didTimeout) return;
				
				Tools.logInfo(this, "Box", "getListOfFilesToDownload().onComplete()");
				
				listOfFilesToDownload = list;
				listOfFilesToDownloadIterator = listOfFilesToDownload.iterator();
				downloadTheNextFileInTheList();
			}
			
			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				Tools.logError(this, "Box", "getListOfFilesToDownload().onError(), " + errorMessage);
				syncErrorHasOccurred();
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				Tools.logError(this, "Box", "getListOfFilesToDownload().onTimeoutError(), " + errorMessage);
				syncTimeoutHasOccurred();	
			}
		};

		Stage currentStage = stages[currentStageIndex];
		switch (currentStage) {
		case DOWNLOAD_CATEGORY:
			BoxManager.getInstance().getTheListOfFilesInTheCategoryFolder(listener);
			break;
		case DOWNLOAD_HIVE:
			BoxManager.getInstance().getTheListOfFilesInTheHiveFolder(listener);
			break;
		case DOWNLOAD_LOCATION:
			BoxManager.getInstance().getTheListOfFilesInTheLocationFolder(listener);
			break;
		case DOWNLOAD_LOG:
			BoxManager.getInstance().getTheListOfFilesInTheLogFolder(listener);
			break;
		case DOWNLOAD_TASK:
			BoxManager.getInstance().getTheListOfFilesInTheTaskFolder(listener);
			break;
		default:
			break;
		}
	}

	protected void downloadTheNextFileInTheList() {
		Tools.logInfo(this, "Box", "downloadTheNextFileInTheList()");
		
		if (!listOfFilesToDownloadIterator.hasNext()) {
			moveToNextStage();
			return;
		}

		DownloadThisFileListener listener = new DownloadThisFileListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(ByteArrayOutputStream fileStream) {
				if(didTimeout || didError) return;
				
				Tools.logInfo(this, "Box", "downloadTheNextFileInTheList().onComplete()");
				importThisFileIntoTheDatabase(fileStream);
				downloadTheNextFileInTheList();
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				Tools.logError(this, "Box", "downloadTheNextFileInTheList().onError(), " + errorMessage);
				syncTimeoutHasOccurred();
			}

			@Override
			public void onProgressUpdate(String message) {
				
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didTimeout) return;
				didTimeout = true;
				
				Tools.logError(this, "Box", "downloadTheNextFileInTheList().onTimeoutError(), " + errorMessage);
				syncTimeoutHasOccurred();
			}

		};

		BoxFile file = (BoxFile) listOfFilesToDownloadIterator.next();
		BoxManager.getInstance().downloadThisFile(file.getId(), listener);
	}

	protected void importThisFileIntoTheDatabase(ByteArrayOutputStream fileStream) {
		Tools.logInfo(this, "Box", "importThisFileIntoTheDatabase()");
		
		Stage currentStage = stages[currentStageIndex];
		switch (currentStage) {
			case DOWNLOAD_CATEGORY:
				Category category = XmlParser.parseCategoryXml(fileStream);
				if (category != null) {
					db.importCategory(category);
					broadcastProgressUpdateMessage(String.format("Imported run \"%s\"", category.getName()));
				}
				break;
			case DOWNLOAD_HIVE:
				Hive hive = XmlParser.parseHiveXml(fileStream);
				if (hive != null) {
					db.importHive(hive);
					broadcastProgressUpdateMessage(String.format("Imported hive \"%s\"", hive.getQrCode()));
				}
				break;
			case DOWNLOAD_LOCATION:
				Location location = XmlParser.parseLocationXml(fileStream);
				if (location != null) {
					db.importLocation(location);
					broadcastProgressUpdateMessage(String.format("Imported site \"%s\"", location.getName()));
				}
				break;
			case DOWNLOAD_LOG:
				LogEntry logEntry = XmlParser.parseLogEntryXml(fileStream);
				if (logEntry != null) {
					db.importLogEntry(logEntry);
					broadcastProgressUpdateMessage(String.format("Imported logentry \"%s\"", logEntry.getId()));
				}
				break;
			case DOWNLOAD_TASK:
				Task task = XmlParser.parseTaskXml(fileStream);
				if (task != null) {
					db.importTask(task);
					broadcastProgressUpdateMessage(String.format("Imported task \"%s\"", task.getId()));
				}
				break;
			default:
				break;
		}
	}
	
	protected void checkForV2BackupFiles() {
		Tools.logInfo(this, "Box", "checkForV2BackupFiles()");
		
		GetListOfFilesInThisFolderListener listener = new GetListOfFilesInThisFolderListener() {
			
			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(List<? extends BoxFile> list) {
				if(didError || didTimeout) return;
				
				Tools.logInfo(this, "Box", "checkForV2BackupFiles().onComplete()");
				
				if(list.size() > 0) {
					// there are V2 backup files available
					App.setSyncAction(SyncAction.IMPORT_ALL_V2_FILES);
					moveToNextStage();
				} else {
					// there are no categories so we can assume no V2 backup files
					broadcastMessage(Constants.SYNC_STATUS_IN_PROGRESS, 100, "Checking for old backup files");
					checkForV1BackupFiles();
				}
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				Tools.logError(this, "Box", "checkForV2BackupFiles().onError(), " + errorMessage);
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				Tools.logError(this, "Box", "checkForV2BackupFiles().onTimeoutError(), " + errorMessage);
				syncTimeoutHasOccurred();
			}
		};
		BoxManager.getInstance().getTheListOfFilesInTheCategoryFolder(listener);
	}
	
	protected void checkForV1BackupFiles() {
		Tools.logInfo(this, "Box", "checkForV1BackupFiles()");
		
		GetListOfFilesInThisFolderListener listener = new GetListOfFilesInThisFolderListener() {
			
			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(List<? extends BoxFile> list) {
				if(didError || didTimeout) return;
				
				Tools.logInfo(this, "Box", "checkForV1BackupFiles().onComplete()");
				
				if(list != null && list.size() > 0) {
					// V1 backup files possibly exist. lets check for each file
					for(int i = 0; i < list.size(); i++) {
						if(list.get(i).getFileName().equals(Constants.BACKUP_FILENAME_CATEGORY)) {
							// yip, looks like at least the categories exist so we will try importing them
							broadcastProgressUpdateMessage("Old backup files found, now importing");
							downloadV1Backups(list);
						}
					}
				} else {
					// no V1 backup files available
					broadcastProgressUpdateMessage("No files found to import");
					syncHasBeenCompleted();
				}
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout)return;
				didError = true;
				
				Tools.logError(this, "Box", "checkForV1BackupFiles().onError(), " + errorMessage);
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				Tools.logError(this, "Box", "checkForV1BackupFiles().onTimeoutError(), " + errorMessage);
				syncTimeoutHasOccurred();
			}
		};
		BoxManager.getInstance().getTheListOfFilesInTheBackupFolder(listener);
	}
	
	protected void downloadV1Backups(List<? extends BoxFile> fileList) {
		Tools.logInfo(this, "Box", "downloadV1Backups()");
		
		App.setSyncAction(SyncAction.IMPORT_ALL_V1_FILES);
		
		V1BackupDownloader manager = new V1BackupDownloader(fileList);
		manager.downloadV1Backups(new DownloadV1BackupsListener() {
			
			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete() {
				if(didError || didTimeout) return;
				
				Tools.logInfo(this, "Box", "downloadV1Backups().onComplete()");
				
				// we've imported all the V1 stuff. now the user needs to upload it to instantiate v2 folders.
				syncHasBeenCompleted(SyncAction.UPLOAD_ALL_V2_FILES);
				//App.setPrefAsString(Constants.PREFS_SYNC_LAST_TYPE, Constants.SYNC_TYPE_DOWNLOAD_V1);
			}

			@Override
			public void onProgressUpdate(String message) {
				broadcastProgressUpdateMessage(message);
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				Tools.logError(this, "Box", "downloadV1Backups().onError(), " + errorMessage);
				syncHasFailed();
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				Tools.logError(this, "Box", "downloadV1Backups().onTimeoutError(), " + errorMessage);
				syncTimeoutHasOccurred();
			}
			
		});
	}
	
	protected void setup() {
		Tools.logInfo(this, "Box", "setup()");
		
		db = AppDataSource.getInstance();
		db.open();
		maxProgressValue = 100;
		currentProgress = 0;
	}

	protected void tearDown() {
		Tools.logInfo(this, "Box", "tearDown()");
		
		db.close();
	}
}
