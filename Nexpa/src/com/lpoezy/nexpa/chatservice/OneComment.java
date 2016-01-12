package com.lpoezy.nexpa.chatservice;

import android.content.Context;
import android.database.Cursor;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	@JsonCreator
	public OneComment(
			@JsonProperty("senderId")long senderId, 
			@JsonProperty("receiverId")long receiverId, 
			@JsonProperty("left")boolean left, 
			@JsonProperty("comment")String comment, 
			@JsonProperty("success")boolean success, 
			@JsonProperty("date")String date, 
			@JsonProperty("isUnread")boolean isUnread, 
			@JsonProperty("isSyncedOnline")boolean isSyncedOnline) {
		
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.left = left;
		this.comment = comment;
		this.success = success;
		this.date = date;
		this.isUnread = isUnread;
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
	
	public static void saveMultipleOffline(Context context, List<OneComment> msgsForBulkInsert) {
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		//long userId = Long.valueOf(db.getUserDetails().get("uid"));

		db.saveMultipleMsgsAndMarkAsReceived(msgsForBulkInsert);
		db.close();

	}
	
	
	public void saveReceivedMsgOffline(Context context, String sendername) {
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		

		
		db.close();
	}	
	
	
	//do not rely on members values while saving local data,
	//senderId and receiverId can change anytime
	public void saveOffline(Context context, long senderId, long receiverId) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		//long userId = Long.valueOf(db.getUserDetails().get("uid"));

		db.saveMessage(senderId, receiverId, left, comment, success, date, isUnread, dateReceived, true);
		db.close();

	}
	public static boolean markMsgsAsReadOnline(List<OneComment> list) throws JsonProcessingException {
		L.debug("============================================");
		L.debug("OneComment, markMsgsAsReadOnline");
		ObjectMapper mapper = new ObjectMapper();
		String msgs = mapper.writeValueAsString(list);
		L.debug("msgs "+msgs);
		
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "mark_msgs_as_read");
		postDataParams.put("msgs_to_update", msgs);
		
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
		L.debug("============================================");
		return false;
		
	}
	
	
	
	
	public boolean markMsgAsReceivedOnline() {
		

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
	
	public void markAsSyncedOffline(Context context) {
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		db.markMessageAsSynced(senderId, date, dateReceived);
		db.close();
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
			this.senderId = Long.parseLong(map.get(SQLiteHandler.MSG_SENDER_NAME));
			this.receiverId = Long.parseLong(map.get(SQLiteHandler.MSG_RECEIVER_NAME));
			this.left = StringFormattingUtils.getBoolean(map.get(SQLiteHandler.MSG_IS_LEFT));
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

	public static List<OneComment> downloadAllMessagesByUserIdAndCorrespondentIdOnline(final Context context, final long correspondentId) {
		
		L.debug("=============OneComment, downloadAllMessagesByUserIdAndCorrespondentIdOnline ================");
		long last = System.currentTimeMillis();
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
				final JSONArray jArr = result.getJSONArray("messages");
				L.debug("jArr.length() " + jArr.length());
				if (jArr.length() != 0) {
					
					int MAX_THREADS = 5;
					int nThreads=jArr.length()> MAX_THREADS ? MAX_THREADS : jArr.length();
					
					Executor executor = Executors.newFixedThreadPool(nThreads);
					ExecutorCompletionService<OneComment> ecs = new ExecutorCompletionService<OneComment>(executor);
					
					for (int i = 0; i < jArr.length(); i++) {
						final JSONObject jObj = jArr.getJSONObject(i);
						ecs.submit(new Callable<OneComment>() {
							
							@Override
							public OneComment call() throws Exception {
								
								
								// long correspondentId =
								// jObj.getLong("correspondent_id");
								long senderId = Long.parseLong(jObj.getString("user_id"));
								long receiverId = Long.parseLong(jObj.getString("correspondent_id"));
								//String username = jObj.getString("firstname");
								//String email = "";
								//String fname = username;
								//Correspondent correspondent = new Correspondent(correspondentId, username);

								boolean left = StringFormattingUtils.getBoolean(jObj.getString("is_left"));
								String comment = jObj.getString("message");
								boolean success = StringFormattingUtils.getBoolean(jObj.getString("is_success"));
								String date = jObj.getString("date_created");
								boolean isUnread = StringFormattingUtils.getBoolean(jObj.getString("is_unread"));
								String dateReceived = jObj.getString("date_received");
								
								OneComment message = new OneComment(senderId, receiverId, left, comment, success, date, isUnread, true);
								
								
//								if(!OneComment.isExisting(context, message)){
//									message.saveOffline(context, senderId, receiverId);
//									
//									
//									if (message.dateReceived == null || message.dateReceived.equalsIgnoreCase("0000-00-00 00:00:00")) {
//
//										long now = System.currentTimeMillis();
//										 
//										message.dateReceived = DateUtils.millisToSimpleDate(now, DateFormatz.DATE_FORMAT_5);
//										
//										message.markAsReceivedOffline(context);
//									}
//									
//									if (message.isUnread) {
//										// marking messages read online is moved,
//										// to the sync button in the settings screen
//										// if (comment.markAsReadOnline(context, id)) {
//
//										message.markAsReadOffline(context, correspondentId);
//
//										// }
//									}
//									
//									
//								}
								
								return message;
							}
						});
						
						
						//correspondent.addMessage(message);
						
						//correspondent.saveOffline(context, senderId, receiverId, true);

					}
					
					
					List<OneComment> msgsForBulkInsert = new ArrayList<OneComment>();
					for (int i = 0; i < jArr.length(); i++) {
						try {
							
							Future<OneComment> f = ecs.take();
							OneComment msg = f.get();
							//messages.add(msg);
							
							msgsForBulkInsert.add(msg);
						
						} catch (InterruptedException e) {
							L.error(""+e);
						} catch (ExecutionException e) {
							L.error(""+e);
						}
					}
					
					OneComment.saveMultipleOffline(context, msgsForBulkInsert);

				}
			}
		} catch (JSONException e) {
			L.error("" + e);
		}
		
		
		messages = db.downloadMessagesByIds(userId, Long.toString(correspondentId));
		
		db.close();
		long now = System.currentTimeMillis();
		L.debug("exec time "+(now-last) / 1000+" seconds");
		
		L.debug("================================================");
		
		
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
	
	public static List<OneComment> downloadMyUnsyncReadMsgsOffline(Context context) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		
		List<OneComment> list = db.downloadMyUnsyncReadMsgs();
		db.close();
		
		return list;
		
	}

	public static List<OneComment> downloadAllMessagesOffline(Context context, long correspondentId) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();

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
		
		String senderId 		= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_SENDER_NAME));
		String receiverId 		= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_RECEIVER_NAME));
		int left 				= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_IS_LEFT));
		String msg 				= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_BODY));
		int success 			= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_SUCCESS));
		String date 			= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_DATE));
		int isUnread 			= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_IS_UNREAD));
		//String dateReceived 	= cursor.getString(cursor.getColumnIndex(SQLiteHandler.MSG_DATE_RECEIVED));
		int isSyncedOnline 		= cursor.getInt(cursor.getColumnIndex(SQLiteHandler.MSG_IS_SYNCED_ONLINE));
		
		L.error("msg: "+msg+", date "+date+", isSyncedOnline "+isSyncedOnline);
		OneComment comment = new OneComment(Long.parseLong(senderId), 
				Long.parseLong(receiverId), 
				StringFormattingUtils.getBoolean(left), 
				msg, 
				StringFormattingUtils.getBoolean(success), 
				date, 
				
				StringFormattingUtils.getBoolean(isUnread),
				StringFormattingUtils.getBoolean(isSyncedOnline)
				);
		
		return comment;
	}

	

	
}