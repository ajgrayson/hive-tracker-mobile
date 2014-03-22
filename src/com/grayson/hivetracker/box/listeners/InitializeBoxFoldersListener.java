package com.grayson.hivetracker.box.listeners;

public interface InitializeBoxFoldersListener extends BoxRequestListener {
	public void onInitializedBoxFolders();
	
	public void onProgressUpdate(String message, int progress);
	
	public void onError(String errorMessage);
}
