package com.lpoezy.nexpa.objects;

import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.sqlite.SQLiteHandler;

import android.content.Context;

public class Favorites {

	private List<Favorite> mFavorites = new ArrayList<Favorite>();
	
	public List<Favorite> getFavorites() {
		return mFavorites;
	}
	
	public int size(){
		return mFavorites.size();
		
	}
	
	public Favorite get(int position){
		return mFavorites.get(position);
		
	}

	public void add(Favorite fave){
		getFavorites().add(fave);
	}
	
	public void remove(Favorite fave){
		
		int i = mFavorites.indexOf(fave);
		
		if(i!=-1){
			
			mFavorites.remove(i);
			
		}else{
			
			throw new ArrayIndexOutOfBoundsException("Correspondent don't exists");
		}
	}
	
	public void downloadOffline(Context c){
		SQLiteHandler db = new SQLiteHandler(c);
		db.openToRead();
		mFavorites.addAll(db.downloadAllFavorites());
		db.close();
	}
	
	
	public List<String> getNames(){
		
		List<String> names = new ArrayList<String>();
		for(Favorite f : mFavorites){
			names.add(f.getName());
		}
		
		return names;
		
	}

}
