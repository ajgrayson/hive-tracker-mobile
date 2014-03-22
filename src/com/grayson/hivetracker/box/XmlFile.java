package com.grayson.hivetracker.box;

import com.grayson.hivetracker.tools.Tools;

public class XmlFile {
	private long localId;
	private String filename;
	private String data;
	
	public String getFilename() {
		return filename;
	}
	//public void setFilename(String filename) {
	//	this.filename = filename;
	//}
	public String getData() {
		return data;
	}
	//public void setData(String data) {
	//	this.data = data;
	//}
	
	public long getId() {
		return localId;
	}
	
	public XmlFile(long localId, String filename, String data) {
		this.localId = localId;
		this.filename = String.format("%s.xml", Tools.cleanFilename(filename));
		this.data = data;
	}
}
