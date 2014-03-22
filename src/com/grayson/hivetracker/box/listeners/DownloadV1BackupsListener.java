package com.grayson.hivetracker.box.listeners;

public interface DownloadV1BackupsListener extends BoxRequestListener {
	public void onComplete();
	public void onProgressUpdate(String message);
	public void onError(String errorMessage);
}
