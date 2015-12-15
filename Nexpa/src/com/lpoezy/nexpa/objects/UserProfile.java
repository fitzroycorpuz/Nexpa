package com.lpoezy.nexpa.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.DateUtils.DateFormatz;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.content.Context;

public class UserProfile {

	private long id;
	private String username;
	private String description;
	private String profession;
	private String url0;
	private String url1;
	private String url2;
	private String dateUpdated;

	public UserProfile() {
	}

	public UserProfile(String username, String description, String profession, String url0, String url1, String url2,
			String dateUpdated) {

		this.username = username;
		this.description = description;
		this.profession = profession;
		this.url0 = url0;
		this.url1 = url1;
		this.url2 = url2;
		this.dateUpdated = dateUpdated;
	}

	public UserProfile(long id, String username, String description, String profession, String url0, String url1,
			String url2, String dateUpdated) {

		this.id = id;
		this.username = username;
		this.description = description;
		this.profession = profession;
		this.url0 = url0;
		this.url1 = url1;
		this.url2 = url2;
		this.dateUpdated = dateUpdated;

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
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getUrl0() {
		return url0;
	}

	public void setUrl0(String url0) {
		this.url0 = url0;
	}

	public String getUrl1() {
		return url1;
	}

	public void setUrl1(String url1) {
		this.url1 = url1;
	}

	public String getUrl2() {
		return url2;
	}

	public void setUrl2(String url2) {
		this.url2 = url2;
	}

	public String getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public void updateOffline(Context context) {
		L.debug("start updating user info offline");
		SQLiteHandler db = new SQLiteHandler(context);

		db.openToWrite();

		// insert... on duplicate key is not working in sqlite,
		// check if user id alredy exist in local db
		if (db.downloadUserProfile(id) != null) {
			// update if existing
			db.updateUserProfile(id, username, description, profession, url0, url1, url2, dateUpdated);
		} else {
			// insert new user profile if not
			db.saveUserProfile(id, username, description, profession, url0, url1, url2, dateUpdated);
		}

		db.close();
	}

	public String saveOnline(Context context) {
		L.debug("start updating user info online");
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		setId(Long.parseLong(db.getLoggedInID()));

		HashMap<String, String> postDataParams = new HashMap<String, String>();

		postDataParams.put("tag", "profile_update");
		postDataParams.put("user_id", Long.toString(this.id));
		postDataParams.put("username", this.username);
		postDataParams.put("description", this.description);
		postDataParams.put("title", this.profession);
		postDataParams.put("url0", this.url0);
		postDataParams.put("url1", this.url1);
		postDataParams.put("url2", this.url2);
		postDataParams.put("date_updated", this.dateUpdated);

		final String spec = AppConfig.URL_USER_PROFILES;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);

		L.debug("UserProfile, webPage: " + webPage);

		db.close();
		L.debug("updating user info online complete");

		return webPage;
	}

	public boolean downloadOnline() {

		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "profile_download");
		postDataParams.put("user_id", Long.toString(this.id));

		final String spec = AppConfig.URL_USER_PROFILES;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);

		L.debug("UserProfile, downloadOnline, webPage: " + webPage);

		try {
			JSONObject jResult = new JSONObject(webPage);
			// org.json.JSONException: Value {"id":"8","title":"mobile app
			// developer","username":"h","url1":"www.lpoezy.com","url2":"www.lpoezy.com","description":"gghuyyrt","date_updated":"2015-12-08
			// 14:06:41","url0":"www.lpoezy.com"} at user_profiles of type
			// org.json.JSONObject cannot be converted to JSONArray
			if (!jResult.getBoolean("error")) {

				JSONObject jProfile = jResult.getJSONObject("user_profiles");
				this.id = Long.parseLong(jProfile.getString("id"));
				this.username = jProfile.getString("username");
				this.description = jProfile.getString("description");
				this.profession = jProfile.getString("title");
				this.url0 = jProfile.getString("url0");
				this.url1 = jProfile.getString("url1");
				this.url2 = jProfile.getString("url2");
				this.dateUpdated = jProfile.getString("date_updated");

				return true;
			}

		} catch (JSONException e) {
			L.error("" + e);
		}

		return false;
	}

	public void downloadOffline(Context context) {
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		Map<String, String> map = db.downloadUserProfile(this.id);
		// L.debug("UserProfile, downloadOffline: "+map.size());
		if (map != null) {
			this.id = Long.parseLong(map.get(SQLiteHandler.USER_PROFILE_USER_ID));
			this.username = map.get(SQLiteHandler.USER_PROFILE_USERNAME);
			this.description = map.get(SQLiteHandler.USER_PROFILE_DESCRIPTION);
			this.profession = map.get(SQLiteHandler.USER_PROFILE_PROFESSION);
			this.url0 = map.get(SQLiteHandler.USER_PROFILE_URL0);
			this.url1 = map.get(SQLiteHandler.USER_PROFILE_URL1);
			this.url2 = map.get(SQLiteHandler.USER_PROFILE_URL2);
			this.dateUpdated = map.get(SQLiteHandler.USER_PROFILE_DATE_UPDATED);

		}

		db.close();

	}

	public static List<UserProfile> downloadOnlineWithIds(String ids) {

		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "profile_download_with_ids");
		postDataParams.put("user_ids", ids);

		final String spec = AppConfig.URL_USER_PROFILES;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);

		L.debug("UserProfile, downloadOnlineWithIds, webPage: " + webPage);

		try {
			JSONObject jResult = new JSONObject(webPage);
			// org.json.JSONException: Value {"id":"8","title":"mobile app
			// developer","username":"h","url1":"www.lpoezy.com","url2":"www.lpoezy.com","description":"gghuyyrt","date_updated":"2015-12-08
			// 14:06:41","url0":"www.lpoezy.com"} at user_profiles of type
			// org.json.JSONObject cannot be converted to JSONArray
			if (!jResult.getBoolean("error")) {

				JSONArray jProfiles = jResult.getJSONArray("user_profiles");
				List<UserProfile> profiles = new ArrayList<UserProfile>();
				for (int i = 0; i < jProfiles.length(); i++) {
					
					JSONObject jProfile = jProfiles.getJSONObject(i);
					
					long id = Long.parseLong(jProfile.getString("id"));
					String username = jProfile.getString("username");
					String description = jProfile.getString("description");
					String profession = jProfile.getString("title");
					String url0 = jProfile.getString("url0");
					String url1 = jProfile.getString("url1");
					String url2 = jProfile.getString("url2");
					String dateUpdated = jProfile.getString("date_updated");

					UserProfile prof = new UserProfile(id, username, description, profession, url0, url1, url2,
							dateUpdated);
					
					profiles.add(prof);
				}

				return profiles;
			}

		} catch (JSONException e) {
			L.error("" + e);
		}
		
		return null;

	}

}
