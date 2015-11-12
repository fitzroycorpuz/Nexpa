package com.lpoezy.nexpa.objects;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.lpoezy.nexpa.chatservice.OneComment;
import com.lpoezy.nexpa.sqlite.SQLiteHandler;
import com.lpoezy.nexpa.utility.HttpUtilz;
import com.lpoezy.nexpa.utility.L;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Correspondent {
	
	private long id 						= -1;
	private String username 				= "";
	private String email 					= "";
	private String fname 					= "";
	
	private Bitmap profilePic;
	
	public static final String ACTION_UPDATE = "com.lpoezy.nexpa.actions.CORRESPONDENT_UPDATE";
	
	private List<OneComment> conversation 	= new ArrayList<OneComment>();
	
	public Correspondent(long id, String username, String email, String fname){
		
		this.id = id;
		this.username = username;
		this.email = email;
		this.fname = fname;
		
	}
	
	public Correspondent(){	}
	
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}
	
	public Bitmap getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(Bitmap profilePic) {
		this.profilePic = profilePic;
	}

	public void addMessage(OneComment msg){
		
		conversation.add(msg);
		
		L.debug("comment added to conversation array :" +msg.comment +", conversation size: "+conversation.size());
	}

	public List<OneComment> getConversation() {
		return conversation;
	}
	
	public void clearExistingConversation() {
		conversation.clear();
		
	}
	
	public void saveOffline(Context context, boolean clearConversationStacks) {
		
		//don't save this session if there is no conversation happen 
		if(conversation.size()== 0 || conversation.isEmpty())return;
		
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		
		//correspondent does not exist in db
		if(id == -1){
			//save correspondent 
			id = db.saveCorrespondentOffline(username, email, fname);
			
		}
		db.close();
		
		//save messages to db
		Log.e("Correspondent", "id: " + id);
		for(OneComment comment : conversation){
			comment.saveOffline(context, id);
		}
		
		//clear all the conversation ,
		//if all the messages were save in db
		if(clearConversationStacks)conversation.clear();
		
	}

	public boolean isExisting(Context context) {
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		Correspondent result = db.downloadCorrespondentByEmail(email);
		db.close();
		
		if(result!=null){
			
			id = result.id;
			username = result.username;
			email = result.email;
			fname = result.fname;
			
			if(id != -1)return true;
		}
		
		
		return false;
		
		
	}
	
	public void downloadLatestMsgOffline(Context context){
		
		OneComment comment = new OneComment();
		comment.downloadLatestOffline(context, id);
		
		
		conversation.add(comment);
		
	}

	public void downloadOfflineMessages(Context context) {
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToWrite();
		
		conversation.addAll(db.downloadMessages(id));
		
		for(OneComment comment : conversation){
			comment.markAsReadOffline(context, id);
		}
		
		db.close();
	}
	
	public void downloadProfilePicOnline(final Context context){
		
		if(profilePic!=null)return;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String spec = "http://www.lpoezy.com/happn/profile_pictures/ef75a17963e785522CAM00007.jpg";
				
				//InputStream is = ;
				
				profilePic =HttpUtilz.downloadImage("http://www.lpoezy.com/happn/profile_pictures/ef75a17963e785522CAM00007.jpg");
				 L.debug("profilePic_ "+profilePic);
				context.sendBroadcast(new Intent(ACTION_UPDATE));
				
			}
			
		}).start();
		
	}

	public static List<Correspondent> downloadAllOffline(Context context){
		
		SQLiteHandler db = new SQLiteHandler(context);
		db.openToRead();
		List<Correspondent> correspondents = db.downloadAllCorrespondents();
		db.close();
		
		return correspondents;
	}
	
	

}

