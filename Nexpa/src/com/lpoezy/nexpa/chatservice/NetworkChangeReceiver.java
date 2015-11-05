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
		
		if(isLoggegIn){
			
			Intent service = new Intent(context, ChatMessagesService.class);
			
			if(isNetworkAvailable){
				
				if(!isChatServiceRunning){
	 				context.startService(service);
					
				}
			}else{
				if(isChatServiceRunning){
					
					context.stopService(service);
				}
			}
		}
	}

}
