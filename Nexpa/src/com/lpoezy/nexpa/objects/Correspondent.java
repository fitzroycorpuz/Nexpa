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

	private List<NewMessage> conversation = new ArrayList<NewMessage>();
	
	public Correspondent(String username) {

		this.username = username;
		//mCorrespondentProfile.setUsername(username);
	}

	public Correspondent() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		//mCorrespondentProfile.setUsername(username);
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

	public void addMessage(NewMessage msg) {

		conversation.add(msg);

		L.debug("comment added to conversation array :" + msg.getBody() + ", conversation size: " + conversation.size());
	}

	public List<NewMessage> getConversation() {
		return conversation;
	}

	public void clearExistingConversation() {
		conversation.clear();

	}


	// only save correspondent
	public void saveOffline(Context context) {
		L.debug("Correspondent, saveOffline");

		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		db.saveCorrespondent(username);
		db.close();

	}

	public void downloadCorrespondentIdOffline(Context context){
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		this.id = db.downloadCorrespondentId(this.username);
		db.close();
	}

	public void downloadLatestMsgOffline(Context context) {

		//OneComment comment = new OneComment();
		NewMessage comment = new NewMessage();
		comment.downloadLatestOfflineMessageOf(context, this.username);

		conversation.add(comment);
	}

	public void downloadProfilePicOnline(final Context context, long userId) {
		
		if (profilePic != null)
			return;
		
		ProfilePicture profilePicture = new ProfilePicture();
		profilePicture.setUserId(userId);

		// download profile pic info offline
		profilePicture.downloadOffline(context);
		
		if ((profilePicture.getImgDir() != null && !profilePicture.getImgDir().isEmpty())
				&& (profilePicture.getImgFile() != null && !profilePicture.getImgFile().isEmpty())) {
			
			String spec = AppConfig.URL + "/" + profilePicture.getImgDir() + "/" + profilePicture.getImgFile();
			//L.debug("spec" + spec);
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
