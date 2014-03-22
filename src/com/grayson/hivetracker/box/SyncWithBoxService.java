package com.grayson.hivetracker.box;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;

import com.box.androidlib.DAO.BoxFile;
import com.grayson.hivetracker.App;
import com.grayson.hivetracker.Constants;
import com.grayson.hivetracker.box.listeners.DeleteThisFileListener;
import com.grayson.hivetracker.box.listeners.DownloadThisFileListener;
import com.grayson.hivetracker.box.listeners.GetListOfFilesInThisFolderListener;
import com.grayson.hivetracker.box.listeners.InitializeBoxFoldersListener;
import com.grayson.hivetracker.box.listeners.UploadTheseFilesListener;
import com.grayson.hivetracker.database.AppDataSource;
import com.grayson.hivetracker.database.ListValue;
import com.grayson.hivetracker.tools.Tools;

public class SyncWithBoxService extends IntentService {

	protected enum Stage {
		INITIALIZE, UPLOAD_CATEGORY, UPLOAD_LOCATION, UPLOAD_HIVE, UPLOAD_TASK, UPLOAD_LOG, UPLOAD_CATEGORY_HISTORY, UPLOAD_LOCATION_HISTORY, UPLOAD_HIVE_HISTORY, UPLOAD_HARVEST_RECORDS, DELETE_CATEGORY, DELETE_LOCATION, DELETE_TASK, DELETE_LOG, DOWNLOAD_CONFIGS, DONE
	}

	protected int currentStageIndex;

	protected Stage[] stages = { Stage.INITIALIZE, Stage.UPLOAD_CATEGORY, Stage.UPLOAD_LOCATION, Stage.UPLOAD_HIVE, Stage.UPLOAD_TASK,
			Stage.UPLOAD_LOG, Stage.UPLOAD_CATEGORY_HISTORY, Stage.UPLOAD_LOCATION_HISTORY, Stage.UPLOAD_HIVE_HISTORY, Stage.UPLOAD_HARVEST_RECORDS,
			Stage.DELETE_CATEGORY, Stage.DELETE_LOCATION, Stage.DELETE_TASK, Stage.DELETE_LOG, Stage.DOWNLOAD_CONFIGS, Stage.DONE };

	protected AppDataSource db;

	protected Cursor cursor;

	protected int maxProgressValue;

	protected int currentProgress;

	protected int theNumberOfRecordsToBeUploaded;

	protected int numberOfFilesToUploadInEachBatch = 20;

	protected List<XmlFile> listOfFilesToUpload;

	protected boolean onlySyncChangedFiles = true;

	//protected boolean uploadFailedAbortEverything = false;

	public SyncWithBoxService() {
		super("UploadToBoxService");
		Tools.logInfo(this, "Box", "Service created");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		theNumberOfRecordsToBeUploaded = -1; // reset

		Tools.logInfo(this, "Box", "onHandleIntent(), SyncAction = " + App.getSyncAction().toString());
		
		// OK, so we could have gotten here if
		//	previous sync failed part way
		//	user is starting new sync
		// 	user just downloaded v1 files
		
		// Lets see if an error occurred (catches v1 files
		switch (App.getSyncAction()) {
			case UPLOAD_ALL_V2_FILES:
				onlySyncChangedFiles = false;
				broadcastProgressUpdateMessage(String.format("Uploading all files."));
				break;
			case UPLOAD_CHANGES:
			case NONE:
			default:
				broadcastProgressUpdateMessage(String.format("Only syncing changes."));
				onlySyncChangedFiles = true;
				break;
		}
		
		try {
//			String lastSyncType = App.getSharedPrefs().getString(Constants.PREFS_SYNC_LAST_TYPE, "");
//			if (lastSyncType.equals(Constants.SYNC_TYPE_DOWNLOAD_V1)) {
//				// the last sync was an import of V1 files so that means we
//				// don't have any V2 individual files up on the server..
//				// we need to push everything up to the server.
//				onlySyncChangedFiles = false;
//			} else {
//				// the last sync was a normal sync so we should only
//				// need to upload changed files.
//				onlySyncChangedFiles = true;
//			}

			setup();

			if (getTheNumberOfRecordsToBeUploaded() == 0) {
				broadcastProgressUpdateMessage("No files to be sync'd");
				syncHasBeenCompleted();
			} else {
				broadcastProgressUpdateMessage(String.format("Total of %s steps to complete", getTheNumberOfRecordsToBeUploaded()));
				startTheSync();
			}
		} catch (Exception e) {
			Tools.logError(this, "Box", e.getMessage());
			syncHasFailed();
		}
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
		broadcastMessage(Constants.SYNC_STATUS_IN_PROGRESS, getCurrentProgress(), message);
	}

	protected int getCurrentProgress() {
		return currentProgress;
	}

	protected void startTheSync() {
		Tools.logInfo(this, "Box", "startTheSync()");
		
		currentStageIndex = -1;
		moveToNextStage();
	}

	protected void syncHasFailed() {
		Tools.logInfo(this, "Box", "syncHasFailed()");
		
		BoxManager.getInstance().cancelAllRequests();
		
		tearDown();
		//uploadFailedAbortEverything = true;
		broadcastMessage(Constants.SYNC_STATUS_FAILED, getCurrentProgress(), "The sync has failed.");
		stopSelf();
	}

	protected void syncTimeoutHasOccurred() {
		Tools.logInfo(this, "Box", "syncTimeoutHasOccurred()");
		
		broadcastProgressUpdateMessage("Sync failed. Please check your internet connection.");
		syncHasFailed();
	}
	
	protected void syncHasBeenCompleted() {
		Tools.logInfo(this, "Box", "syncHasBeenCompleted()");
		
		tearDown();
		App.setSyncAction(SyncAction.NONE);
		App.setPrefAsLong(Constants.PREFS_SYNC_LAST_COMPLETED, new Date().getTime());
		broadcastMessage(Constants.SYNC_STATUS_COMPLETED, getCurrentProgress(), "The sync has been completed.");
		stopSelf();
	}

	protected void moveToNextStage() {
		currentStageIndex++;
		if (currentStageIndex >= stages.length)
			return; // this should never happen - DONE stage should result in
					// exiting

		Stage currentStage = stages[currentStageIndex];

		Tools.logInfo(this, "Box", "moveToNextStage(), " + currentStage.toString());
		
		switch (currentStage) {
		case INITIALIZE:
			doStageInitialize();
			break;
		case UPLOAD_CATEGORY:
			doStageUploadCategory();
			break;
		case UPLOAD_CATEGORY_HISTORY:
			doStageUploadCategoryHistory();
			break;
		case UPLOAD_LOCATION_HISTORY:
			doStageUploadLocationHistory();
			break;
		case UPLOAD_HIVE_HISTORY:
			doStageUploadHiveHistory();
			break;
		case UPLOAD_HARVEST_RECORDS:
			doStageUploadHarvestRecords();
			break;
		case UPLOAD_HIVE:
			doStageUploadHive();
			break;
		case UPLOAD_LOCATION:
			doStageUploadLocation();
			break;
		case UPLOAD_LOG:
			doStageUploadLog();
			break;
		case UPLOAD_TASK:
			doStageUploadTask();
			break;
		case DELETE_CATEGORY:
			doStageDeleteCategory();
			break;
		case DELETE_LOCATION:
			doStageDeleteLocation();
			break;
		case DELETE_TASK:
			doStageDeleteTask();
			break;
		case DELETE_LOG:
			doStageDeleteLog();
			break;
		case DOWNLOAD_CONFIGS:
			doStageDownloadConfigs();
			break;
		case DONE:
			syncHasBeenCompleted();
			break;
		default:
			break;
		}
	}

	protected void doStageInitialize() {
		Tools.logInfo(this, "Box", "doStageInitialize()");
		
		BoxManager.getInstance().initializeBoxFolders(new InitializeBoxFoldersListener() {
			
			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onInitializedBoxFolders() {
				if(didError || didTimeout) return;
				
				//if (!uploadFailedAbortEverything) {
					broadcastProgressUpdateMessage("The box folders have been initialized.");
					moveToNextStage();
				//}
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				//if (!uploadFailedAbortEverything) {
					Tools.logError(this, "Box", "Initialize box folders failed on error \"" + errorMessage + "\"");
					syncHasFailed();
				//}
			}

			@Override
			public void onProgressUpdate(String message, int progress) {
				if(didError || didTimeout) return;
					
				//if (!uploadFailedAbortEverything) {
					broadcastProgressUpdateMessage(message);
				//}
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				//if (!uploadFailedAbortEverything) {
					syncTimeoutHasOccurred();
				//}
			}

		});
	}

	protected void doStageUploadCategory() {
		if (onlySyncChangedFiles) {
			cursor = db.getDirtyCategoryCursor();
		} else {
			cursor = db.getCategoryCursor();
		}

		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageUploadLocation() {
		if (onlySyncChangedFiles) {
			cursor = db.getDirtyLocationCursor();
		} else {
			cursor = db.getLocationCursor();
		}

		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageUploadHive() {
		if (onlySyncChangedFiles) {
			cursor = db.getDirtyHiveCursor();
		} else {
			cursor = db.getHiveCursor();
		}

		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageUploadTask() {
		if (onlySyncChangedFiles) {
			cursor = db.getDirtyTaskCursor();
		} else {
			cursor = db.getTaskCursor();
		}

		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageUploadLog() {
		if (onlySyncChangedFiles) {
			cursor = db.getDirtyLogEntryCursor();
		} else {
			cursor = db.getLogEntryCursor();
		}

		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageUploadCategoryHistory() {
		cursor = db.getCategoryHistoryCursor();
		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageUploadLocationHistory() {
		cursor = db.getLocationHistoryCursor();
		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageUploadHiveHistory() {
		cursor = db.getHiveHistoryCursor();
		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageUploadHarvestRecords() {
		cursor = db.getHarvestRecordCursor();
		if (cursor != null) {
			cursor.moveToFirst();
			processTheNextRecordInCursor();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageDeleteCategory() {
		//broadcastProgressUpdateMessage("Cleaning up sites");
		//if (db.getDeletedCategoryCount() > 0) {
		//	getTheListOfFilesInTheFolder();
		//} else {
			moveToNextStage();
		//}
	}

	protected void doStageDeleteLocation() {
		//broadcastProgressUpdateMessage("Cleaning up locations");
		//if (db.getDeletedLocationCount() > 0) {
		//	getTheListOfFilesInTheFolder();
		//} else {
			moveToNextStage();
		//}
	}

	protected void doStageDeleteTask() {
		broadcastProgressUpdateMessage("Cleaning up tasks");
		if (db.getDeletedTaskCount() > 0) {
			getTheListOfFilesInTheFolder();
		} else {
			moveToNextStage();
		}
	}

	protected void doStageDeleteLog() {
		broadcastProgressUpdateMessage("Cleaning up logs");
		if (db.getDeletedLogEntryCount() > 0) {
			getTheListOfFilesInTheFolder();
		} else {
			moveToNextStage();
		}
	}

	List<? extends BoxFile> listOfConfigurationFiles;
	Iterator<? extends BoxFile> listOfConfigurationFilesIterator;

	// BoxFile theConfigurationFileToDownload;

	protected void doStageDownloadConfigs() {
		Tools.logInfo(this, "Box", "doStageDownloadConfigs()");
		
		broadcastProgressUpdateMessage("Updating configs");
		
		GetListOfFilesInThisFolderListener listener = new GetListOfFilesInThisFolderListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(List<? extends BoxFile> list) {
				if(didError || didTimeout) return;
				
				//if (!uploadFailedAbortEverything) {
					if (list != null && list.size() > 0) {
						listOfConfigurationFiles = list;
						listOfConfigurationFilesIterator = listOfConfigurationFiles.iterator();
						downloadTheNextConfigurationFile();
					} else {
						moveToNextStage();
					}
				//}
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout)return;
				didError = true;
				
				//if (!uploadFailedAbortEverything) {
					broadcastProgressUpdateMessage(errorMessage);
					syncHasFailed();
				//}
				// /moveToNextStage();
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				//if (!uploadFailedAbortEverything) {
					syncTimeoutHasOccurred();
				//}
			}
		};
		BoxManager.getInstance().getTheListOfFilesInTheConfigFolder(listener);
	}

	protected void downloadTheNextConfigurationFile() {
		Tools.logInfo(this, "Box", "doStageDownloadTheNextConfigurationFile()");
		
		if (!listOfConfigurationFilesIterator.hasNext()) {
			currentProgress++;
			moveToNextStage();
			return;
		}

		DownloadThisFileListener listener = new DownloadThisFileListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(ByteArrayOutputStream fileStream) {
				if(didError || didTimeout) return;
				
				//if (!uploadFailedAbortEverything) {
					importConfigrationFileIntoTheDatabase(fileStream);
					downloadTheNextConfigurationFile();
				//}
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				//if (!uploadFailedAbortEverything) {
					// downloadTheNextConfigurationFile();
					broadcastProgressUpdateMessage(errorMessage);
					syncHasFailed();
				//}
			}

			@Override
			public void onProgressUpdate(String message) {

			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				//if (!uploadFailedAbortEverything) {
					syncTimeoutHasOccurred();
				//}
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

	protected void getTheListOfFilesInTheFolder() {
		Tools.logInfo(this, "Box", "getTheListOfFilesInTheFolder()");
		
		GetListOfFilesInThisFolderListener listener = new GetListOfFilesInThisFolderListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onComplete(List<? extends BoxFile> list) {
				if(didError || didTimeout) return;
				
				//if (!uploadFailedAbortEverything) {
					findTheFilesToDelete(list);
				//}
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				//if (!uploadFailedAbortEverything) {
					// moveToNextStage();
					broadcastProgressUpdateMessage(errorMessage);
					syncHasFailed();
				//}
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				//if (!uploadFailedAbortEverything) {
					syncTimeoutHasOccurred();
				//}
			}
		};

		Stage currentStage = stages[currentStageIndex];
		switch (currentStage) {
		case DELETE_CATEGORY:
			BoxManager.getInstance().getTheListOfFilesInTheCategoryFolder(listener);
			break;
		case DELETE_LOCATION:
			BoxManager.getInstance().getTheListOfFilesInTheLocationFolder(listener);
			break;
		case DELETE_LOG:
			BoxManager.getInstance().getTheListOfFilesInTheLogFolder(listener);
			break;
		case DELETE_TASK:
			BoxManager.getInstance().getTheListOfFilesInTheTaskFolder(listener);
			break;
		default:
			break;

		}
	}

	protected void deleteRecordsMarkedForDeletion() {
		Stage currentStage = stages[currentStageIndex];
		switch (currentStage) {
		case DELETE_CATEGORY:
			///db.deleteAllCategoriesMarkedForDeletion();
			break;
		case DELETE_LOCATION:
			//db.deleteAllLocationsMarkedForDeletion();
			break;
		case DELETE_LOG:
			db.deleteAllLogEntriesMarkedForDeletion();
			break;
		case DELETE_TASK:
			db.deleteAllTasksMarkedForDeletion();
			break;
		default:
			break;

		}
	}

	protected List<Long> listOfSelectedFilesToDelete;
	protected Iterator<Long> listOfSelectedFilesToDeleteIterator;

	protected void findTheFilesToDelete(List<? extends BoxFile> list) {
		listOfSelectedFilesToDelete = new ArrayList<Long>();

		Stage currentStage = stages[currentStageIndex];
		switch (currentStage) {
		case DELETE_CATEGORY:

//			if (list != null && list.size() > 0) {
//
//				Cursor cursor = db.getDeletedCategoryCursor();
//				if (cursor != null && cursor.getCount() > 0) {
//
//					cursor.moveToFirst();
//					while (!cursor.isAfterLast()) {
//
//						long id = cursor.getLong(0);
//						String name = cursor.getString(1);
//						String filename = String.format("%s_%s", name, id);
//						filename = String.format("%s.xml", Tools.cleanFilename(filename));
//
//						for (int i = 0; i < list.size(); i++) {
//
//							String boxFilename = list.get(i).getFileName().toString();
//							if (boxFilename.equals(filename)) {
//
//								listOfSelectedFilesToDelete.add(list.get(i).getId());
//							}
//						}
//
//						cursor.moveToNext();
//					}
//				}
//				cursor.close();
//				// db.deleteAllCategoriesMarkedForDeletion();
//			}

			break;
		case DELETE_LOCATION:

//			if (list != null && list.size() > 0) {
//
//				Cursor cursor = db.getDeletedLocationCursor();
//				if (cursor != null && cursor.getCount() > 0) {
//
//					cursor.moveToFirst();
//					while (!cursor.isAfterLast()) {
//
//						long id = cursor.getLong(0);
//						String name = cursor.getString(2);
//						String filename = String.format("%s_%s", name, id);
//						filename = String.format("%s.xml", Tools.cleanFilename(filename));
//
//						for (int i = 0; i < list.size(); i++) {
//
//							String boxFilename = list.get(i).getFileName().toString();
//							if (boxFilename.equals(filename)) {
//
//								listOfSelectedFilesToDelete.add(list.get(i).getId());
//							}
//						}
//
//						cursor.moveToNext();
//					}
//				}
//				cursor.close();
//				// db.deleteAllLocationsMarkedForDeletion();
//			}

			break;
		case DELETE_LOG:

			if (list != null && list.size() > 0) {

				Cursor cursor = db.getDeletedLogEntryCursor();
				if (cursor != null && cursor.getCount() > 0) {

					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {

						String id = String.valueOf(cursor.getLong(0));
						String filename = String.format("%s.xml", Tools.cleanFilename(id));

						for (int i = 0; i < list.size(); i++) {

							String boxFilename = list.get(i).getFileName().toString();
							if (boxFilename.equals(filename)) {

								listOfSelectedFilesToDelete.add(list.get(i).getId());
							}
						}

						cursor.moveToNext();
					}
				}
				cursor.close();
				// db.deleteAllLogEntriesMarkedForDeletion();
			}

			break;
		case DELETE_TASK:

			if (list != null && list.size() > 0) {

				Cursor cursor = db.getDeletedTaskCursor();
				if (cursor != null && cursor.getCount() > 0) {

					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {

						String id = String.valueOf(cursor.getLong(0));
						String filename = String.format("%s.xml", Tools.cleanFilename(id));

						for (int i = 0; i < list.size(); i++) {

							String boxFilename = list.get(i).getFileName().toString();
							if (boxFilename.equals(filename)) {

								listOfSelectedFilesToDelete.add(list.get(i).getId());
							}
						}

						cursor.moveToNext();
					}
				}
				cursor.close();
				// db.deleteAllTasksMarkedForDeletion();
			}

			break;
		default:
			break;

		}

		if (listOfSelectedFilesToDelete.size() > 0) {

			listOfSelectedFilesToDeleteIterator = listOfSelectedFilesToDelete.iterator();
		}
		deleteTheNextFileInTheListOfSelectedFiles();
	}

	protected void deleteTheNextFileInTheListOfSelectedFiles() {
		Tools.logInfo(this, "Box", "deleteTheNextFileInTheListOfSelectedFiles()");
		
		DeleteThisFileListener listener = new DeleteThisFileListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onCompleted() {
				if(didError || didTimeout)return;
				
				//if (!uploadFailedAbortEverything) {
					currentProgress++;
					broadcastProgressUpdateMessage("  deleted file from box");
					deleteTheNextFileInTheListOfSelectedFiles();
				//}
			}

			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				//if (!uploadFailedAbortEverything) {
					currentProgress++;
					broadcastProgressUpdateMessage(errorMessage);
					deleteTheNextFileInTheListOfSelectedFiles();
				//}
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				
				//if (!uploadFailedAbortEverything) {
					syncTimeoutHasOccurred();
				//}
			}

		};

		if (listOfSelectedFilesToDelete.size() > 0 && listOfSelectedFilesToDeleteIterator.hasNext()) {
			Long fileId = listOfSelectedFilesToDeleteIterator.next();
			BoxManager.getInstance().deleteThisFile(fileId.longValue(), listener);
		} else {
			deleteRecordsMarkedForDeletion();
			moveToNextStage();
		}
	}

	protected void processTheNextRecordInCursor() {
		Tools.logInfo(this, "Box", "processTheNextRecordInCursor()");
		
		if (cursor == null)
			return;

		if (cursor.isAfterLast()) {
			cursor.close();
			moveToNextStage();
			return;
		}

		UploadTheseFilesListener listener = new UploadTheseFilesListener() {

			private boolean didError = false;
			private boolean didTimeout = false;
			
			@Override
			public void onError(String errorMessage) {
				if(didTimeout) return;
				didError = true;
				
				//if (!uploadFailedAbortEverything) {
					currentProgress++;
					processTheNextRecordInCursor();
				//}
			}

			@Override
			public void onUploadCompleted(List<BoxFile> files) {
				if(didError || didTimeout) return;
				
				//if (!uploadFailedAbortEverything) {
					String logMessage = String.format("Uploaded %s of %s files", files.size(), listOfFilesToUpload.size());

					// make sure all the files uploaded
					if (files.size() < listOfFilesToUpload.size()) {
						logMessage += "\nPrepared files:";
						for (int k = 0; k < listOfFilesToUpload.size(); k++) {
							logMessage += String.format("\n filename=%s", listOfFilesToUpload.get(k));
						}

						logMessage += "\nUploaded files:";
						for (int k = 0; k < files.size(); k++) {
							logMessage += String.format("\n boxFile=%s", files.get(k).getFileName());
						}
					} else {
						clearDiryFlagForAllUploadedFiles();
					}

					// increment before broadcast so the percentage is correct.
					currentProgress++;
					broadcastProgressUpdateMessage(String.format("%s (%s of %s steps)", logMessage, currentProgress - 1, maxProgressValue));
					processTheNextRecordInCursor();
				//}
			}

			@Override
			public void onTimeoutError(String errorMessage) {
				if(didError) return;
				didTimeout = true;
				//if (!uploadFailedAbortEverything) {
					syncTimeoutHasOccurred();
				//}
			}
		};

		int count = 0;
		listOfFilesToUpload = new ArrayList<XmlFile>();

		Stage currentStage = stages[currentStageIndex];
		switch (currentStage) {
		case UPLOAD_CATEGORY:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateCategoryXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheCategoryFolder(listOfFilesToUpload, listener);

			break;
		case UPLOAD_CATEGORY_HISTORY:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateCategoryHistoryXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheHistoryFolder(listOfFilesToUpload, listener);

			break;
		case UPLOAD_LOCATION_HISTORY:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateLocationHistoryXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheHistoryFolder(listOfFilesToUpload, listener);

			break;
		case UPLOAD_HIVE_HISTORY:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateHiveHistoryXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheHistoryFolder(listOfFilesToUpload, listener);

			break;
		case UPLOAD_HARVEST_RECORDS:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateHarvestRecordsXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheHistoryFolder(listOfFilesToUpload, listener);

			break;
		case UPLOAD_HIVE:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateHiveXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheHiveFolder(listOfFilesToUpload, listener);

			break;
		case UPLOAD_LOCATION:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateLocationXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheLocationFolder(listOfFilesToUpload, listener);

			break;
		case UPLOAD_LOG:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateLogXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheLogFolder(listOfFilesToUpload, listener);

			break;
		case UPLOAD_TASK:

			while (!cursor.isAfterLast() && count < numberOfFilesToUploadInEachBatch) {
				XmlFile file = XmlGenerator.generateTaskXML(cursor);
				if (file != null) {
					listOfFilesToUpload.add(file);
				}
				cursor.moveToNext();
				count++;
			}
			BoxManager.getInstance().uploadTheseFilesToTheTaskFolder(listOfFilesToUpload, listener);

			break;
		default:
			break;

		}
	}

	protected void clearDiryFlagForAllUploadedFiles() {
		Tools.logInfo(this, "Box", "clearDiryFlagForAllUploadedFiles()");
		
		Stage currentStage = stages[currentStageIndex];

		switch (currentStage) {
		case UPLOAD_CATEGORY:
			for (int i = 0; i < listOfFilesToUpload.size(); i++) {
				db.clearCategoryDirtyFlag(listOfFilesToUpload.get(i).getId());
			}
			break;
		case UPLOAD_CATEGORY_HISTORY:
			db.clearCategoryHistory();
			break;
		case UPLOAD_HIVE:
			for (int i = 0; i < listOfFilesToUpload.size(); i++) {
				db.clearHiveDirtyFlag(listOfFilesToUpload.get(i).getId());
			}
			break;
		case UPLOAD_HIVE_HISTORY:
			db.clearHiveHistory();
			break;
		case UPLOAD_HARVEST_RECORDS:
			db.clearHarvestRecords();
		case UPLOAD_LOCATION:
			for (int i = 0; i < listOfFilesToUpload.size(); i++) {
				db.clearLocationDirtyFlag(listOfFilesToUpload.get(i).getId());
			}
			break;
		case UPLOAD_LOCATION_HISTORY:
			db.clearLocationHistory();
			break;
		case UPLOAD_LOG:
			for (int i = 0; i < listOfFilesToUpload.size(); i++) {
				db.clearLogEntryDirtyFlag(listOfFilesToUpload.get(i).getId());
			}
			break;
		case UPLOAD_TASK:
			for (int i = 0; i < listOfFilesToUpload.size(); i++) {
				db.clearTaskDirtyFlag(listOfFilesToUpload.get(i).getId());
			}
			break;
		default:
			break;

		}
	}

	protected int getTheNumberOfRecordsToBeUploaded() {
		if (theNumberOfRecordsToBeUploaded < 0) {
			theNumberOfRecordsToBeUploaded = 0;

			if (onlySyncChangedFiles) {
				theNumberOfRecordsToBeUploaded = (int) Math.ceil(((db.getDirtyCategoryCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
				theNumberOfRecordsToBeUploaded += (int) Math.ceil(((db.getDirtyLocationCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
				theNumberOfRecordsToBeUploaded += (int) Math.ceil(((db.getDirtyHiveCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
				theNumberOfRecordsToBeUploaded += (int) Math.ceil(((db.getDirtyTaskCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
				theNumberOfRecordsToBeUploaded += (int) Math.ceil(((db.getDirtyLogEntryCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));

				//theNumberOfRecordsToBeUploaded += db.getDeletedCategoryCount();
				//theNumberOfRecordsToBeUploaded += db.getDeletedLocationCount();
				theNumberOfRecordsToBeUploaded += db.getDeletedTaskCount();
				theNumberOfRecordsToBeUploaded += db.getDeletedLogEntryCount();

			} else {
				theNumberOfRecordsToBeUploaded = (int) Math.ceil(((db.getCategoryCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
				theNumberOfRecordsToBeUploaded += (int) Math.ceil(((db.getLocationCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
				theNumberOfRecordsToBeUploaded += (int) Math.ceil(((db.getHiveCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
				theNumberOfRecordsToBeUploaded += (int) Math.ceil(((db.getTaskCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
				theNumberOfRecordsToBeUploaded += (int) Math.ceil(((db.getLogEntryCount() * 1.0) / (numberOfFilesToUploadInEachBatch * 1.0)));
			}

			theNumberOfRecordsToBeUploaded += db.getCategoryHistoryCount() > 0 ? 1 : 0;
			theNumberOfRecordsToBeUploaded += db.getLocationHistoryCount() > 0 ? 1 : 0;
			theNumberOfRecordsToBeUploaded += db.getHiveHistoryCount() > 0 ? 1 : 0;
			theNumberOfRecordsToBeUploaded += db.getHarvestRecordCount() > 0 ? 1 : 0;
			theNumberOfRecordsToBeUploaded += 1; // config files
		}
		return theNumberOfRecordsToBeUploaded;
	}

	protected void setup() {
		db = AppDataSource.getInstance();
		db.open();

		maxProgressValue = getTheNumberOfRecordsToBeUploaded();
		currentProgress = 0;
	}

	protected void tearDown() {
		//App.setPrefAsBoolean(Constants.PREFS_SYNC_IS_IN_PROGRESS, false);
		db.close();
	}
}
