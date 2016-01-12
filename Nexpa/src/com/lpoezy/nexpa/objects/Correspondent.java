package com.lpoezy.nexpa.objects;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lpoezy.nexpa.activities.ChatActivity;
import com.lpoezy.nexpa.chatservice.OneComment;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.DateUtils.DateFormatz;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.StringFormattingUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Correspondent {

	private long id = -1;
	private String username = "";

	private Bitmap profilePic;

	private List<OnCorrespondentUpdateListener> listeners = new ArrayList<Correspondent.OnCorrespondentUpdateListener>();

	public static final String ACTION_UPDATE = "com.lpoezy.nexpa.actions.CORRESPONDENT_UPDATE";

	private List<OneComment> conversation = new ArrayList<OneComment>();

	private UserProfile mAdaptee = new UserProfile();

	public Correspondent(long id, String username) {

		this.id = id;
		this.username = username;

		mAdaptee.setId(id);
		mAdaptee.setUsername(username);
	}

	public Correspondent() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
		mAdaptee.setId(id);
		notifyListeners();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		mAdaptee.setUsername(username);
		notifyListeners();
	}

	public Bitmap getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(Bitmap profilePic) {
		this.profilePic = profilePic;

		if (this.profilePic != null)
			notifyListeners();
	}

	public void addMessage(OneComment msg) {

		conversation.add(msg);

		L.debug("comment added to conversation array :" + msg.comment + ", conversation size: " + conversation.size());
	}

	public List<OneComment> getConversation() {
		return conversation;
	}

	public void clearExistingConversation() {
		conversation.clear();

	}

	public void saveOnline(Context context, boolean clearConversationStacks) {
		// don't save this session if there is no conversation happen
		if (conversation.size() == 0 || conversation.isEmpty())
			return;

		boolean success = false;
		for (OneComment comment : conversation) {

			long now = System.currentTimeMillis();
			// comment will automatically marked as read,
			// if ChatActivity is running
			// if (ChatActivity.isRunning) {
			// comment.isUnread = false;
			// }

			comment.isUnread = comment.success;

			long senderId = comment.senderId;
			long receiverId = comment.receiverId;
			success = comment.saveOnline(context, senderId, receiverId);

			if (success) {

				comment.saveOffline(context, senderId, receiverId);
			}
		}

		// clear all the conversation ,
		// if all the messages were save in db
		if (clearConversationStacks)
			conversation.clear();

	}

	// will save the correspondent offline,
	// then save the newly received message to online and offline
	public boolean saveNewlySendMsgOnline(Context context, boolean clearConversationStacks) {

		// don't save this session if there is no conversation happen
		if (conversation.size() == 0 || conversation.isEmpty())
			return false;

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		long senderId = Long.parseLong(db.getLoggedInID());
		long receiverId = id;
		// correspondent does not exist in db
		// if (!isExisting(context)) {
		// // save correspondent
		// db.saveCorrespondent(Long.toString(id), username, "", "");
		// }
		mAdaptee.updateOffline(context);

		// save messages to db
		L.debug("Correspondent id: " + id);
		boolean success = false;
		for (OneComment comment : conversation) {

			long now = System.currentTimeMillis();
			// comment will automatically marked as read,
			// if ChatActivity is running
			// if (ChatActivity.isRunning) {
			// comment.isUnread = false;
			// }

			comment.isUnread = comment.success;
			String date = DateUtils.millisToSimpleDate(now, DateFormatz.DATE_FORMAT_5);
			comment.date = date;

			success = comment.saveOnline(context, senderId, receiverId);

			if (success) {

				comment.saveOffline(context, senderId, receiverId);
			}
		}

		// clear all the conversation ,
		// if all the messages were save in db
		if (clearConversationStacks)
			conversation.clear();

		db.close();
		return success;
	}

	// only save correspondent
	public void saveOffline(Context context) {
		L.debug("Correspondent, saveOffline");

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		// correspondent does not exist in db
		if (!isExisting(context)) {
			// save correspondent

			db.saveCorrespondent(Long.toString(id), username, "", "");
		}

		db.close();

	}

	// will save the correspondent queried online,
	// plus all the messages pointing to the correspondent id
	public void saveOffline(Context context, long senderId, long receiverId, boolean clearConversationStacks) {
		L.debug("Correspondent, saveOffline");
		// don't save this session if there is no conversation happen
		if (conversation.size() == 0 || conversation.isEmpty())
			return;

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();

		// correspondent does not exist in db
		if (!isExisting(context)) {
			// save correspondent

			db.saveCorrespondent(Long.toString(id), username, "", "");
		}

		db.close();

		// save messages to db
		L.debug("sender id: " + id);

		for (OneComment comment : conversation) {
			boolean isExisting = OneComment.isExisting(context, comment);
			// L.debug("comment "+comment.comment+", senderId:
			// "+comment.senderId+", receiverId: "+comment.receiverId);
			if (!isExisting) {
				comment.isUnread = ChatActivity.isRunning ? false : true;

				comment.saveOffline(context, senderId, receiverId);
			}

		}

		// clear all the conversation ,
		// if all the messages were save in db
		if (clearConversationStacks)
			conversation.clear();

	}

	public boolean isExisting(Context context) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		Correspondent result = db.downloadCorrespondentByUserId(id);

		db.close();

		if (result != null) {

			id = result.id;
			username = result.username;

			if (id != -1)
				return true;
		}

		return false;
	}

	public void downloadLatestMsgOffline(Context context) {

		OneComment comment = new OneComment();
		comment.downloadLatestOffline(context, id);

		conversation.add(comment);
	}

	public void downloadAllMessagesByUserIdAndCorrespondentIdOnline(Context context) {

		conversation.addAll(OneComment.downloadAllMessagesByUserIdAndCorrespondentIdOnline(context, id));

		// for (OneComment comment : conversation) {
		//
		// if (comment.dateReceived == null ||
		// comment.dateReceived.equalsIgnoreCase("0000-00-00 00:00:00")) {
		//
		// long now = System.currentTimeMillis();
		// String dateReceived = DateUtils.millisToSimpleDate(now,
		// DateFormatz.DATE_FORMAT_5);
		// comment.dateReceived = dateReceived;
		//
		// comment.markAsReceivedOffline(context);
		// }
		// if (comment.isUnread) {
		// // marking messages read online is moved,
		// // to the sync button in the settings screen
		// // if (comment.markAsReadOnline(context, id)) {
		//
		// comment.markAsReadOffline(context, id);
		//
		// // }
		// }
		// }

	}

	public void downloadOfflineMessagesByIds(Context context, long senderId, long receiverId) {

		conversation.addAll(
				OneComment.downloadMessagesOfflineByIds(context, Long.toString(senderId), Long.toString(receiverId)));
		for (OneComment comment : conversation) {

			if (comment.isUnread) {

				comment.markAsReadOffline(context, id);

			}
		}

	}

	public void downloadOfflineMessages(Context context) {

		conversation.addAll(OneComment.downloadAllMessagesOffline(context, id));

		for (OneComment comment : conversation) {

			if (comment.isUnread) {

				comment.markAsReadOffline(context, id);

			}
		}

	}

	public void downloadProfilePicOnline(final Context context) {

		if (profilePic != null)
			return;

		ProfilePicture profilePicture = new ProfilePicture();
		profilePicture.setUserId(id);

		// download profile pic info offline
		profilePicture.downloadOffline(context);

		if ((profilePicture.getImgDir() != null && !profilePicture.getImgDir().isEmpty())
				&& (profilePicture.getImgFile() != null && !profilePicture.getImgFile().isEmpty())) {

			String spec = AppConfig.URL + "/" + profilePicture.getImgDir() + "/" + profilePicture.getImgFile();
			L.debug("spec " + spec);
			setProfilePic(HttpUtilz.downloadImage(spec));
			// context.sendBroadcast(new Intent(ACTION_UPDATE));

		}
	}

	public void removeListener(OnCorrespondentUpdateListener listener) {

		int index = listeners.indexOf(listener);
		if (index != -1)
			listeners.remove(index);

	}

	public void addListener(OnCorrespondentUpdateListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners() {
		for (OnCorrespondentUpdateListener listener : listeners) {

			listener.onCorrespondentUpdate();
		}

	}

	public static List<Correspondent> downloadLatestMsgsOnline(final Context context) {
		L.debug("=============Correspondent, downloadLatestMsgsOnline ================");
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
		// L.debug("userId: "+userId+", webPage: " + webPage);

		// List<Correspondent> list = new ArrayList<Correspondent>();
		
		Correspondents correspondents = new Correspondents();
		Messages messages = new Messages();
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

						// *
						Correspondent correspondent = new Correspondent();
						correspondent.setId(correspondentId);
						correspondent.setUsername(correspondentName);
						
						correspondents.add(correspondent);
						// OneComment message = new OneComment(left, comment,
						// success, date, isUnread);

						OneComment message = new OneComment(senderId, receiverId, left, comment, success, date,
								 isUnread, true);
						
						messages.add(message);
						// message.dateReceived = dateReceived;
						correspondent.addMessage(message);

						//correspondent.saveOffline(context, senderId, receiverId, false);
						// list.add(correspondent);
						// */

					}
					
					correspondents.saveOffline(context);
					messages.saveMsgsAndMarkAsReceivedOffline(context);
				}
			}
		} catch (JSONException e) {
			L.error("" + e);
		}

		//List<Correspondent> list = downloadAllOffline(context);
		List<Correspondent> list = null;
		long now = System.currentTimeMillis();

		L.debug("exec time " + (now - last) / 1000 + " seconds");

		L.debug("================================================");

		return list;

	}

//	public static List<Correspondent> downloadAllMsgsOnline(final Context context) {
//		L.debug("=============Correspondent, downloadAllMsgsOnline================");
//
//		long last = System.currentTimeMillis();
//
//		SQLiteHandler db = new SQLiteHandler(context);
//		db.openToWrite();
//		final String userId = db.getLoggedInID();
//
//		// will download all the messages info
//		HashMap<String, String> postDataParams = new HashMap<String, String>();
//		postDataParams.put("tag", "download_all");
//		postDataParams.put("user_id", userId);
//
//		L.debug("Correspondent, getting all  msgs of " + userId + " online");
//
//		final String spec = AppConfig.URL_MSG;
//		String webPage = HttpUtilz.makeRequest(spec, postDataParams);
//		// L.debug("webPage: " + webPage);
//		db.close();
//		List<Correspondent> list = new ArrayList<Correspondent>();
//		try {
//			JSONObject result = new JSONObject(webPage);
//			if (!result.getBoolean("error")) {
//				final JSONArray jArr = result.getJSONArray("messages");
//				// L.debug("jArr.length() "+jArr.length());
//				if (jArr.length() != 0) {
//
//					int n = jArr.length() < 5 && jArr.length() != 0 ? jArr.length() : 5;
//					StringBuilder sb = new StringBuilder();
//					for (int i = 0; i < jArr.length(); i++) {
//
//						JSONObject jObj = jArr.getJSONObject(i);
//						final long senderId = Long.parseLong(jObj.getString("user_id"));// from
//						final long receiverId = Long.parseLong(jObj.getString("correspondent_id"));// to
//						final long correspondentId = (Long.parseLong(userId) == senderId) ? receiverId : senderId;
//
//						final boolean left = StringFormattingUtils.getBoolean(jObj.getString("is_left"));
//						final String comment = jObj.getString("message");
//						final boolean success = StringFormattingUtils.getBoolean(jObj.getString("is_success"));
//						final String date = jObj.getString("date_created");
//						final String dateReceived = jObj.getString("date_received");
//
//						final boolean isUnread = StringFormattingUtils.getBoolean(jObj.getString("is_unread"));
//
//						if (!sb.toString().contains("" + correspondentId)) {
//							sb.append(correspondentId + ",");
//
//							// if(i<jArr.length()-1)sb.append(",");
//						}
//
//						/*
//						 * Correspondent correspondent = new Correspondent();
//						 * correspondent.setId(correspondentId);
//						 * 
//						 * correspondent.downloadOnline(context);
//						 * 
//						 * // OneComment message = new OneComment(left, comment,
//						 * // success, date, isUnread);
//						 * 
//						 * OneComment message = new OneComment(senderId,
//						 * receiverId, left, comment, success, date,
//						 * dateReceived, isUnread, true);
//						 * 
//						 * //message.dateReceived = dateReceived;
//						 * correspondent.addMessage(message);
//						 * 
//						 * correspondent.saveOffline(context, senderId,
//						 * receiverId, true); //
//						 */
//
//						OneComment message = new OneComment(senderId, receiverId, left, comment, success, date,
//								dateReceived, isUnread, true);
//
//						boolean isExisting = OneComment.isExisting(context, message);
//						// L.debug("comment "+comment.comment+", senderId:
//						// "+comment.senderId+", receiverId:
//						// "+comment.receiverId);
//
//						if (!isExisting) {
//							message.isUnread = ChatActivity.isRunning ? false : true;
//
//							message.saveOffline(context, senderId, receiverId);
//						}
//					}
//
//					String ids = sb.deleteCharAt(sb.length() - 1).toString();
//					L.debug("ids " + ids);
//					List<Correspondent> correspondents = Correspondent.downloadOnlineWithIds(ids);
//
//					list.clear();
//					list.addAll(correspondents);
//
//					if (correspondents != null && !correspondents.isEmpty()) {
//						for (Correspondent c : correspondents) {
//							c.saveOffline(context);
//							c.downloadLatestMsgOffline(context);
//						}
//					}
//				}
//			}
//		} catch (JSONException e) {
//			L.error("" + e);
//		}
//
//		// List<Correspondent> list = downloadAllOffline(context);
//
//		long now = System.currentTimeMillis();
//
//		L.debug("exec time " + (now - last) / 1000 + " seconds");
//
//		L.debug("================================================");
//		return list;
//
//	}

//	private static List<Correspondent> downloadOnlineWithIds(String ids) {
//		List<UserProfile> profiles = UserProfile.downloadOnlineWithIds(ids);
//		List<Correspondent> correspondents = new ArrayList<Correspondent>();
//		if (profiles != null && !profiles.isEmpty()) {
//
//			for (UserProfile profile : profiles) {
//
//				Correspondent correspondent = new Correspondent(profile.getId(), profile.getUsername());
//				correspondents.add(correspondent);
//			}
//			return correspondents;
//		}
//
//		return null;
//
//	}

//	public boolean downloadOnline(Context context) {
//
//		// only download user profile,
//		// if correspondent do not exist in db
//		if (!isExisting(context)) {
//
//			if (mAdaptee.downloadOnline()) {
//
//				this.id = mAdaptee.getId();
//				this.username = mAdaptee.getUsername();
//
//				return true;
//			}
//		}
//
//		// if (!isExisting(context)) {
//		// L.debug("Correspondent, downloadOnline");
//		// HashMap<String, String> postDataParams = new HashMap<String,
//		// String>();
//		// postDataParams.put("tag", "profile_download");
//		// postDataParams.put("user", "" + this.id);
//		//
//		// L.debug("Correspondent, getting profile details of user id: " +
//		// this.id + " online");
//		//
//		// final String spec = AppConfig.URL_PROFILE;
//		// String webPage = HttpUtilz.makeRequest(spec, postDataParams);
//		//
//		// // L.debug("webPage "+webPage);
//		// try {
//		// JSONObject result = new JSONObject(webPage);
//		// if (!result.getBoolean("error")) {
//		// JSONObject profile = result.getJSONObject("profile");
//		// this.id = Long.parseLong(profile.getString("user"));
//		// this.fname = profile.getString("firstname");
//		// this.username = profile.getString("username");
//		// this.email = profile.getString("email_address");
//		//
//		// return true;
//		// }
//		// } catch (JSONException e) {
//		// L.error("" + e);
//		// }
//		// }
//
//		return false;
//	}

	// public static List<Correspondent> downloadAllMsgsReceivedOnline(Context
	// context) {
	//
	// SQLiteHandler db = new SQLiteHandler(context);
	// db.openToWrite();
	// String userId = db.getLoggedInID();
	//
	// HashMap<String, String> postDataParams = new HashMap<String, String>();
	// postDataParams.put("tag", "download_all_received_msgs");
	// postDataParams.put("user_id", userId);
	//
	// L.debug("Correspondent, getting all msgs of " + userId + " online");
	//
	// final String spec = AppConfig.URL_MSG;
	// String webPage = HttpUtilz.makeRequest(spec, postDataParams);
	// // L.debug("webPage: " + webPage);
	// db.close();
	//
	// JSONObject result;
	//
	//
	// try {
	// result = new JSONObject(webPage);
	// if (!result.getBoolean("error")) {
	// JSONArray jArr = result.getJSONArray("messages");
	// L.debug("jArr.length() " + jArr.length());
	// if (jArr.length() != 0) {
	//
	// for (int i = 0; i < jArr.length(); i++) {
	//
	// JSONObject jObj = jArr.getJSONObject(i);
	// long senderId = jObj.getLong("user_id");// from
	// long receiverId = jObj.getLong("correspondent_id");// to
	// String username = jObj.getString("firstname");
	//
	// Correspondent correspondent = new Correspondent(senderId, username);
	//
	// boolean left =
	// StringFormattingUtils.getBoolean(jObj.getString("is_left"));
	// String comment = jObj.getString("message");
	// boolean success =
	// StringFormattingUtils.getBoolean(jObj.getString("is_success"));
	// String date = jObj.getString("date_created");
	// String dateReceived = jObj.getString("date_received");
	//
	// boolean isUnread =
	// StringFormattingUtils.getBoolean(jObj.getString("is_unread"));
	// OneComment message = new OneComment(left, comment, success, date,
	// isUnread);
	// message.dateReceived = dateReceived;
	// correspondent.addMessage(message);
	//
	// correspondent.saveOffline(context, senderId, receiverId, true);
	//
	// }
	// }
	// }
	// } catch (JSONException e) {
	// L.error("" + e);
	// }
	//
	// List<Correspondent> list = downloadAllOffline(context);
	//
	// return list;
	//
	// }

	 
	public void downloadByUsernameOffline(Context context, String name){
		
	}

	public static List<Correspondent> downloadAllOffline(Context context) {

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		// List<Correspondent> correspondents = db.downloadAllCorrespondents();
		List<Correspondent> correspondents = db.downloadAllCorrespondents();
		db.close();

		for (Correspondent correspondent : correspondents) {
			// correspondent.downloadOfflineMessages(context);
			correspondent.downloadLatestMsgOffline(context);
		}

		return correspondents;
	}

	public interface OnCorrespondentUpdateListener {

		public void onCorrespondentUpdate();
	}

}
