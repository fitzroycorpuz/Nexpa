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

public class Messages {
	
	private List<OneComment> mMessages = new ArrayList<OneComment>();
	
	
	
	public List<OneComment> getMessages() {
		
		return mMessages;
	}

	public void add(OneComment message){
		
		mMessages.add(message);
	}
	
	public void remove(OneComment message){
		
		int i = mMessages.indexOf(message);
		
		if(i!=-1){
			
			mMessages.remove(i);
			
		}else{
			
			throw new ArrayIndexOutOfBoundsException("Message don't exists");
		}
		
	}
	
	public void saveMultipleMsgsOffline(Context context){
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		
		if(mMessages.size()>0) {
			
			db.saveMultipleMsgs(mMessages);
			
		} else {
			
			throw new ArrayIndexOutOfBoundsException("No messages details to save");
		}
		
		db.close();
	}
	
	public void saveMsgsAndMarkAsReceivedOffline(Context context){
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		
		if(mMessages.size()>0) {
			
			db.saveMultipleMsgsAndMarkAsReceived(mMessages);
			
		} else {
			
			throw new ArrayIndexOutOfBoundsException("No messages details to save");
		}
		
		db.close();
	}

	public void downloadMyMsgsOnline(Context context) {
		L.debug("=============Comments, downloadMyMsgsOnline ================");
		long last = System.currentTimeMillis();

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		final String userId = db.getLoggedInID();
		db.close();

		// will download all the latest messages info online
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "download_all_msgs_by_uid");
		postDataParams.put("user_id", userId);

		final String spec = AppConfig.URL_MSG;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
		
		try {
			JSONObject result = new JSONObject(webPage);
			if (!result.getBoolean("error")) {
				final JSONArray jArr = result.getJSONArray("messages");
				
				if (jArr.length() != 0) {
					
					for (int i = 0; i < jArr.length(); i++) {
						final JSONObject jObj = jArr.getJSONObject(i);

						long senderId = Long.parseLong(jObj.getString("user_id"));
						final String senderName = jObj.getString("username");
						final long receiverId = Long.parseLong(jObj.getString("correspondent_id"));// to
						final String receiverName = jObj.getString("correspondent_name");
						final long correspondentId = (Long.parseLong(userId) == senderId) ? receiverId : senderId;
						final String correspondentName = (Long.parseLong(userId) == senderId) ? receiverName
								: senderName;
						final boolean left = StringFormattingUtils.getBoolean(jObj.getString("is_left"));
						final String comment = jObj.getString("message");
						final boolean success = StringFormattingUtils.getBoolean(jObj.getString("is_success"));
						final String date = jObj.getString("date_created");
						final String dateReceived = jObj.getString("date_received");

						final boolean isUnread = StringFormattingUtils.getBoolean(jObj.getString("is_unread"));

						OneComment message = new OneComment(senderId, receiverId, left, comment, success, date,
								dateReceived, isUnread, true);
						
						mMessages.add(message);
						

					}
					
					
				}
			}
		} catch (JSONException e) {
			L.error("" + e);
		}
		
	}
	

}
