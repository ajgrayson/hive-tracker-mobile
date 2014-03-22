package com.grayson.hivetracker.box;

import java.io.ByteArrayOutputStream;
import java.util.List;

import com.box.androidlib.DAO.BoxFile;
import com.grayson.hivetracker.Constants;
import com.grayson.hivetracker.box.listeners.DownloadThisFileListener;
import com.grayson.hivetracker.box.listeners.DownloadV1BackupsListener;
import com.grayson.hivetracker.database.AppDataSource;

public class V1BackupDownloader {
	protected enum Stage {
		FIND_FILES, DOWNLOAD_CATEGORY, DOWNLOAD_LOCATION, DOWNLOAD_HIVE, DOWNLOAD_TASK, DOWNLOAD_LOG, DONE
	}

	protected AppDataSource db;

	protected DownloadV1BackupsListener downloadV1BackupsListener;

	protected List<? extends BoxFile> listOfFilesInBackupFolder;

	protected int currentStageIndex;

	protected Stage[] stages = { Stage.FIND_FILES, Stage.DOWNLOAD_CATEGORY, Stage.DOWNLOAD_LOCATION, Stage.DOWNLOAD_HIVE, Stage.DOWNLOAD_TASK,
			Stage.DOWNLOAD_LOG, Stage.DONE };

	protected long backupCategoryFileId;
	protected long backupLocationFileId;
	protected long backupHiveFileId;
	protected long backupTaskFileId;
	protected long backupLogFileId;

	public V1BackupDownloader(List<? extends BoxFile> listOfFilesInBackupFolder) {
		this.listOfFilesInBackupFolder = listOfFilesInBackupFolder;
	}

	public void downloadV1Backups(DownloadV1BackupsListener downloadV1BackupsListener) {
		this.downloadV1BackupsListener = downloadV1BackupsListener;

		setup();

		currentStageIndex = -1;
		moveToNextStage();
	}

	protected void notifyListenerThatTheDownloadFailed(String reason) {
		tearDown();
		downloadV1BackupsListener.onError(String.format("Backup failed because %s", reason));
	}

	protected void provideListenerAProgressUpdate(String message) {
		downloadV1BackupsListener.onProgressUpdate(message);
	}

	protected void notifyListenerThatTheDownloadHasBeenCompleted() {
		tearDown();
		downloadV1BackupsListener.onComplete();
	}

	protected void moveToNextStage() {
		currentStageIndex++;
		if (currentStageIndex >= stages.length)
			return; // this should never happen - DONE stage should result in
					// exiting

		Stage currentStage = stages[currentStageIndex];

		switch (currentStage) {
		case FIND_FILES:
			doStageFindFiles();
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
		case DONE:
			notifyListenerThatTheDownloadHasBeenCompleted();
			break;
		default:
			break;
		}
	}

	private void doStageFindFiles() {
		if (listOfFilesInBackupFolder == null || listOfFilesInBackupFolder.size() == 0) {
			notifyListenerThatTheDownloadFailed("No files found to download.");
			return;
		}

		for (int i = 0; i < listOfFilesInBackupFolder.size(); i++) {
			BoxFile file = listOfFilesInBackupFolder.get(i);
			String filename = file.getFileName();

			if (filename.equals(Constants.BACKUP_FILENAME_CATEGORY)) {
				backupCategoryFileId = file.getId();
			} else if (filename.equals(Constants.BACKUP_FILENAME_LOCATION)) {
				backupLocationFileId = file.getId();
			} else if (filename.equals(Constants.BACKUP_FILENAME_HIVE)) {
				backupHiveFileId = file.getId();
			} else if (filename.equals(Constants.BACKUP_FILENAME_TASK)) {
				backupTaskFileId = file.getId();
			} else if (filename.equals(Constants.BACKUP_FILENAME_LOGENTRY)) {
				backupLogFileId = file.getId();
			}

			// although technically it shouldn't fail if there are some files missing e.g. tasks, logs etc. at the time of this comment
			// there is no legimate case for any files to be missing as they should always be present even if empty.
			if (backupCategoryFileId > 0 && backupLocationFileId > 0 && backupHiveFileId > 0 && backupTaskFileId > 0 && backupLogFileId > 0) {
				// done - we have all the files we need. lets exist this loop!
				i = listOfFilesInBackupFolder.size();
			}
		}
		moveToNextStage();
	}

	private void doStageDownloadCategory() {
		if (backupCategoryFileId > 0) {
			DownloadThisFileListener listener = new DownloadThisFileListener() {

				private boolean didError = false;
				private boolean didTimeout = false;
				
				@Override
				public void onComplete(ByteArrayOutputStream fileStream) {
					if(didTimeout || didError) return;
					
					importThisFileIntoTheDatabase(fileStream);
					moveToNextStage();
				}

				@Override
				public void onError(String errorMessage) {
					if(didTimeout) return;
					didError = true;
					
					notifyListenerThatTheDownloadFailed(errorMessage);
				}

				@Override
				public void onProgressUpdate(String message) {

				}

				@Override
				public void onTimeoutError(String errorMessage) {
					if(didError) return;
					didTimeout = true;
					
					notifyListenerThatTheDownloadFailed("A timeout error has occured. Please check your internet connection.");
				}

			};

			BoxManager.getInstance().downloadThisFile(backupCategoryFileId, listener);
		} else {
			notifyListenerThatTheDownloadFailed("No run files found!");
		}
	}

	private void doStageDownloadLocation() {
		if (backupLocationFileId > 0) {
			DownloadThisFileListener listener = new DownloadThisFileListener() {
				
				private boolean didError = false;
				private boolean didTimeout = false;
				
				@Override
				public void onComplete(ByteArrayOutputStream fileStream) {
					if(didError || didTimeout) return;
					
					importThisFileIntoTheDatabase(fileStream);
					moveToNextStage();
				}

				@Override
				public void onError(String errorMessage) {
					if(didTimeout) return;
					didError = true;
					
					notifyListenerThatTheDownloadFailed("An error occurred.");
				}

				@Override
				public void onProgressUpdate(String message) {

				}

				@Override
				public void onTimeoutError(String errorMessage) {
					if(didError) return;
					didTimeout = true;
					
					notifyListenerThatTheDownloadFailed("A timeout error has occured. Please check your internet connection.");
				}

			};

			BoxManager.getInstance().downloadThisFile(backupLocationFileId, listener);
		} else {
			notifyListenerThatTheDownloadFailed("No run files found!");
		}
	}

	private void doStageDownloadHive() {
		if (backupHiveFileId > 0) {
			DownloadThisFileListener listener = new DownloadThisFileListener() {
				
				private boolean didError = false;
				private boolean didTimeout = false;
				
				@Override
				public void onComplete(ByteArrayOutputStream fileStream) {
					if(didError || didTimeout) return;
					
					importThisFileIntoTheDatabase(fileStream);
					moveToNextStage();
				}

				@Override
				public void onError(String errorMessage) {
					if(didTimeout) return;
					didError = true;
					
					notifyListenerThatTheDownloadFailed("An error occurred.");
				}

				@Override
				public void onProgressUpdate(String message) {
					provideListenerAProgressUpdate(message);
				}

				@Override
				public void onTimeoutError(String errorMessage) {
					if(didError) return;
					didTimeout = true;
					
					notifyListenerThatTheDownloadFailed("A timeout error has occured. Please check your internet connection.");
				}

			};

			BoxManager.getInstance().downloadThisFile(backupHiveFileId, listener);
		} else {
			notifyListenerThatTheDownloadFailed("No run files found!");
		}
	}

	private void doStageDownloadLog() {
		if (backupLogFileId > 0) {
			DownloadThisFileListener listener = new DownloadThisFileListener() {

				private boolean didError = false;
				private boolean didTimeout = false;
				
				@Override
				public void onComplete(ByteArrayOutputStream fileStream) {
					if(didError || didTimeout) return;
					
					importThisFileIntoTheDatabase(fileStream);
					moveToNextStage();
				}

				@Override
				public void onError(String errorMessage) {
					if(didTimeout) return;
					didError = true;
					
					notifyListenerThatTheDownloadFailed("An error occurred.");
				}

				@Override
				public void onProgressUpdate(String message) {

				}

				@Override
				public void onTimeoutError(String errorMessage) {
					if(didError) return;
					didTimeout = true;
					
					notifyListenerThatTheDownloadFailed("A timeout error has occured. Please check your internet connection.");
				}

			};

			BoxManager.getInstance().downloadThisFile(backupLogFileId, listener);
		} else {
			notifyListenerThatTheDownloadFailed("No run files found!");
		}
	}

	private void doStageDownloadTask() {
		if (backupTaskFileId > 0) {
			DownloadThisFileListener listener = new DownloadThisFileListener() {
				
				private boolean didError = false;
				private boolean didTimeout = false;
				
				@Override
				public void onComplete(ByteArrayOutputStream fileStream) {
					if(didTimeout || didError) return;
					
					importThisFileIntoTheDatabase(fileStream);
					moveToNextStage();
				}

				@Override
				public void onError(String errorMessage) {
					if(didTimeout) return;
					notifyListenerThatTheDownloadFailed("An error occurred.");
				}

				@Override
				public void onProgressUpdate(String message) {

				}

				@Override
				public void onTimeoutError(String errorMessage) {
					if(didError) return;
					didTimeout = true;
					
					notifyListenerThatTheDownloadFailed("A timeout error has occured. Please check your internet connection.");
				}

			};

			BoxManager.getInstance().downloadThisFile(backupTaskFileId, listener);
		} else {
			notifyListenerThatTheDownloadFailed("No run files found!");
		}
	}
	
	protected void importThisFileIntoTheDatabase(ByteArrayOutputStream xmlStream) {
		Stage currentStage = stages[currentStageIndex];
		switch (currentStage) {
		case DOWNLOAD_CATEGORY:
			V1ParseAndImportTool.importCategoriesXML(xmlStream);
			provideListenerAProgressUpdate("Downloaded categories successfully");
			break;
		case DOWNLOAD_HIVE:
			V1ParseAndImportTool.importHivesXML(xmlStream);
			provideListenerAProgressUpdate("Downloaded hives successfully");
			break;
		case DOWNLOAD_LOCATION:
			V1ParseAndImportTool.importLocationsXML(xmlStream);
			provideListenerAProgressUpdate("Downloaded locations successfully");
			break;
		case DOWNLOAD_LOG:
			V1ParseAndImportTool.importLogEntryXML(xmlStream);
			provideListenerAProgressUpdate("Downloaded logentries successfully");
			break;
		case DOWNLOAD_TASK:
			V1ParseAndImportTool.importTasksXML(xmlStream);
			provideListenerAProgressUpdate("Downloaded tasks successfully");
			break;
		default:
			break;
		}
	}

	protected void setup() {
		db = AppDataSource.getInstance();
		// db.open(); - the calling service must have opened the database!
	}

	protected void tearDown() {
		// db.close();
	}
}
