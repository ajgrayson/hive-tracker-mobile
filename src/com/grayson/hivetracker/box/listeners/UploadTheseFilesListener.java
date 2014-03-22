package com.grayson.hivetracker.box.listeners;

import java.util.List;

import com.box.androidlib.DAO.BoxFile;
import com.grayson.hivetracker.box.XmlFile;

public interface UploadTheseFilesListener extends BoxRequestListener {
	public void onUploadCompleted(List<BoxFile> files);
	public void onError(String errorMessage);
}
