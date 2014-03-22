package com.grayson.hivetracker.database;


public class HarvestRecord {
	private long id;
	private long date;
	private boolean disease;
	private long supers;
	private long floralType;
	private long categoryId;
	private long locationId;
	
	public long getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	public long getLocationId() {
		return locationId;
	}
	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public boolean isDisease() {
		return disease;
	}
	public void setDisease(boolean disease) {
		this.disease = disease;
	}
	public long getSupers() {
		return supers;
	}
	public void setSupers(long supers) {
		this.supers = supers;
	}
	public long getFloralType() {
		return floralType;
	}
	public void setFloralType(long floralType) {
		this.floralType = floralType;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
}
