package com.grayson.hivetracker.box.listeners;

import com.box.androidlib.DAO.User;

public interface GetLoggedInUserListener extends BoxRequestListener {
	
	public void onUserLogin(User user);
	
	public void onUserLogout();
	
	public void onError(String errorMsg);

	public void onUserIsNotLoggedIn();
}
