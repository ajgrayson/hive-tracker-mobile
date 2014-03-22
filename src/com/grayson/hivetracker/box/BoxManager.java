package com.grayson.hivetracker.box;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Handler;

import com.box.androidlib.Box;
import com.box.androidlib.DAO.BoxFile;
import com.box.androidlib.DAO.BoxFolder;
import com.box.androidlib.DAO.User;
import com.box.androidlib.ResponseListeners.CreateFolderListener;
import com.box.androidlib.ResponseListeners.DeleteListener;
import com.box.androidlib.ResponseListeners.FileDownloadListener;
import com.box.androidlib.ResponseListeners.FileUploadListener;
import com.box.androidlib.ResponseListeners.GetAccountInfoListener;
import com.box.androidlib.ResponseListeners.GetAccountTreeListener;
import com.box.androidlib.Utils.Cancelable;
import com.grayson.hivetracker.App;
import com.grayson.hivetracker.Constants;
import com.grayson.hivetracker.box.listeners.BoxRequestListener;
import com.grayson.hivetracker.box.listeners.DeleteThisFileListener;
import com.grayson.hivetracker.box.listeners.DownloadThisFileListener;
import com.grayson.hivetracker.box.listeners.GetListOfFilesInThisFolderListener;
import com.grayson.hivetracker.box.listeners.GetLoggedInUserListener;
import com.grayson.hivetracker.box.listeners.InitializeBoxFoldersListener;
import com.grayson.hivetracker.box.listeners.UploadTheseFilesListener;
import com.grayson.hivetracker.box.listeners.UploadThisFileListener;
import com.grayson.hivetracker.tools.Tools;

public class BoxManager {
	private static BoxManager instance = null;

	protected Box box;

	protected BoxFolder boxAccountFolderTree;

	protected long boxUserId;

	protected long appFolderId;
	protected long appBackupFolderId;
	protected long appBackupHiveFolderId;
	protected long appBackupLocationFolderId;
	protected long appBackupCategoryFolderId;
	protected long appBackupTaskFolderId;
	protected long appBackupLogFolderId;
	protected long appHistoryFolderId;
	protected long appConfigFolderId;

	protected OnSharedPreferenceChangeListener prefChangeListener;

	protected String authToken;

	protected long timeoutPeriod = 30000;

	protected Handler timeoutHandler;

	protected BoxRunnable getLoggedInUserRunnable;
	
	protected BoxRunnable initializeBoxFolderRunnable;

	private BoxRunnable uploadThisFileRunnable;

	private BoxRunnable uploadTheseFilesRunnable;

	private BoxRunnable getListOfFilesInThisFolderRunnable;

	private BoxRunnable downloadThisFileRunnable;

	private BoxRunnable deleteThisFileRunnable;

	private BoxRunnable createFolderOnBoxRunnable;

	private Cancelable uploadMultipleCancelable;

	private Cancelable uploadThisFileCancelable;

	private Cancelable downloadThisFileCancelable;

	private BoxManager() {
		box = Box.getInstance(Constants.API_KEY);

		timeoutHandler = new Handler();

		// since this class is singleton we need to monitor the preferences
		// for an updated authToken since the Box Authentication activity
		// saves it there when it logs the user in
		prefChangeListener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences prefs, String prefkey) {
				if (prefkey.compareTo(Constants.PREFS_KEY_AUTH_TOKEN) == 0) {
					authToken = prefs.getString(Constants.PREFS_KEY_AUTH_TOKEN, "");
				}
			}
		};
		App.getSharedPrefs().registerOnSharedPreferenceChangeListener(prefChangeListener);
	}

	public static BoxManager getInstance() {
		if (instance == null) {
			instance = new BoxManager();
		}
		return instance;
	}
	
	public static void resetInstance() {
		instance = new BoxManager();
	}

	// GETTERS && SETTERS

	public String getAuthToken() {
		if (authToken == null) {
			authToken = App.getSharedPrefs().getString(Constants.PREFS_KEY_AUTH_TOKEN, "");
		}
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	// CLASS METHODS

	protected void aTimeoutHasOccurred(BoxRequestListener listener, String message) {
		listener.onTimeoutError(message);
	}
	
	public void cancelAllRequests() {
		if (uploadMultipleCancelable != null) {
			uploadMultipleCancelable.cancel();
		}
		if (downloadThisFileCancelable != null) {
			downloadThisFileCancelable.cancel();
		}
		if (uploadThisFileCancelable != null) {
			uploadThisFileCancelable.cancel();
		}
	}

	public void getLoggedInUser(final GetLoggedInUserListener listener) {
		Tools.logInfo(this, "Box", "getLoggedInUser()");
		
		getLoggedInUserRunnable = new BoxRunnable() {
			@Override
			public void whenRun() {
				aTimeoutHasOccurred(listener, "getLoggedInUser()");
			}
		};
		timeoutHandler.postDelayed(getLoggedInUserRunnable, timeoutPeriod);
		
		box.getAccountInfo(getAuthToken(), new GetAccountInfoListener() {
			@Override
			public void onComplete(final User boxUser, final String status) {
				Tools.logInfo(this, "Box", "getLoggedInUser().onComplete()");
				
				getLoggedInUserRunnable.stop();
				timeoutHandler.removeCallbacks(getLoggedInUserRunnable);

				if (status.equals(GetAccountInfoListener.STATUS_GET_ACCOUNT_INFO_OK) && boxUser != null) {
					boxUserId = boxUser.getId();
					listener.onUserLogin(boxUser);
				} else if (status.equals(GetAccountInfoListener.STATUS_NOT_LOGGED_IN)) {
					listener.onUserIsNotLoggedIn();
				} else {
					listener.onError("Failed to get user account info [status=" + status + "]");
				}
			}

			@Override
			public void onIOException(IOException e) {
				Tools.logInfo(this, "Box", "getLoggedInUser().onIOException()");
				
				getLoggedInUserRunnable.stop();
				timeoutHandler.removeCallbacks(getLoggedInUserRunnable);
				
				listener.onError(e.getMessage());
			}
		});
	}

	public void initializeBoxFolders(final InitializeBoxFoldersListener listener) {
		Tools.logInfo(this, "Box", "initializeBoxFolders()");
		
		initializeBoxFolderRunnable = new BoxRunnable() {
			@Override
			public void whenRun() {
				aTimeoutHasOccurred(listener, "initializeBoxFolders()");
			}
		};
		timeoutHandler.postDelayed(initializeBoxFolderRunnable, timeoutPeriod);

		box.getAccountTree(getAuthToken(), 0, new String[] { Box.PARAM_NOFILES }, new GetAccountTreeListener() {
			@Override
			public void onComplete(BoxFolder boxFolder, String status) {
				Tools.logInfo(this, "Box", "initializeBoxFolders().onComplete()");
				
				initializeBoxFolderRunnable.stop();
				timeoutHandler.removeCallbacks(initializeBoxFolderRunnable);

				if (!status.equals(GetAccountTreeListener.STATUS_LISTING_OK)) {
					listener.onError("Retrieving folders failed '" + status + "'");
					return;
				}

				boxAccountFolderTree = boxFolder;

				findAppFolders(boxFolder);
				createMissingFolders(listener);
			}

			@Override
			public void onIOException(final IOException e) {
				Tools.logInfo(this, "Box", "initializeBoxFolders().onIOException()");
				
				initializeBoxFolderRunnable.stop();
				timeoutHandler.removeCallbacks(initializeBoxFolderRunnable);
				
				listener.onError(e.getMessage());
			}
		});
	}

	public void uploadThisFile(String filename, String fileData, long parentFolderId, final UploadThisFileListener listener) {
		Tools.logInfo(this, "Box", "uploadThisFile()");
		
		InputStream stream = new ByteArrayInputStream(fileData.getBytes());

		uploadThisFileRunnable = new BoxRunnable() {
			@Override
			public void whenRun() {
				uploadThisFileCancelable.cancel();
				aTimeoutHasOccurred(listener, "uploadThisFile()");
			}
		};
		timeoutHandler.postDelayed(uploadThisFileRunnable, timeoutPeriod);

		uploadThisFileCancelable = box.upload(authToken, Box.UPLOAD_ACTION_UPLOAD, stream, filename, parentFolderId, new FileUploadListener() {
			@Override
			public void onIOException(IOException e) {
				Tools.logInfo(this, "Box", "uploadThisFile().IOException()");
				
				uploadThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(uploadThisFileRunnable);
				
				listener.onError(e.getMessage());
			}

			@Override
			public void onProgress(long bytesTransferredCumulative) {
				listener.onProgressUpdate(50);
			}

			@Override
			public void onComplete(BoxFile boxFile, String status) {
				Tools.logInfo(this, "Box", "uploadThisFile().onComplete()");
				
				uploadThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(uploadThisFileRunnable);
				
				listener.onUploadCompleted(boxFile.getFileName());
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e) {
				Tools.logInfo(this, "Box", "uploadThisFile().onFileNotFoundException()");
				
				uploadThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(uploadThisFileRunnable);
				
				listener.onError(e.getMessage());
			}

			@Override
			public void onMalformedURLException(MalformedURLException e) {
				Tools.logInfo(this, "Box", "uploadThisFile().onMalformedURLException()");
				
				uploadThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(uploadThisFileRunnable);
				
				listener.onError(e.getMessage());
			}

			@Override
			public void onComplete(List<BoxFile> boxFile, String status) {
				Tools.logInfo(this, "Box", "uploadThisFile().onComplete()");
				
				uploadThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(uploadThisFileRunnable);
				
				listener.onUploadCompleted("Success");
			}
		});
	}

	protected void uploadTheseFilesToTheCategoryFolder(List<XmlFile> files, UploadTheseFilesListener listener) {
		uploadTheseFiles(files, appBackupCategoryFolderId, listener);
	}

	protected void uploadTheseFilesToTheLocationFolder(List<XmlFile> files, UploadTheseFilesListener listener) {
		uploadTheseFiles(files, appBackupLocationFolderId, listener);
	}

	protected void uploadTheseFilesToTheHiveFolder(List<XmlFile> files, UploadTheseFilesListener listener) {
		uploadTheseFiles(files, appBackupHiveFolderId, listener);
	}

	protected void uploadTheseFilesToTheTaskFolder(List<XmlFile> files, UploadTheseFilesListener listener) {
		uploadTheseFiles(files, appBackupTaskFolderId, listener);
	}

	protected void uploadTheseFilesToTheLogFolder(List<XmlFile> files, UploadTheseFilesListener listener) {
		uploadTheseFiles(files, appBackupLogFolderId, listener);
	}

	protected void uploadTheseFilesToTheHistoryFolder(List<XmlFile> files, UploadTheseFilesListener listener) {
		uploadTheseFiles(files, appHistoryFolderId, listener);
	}

	public void uploadTheseFiles(List<XmlFile> fileList, long parentFolderId, final UploadTheseFilesListener listener) {
		Tools.logInfo(this, "Box", "uploadTheseFiles()");
		
		final List<String> filenames = new ArrayList<String>();
		List<InputStream> streams = new ArrayList<InputStream>();

		for (int i = 0; i < fileList.size(); i++) {
			filenames.add(fileList.get(i).getFilename());
			streams.add(new ByteArrayInputStream(fileList.get(i).getData().getBytes()));
		}

		uploadTheseFilesRunnable = new BoxRunnable() {
			@Override
			public void whenRun() {
				uploadMultipleCancelable.cancel();
				aTimeoutHasOccurred(listener, "uploadTheseFiles()");
			}
		};
		timeoutHandler.postDelayed(uploadTheseFilesRunnable, timeoutPeriod);

		uploadMultipleCancelable = box.uploadMultiple(authToken, Box.UPLOAD_ACTION_UPLOAD, streams, filenames, parentFolderId,
				new FileUploadListener() {
					@Override
					public void onIOException(IOException e) {
						Tools.logInfo(this, "Box", "uploadTheseFiles().onIOException()");
						
						uploadTheseFilesRunnable.stop();
						timeoutHandler.removeCallbacks(uploadTheseFilesRunnable);
						
						listener.onError(e.getMessage());
					}

					@Override
					public void onProgress(long bytesTransferredCumulative) {
					}

					@Override
					public void onFileNotFoundException(FileNotFoundException e) {
						Tools.logInfo(this, "Box", "uploadTheseFiles().onFileNotFoundException()");
						
						uploadTheseFilesRunnable.stop();
						timeoutHandler.removeCallbacks(uploadTheseFilesRunnable);
						
						listener.onError(e.getMessage());
					}

					@Override
					public void onMalformedURLException(MalformedURLException e) {
						Tools.logInfo(this, "Box", "uploadTheseFiles().onMalformedURLException()");
						
						uploadTheseFilesRunnable.stop();
						timeoutHandler.removeCallbacks(uploadTheseFilesRunnable);
						
						listener.onError(e.getMessage());
					}

					@Override
					public void onComplete(List<BoxFile> boxFiles, String status) {
						Tools.logInfo(this, "Box", "uploadTheseFiles().onComplete()");
						
						uploadTheseFilesRunnable.stop();
						timeoutHandler.removeCallbacks(uploadTheseFilesRunnable);
						
						listener.onUploadCompleted(boxFiles);
					}

					@Override
					public void onComplete(BoxFile boxFile, String status) {
						Tools.logInfo(this, "Box", "uploadTheseFiles().onComplete()");
						
						uploadTheseFilesRunnable.stop();
						timeoutHandler.removeCallbacks(uploadTheseFilesRunnable);
						
						listener.onError("Batch upload resulted in a single file upload complete event... something odd is going on.");
					}
				});
	}

	public void getTheListOfFilesInTheConfigFolder(GetListOfFilesInThisFolderListener listener) {
		getListOfFilesInThisFolder(appConfigFolderId, listener);
	}

	public void getTheListOfFilesInTheBackupFolder(GetListOfFilesInThisFolderListener listener) {
		getListOfFilesInThisFolder(appBackupFolderId, listener);
	}

	public void getTheListOfFilesInTheCategoryFolder(GetListOfFilesInThisFolderListener listener) {
		getListOfFilesInThisFolder(appBackupCategoryFolderId, listener);
	}

	public void getTheListOfFilesInTheLocationFolder(GetListOfFilesInThisFolderListener listener) {
		getListOfFilesInThisFolder(appBackupLocationFolderId, listener);
	}

	public void getTheListOfFilesInTheHiveFolder(GetListOfFilesInThisFolderListener listener) {
		getListOfFilesInThisFolder(appBackupHiveFolderId, listener);
	}

	public void getTheListOfFilesInTheTaskFolder(GetListOfFilesInThisFolderListener listener) {
		getListOfFilesInThisFolder(appBackupTaskFolderId, listener);
	}

	public void getTheListOfFilesInTheLogFolder(GetListOfFilesInThisFolderListener listener) {
		getListOfFilesInThisFolder(appBackupLogFolderId, listener);
	}

	public void getListOfFilesInThisFolder(long folderId, final GetListOfFilesInThisFolderListener listener) {
		Tools.logInfo(this, "Box", "getListOfFilesInThisFolder()");
		
		getListOfFilesInThisFolderRunnable = new BoxRunnable() {
			@Override
			public void whenRun() {
				aTimeoutHasOccurred(listener, "getListOfFilesInThisFolder()");
			}
		};
		timeoutHandler.postDelayed(getListOfFilesInThisFolderRunnable, timeoutPeriod);

		box.getAccountTree(getAuthToken(), folderId, null, new GetAccountTreeListener() {
			@Override
			public void onComplete(BoxFolder boxFolder, String status) {
				Tools.logInfo("BoxManager", "Box", "getListOfFilesInThisFolder().onComplete(), status = " + status);
				
				getListOfFilesInThisFolderRunnable.stop();
				timeoutHandler.removeCallbacks(getListOfFilesInThisFolderRunnable);
				if (!status.equals(GetAccountTreeListener.STATUS_LISTING_OK) || boxFolder == null) {
					listener.onError("Retrieving folders failed '" + status + "'");
					return;
				}
				listener.onComplete(boxFolder.getFilesInFolder());
			}

			@Override
			public void onIOException(final IOException e) {
				Tools.logInfo("BoxManager", "Box", "getListOfFilesInThisFolder().onIOException()");
				
				getListOfFilesInThisFolderRunnable.stop();
				timeoutHandler.removeCallbacks(getListOfFilesInThisFolderRunnable);
				
				listener.onError(e.getMessage());
			}
		});
	}

	public void downloadThisFile(long fileId, final DownloadThisFileListener listener) {
		Tools.logInfo(this, "Box", "downloadThisFile()");
		
		final ByteArrayOutputStream destinationOutputStream = new ByteArrayOutputStream();
		
		downloadThisFileRunnable = new BoxRunnable() {
			@Override
			public void whenRun() {
				Tools.logInfo(this, "Box", "downloadThisFile().whenRun()");
				
				downloadThisFileCancelable.cancel();
				aTimeoutHasOccurred(listener, "downloadThisFile()");	
			}
		};
		timeoutHandler.postDelayed(downloadThisFileRunnable, timeoutPeriod);

		downloadThisFileCancelable = box.download(getAuthToken(), fileId, destinationOutputStream, null, new FileDownloadListener() {

			@Override
			public void onIOException(IOException e) {
				Tools.logInfo("BoxManager", "Box", "downloadThisFile().onIOException()");
				
				downloadThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(downloadThisFileRunnable);
				
				listener.onError(e.getMessage());
			}

			@Override
			public void onProgress(long bytesDownloaded) {
				//listener.onProgressUpdate(String.format("Downloaded %s bytes", bytesDownloaded));
			}

			@Override
			public void onComplete(String status) {
				Tools.logInfo("BoxManager", "Box", "downloadThisFile().onComplete(), status = " + status);
				
				downloadThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(downloadThisFileRunnable);
				// String content = new
				// String(destinationOutputStream.toByteArray());
				listener.onComplete(destinationOutputStream);
			}
		});
	}

	public void deleteThisFile(long fileId, final DeleteThisFileListener listener) {
		Tools.logInfo(this, "Box", "deleteThisFile()");
		
		deleteThisFileRunnable = new BoxRunnable() {
			@Override
			public void whenRun() {
				aTimeoutHasOccurred(listener, "deleteThisFile()");
			}
		};
		timeoutHandler.postDelayed(deleteThisFileRunnable, timeoutPeriod);

		box.delete(getAuthToken(), Box.TYPE_FILE, fileId, new DeleteListener() {

			@Override
			public void onIOException(IOException e) {
				Tools.logInfo(this, "Box", "deleteThisFile().onIOException()");
				
				deleteThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(deleteThisFileRunnable);
				
				listener.onError(e.getMessage());
			}

			@Override
			public void onComplete(String status) {
				Tools.logInfo(this, "Box", "deleteThisFile().onComplete()");
				
				deleteThisFileRunnable.stop();
				timeoutHandler.removeCallbacks(deleteThisFileRunnable);
				
				listener.onCompleted();
			}
		});
	}

	protected void findAppFolders(BoxFolder boxFolder) {
		Tools.logInfo(this, "Box", "findAppFolders()");
		
		BoxFolder appFolder = null;

		Iterator<? extends BoxFolder> folderIterator = boxFolder.getFoldersInFolder().iterator();
		while (folderIterator.hasNext() && appFolder == null) {
			BoxFolder boxSubFolder = folderIterator.next();

			if (boxSubFolder.getFolderName().compareTo(Constants.BOX_APP_FOLDER_NAME) == 0 && boxSubFolder.userId() == boxUserId) {
				appFolder = boxSubFolder;
				appFolderId = boxSubFolder.getId();
			}
		}

		BoxFolder backupFolder = null;

		// find the sub folders
		if (appFolder != null) {
			Iterator<? extends BoxFolder> folderIterator2 = appFolder.getFoldersInFolder().iterator();
			while (folderIterator2.hasNext()) {
				BoxFolder subFolder = folderIterator2.next();

				if (subFolder.getFolderName().compareTo(Constants.BOX_APP_FOLDER_BACKUP_NAME) == 0) {
					backupFolder = subFolder;
				}
				assignFolderId(subFolder.getId(), subFolder.getFolderName());
			}
		}

		if (backupFolder != null) {
			Iterator<? extends BoxFolder> folderIterator3 = backupFolder.getFoldersInFolder().iterator();
			while (folderIterator3.hasNext()) {
				BoxFolder subFolder = folderIterator3.next();
				assignFolderId(subFolder.getId(), subFolder.getFolderName());
			}
		}
	}

	protected void createMissingFolders(InitializeBoxFoldersListener listener) {
		Tools.logInfo(this, "Box", "createMissingFolders()");
		
		if (appFolderId <= 0) {
			// create the app folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_NAME, 0);
		} else if (appBackupFolderId <= 0) {
			// create the backup folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_BACKUP_NAME, appFolderId);
		} else if (appHistoryFolderId <= 0) {
			// create the history folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_HISTORY_NAME, appFolderId);
		} else if (appBackupCategoryFolderId <= 0) {
			// create the category folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_BACKUP_CATEGORY_NAME, appBackupFolderId);
		} else if (appBackupLocationFolderId <= 0) {
			// create the location folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_BACKUP_LOCATION_NAME, appBackupFolderId);
		} else if (appBackupHiveFolderId <= 0) {
			// create the hive folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_BACKUP_HIVE_NAME, appBackupFolderId);
		} else if (appBackupTaskFolderId <= 0) {
			// create the task folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_BACKUP_TASK_NAME, appBackupFolderId);
		} else if (appBackupLogFolderId <= 0) {
			// create the log folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_BACKUP_LOG_NAME, appBackupFolderId);
		} else if (appConfigFolderId <= 0) {
			// create the log folder
			createFolderOnBox(listener, Constants.BOX_APP_FOLDER_CONFIG_NAME, appFolderId);
		} else {
			// all done
			listener.onInitializedBoxFolders();
		}
	}

	protected void createFolderOnBox(final InitializeBoxFoldersListener listener, String folderName, long parentFolderId) {
		Tools.logInfo(this, "Box", "createFolderOnBox()");
		
		createFolderOnBoxRunnable = new BoxRunnable() {
			@Override
			public void whenRun() {
				aTimeoutHasOccurred(listener, "createFolderOnBox()");
			}
		};
		timeoutHandler.postDelayed(createFolderOnBoxRunnable, timeoutPeriod);

		box.createFolder(getAuthToken(), parentFolderId, folderName, false, new CreateFolderListener() {

			@Override
			public void onComplete(BoxFolder boxFolder, String status) {
				Tools.logInfo(this, "Box", "createFolderOnBox().onComplete()");
				
				createFolderOnBoxRunnable.stop();
				timeoutHandler.removeCallbacks(createFolderOnBoxRunnable);
				if (!status.equals("create_ok")) {
					listener.onError("Error creating folder '" + status + "'");
					return;
				}
				assignFolderId(boxFolder.getId(), boxFolder.getFolderName());
				createMissingFolders(listener);
			}

			@Override
			public void onIOException(IOException e) {
				Tools.logInfo(this, "Box", "createFolderOnBox().onIOException()");
				
				createFolderOnBoxRunnable.stop();
				timeoutHandler.removeCallbacks(createFolderOnBoxRunnable);
				
				listener.onError(e.getMessage());
			}

		});
	}

	protected void assignFolderId(long folderId, String folderName) {
		if (folderName.compareTo(Constants.BOX_APP_FOLDER_NAME) == 0) {
			appFolderId = folderId;
		} else if (folderName.compareTo(Constants.BOX_APP_FOLDER_BACKUP_NAME) == 0) {
			appBackupFolderId = folderId;
		} else if (folderName.compareTo(Constants.BOX_APP_FOLDER_HISTORY_NAME) == 0) {
			appHistoryFolderId = folderId;
		} else if (folderName.compareTo(Constants.BOX_APP_FOLDER_BACKUP_CATEGORY_NAME) == 0) {
			appBackupCategoryFolderId = folderId;
		} else if (folderName.compareTo(Constants.BOX_APP_FOLDER_BACKUP_LOCATION_NAME) == 0) {
			appBackupLocationFolderId = folderId;
		} else if (folderName.compareTo(Constants.BOX_APP_FOLDER_BACKUP_HIVE_NAME) == 0) {
			appBackupHiveFolderId = folderId;
		} else if (folderName.compareTo(Constants.BOX_APP_FOLDER_BACKUP_TASK_NAME) == 0) {
			appBackupTaskFolderId = folderId;
		} else if (folderName.compareTo(Constants.BOX_APP_FOLDER_BACKUP_LOG_NAME) == 0) {
			appBackupLogFolderId = folderId;
		} else if (folderName.compareTo(Constants.BOX_APP_FOLDER_CONFIG_NAME) == 0) {
			appConfigFolderId = folderId;
		}
	}
}
