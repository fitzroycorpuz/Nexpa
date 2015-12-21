package com.lpoezy.nexpa.chatservice;

import com.lpoezy.nexpa.sqlite.SessionManager;
import com.lpoezy.nexpa.utility.L;
import com.lpoezy.nexpa.utility.SystemUtilz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//this will start or stop the ChatMessagesService,
//according to network state
public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		SessionManager seesionManager = new SessionManager(context.getApplicationContext());
		boolean isLoggegIn = seesionManager.isLoggedIn();
		boolean isNetworkAvailable = SystemUtilz.isNetworkAvailable(context.getApplicationContext());
		boolean isChatServiceRunning = ChatMessagesService.isRunning;
		boolean isSyncDataRunning = SyncDataService.isRunning;
		boolean isSyncUserProfileRunning = SyncUserProfileService.isRunning;
		boolean isSyncProfilePictureRunning = SyncProfilePictureService.isRunning;
		
		if(isLoggegIn){
			
			Intent msgService = new Intent(context, ChatMessagesService.class);
			Intent syncDataService = new Intent(context, SyncDataService.class);
			Intent syncUserProfileService = new Intent(context, SyncUserProfileService.class);
			Intent syncProfilePictureService = new Intent(context, SyncProfilePictureService.class);
			
			if(isNetworkAvailable){
				
				if(!isChatServiceRunning){
	 				context.startService(msgService);
	 				
				}
				
				if(!isSyncDataRunning){
					context.startService(syncDataService);
				}
				
				if(!isSyncUserProfileRunning){
					context.startService(syncUserProfileService);
				}
				
				if(!isSyncProfilePictureRunning){
					context.startService(syncProfilePictureService);
				}
				
				
			}else{
				if(isChatServiceRunning){
					
					context.stopService(msgService);
				}
				
				if(isSyncDataRunning){
					
					context.stopService(syncDataService);
				}
				
				if(isSyncUserProfileRunning){
					
					context.stopService(syncUserProfileService);
				}
				
				if(isSyncProfilePictureRunning){
					
					context.stopService(syncProfilePictureService);
				}
			}
		
		}
	}

}
