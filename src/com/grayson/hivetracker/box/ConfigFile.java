package com.grayson.hivetracker.box;

import java.util.List;

import com.grayson.hivetracker.database.ListValue;

public class ConfigFile {
	private List<ListValue> values;
	private String tablename;
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public List<ListValue> getValues() {
		return values;
	}
	public void setValues(List<ListValue> values) {
		this.values = values;
	}
}
