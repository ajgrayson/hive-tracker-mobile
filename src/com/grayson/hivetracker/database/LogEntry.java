package com.grayson.hivetracker.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grayson.hivetracker.tools.Tools;

public class LogEntry {
	private long id;
	private String description;
	private long date;

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
	
	public String toString() {
		
		Date dateObj = new Date(date);
		
		String dateString = SimpleDateFormat.getDateInstance().format(dateObj);
		
		return dateString + " " + Tools.tidyString(description, 25);
	}
}
