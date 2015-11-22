package com.lpoezy.nexpa.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.lpoezy.nexpa.configuration.AppConfig;
import com.lpoezy.nexpa.objects.Correspondent.OnCorrespondentUpdateListener;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;

public class ProfilePicture {

	private long userId;
	private String imgDir;
	private String imgFile;
	private String dateUploaded;
	private Bitmap img;
	
	
	
	public ProfilePicture(){}

	public ProfilePicture(long userId, String imgDir, String imgFile, String dateCreated) {
		
		this.userId 		= userId;
		this.imgDir 		= imgDir;
		this.imgFile 		= imgFile;
		this.dateUploaded 	= dateCreated;
		
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

	public void saveOffline(Context context) {
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		//save picture info offline 
		db.saveProfilePicture(userId, imgDir, imgFile, dateUploaded);
		db.close();
	}
	
	public void downloadOnline(){
		
		
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
			
		}
		
		db.close();
	}
	
	public  String downloadImageOnline(){
		
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
