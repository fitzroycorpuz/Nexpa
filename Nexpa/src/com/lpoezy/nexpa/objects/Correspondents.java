package com.lpoezy.nexpa.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lpoezy.nexpa.chatservice.OneComment;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.StringFormattingUtils;

import android.app.Activity;
import android.content.Context;

public class Correspondents {
	
	private List<Correspondent> mCorrespondents = new ArrayList<Correspondent>();

	public List<Correspondent> getCorrespondents() {
		
		return mCorrespondents;
	}
	
	public int size(){
		return mCorrespondents.size();
		
	}
	
	public Correspondent get(int position){
		return mCorrespondents.get(position);
		
	}
	
	public void add(Correspondent corrspondent){
		getCorrespondents().add(corrspondent);
	}
	
	public void remove(Correspondent corrspondent){
		
		int i = mCorrespondents.indexOf(corrspondent);
		
		if(i!=-1){
			
			mCorrespondents.remove(i);
			
		}else{
			
			throw new ArrayIndexOutOfBoundsException("Correspondent don't exists");
		}
	}
	
	public void saveOffline(Context context){
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		
		if(mCorrespondents.size()>0) {
			
			db.saveMultipleCorrespondents(mCorrespondents);
			
		} else {
			
			throw new ArrayIndexOutOfBoundsException("No correspondents details to save");
		}
		
		db.close();
	}

	public void downloadOffline(Context context) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		List<Correspondent> list = db.downloadAllCorrespondents();
		mCorrespondents.addAll(list);
		db.close();
		
	}

	public void downloadLatestMsgOffline(Context context) {
		
		for(Correspondent correspondent : mCorrespondents){
			correspondent.downloadLatestMsgOffline(context);
		}
	}
}
