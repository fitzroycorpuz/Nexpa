package com.lpoezy.nexpa.chatservice;

import android.content.Context;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.StringFormattingUtils;
import com.lpoezy.nexpa.utility.DateUtils.DateFormatz;

public class OneComment {
	
	public boolean left;
	public boolean success;
	public String comment;
	public String date;
	public boolean isUnread;
	
	public OneComment(boolean left, String comment, boolean success) {
		super();
		this.left = left;
		this.comment = comment;
		this.success = success;
	}
	
	public OneComment(boolean left, String comment, boolean success, String date, boolean isUnread) {
		super();
		this.left = left;
		this.comment = comment;
		this.success = success;
		this.date = date;
		this.isUnread = isUnread;
	}
	
	public OneComment() {
		
	}

	public void saveOffline(Context context, long correspondentId){
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		
		long userId = Long.valueOf(db.getUserDetails().get("uid"));
		
		db.saveMessageOffline(userId, correspondentId, left, comment,success, date, isUnread);
		db.close();
		
	}
	
	public void markAsReadOffline(Context context, long correspondentId){
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		db.markMessageAsReadOffline(correspondentId);
		db.close();
		
	}

	public void downloadLatestOffline(Context context, long correspondentId) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		
		long userId = Long.valueOf(db.getUserDetails().get("uid"));
		
		Map<String, String>map = db.downloadLatestMsgOffline(userId, correspondentId);
		
		if(map.size()>0){
			
			this.left = StringFormattingUtils.getBoolean(map.get(SQLiteHandler.MSG_LEFT));
			this.comment = map.get(SQLiteHandler.MSG_BODY);
			this.success = StringFormattingUtils.getBoolean(map.get(SQLiteHandler.MSG_SUCCESS));
			this.date = map.get(SQLiteHandler.MSG_DATE);
			this.isUnread = StringFormattingUtils.getBoolean(map.get(SQLiteHandler.MSG_IS_UNREAD));
		}
		
		db.close();
	}

	public static int getUnReadMsgCountOffline(Context context) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		
		long userId = Long.valueOf(db.getUserDetails().get("uid"));
		int count = db.getUnReadMsgCount(userId);
		db.close();
		
		return count;
	}

}