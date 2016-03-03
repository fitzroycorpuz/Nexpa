package com.lpoezy.nexpa.objects;

import com.lpoezy.nexpa.sqlite.SQLiteHandler;

import android.content.Context;

public class Favorite {
	
	private long id; 
	private String name;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public void saveLocal(Context c) {
		SQLiteHandler db = new SQLiteHandler(c);
		db.openToWrite();
		db.saveFavorite(this.name);
		db.close();
		
	}
	public void deleteLocal(Context c) {
		
		SQLiteHandler db = new SQLiteHandler(c);
		db.openToWrite();
		db.deleteFavorite(this.name);
		db.close();
	}
	
	

}
