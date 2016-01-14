package com.lpoezy.nexpa.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.StringFormattingUtils;

import android.content.Context;
import android.database.Cursor;

public class NewMessage {

	private String senderName;
	private String receiverName;
	private String body;
	private boolean isLeft;
	private boolean isSuccessful;
	private boolean isUnread;
	private boolean isSyncedOnline;
	private long date;
	
	@JsonCreator
	public NewMessage(
			@JsonProperty("senderName")String senderName, 
			@JsonProperty("receiverName")String receiverName, 
			@JsonProperty("body")String body, 
			@JsonProperty("isLeft")boolean isLeft, 
			@JsonProperty("isSuccessful")boolean isSuccessful, 
			@JsonProperty("isUnread")boolean isUnread, 
			@JsonProperty("isSyncedOnline")boolean isSyncedOnline, 
			@JsonProperty("date")long date) {
		
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.body = body;
		this.isLeft = isLeft;
		this.isSuccessful = isSuccessful;
		this.isUnread = isUnread;
		this.isSyncedOnline = isSyncedOnline;
		this.date = date;

	}

	public NewMessage() {
		
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public boolean isLeft() {
		return isLeft;
	}

	public void setLeft(boolean isLeft) {
		this.isLeft = isLeft;
	}

	public boolean isSuccessful() {
		return isSuccessful;
	}

	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	
	public boolean isUnread() {
		return isUnread;
	}

	public void setUnread(boolean isUnread) {
		this.isUnread = isUnread;
	}
	
	public boolean isSyncedOnline() {
		return isSyncedOnline;
	}

	public void setSyncedOnline(boolean isSyncedOnline) {
		this.isSyncedOnline = isSyncedOnline;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void saveMyReceivedMsgOffline(Context context) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		receiverName = db.getUsername();
		db.saveMsg(senderName, receiverName, body, isLeft, isSuccessful, isUnread, isSyncedOnline, date);
		db.close();
		
	}
	
	public void saveMySentMsgOffline(Context context) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		senderName = db.getUsername();
		db.saveMsg(senderName, receiverName, body, isLeft, isSuccessful, isUnread, isSyncedOnline, date);
		db.close();
		
	}
	
	
	public static int getUnReadMsgCountOffline(Context context) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();

		String username = db.getUsername();
		int count = db.getUnReadMsgCount(username);
		db.close();

		return count;
	}

	
	
	public void downloadLatestOfflineMessageOf(Context context, String username) {
		// TODO Auto-generated method stub
		
	}

	public static NewMessage getMsg(Cursor cursor) {
		
		String senderName 		= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_SENDER_NAME));
		String receiverName 		= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_RECEIVER_NAME));
		int isLeft 				= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_IS_LEFT));
		String body 				= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_BODY));
		int isSuccessful 			= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_SUCCESS));
		long date 			= cursor.getLong(cursor.getColumnIndex(SQLiteHandler.MSG_DATE));
		int isUnread 			= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_IS_UNREAD));
		//String dateReceived 	= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_DATE_RECEIVED));
		int isSyncedOnline 		= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_IS_SYNCED_ONLINE));
		
		
		
		NewMessage comment = new NewMessage(senderName, receiverName, body, 
				StringFormattingUtils.getBoolean(isLeft), 
				StringFormattingUtils.getBoolean(isSuccessful), 
				StringFormattingUtils.getBoolean(isUnread), 
				StringFormattingUtils.getBoolean(isSyncedOnline), 
				date);
		
		return comment;
	}

	

}
