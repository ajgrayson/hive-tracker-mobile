package com.grayson.hivetracker.database;

import com.grayson.hivetracker.tools.Tools;


public class Task {
	private long id;
	private String description;
	private long date;
	private Boolean done;
	private long hiveId;
	private String siteName;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getDate() {
		return date;
	}
	
	public void setDate(long date) {
		this.date = date;
	}
	
	public Boolean getDone() {
		return done;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public long getHiveId() {
		return hiveId;
	}

	public void setHiveId(long hiveId) {
		this.hiveId = hiveId;
	}
	
	public String getSiteName() {
		return this.siteName;
	}
	
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	
	public String toString() {
		return Tools.tidyString(description, 35) + " [" + Tools.tidyString(siteName, 25) + "]";
	}
}
