package com.grayson.hivetracker.box.listeners;

import java.util.List;

import com.box.androidlib.DAO.BoxFile;

public interface GetListOfFilesInThisFolderListener extends BoxRequestListener {
	public void onComplete(List<? extends BoxFile> list);
	public void onError(String errorMessage);
}
