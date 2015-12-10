package com.lpoezy.nexpa.chatservice;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lpoezy.nexpa.activities.ChatActivity;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Correspondent;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.StringFormattingUtils;
import com.lpoezy.nexpa.utility.DateUtils.DateFormatz;

public class OneComment {

	public long senderId;
	public long receiverId;
	public boolean left;
	public boolean success;
	public String comment;
	public String date;
	public boolean isUnread;
	public String dateReceived;
	public boolean isSyncedOnline;

	public OneComment(boolean left, String comment, boolean success) {
		
		this.left = left;
		this.comment = comment;
		this.success = success;
	}

	public OneComment(boolean left, String comment, boolean success, String date, boolean isUnread) {
		
		this.left = left;
		this.comment = comment;
		this.success = success;
		this.date = date;
		this.isUnread = isUnread;
	}
	
	public OneComment(long senderId, long receiverId, boolean left, String comment, boolean success, String date, String dateReceived, boolean isUnread, boolean isSyncedOnline) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.left = left;
		this.comment = comment;
		this.success = success;
		this.date = date;
		this.isUnread = isUnread;
		this.dateReceived = dateReceived;
		this.isSyncedOnline = isSyncedOnline;
	}

	public OneComment() {

	}

	//here correspondentId will be the receiver of the msg
	public boolean saveOnline(Context context, long senderId, long receiverId) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		String userId = db.getLoggedInID();
		
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "upload");
		postDataParams.put("user_id", Long.toString(senderId));
		postDataParams.put("correspondent_id", Long.toString(receiverId));
		postDataParams.put("message", comment);
		postDataParams.put("is_left", StringFormattingUtils.getBoolean(left));
		postDataParams.put("is_success", StringFormattingUtils.getBoolean(success));
		postDataParams.put("is_unread", StringFormattingUtils.getBoolean(isUnread));
		postDataParams.put("date_created", date);
		postDataParams.put("date_received", "0000-00-00 00:00:00");

		L.debug("OneComment, saving msg from " + senderId + " online");

		final String spec = AppConfig.URL_MSG;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
		L.debug("webPage: " + webPage);
		db.close();

		try {
			JSONObject result = new JSONObject(webPage);
			JSONObject message = result.getJSONObject("message");
			if (!result.getBoolean("error")) {
				this.date = message.getString("date_created");
			}

			return true;
		} catch (JSONException e) {
			L.error("" + e);
		}

		return false;
	}

	
	public void saveOffline(Context context, long senderId, long receiverId) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		//long userId = Long.valueOf(db.getUserDetails().get("uid"));

		db.saveMessage(senderId, receiverId, left, comment, success, date, isUnread, dateReceived, true);
		db.close();

	}
	
	public boolean markAsReceivedOnline() {
		

		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "mark_as_received");
		postDataParams.put("user_id", Long.toString(senderId));
		postDataParams.put("correspondent_id", Long.toString(receiverId));
		postDataParams.put("date_created", date);
		String isUnread_ = "0";
		postDataParams.put("is_unread", isUnread_);
		postDataParams.put("date_received", dateReceived);

		L.debug("OneComment, updating msg from senderId: " + senderId +", receiverId: "+receiverId+", date: "+date +", dateReceived: "+dateReceived+" online");

		final String spec = AppConfig.URL_MSG;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
		L.debug("webPage: " + webPage);
		
		try {
			JSONObject result = new JSONObject(webPage);
			if (!result.getBoolean("error")) {
				return true;
			}

		} catch (JSONException e) {
			L.error("" + e);
		}

		return false;
	}

	public boolean markAsReadOnline(Context context) {
		

		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "mark_as_read");
		postDataParams.put("user_id", ""+senderId);
		postDataParams.put("correspondent_id", ""+receiverId);
		postDataParams.put("date_created", date);

		

		final String spec = AppConfig.URL_MSG;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
		L.debug("OneComment, updating msg: "+comment+" from senderId: " + senderId +", receiverId: "+receiverId+ " online");
		L.error("###################################");
		L.error("webPage: " + webPage);
		L.error("###################################");
		
		try {
			JSONObject result = new JSONObject(webPage);
			if (!result.getBoolean("error")) {
				return true;
			}

		} catch (JSONException e) {
			L.error("" + e);
		}

		return false;

	}
	
	public void markAsReceivedOffline(Context context) {
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		db.markMessageAsReceived(senderId, date, dateReceived);
		db.close();
		
	}
	

	public void markAsReadOffline(Context context, long senderId) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		db.markMessageAsRead(senderId, date);
		db.close();

	}

	public void downloadLatestOffline(Context context, long senderId) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();

		long userId = Long.valueOf(db.getLoggedInID());
		
		Map<String, String> map = db.downloadLatestMsgOffline(userId, senderId);
		
		if (map.size() > 0) {
			this.senderId = Long.parseLong(map.get(SQLiteHandler.MSG_USER_ID));
			this.receiverId = Long.parseLong(map.get(SQLiteHandler.MSG_CORRESPONDENT_ID));
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

		long userId = Long.valueOf(db.getLoggedInID());
		int count = db.getUnReadMsgCount(userId);
		db.close();

		return count;
	}

	public static List<OneComment> downloadAllMessagesByUserIdAndCorrespondentIdOnline(Context context, long correspondentId) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		String userId = db.getLoggedInID();
		
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "download_all_msgs_by_user_id_and_correspondent_id");
		postDataParams.put("user_id", userId);
		postDataParams.put("correspondent_id", Long.toString(correspondentId));

		L.debug("OneComment, getting all  msgs of userId: " + userId +", correspondentId: "+correspondentId+ " online");
		
		final String spec = AppConfig.URL_MSG;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
		L.debug("webPage: " + webPage);
		

		JSONObject result;

		List<OneComment> messages = new ArrayList<OneComment>();
		
		try {
			result = new JSONObject(webPage);
			if (!result.getBoolean("error")) {
				JSONArray jArr = result.getJSONArray("messages");
				L.debug("jArr.length() " + jArr.length());
				if (jArr.length() != 0) {

					for (int i = 0; i < jArr.length(); i++) {

						JSONObject jObj = jArr.getJSONObject(i);

						// long correspondentId =
						// jObj.getLong("correspondent_id");
						long senderId = Long.parseLong(jObj.getString("user_id"));
						long receiverId = Long.parseLong(jObj.getString("correspondent_id"));
						String username = jObj.getString("firstname");
						String email = "";
						String fname = username;
						Correspondent correspondent = new Correspondent(correspondentId, username);

						boolean left = StringFormattingUtils.getBoolean(jObj.getString("is_left"));
						String comment = jObj.getString("message");
						boolean success = StringFormattingUtils.getBoolean(jObj.getString("is_success"));
						String date = jObj.getString("date_created");
						boolean isUnread = StringFormattingUtils.getBoolean(jObj.getString("is_unread"));
						String dateReceived = jObj.getString("date_received");
						
						OneComment message = new OneComment(senderId, receiverId, left, comment, success, date, dateReceived, isUnread, true);
						
						messages.add(message);
						correspondent.addMessage(message);
						
						correspondent.saveOffline(context, senderId, receiverId, true);

					}

				}
			}
		} catch (JSONException e) {
			L.error("" + e);
		}
		
		
		messages = db.downloadMessagesByIds(userId, Long.toString(correspondentId));
		
		db.close();
		return messages;

	}
	
	public static List<OneComment> downloadReceivedMsgsOffline(Context context) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		List<OneComment> list = db.downloadReceivedMsgs(context);
		db.close();

		return list;

	}
	
	
	public static List<OneComment> downloadMessagesOfflineByIds(Context context, String senderId, String receiverId) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		List<OneComment> list = db.downloadMessagesByIds(senderId, receiverId);
		db.close();

		return list;

	}

	public static List<OneComment> downloadAllMessagesOffline(Context context, long correspondentId) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		List<OneComment> list = db.downloadMessages(correspondentId);
		db.close();

		return list;

	}

	public static boolean isExisting(Context context, OneComment c) {
		//L.debug("checking if message exists "+c.comment+", "+c.date);
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		Map<String, String> result = db.downloadMsgBySenderIdAndDate(c.senderId, c.receiverId, c.date, c.dateReceived);
		db.close();
		
		if (result != null && result.size()!=0) {
			L.debug("message exists "+c.comment);
			return true;
		}
		
		L.debug("message don't exists ");
		return false;
	}

	public static OneComment getMsg(Cursor cursor) {
		
		String senderId 		= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_USER_ID));
		String receiverId 		= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_CORRESPONDENT_ID));
		int left 				= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_LEFT));
		String msg 				= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_BODY));
		int success 			= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_SUCCESS));
		String date 			= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_DATE));
		int isUnread 			= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_IS_UNREAD));
		String dateReceived 	= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_DATE_RECEIVED));
		int isSyncedOnline 		= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_IS_SYNCED_ONLINE));
		

		OneComment comment = new OneComment(Long.parseLong(senderId), 
				Long.parseLong(receiverId), 
				StringFormattingUtils.getBoolean(left), 
				msg, 
				StringFormattingUtils.getBoolean(success), 
				date, 
				dateReceived, 
				StringFormattingUtils.getBoolean(isUnread),
				StringFormattingUtils.getBoolean(isSyncedOnline)
				);
		
		return comment;
	}

	

}