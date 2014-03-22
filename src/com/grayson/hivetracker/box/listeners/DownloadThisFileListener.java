package com.grayson.hivetracker.box.listeners;

import java.io.ByteArrayOutputStream;

public interface DownloadThisFileListener extends BoxRequestListener {
	public void onComplete(ByteArrayOutputStream fileStream);
	public void onProgressUpdate(String message);
	public void onError(String errorMessage);
}
