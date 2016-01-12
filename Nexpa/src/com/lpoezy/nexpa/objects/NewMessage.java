package com.lpoezy.nexpa.objects;

import com.lpoezy.nexpa.sqlite.SQLiteHandler;

import android.content.Context;

public class NewMessage {

	private String senderName;
	private String receiverName;
	private String body;
	private boolean isLeft;
	private boolean isSuccessful;
	private boolean isUnread;
	private boolean isSyncedOnline;
	private long date;

	public NewMessage(String senderName, String receiverName, String body, boolean isLeft, boolean isSuccessful, boolean isUnread, boolean isSyncedOnline, long date) {
		
		this.senderName = senderName;
		this.receiverName = receiverName;
		this.body = body;
		this.isLeft = isLeft;
		this.isSuccessful = isSuccessful;
		this.isUnread = isUnread;
		this.isSyncedOnline = isSyncedOnline;
		this.date = date;

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

}
