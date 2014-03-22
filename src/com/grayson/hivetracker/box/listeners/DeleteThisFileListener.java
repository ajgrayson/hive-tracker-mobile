package com.grayson.hivetracker.box.listeners;

public interface DeleteThisFileListener extends BoxRequestListener {
	public void onCompleted();
	public void onError(String errorMessage);
}
