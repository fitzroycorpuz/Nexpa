package com.lpoezy.nexpa.objects;

import java.util.HashMap;
import java.util.Map;

import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.content.Context;

public class UserProfile {

	private long id;
	private String fname;
	private String lname;
	private String birthday;
	private String gender;
	private String dateUpdated;

	public UserProfile() {
	}

	public UserProfile(String fname, String lname, String birthday, String gender, String dateUpdated) {

		this.fname = fname;
		this.lname = lname;
		this.birthday = birthday;
		this.gender = gender;
		this.dateUpdated = dateUpdated;
	}

	public UserProfile(long id, String fname, String lname, String birthday, String gender, String dateUpdated) {

		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.birthday = birthday;
		this.gender = gender;
		this.dateUpdated = dateUpdated;

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	
	public void updateOffline(Context context){
		L.debug("start updating user info offline");
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		db.updateUserProfile(id, fname, gender, birthday);
		db.close();
	}

	public void saveOnline(Context context) {
		L.debug("start updating user info online");
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		setId(Long.parseLong(db.getLoggedInID()));

		HashMap<String, String> postDataParams = new HashMap<String, String>();

		postDataParams.put("tag", "profile_basic");
		postDataParams.put("user", Long.toString(this.id));
		postDataParams.put("fname", this.fname);
		postDataParams.put("age", this.birthday);
		postDataParams.put("gender", this.gender);
		postDataParams.put("date_update", this.dateUpdated);

		final String spec = AppConfig.URL_PROFILE;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);

		L.debug("UserProfile, webPage: " + webPage);

		db.close();
		L.debug("updating user info online complete");
	}

	public void downloadOffline(Context context) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		fname = db.getFName();
		gender = db.getGender();
		birthday = db.getBDate();
		db.close();
		
	}

}
