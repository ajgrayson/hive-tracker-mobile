package com.grayson.hivetracker.box.listeners;

public interface UploadThisFileListener extends BoxRequestListener {
	public void onUploadCompleted(String filename);
	public void onError(String errorMessage);
	public void onProgressUpdate(int progress);
}
