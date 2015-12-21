package com.lpoezy.nexpa.objects;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import com.devspark.appmsg.AppMsg;
import com.lpoezy.nexpa.activities.SettingsActivity;
import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Correspondent.OnCorrespondentUpdateListener;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.BmpFactory;
import com.lpoezy.nexpa.utility.DateUtils;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.StringFormattingUtils;
import com.lpoezy.nexpa.utility.SystemUtilz;
import com.lpoezy.nexpa.utility.DateUtils.DateFormatz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

public class ProfilePicture {
	
	public static final String TEMP_LOC = "PROFILE_PIC_LOC";
	
	private long userId;
	private String imgDir;
	private String imgFile;
	private String dateUploaded;
	private Bitmap img;
	private boolean isSyncedOnline;
	
	
	
	public ProfilePicture(){}

	public ProfilePicture(long userId, String imgDir, String imgFile, String dateCreated, boolean isSyncOnline) {
		
		this.userId 		= userId;
		this.imgDir 		= imgDir;
		this.imgFile 		= imgFile;
		this.dateUploaded 	= dateCreated;
		this.isSyncedOnline = isSyncOnline;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
		notifyListeners();
	}

	public String getImgDir() {
		return imgDir;
	}

	public void setImgDir(String imgDir) {
		this.imgDir = imgDir;
		notifyListeners();
	}

	public String getImgFile() {
		return imgFile;
	}

	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
		notifyListeners();
	}

	public String getDateUploaded() {
		return dateUploaded;
	}

	public void setDateUploaded(String dateUploaded) {
		this.dateUploaded = dateUploaded;
		notifyListeners();
	}
	

	public Bitmap getImg() {
		return img;
	}

	public void setImg(Bitmap img) {
		this.img = img;
		notifyListeners();
	}
	
	public boolean isSyncedOnline() {
		return isSyncedOnline;
	}

	public void setSyncedOnline(boolean isSyncedOnline) {
		this.isSyncedOnline = isSyncedOnline;
	}

	public boolean saveImgOnline(Context context){
		
		String imgDecodableString = getUserImgDecodableString(context);
		
		if(imgDecodableString==null)return false;
		
		L.debug("started sending profile pic to server directory...");
		
		BmpFactory bmpFactory = new BmpFactory();
		final int MAX_SIZE = 150;
		Bitmap bmp = bmpFactory.getBmpWithTargetWTargetHFrm(MAX_SIZE, MAX_SIZE, imgDecodableString);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		bmp.compress(Bitmap.CompressFormat.PNG, 90, stream); // compress
																// to
																// which
																// format
																// you
																// want.

		byte[] byte_arr = stream.toByteArray();

		final String imageStr = Base64.encodeBytes(byte_arr);
		//L.debug("imageStr "+imageStr);
		//long now = System.currentTimeMillis();
		//final String dateCreated = DateUtils.millisToSimpleDate(now, DateFormatz.DATE_FORMAT_5);
		
		Uri uri = Uri.parse(imgDecodableString);
		imgFile = SystemUtilz.getDeviceUniqueId(context.getApplicationContext()) + userId
				+ uri.getLastPathSegment().replace(" ", "");
		L.debug("imgFile " + imgFile);
		
		HashMap<String, String> postDataParams = new HashMap<String, String>();
		postDataParams.put("tag", "upload");
		postDataParams.put("image", imageStr);
		postDataParams.put("img_file", imgFile);
		postDataParams.put("user_id", Long.toString(userId));
		postDataParams.put("date_created", this.dateUploaded);

		final String spec = AppConfig.URL_PROFILE_PIC;
		String webPage = HttpUtilz.makeRequest(spec, postDataParams);

		L.debug("ProfilePicture, webPage: " + webPage);
		boolean success = false;
		// {"tag":"upload","error":false,"msg":"9a89cdbd7bb2909289a89cdbd7bb290928female2.jpg Image upload complete!!"}
		try {
			JSONObject result = new JSONObject(webPage);
			 boolean error = result.getBoolean("error");
			 
			if(!error)success = true;
			 
			if(error)L.makeText((Activity)context, "Failed to save picture", AppMsg.STYLE_ALERT);
			
		} catch (JSONException e) {
			L.error(""+e);
		}
		return success;
		
	}

	public void saveOffline(Context context) {
		if(imgDir==null||imgFile==null|| imgDir.isEmpty() || imgFile.isEmpty() || imgDir.equalsIgnoreCase("null") || imgFile.equalsIgnoreCase("null")){
			return;
		}
		L.debug("ProfilePicture, saveOffline");
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		//save picture info offline 
		db.saveProfilePicture(userId, imgDir, imgFile, dateUploaded, isSyncedOnline);
		db.close();
	}
	
	public void downloadOnline(){
		
		
	}
	
	public void downloadMyUnsyncPicProfileOffline(Context context) {
		
		L.debug("ProfilePicture, downloadMyUnsyncPicProfileOffline");
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		//download picture info offline 
		HashMap<String, String> map = db.downloadMyUnsyncedPicProfile();
		if(map!=null){
			this.userId = Long.parseLong(map.get(SQLiteHandler.IMG_USER_ID));
			this.imgDir = map.get(SQLiteHandler.IMG_DIR);
			this.imgFile = map.get(SQLiteHandler.IMG_FILE);
			this.dateUploaded = map.get(SQLiteHandler.IMG_DATE_UPLOADED);
			this.isSyncedOnline = StringFormattingUtils.getBoolean(map.get(SQLiteHandler.IMG_IS_SYNCED_ONLINE));
		}
		
		db.close();
	}
	
	public void downloadOffline(Context context){
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		//download picture info offline 
		HashMap<String, String> map = db.downloadProfilePicture(userId);
		if(map!=null){
			this.userId = Long.parseLong(map.get(SQLiteHandler.IMG_USER_ID));
			this.imgDir = map.get(SQLiteHandler.IMG_DIR);
			this.imgFile = map.get(SQLiteHandler.IMG_FILE);
			this.dateUploaded = map.get(SQLiteHandler.IMG_DATE_UPLOADED);
			this.isSyncedOnline = StringFormattingUtils.getBoolean(map.get(SQLiteHandler.IMG_IS_SYNCED_ONLINE));
		}
		
		db.close();
	}
	
	public  String downloadImageOnline(){
		if(imgDir==null||imgFile==null|| imgDir.isEmpty() || imgFile.isEmpty() || imgDir.equalsIgnoreCase("null") || imgFile.equalsIgnoreCase("null")){
			return null;
		}
		String spec = AppConfig.URL+"/"+imgDir+"/"+imgFile;
		
		File file = new File(Environment.getExternalStorageDirectory(), "nexpa/profile_pictures");
		String dir = file.getAbsolutePath();
		String path = HttpUtilz.downloadFileFrmUrl(spec, dir);
		setImgDir(dir);
		///storage/sdcard0/nexpa/profile_pictures/ef75a17963e785522PeterKaiserSimpson-13.jpg
		//L.debug("ProfilePicture, path: "+path);
		return path;
		
	}
	
	public static String getUserImgDecodableString(Context context){
		long userId = -1;
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		userId = Long.parseLong(db.getLoggedInID());
		db.close();
		
		ProfilePicture pic = new ProfilePicture();
		pic.setUserId(userId);
		pic.downloadOffline(context);
		
		String imgDecodableString = null;
		
		if((pic.getImgDir()!=null && !pic.getImgDir().isEmpty()) && (pic.getImgFile()!=null && !pic.getImgFile().isEmpty())){
			imgDecodableString = 
					pic.getImgDir()+"/"+pic.getImgFile();
		}
		
		return imgDecodableString;
	}
	private List<OnProfilePictureUpdateListener> listeners = new ArrayList<OnProfilePictureUpdateListener>();
	private void notifyListeners(){
		for(OnProfilePictureUpdateListener listener : listeners){
			
			listener.onProfilePictureUpdate();
		}
		
	}
	
	
	
	public void removeListener(OnProfilePictureUpdateListener listener){
		
		int index = listeners.indexOf(listener);
		listeners.remove(index);
	}
	
	public void addListener(OnProfilePictureUpdateListener listener){
		listeners.add(listener);
	}
	
	public interface OnProfilePictureUpdateListener{
		
		public void onProfilePictureUpdate();
	}

	

}
