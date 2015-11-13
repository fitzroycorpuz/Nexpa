package com.lpoezy.nexpa.objects;

import java.util.HashMap;

import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.content.Context;
import android.content.Intent;

public class ProfilePicture {

	private long userId;
	private String imgDir;
	private String imgFile;
	private String dateCreated;
	private String dateUpdated;
	
	public ProfilePicture(){}

	public ProfilePicture(long userId, String imgDir, String imgFile, String dateCreated, String dateUpdated) {
		
		this.userId 		= userId;
		this.imgDir 		= imgDir;
		this.imgFile 		= imgFile;
		this.dateCreated 	= dateCreated;
		this.dateUpdated 	= dateUpdated;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getImgDir() {
		return imgDir;
	}

	public void setImgDir(String imgDir) {
		this.imgDir = imgDir;
	}

	public String getImgFile() {
		return imgFile;
	}

	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public void saveOffline(Context context) {
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		//save picture info offline 
		db.saveProfilePicture(userId, imgDir, imgFile, dateCreated, dateUpdated);
		db.close();
	}
	
	public void downloadOnline(){
		
		
	}
	
	public void downloadOffline(Context context){
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		//download picture info offline 
		HashMap<String, String> map = db.downloadProfilePicture(userId);
		if(map!=null){
			this.userId = Long.parseLong(map.get(SQLiteHandler.IMG_USER_ID));
			this.imgDir = map.get(SQLiteHandler.IMG_DIR);
			this.imgFile = map.get(SQLiteHandler.IMG_FILE);
			this.dateCreated = map.get(SQLiteHandler.IMG_DATE_CREATED);
			this.dateUpdated = map.get(SQLiteHandler.IMG_DATE_UPDATED);
		}
		
		db.close();
	}

}
